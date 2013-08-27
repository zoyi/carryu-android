package co.zoyi.carryu.Application.Datas.ValueObjects;

import com.google.gson.annotations.SerializedName;

public class ServerList extends ValueObject {
    public static class ServerInfo {
        public static enum ServerName {
            KR,
            NA,
            EUW
        }
        @SerializedName("rails_host")
        private String railsHost;
        @SerializedName("rtmp_host")
        private String rtmpHost;
        @SerializedName("xmpp_host")
        private String xmppHost;
        @SerializedName("xmpp_port")
        private int xmppPort;
        @SerializedName("api_version")
        private String apiVersion;

        public String getRailsHost() {
            return railsHost;
        }

        public String getRtmpHost() {
            return rtmpHost;
        }

        public String getXmppHost() {
            return xmppHost;
        }

        public int getXmppPort() {
            return xmppPort;
        }

        public String getApiVersion() {
            return apiVersion;
        }
    }

    @SerializedName("kr")
    private ServerInfo koreaServer;
    @SerializedName("na")
    private ServerInfo northAmericaServer;
    @SerializedName("euw")
    private ServerInfo europeWestServer;

    public ServerInfo getNorthAmericaServer() {
        return northAmericaServer;
    }

    public ServerInfo getEuropeWestServer() {
        return europeWestServer;
    }

    public ServerInfo getKoreaServer() {
        return koreaServer;
    }
}
