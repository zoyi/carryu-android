package co.zoyi.carryu.Chat.Services;

import android.os.AsyncTask;
import co.zoyi.carryu.Chat.Events.ChatConnectEvent;
import de.greenrobot.event.EventBus;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class AsyncChatConnectDelegate {
    public static void connect(XMPPConnection connection) {
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
                    EventBus.getDefault().post(new ChatConnectEvent(ChatConnectEvent.Result.SUCCESS));
                } else {
                    EventBus.getDefault().post(new ChatConnectEvent(ChatConnectEvent.Result.FAILED));
                }
            }
        }.execute(connection);
    }
}
