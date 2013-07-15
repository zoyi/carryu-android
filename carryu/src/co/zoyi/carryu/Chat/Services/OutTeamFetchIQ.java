package co.zoyi.carryu.Chat.Services;

import org.jivesoftware.smack.packet.IQ;

public class OutTeamFetchIQ extends IQ {
    public OutTeamFetchIQ(String myJabberId, String groupChatId) {
        super();
        setType(Type.GET);
        setTo(groupChatId);
        setFrom(myJabberId);
//        setPacketID(connectionId);
    }

    @Override
    public String getChildElementXML() {
        return "<query xmlns=\"http://jabber.org/protocol/disco#items\" />";
    }
}
