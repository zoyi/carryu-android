package co.zoyi.Chat.Services;

import android.os.AsyncTask;
import co.zoyi.Chat.Datas.ChatServerInfo;
import co.zoyi.Chat.Etc.Util;
import co.zoyi.Chat.Listeners.ChatStatusChangeListener;
import co.zoyi.Chat.Listeners.FetchOurTeamNamesListener;
import co.zoyi.Chat.Packets.OurTeamNamesIQ;
import co.zoyi.Chat.Packets.UpdateStatusPresence;
import co.zoyi.carryu.Application.Etc.CUUtil;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.*;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;

import java.util.List;

public class ChatService {
    private XMPPConnection connection;
    private ConnectionConfiguration connectionConfiguration;
    private Status status = Status.PENDING;
    private String groupChatId;
    private ChatStatusChangeListener chatStatusChangeListener;
    private ChatPacketProcessor chatPacketProcessor = new ChatPacketProcessor(this);
    private FetchOurTeamNamesListener fetchOurTeamNamesListener;
    private OurTeamNamesIQ lastOurTeamNamesIQ;
    private List<String> ourTeamNames;
    private ChatServerInfo chatServerInfo;
    private Presence lastPresence;
    private String defaultOnlineStatusMessage = "Using CarryU";
//    private HashMap<String, FetchOurTeamNamesListener> fetchOurTeamNameListListenerMap = new HashMap<String, FetchOurTeamNamesListener>();
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
        CONNECTION_CLOSED,
        RECONNECTING;

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
            setStatus(Status.RECONNECTING);
        }

        @Override
        public void reconnectionSuccessful() {
            setStatus(Status.CONNECTED);
        }

        @Override
        public void reconnectionFailed(Exception e) {
            setStatus(Status.FAILED_CONNECT);
        }
    };

    public boolean connect() {
        if (this.connectionConfiguration != null) {
            this.connection = new XMPPConnection(this.connectionConfiguration);

            try {
                this.connection.connect();
            } catch (XMPPException e) {
                e.printStackTrace();
            } finally {
                if (this.connection.isConnected()) {
                    this.connection.addConnectionListener(connectionListener);
                    setStatus(Status.CONNECTED);
                } else {
                    setStatus(Status.FAILED_CONNECT);
                }
            }
        }

        return this.connection != null && this.connection.isConnected();
    }

    public void disconnect() {
        if (isConnected()) {
            this.connection.disconnect();
            this.connection = null;
        }
    }

    public void setLastPresence(Presence lastPresence) {
        this.lastPresence = lastPresence;
        this.connection.sendPacket(new UpdateStatusPresence(lastPresence, this.defaultOnlineStatusMessage));
    }

    public void login(String userId, String userPassword) {
        if (connection.isConnected()) {
            try {
                connection.login(userId, "AIR_" + userPassword);
            } catch (XMPPException e) {
                e.printStackTrace();
            }

            if (connection.isAuthenticated()) {
                addPacketProcessors();
                setStatus(Status.AUTHENTICATED);
            } else {
                setStatus(Status.FAILED_AUTHENTICATE);
            }
        }
    }

    private PacketListener packetListener = new PacketListener() {
        @Override
        public void processPacket(Packet packet) {
            chatPacketProcessor.processPacket(packet);
        }
    };

    private void addPacketProcessors() {
        ProviderManager.getInstance().addIQProvider("query", "http://jabber.org/protocol/disco#items", new OurTeamNamesIQProvider());

        PacketFilter filter = new OrFilter(
            new OrFilter(
                new PacketTypeFilter(Presence.class),
                new IQTypeFilter(IQ.Type.RESULT)
            ),
            new AndFilter(
                new ToContainsFilter(Util.toGameClientJabberId(getUser())),
                new PacketTypeFilter(Message.class)
            )
        );

        this.connection.addPacketListener(packetListener, filter);
    }

    public String getUser() {
        return connection.getUser();
    }

    public String getUserId() {
        if (connection != null && connection.getUser() != null) {
            return Util.getUserIDFromJabberID(connection.getUser());
        }
        return null;
    }

    public void setDefaultOnlineStatusMessage(String defaultOnlineStatusMessage) {
        this.defaultOnlineStatusMessage = defaultOnlineStatusMessage;
    }

    public void setChatStatusChangeListener(ChatStatusChangeListener chatStatusChangeListener) {
        this.chatStatusChangeListener = chatStatusChangeListener;
    }

    public boolean isConnected() {
        return connection != null && connection.isConnected();
    }

    public boolean isAuthenticated() {
        return isConnected() && connection.isAuthenticated();
    }

    public ChatServerInfo getChatServerInfo() {
        return chatServerInfo;
    }

    public void setServerInfo(ChatServerInfo chatServerInfo) {
        this.chatServerInfo = chatServerInfo;
        this.connectionConfiguration = new ConnectionConfiguration(chatServerInfo.host, chatServerInfo.port, "pvp.net");
        this.connectionConfiguration.setSASLAuthenticationEnabled(true);
        this.connectionConfiguration.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);
        this.connectionConfiguration.setSocketFactory(new DummySSLSocketFactory());
        this.connectionConfiguration.setReconnectionAllowed(true);
        this.connectionConfiguration.setRosterLoadedAtLogin(false);
        this.connectionConfiguration.setDebuggerEnabled(true); // TODO: debug code
    }

    private void processStatus(Status status) {
        if (status == Status.OUT_OF_GAME) {
            ourTeamNames = null;
        }
    }

    void setStatus(Status status) {
        if (status != this.status) {
            this.status = status;
            if (this.chatStatusChangeListener != null) {
                this.chatStatusChangeListener.onStatusChanged(status);
            }
            processStatus(status);
        }
    }

    public Status getStatus() {
        return status;
    }

    void setGroupChatId(String groupChatId) {
        this.groupChatId = groupChatId;
    }

    public List<String> getOurTeamNames() {
        return ourTeamNames;
    }

    public void onCompleteFetchOurTeamNames(List<String> names) {
        ourTeamNames = names;
        if (fetchOurTeamNamesListener != null) {
            fetchOurTeamNamesListener.onCompleted(names);
        }
        lastOurTeamNamesIQ = null;
    }

    public void onFailFetchOurTeamNames() {
        if (fetchOurTeamNamesListener != null) {
            fetchOurTeamNamesListener.onFailed();
        }
        lastOurTeamNamesIQ = null;
    }

    public void fetchOurTeamNames(FetchOurTeamNamesListener callback) {
        if (status == Status.CHAMPION_SELECT && lastOurTeamNamesIQ == null) {
            lastOurTeamNamesIQ = new OurTeamNamesIQ(connection.getUser(), this.groupChatId);
            this.connection.sendPacket(lastOurTeamNamesIQ);
            fetchOurTeamNamesListener = callback;
        } else {
            onFailFetchOurTeamNames();
        }
    }

//    public void set() {
//
//    }
}
