package co.zoyi.carryu.Chat.Events;

public class ChatConnectEvent extends CUChatServiceEvent {
    public enum Result {
        SUCCESS, FAILED
    };

    public Result result;

    public ChatConnectEvent(Result result) {
        this.result = result;
    }
}
