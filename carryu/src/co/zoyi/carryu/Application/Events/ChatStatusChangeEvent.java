package co.zoyi.carryu.Application.Events;

import co.zoyi.Chat.Services.ChatService;

public class ChatStatusChangeEvent extends Event {
    ChatService.Status status;

    public ChatService.Status getStatus() {
        return status;
    }

    public ChatStatusChangeEvent(ChatService.Status status) {

        this.status = status;
    }
}
