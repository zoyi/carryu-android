package co.zoyi.carryu.Application.Registries;

import co.zoyi.Chat.Services.ChatService;
import co.zoyi.Chat.Services.ChatStatusChangeListener;
import co.zoyi.carryu.Application.Datas.Serializers.SummonerJSONSerializer;
import co.zoyi.carryu.Application.Events.Chat.ChatStatusChangeEvent;
import de.greenrobot.event.EventBus;

public class Registry {
    private static ChatService chatService;
    private static SummonerJSONSerializer summonerSerializer;

    public static ChatService getChatService() { return chatService; }
    public static SummonerJSONSerializer getSummonerSerializer() { return summonerSerializer; }

    private static void initializeChatService() {
        chatService.setChatStatusChangeListener(new ChatStatusChangeListener() {
            @Override
            public void onStatusChanged(ChatService.Status status) {
                EventBus.getDefault().post(new ChatStatusChangeEvent(status));
            }
        });
    }

    public static void initialize() {
        chatService = new ChatService();
        initializeChatService();
        summonerSerializer = new SummonerJSONSerializer();
    }
}
