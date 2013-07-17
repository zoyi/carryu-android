package co.zoyi.Chat.Datas;

public class ChatServerInfo {
    public String host;
    public String region;
    public int port;

    public ChatServerInfo(String region, String host, int port) {
        this.region = region;
        this.host = host;
        this.port = port;
    }
}
