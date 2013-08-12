package co.zoyi.Chat.Packets;

import org.jivesoftware.smack.packet.Presence;

public class UpdateStatusPresence extends Presence {
    public UpdateStatusPresence(Presence presence) {
        super(presence.getType());
        setStatus(presence.getStatus());
        setMode(presence.getMode());
    }
}
