package co.zoyi.carryu.Chat.Services;

import android.util.Log;
import co.zoyi.carryu.Application.Events.Errors.AuthenticateFailErrorEvent;
import co.zoyi.carryu.Application.Events.Errors.CouldNotConnectChatServerErrorEvent;
import co.zoyi.carryu.Chat.Datas.ChatServerInfo;
import co.zoyi.carryu.Chat.Etc.ChatUtil;
import co.zoyi.carryu.Chat.Etc.DummySSLSocketFactory;
import co.zoyi.carryu.Chat.Events.ChatConnectEvent;
import co.zoyi.carryu.Chat.Events.ChatStatusChangeEvent;
import co.zoyi.carryu.Chat.Events.TeamIdListUpdatedEvent;
import de.greenrobot.event.EventBus;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.*;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

public class ChatService {
    private XMPPConnection connection;
    private String region;
    private ConnectionConfiguration connectionConfiguration;
    private Status status = Status.PENDING;
    private String groupChatId;
    private String IQPacketId;
    private ArrayList<String> ourTeamIdList;

    public enum Status {
        PENDING,
        CONNECTED,
        FAILED_CONNECT,
        AUTHENTICATED,
        FAILED_AUTHENTICATE,
        OUT_OF_GAME,
        IN_QUEUE,
        CHAMPION_SELECT,
        IN_GAME,
        CONNECTION_CLOSED;
    };

    private ConnectionListener connectionListener = new ConnectionListener() {
        @Override
        public void connectionClosed() {
            setStatus(Status.CONNECTION_CLOSED);
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            setStatus(Status.CONNECTION_CLOSED);
        }

        @Override
        public void reconnectingIn(int i) {
        }

        @Override
        public void reconnectionSuccessful() {
        }

        @Override
        public void reconnectionFailed(Exception e) {
        }
    };

    public void onEvent(ChatConnectEvent chatConnectEvent) {
        if (chatConnectEvent.result == ChatConnectEvent.Result.SUCCESS) {
            setStatus(Status.CONNECTED);
        } else {
            setStatus(Status.FAILED_CONNECT);
            EventBus.getDefault().post(new CouldNotConnectChatServerErrorEvent());
        }

        EventBus.getDefault().unregister(this, ChatConnectEvent.class);
    }

    public void connect() {
        this.connection = new XMPPConnection(this.connectionConfiguration);
        EventBus.getDefault().register(this, ChatConnectEvent.class);
        AsyncChatConnectDelegate.connect(this.connection);
    }

    public void disconnect() {
        Log.d("zoyi.smack", "disconnect");
        if (isConnected()) {
            this.connection.disconnect();
            this.connection = null;
        }
    }

    public void login(String userId, String userPassword) {
        if (connection.isConnected()) {
            try {
                connection.login(userId, "AIR_" + userPassword);
            } catch (XMPPException e) {
                e.printStackTrace();
            }

            if (connection.isAuthenticated()) {
//                addIQProvider();
                addPresencePacketOnMeListener();
                setStatus(Status.AUTHENTICATED);
            } else {
                setStatus(Status.FAILED_AUTHENTICATE);
                EventBus.getDefault().post(new AuthenticateFailErrorEvent());
            }
        }
    }

    private void addIQProvider() {
        ProviderManager.getInstance().addIQProvider("query", "http://jabber.org/protocol/disco#items", new DiscoverItemsProvider() {
            @Override
            public IQ parseIQ(XmlPullParser xmlPullParser) throws Exception {
                Log.d("zoyi.smack", xmlPullParser.toString());
                return super.parseIQ(xmlPullParser);
            }
        });
    }

    private void addPresencePacketOnMeListener() {
        PacketFilter filter = new OrFilter(
            new AndFilter(
                new FromContainsFilter(ChatUtil.User.toGameClientJabberId(connection.getUser())),
                new PacketTypeFilter(Presence.class)
            ),
            new AndFilter(
                new ToContainsFilter(ChatUtil.User.toGameClientJabberId(connection.getUser())),
                new PacketTypeFilter(Message.class)
            )
        );

        this.connection.addPacketListener(new ChatPacketListener(this), filter);
        this.connection.addConnectionListener(connectionListener);
    }

    public boolean isConnected() {
        return connection != null && connection.isConnected();
    }

    public boolean isAuthenticated() {
        return isConnected() && connection.isAuthenticated();
    }

    public void setServerInfo(ChatServerInfo chatServerInfo) {
        this.connectionConfiguration = new ConnectionConfiguration(chatServerInfo.host, chatServerInfo.port, "pvp.net");
        this.connectionConfiguration.setSASLAuthenticationEnabled(true);
        this.connectionConfiguration.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);
        this.connectionConfiguration.setSocketFactory(new DummySSLSocketFactory());
        this.connectionConfiguration.setDebuggerEnabled(true); // TODO: debug code
        this.region = chatServerInfo.region;
    }

    void setStatus(Status status) {
        if (this.status != status) {
            EventBus.getDefault().post(new ChatStatusChangeEvent(status));
        }
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    void setGroupChatId(String groupChatId) {
        this.groupChatId = groupChatId;
    }

    public String getGroupChatId() {
        return groupChatId;
    }

    public String getLastIQPacketId() {
        return IQPacketId;
    }

    public void setOurTeamIdList(ArrayList<String> ourTeamIdList) {
        this.ourTeamIdList = ourTeamIdList;
        EventBus.getDefault().post(new TeamIdListUpdatedEvent(ourTeamIdList));
    }

    public ArrayList<String> getOurTeamIdList() {
        return ourTeamIdList;
    }

    public void fetchOurSummonerList() {
        IQ iq = new OutTeamFetchIQ(connection.getUser(), this.groupChatId);
        this.connection.sendPacket(iq);
        this.IQPacketId = iq.getPacketID();
    }

    public String getRegion() {
        return region;
    }
}
