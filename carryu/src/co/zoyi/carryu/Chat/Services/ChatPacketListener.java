package co.zoyi.carryu.Chat.Services;

import android.util.Log;
import co.zoyi.carryu.Application.Registries.CURegistry;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.ArrayList;

public class ChatPacketListener implements PacketListener {
    private ChatService chatService;

    public ChatPacketListener(ChatService chatService) {
        super();
        this.chatService = chatService;
    }

    private void processPresence(Presence presence) {
        if (presence.getType().equals(Presence.Type.unavailable)) {
            chatService.setStatus(ChatService.Status.OUT_OF_GAME);
        } else {
            String gameStatus = null;
            XPath xPath = XPathFactory.newInstance().newXPath();
            InputSource presenceXmlInputSource = new InputSource(new StringReader(presence.getStatus()));

            try {
                gameStatus = (String) xPath.evaluate("/body/gameStatus/text()", presenceXmlInputSource, XPathConstants.STRING);
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }

            if (gameStatus != null) {
                if (gameStatus.equals("outOfGame")){
                    chatService.setStatus(ChatService.Status.OUT_OF_GAME);
                } else if (gameStatus.equals("inQueue") && chatService.getStatus() != ChatService.Status.CHAMPION_SELECT){
                    chatService.setStatus(ChatService.Status.IN_QUEUE);
                } else if (gameStatus.equals("inGame")){
                    chatService.setStatus(ChatService.Status.IN_GAME);
                }
            }
        }
    }

    private void processMessage(Message message) {
        if (message.getFrom().startsWith("pu~") == false && message.getType().equals(Message.Type.groupchat)) {
            chatService.setStatus(ChatService.Status.CHAMPION_SELECT);
            chatService.setGroupChatId(message.getFrom());
        }
    }

    protected void processIQ(IQ iq) {
        if (iq.getPacketID().equals(CURegistry.getChatService().getLastIQPacketId()) && iq.getType().equals(IQ.Type.RESULT)) {
            XPath xPath = XPathFactory.newInstance().newXPath();
            InputSource IQXmlInputSource = new InputSource(new StringReader(iq.getChildElementXML()));
            ArrayList<String> idList = new ArrayList<String>();

            try {
                NodeList nodeList = (NodeList) xPath.evaluate("//@name", IQXmlInputSource, XPathConstants.NODESET);
                for (int i=0; i<nodeList.getLength(); i++) {
                    idList.add(nodeList.item(i).getNodeValue());
                }
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }

            chatService.setOurTeamIdList(idList);
        }
    }

    @Override
    public void processPacket(Packet packet) {
        if (packet instanceof Presence) {
            processPresence((Presence) packet);
        } else if (packet instanceof Message) {
            processMessage((Message) packet);
        } else if (packet instanceof IQ){
            processIQ((IQ) packet);
        }
    }
}
