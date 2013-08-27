package co.zoyi.Chat.Listeners;

import co.zoyi.Chat.Services.ChatService;

public interface ChatStatusChangeListener {
    public void onStatusChanged(ChatService.Status status);
}
