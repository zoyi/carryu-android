package co.zoyi.carryu.Application.Registries;

import co.zoyi.carryu.Chat.Services.ChatService;
import com.google.gson.Gson;

public class CURegistry {
    private static ChatService chatService;
    private static Gson sharedGsonInstance;

    public static Gson getSharedGsonInstance() {
        return sharedGsonInstance;
    }

    public static ChatService getChatService() { return chatService; }

    public static void initialize() {
        chatService = new ChatService();
        sharedGsonInstance = new Gson();
    }
}
