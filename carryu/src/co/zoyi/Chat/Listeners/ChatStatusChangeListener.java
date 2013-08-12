package co.zoyi.Chat.Listeners;

import co.zoyi.Chat.Services.ChatService;

import java.util.List;

public interface ChatStatusChangeListener {
    public void onStatusChanged(ChatService.Status status);
}
