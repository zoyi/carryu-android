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
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatService {
    private XMPPConnection connection;
    private ConnectionConfiguration connectionConfiguration;
    private Status status = Status.PENDING;
    private String groupChatId;
    private ChatStatusChangeListener chatStatusChangeListener;
    private ChatPacketProcessor chatPacketProcessor = new ChatPacketProcessor(this);
    private FetchOurTeamNameListListener fetchOurTeamNameListListener;
    private OurTeamNamesIQ lastOurTeamNamesIQ;
    private List<String> lastUpdatedTeamNames;
//    private HashMap<String, FetchOurTeamNameListListener> fetchOurTeamNameListListenerMap = new HashMap<String, FetchOurTeamNameListListener>();

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
                addIQProvider();
                addPacketListeners();
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

    private void addPacketListeners() {
        PacketFilter filter = new OrFilter(
            new PacketTypeFilter(Presence.class),
            new AndFilter(
                new ToContainsFilter(Util.toGameClientJabberId(getUser())),
                new PacketTypeFilter(Message.class)
            )
        );

        this.connection.addPacketListener(packetListener, filter);
    }

    private void addIQProvider() {
        ProviderManager.getInstance().addIQProvider("query", "http://jabber.org/protocol/disco#items", new IQProvider() {
            @Override
            public IQ parseIQ(XmlPullParser xmlPullParser) {
                try {
                    List<String> names = new ArrayList<String>();
                    int eventType = xmlPullParser.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if(eventType == XmlPullParser.START_TAG) {
                            if (xmlPullParser.getName().equals("item")) {
                                for (int indexOfAttr=0; indexOfAttr<xmlPullParser.getAttributeCount(); indexOfAttr++ ){
                                    String attrName = xmlPullParser.getAttributeName(indexOfAttr);
                                    if (attrName.equals("name")) {
                                        names.add(xmlPullParser.getAttributeValue(indexOfAttr));
//                                        CUUtil.log(xmlPullParser.getAttributeValue(indexOfAttr));
                                    }
                                }
                            }
                        } else if (eventType == XmlPullParser.END_TAG) {
                            if (xmlPullParser.getName().equals("query")) {
                                break;
                            }
                        }

                        eventType = xmlPullParser.next();
                    }

                    fireFetchOurSummonerNamePacketCallback(names);
                } catch(Exception e) {
                    fireFetchOurSummonerNamePacketCallbackToFail();
                    e.printStackTrace();
                }

                return null;
            }
        });
    }

    public String getUser() {
        return connection.getUser();
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

    public void setServerInfo(ChatServerInfo chatServerInfo) {
        this.connectionConfiguration = new ConnectionConfiguration(chatServerInfo.host, chatServerInfo.port, "pvp.net");
        this.connectionConfiguration.setSASLAuthenticationEnabled(true);
        this.connectionConfiguration.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);
        this.connectionConfiguration.setSocketFactory(new DummySSLSocketFactory());
        this.connectionConfiguration.setReconnectionAllowed(true);
        this.connectionConfiguration.setDebuggerEnabled(true); // TODO: debug code
    }

    void setStatus(Status status) {
        if (status != this.status) {
            CUUtil.log(String.format("ChatServer Status [%s]", status.toString()));
            if (this.chatStatusChangeListener != null) {
                this.chatStatusChangeListener.onStatusChanged(status);
            }
            this.status = status;
        }
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

    public void fireFetchOurSummonerNamePacketCallback(List<String> names) {
        CUUtil.log(String.format("fireFetchOurSummonerNamePacketCallbackToFail onCompleted # %d", names.size()));
        lastUpdatedTeamNames = names;
        fetchOurTeamNameListListener.onCompleted(names);
        lastOurTeamNamesIQ = null;
    }

    public List<String> getLastUpdatedTeamNames() {
        return lastUpdatedTeamNames;
    }

    public void fireFetchOurSummonerNamePacketCallbackToFail() {
        CUUtil.log("fireFetchOurSummonerNamePacketCallbackToFail: onFailed");
        fetchOurTeamNameListListener.onFailed();
        lastOurTeamNamesIQ = null;
    }

    public void fetchOurSummonerNameList(FetchOurTeamNameListListener callback) {
        CUUtil.log("fetchOurSummonerNameList");
        if (lastOurTeamNamesIQ == null) {
            lastUpdatedTeamNames = null;
            lastOurTeamNamesIQ = new OurTeamNamesIQ(connection.getUser(), this.groupChatId);
            this.connection.sendPacket(lastOurTeamNamesIQ);
            fetchOurTeamNameListListener = callback;
        }
    }
}
