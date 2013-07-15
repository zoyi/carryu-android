package co.zoyi.carryu.Chat.Events;

import co.zoyi.carryu.Chat.Services.ChatService;

public class ChatStatusChangeEvent extends CUChatServiceEvent {
    public final ChatService.Status status;

    public ChatStatusChangeEvent(ChatService.Status status) {
        this.status = status;
    }
}
