package co.zoyi.carryu.Application.Registries;

import co.zoyi.Chat.Services.ChatService;
import co.zoyi.Chat.Listeners.ChatStatusChangeListener;
import co.zoyi.carryu.Application.Datas.Serializers.ActiveGameJSONSerializer;
import co.zoyi.carryu.Application.Datas.Serializers.SummonerJSONSerializer;
import co.zoyi.carryu.Application.Events.ChatStatusChangeEvent;
import de.greenrobot.event.EventBus;

public class Registry {
    private static ChatService chatService;
    private static SummonerJSONSerializer summonerSerializer;
    private static ActiveGameJSONSerializer activeGameSerializer;

    public static ChatService getChatService() { return chatService; }
    public static SummonerJSONSerializer getSummonerSerializer() { return summonerSerializer; }
    public static ActiveGameJSONSerializer getActiveGameSerializer() { return activeGameSerializer; }

    public static void initialize() {
        chatService = new ChatService();
        summonerSerializer = new SummonerJSONSerializer();
        activeGameSerializer = new ActiveGameJSONSerializer();
    }
}
