package co.zoyi.Chat.Services;

import co.zoyi.Chat.Etc.Util;
import co.zoyi.carryu.Application.Etc.CUUtil;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.xml.sax.InputSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

public class ChatPacketProcessor {
    private ChatService chatService;

    public ChatPacketProcessor(ChatService chatService) {
        super();
        this.chatService = chatService;
    }

    private String getGameStatusFromPresence(Presence presence) {
        String gameStatus = null;
        XPath xPath = XPathFactory.newInstance().newXPath();
        InputSource presenceXmlInputSource = new InputSource(new StringReader(presence.getStatus()));

        try {
            gameStatus = (String) xPath.evaluate("/body/gameStatus/text()", presenceXmlInputSource, XPathConstants.STRING);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return gameStatus;
    }

    private void processPresence(Presence presence) {
        if (presence.getType().equals(Presence.Type.unavailable)) {
            chatService.setStatus(ChatService.Status.OUT_OF_GAME);
        } else if (presence.getFrom().equals(Util.toGameClientJabberId(chatService.getUser()))){
            String gameStatus = getGameStatusFromPresence(presence);
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

    private void processOurTeamNamesIQ(OurTeamNamesIQ iq) {
        if (iq.getNames().size() == 0) {
            this.chatService.onFailFetchOurTeamNames();
        } else {
            this.chatService.onCompleteFetchOurTeamNames(iq.getNames());
        }
    }

    public void processPacket(Packet packet) {
//        CUUtil.log("processPacket: " + packet.toXML());
        CUUtil.log(String.format("PacketClass[%s] PacketID[%s]", packet.getClass().getSimpleName(), packet.getPacketID()));
        if (packet instanceof Presence) {
            processPresence((Presence) packet);
        } else if (packet instanceof Message) {
            processMessage((Message) packet);
        } else if (packet instanceof OurTeamNamesIQ){
            processOurTeamNamesIQ((OurTeamNamesIQ) packet);
        }
    }
}
