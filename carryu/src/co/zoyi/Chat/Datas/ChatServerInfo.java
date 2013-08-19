package co.zoyi.Chat.Datas;

public class ChatServerInfo {
    public String host;
    public int port;

    public String getHost() {
        return host;
    }

    public ChatServerInfo(String host, int port) {
        this.host = host;
        this.port = port;
    }
}
