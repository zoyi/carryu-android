package co.zoyi.Chat.Services;

import java.util.List;

public interface ChatStatusChangeListener {
    public void onStatusChanged(ChatService.Status status);
}
