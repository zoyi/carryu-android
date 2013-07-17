package co.zoyi.Chat.Services;

import org.jivesoftware.smack.packet.IQ;

import java.util.ArrayList;
import java.util.List;

public class OurTeamNamesIQ extends IQ {
    public OurTeamNamesIQ() {
        super();
    }

    public OurTeamNamesIQ(String myJabberId, String groupChatId) {
        super();
        setType(Type.GET);
        setTo(groupChatId);
        setFrom(myJabberId);
    }

    @Override
    public String getChildElementXML() {
        return "<query xmlns=\"http://jabber.org/protocol/disco#items\" />";
    }

    private List<String> names = new ArrayList<String>();

    public void addName(String name) {
        this.names.add(name);
    }
}
