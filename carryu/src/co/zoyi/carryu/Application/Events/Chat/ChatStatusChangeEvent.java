package co.zoyi.carryu.Application.Events.Chat;

import co.zoyi.Chat.Services.ChatService;
import co.zoyi.carryu.Application.Events.Event;

public class ChatStatusChangeEvent extends Event {
    ChatService.Status status;

    public ChatService.Status getStatus() {
        return status;
    }

    public ChatStatusChangeEvent(ChatService.Status status) {

        this.status = status;
    }
}
