package co.zoyi.Chat.Services;

import co.zoyi.Chat.Packets.OurTeamNamesIQ;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

public class OurTeamNamesIQProvider implements IQProvider {
    @Override
    public IQ parseIQ(XmlPullParser xmlPullParser) {
        int count = 0;
        OurTeamNamesIQ ourTeamNamesIQ = new OurTeamNamesIQ();

        try {
            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT && ++count < 100) {
                if(eventType == XmlPullParser.START_TAG) {
                    if (xmlPullParser.getName().equals("item")) {
                        for (int indexOfAttr=0; indexOfAttr<xmlPullParser.getAttributeCount(); indexOfAttr++ ){
                            String attrName = xmlPullParser.getAttributeName(indexOfAttr);
                            if (attrName.equals("name")) {
                                ourTeamNamesIQ.addName(xmlPullParser.getAttributeValue(indexOfAttr));
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
        } catch(Exception e) {
            e.printStackTrace();
        }

        return ourTeamNamesIQ;
    }
}
