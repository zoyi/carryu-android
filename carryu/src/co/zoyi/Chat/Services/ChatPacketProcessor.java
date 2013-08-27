package co.zoyi.Chat.Services;

import co.zoyi.Chat.Etc.Util;
import co.zoyi.Chat.Packets.OurTeamNamesIQ;
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
        } else if (presence.getFrom().equals(Util.toGameClientJabberId(chatService.getUser()))){
            String gameStatus = getGameStatusFromPresence(presence);
            if (gameStatus != null) {
                boolean needToUpdatePresence = true;

                if (gameStatus.equals("outOfGame")){
                    chatService.setStatus(ChatService.Status.OUT_OF_GAME);
                } else if (gameStatus.equals("inQueue")){
                    if (chatService.getStatus() == ChatService.Status.IN_QUEUE) {
                        chatService.setStatus(ChatService.Status.CHAMPION_SELECT);
                    } else if (chatService.getStatus() != ChatService.Status.CHAMPION_SELECT) {
                        chatService.setStatus(ChatService.Status.IN_QUEUE);
                    }
                } else if (gameStatus.equals("inGame")){
                    chatService.setStatus(ChatService.Status.IN_GAME);
                } else {
                    needToUpdatePresence = false;
                }

                if (needToUpdatePresence) {
                    CUUtil.log(this, "[processPresence] " + gameStatus + " " + "needToUpdatePresence");
                    CUUtil.log(this, "[processPresence] " + presence.getStatus());
                    chatService.setLastPresence(presence);
                }
            }
        }
    }

    private void processMessage(Message message) {
        if (message.getFrom().startsWith("pu~") == false && message.getType().equals(Message.Type.groupchat) && chatService.getStatus() == ChatService.Status.IN_QUEUE) {
            chatService.setGroupChatId(Util.getGroupChatID(message.getFrom()));
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
        if (packet instanceof Presence) {
            processPresence((Presence) packet);
        } else if (packet instanceof Message) {
            processMessage((Message) packet);
        } else if (packet instanceof OurTeamNamesIQ){
            processOurTeamNamesIQ((OurTeamNamesIQ) packet);
        }
    }
}
