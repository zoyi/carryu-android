package co.zoyi.Chat.Services;

import android.os.AsyncTask;
import co.zoyi.Chat.Datas.ChatServerInfo;
import co.zoyi.Chat.Etc.Util;
import co.zoyi.carryu.Application.Etc.CUUtil;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.*;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.sasl.SASLMechanism;

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

    public void connect() {
        if (this.connectionConfiguration != null) {
            this.connection = new XMPPConnection(this.connectionConfiguration);

            new AsyncTask<XMPPConnection, Void, XMPPConnection>() {
                @Override
                protected XMPPConnection doInBackground(XMPPConnection... objects) {
                    XMPPConnection xmppConnection = objects[0];

                    try {
                        xmppConnection.connect();
                    } catch (XMPPException e) {
                        e.printStackTrace();
                        xmppConnection = null;
                    }

                    return xmppConnection;
                }

                @Override
                protected void onPostExecute(XMPPConnection xmppConnection) {
                    if (xmppConnection != null && xmppConnection.isConnected()) {
                        setStatus(ChatService.Status.CONNECTED);
                        connection.addConnectionListener(connectionListener);
                    } else {
                        setStatus(ChatService.Status.FAILED_CONNECT);
                    }
                }
            }.execute(this.connection);
        }
    }

    public void disconnect() {
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
            CUUtil.log(String.format("processPacketInListener [%s]", packet.getClass().getSimpleName()));
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

//    public String

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
        this.connectionConfiguration.setDebuggerEnabled(true); // TODO: debug code
    }

    private void processStatus(Status status) {
        if (status == Status.OUT_OF_GAME) {
            ourTeamNames = null;
        }
    }

    void setStatus(Status status) {
        if (status != this.status) {
            CUUtil.log(String.format("ChatServer Status [%s]", status.toString()));

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
        CUUtil.log("groupChatId: " + groupChatId);
        this.groupChatId = groupChatId;
    }

    public List<String> getOurTeamNames() {
        return ourTeamNames;
    }

    public void onCompleteFetchOurTeamNames(List<String> names) {
        CUUtil.log(String.format("onFailFetchOurTeamNames onCompleted # %d", names.size()));
        ourTeamNames = names;
        if (fetchOurTeamNamesListener != null) {
            fetchOurTeamNamesListener.onCompleted(names);
        }
        lastOurTeamNamesIQ = null;
    }

    public void onFailFetchOurTeamNames() {
        CUUtil.log("onFailFetchOurTeamNames: onFailed");
        if (fetchOurTeamNamesListener != null) {
            fetchOurTeamNamesListener.onFailed();
        }
        lastOurTeamNamesIQ = null;
    }

    public void fetchOurTeamNames(FetchOurTeamNamesListener callback) {
        CUUtil.log("fetchOurTeamNames");
        if (status == Status.CHAMPION_SELECT && lastOurTeamNamesIQ == null) {
            lastOurTeamNamesIQ = new OurTeamNamesIQ(connection.getUser(), this.groupChatId);
            this.connection.sendPacket(lastOurTeamNamesIQ);
            fetchOurTeamNamesListener = callback;
        } else {
            onFailFetchOurTeamNames();
        }
    }
}
