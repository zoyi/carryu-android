package co.zoyi.carryu.Application.Etc;

import co.zoyi.carryu.Application.Datas.ValueObjects.ServerList;

public class CURouter {
    private static ServerList.ServerInfo serverInfo;
    public static void setServerInfo(ServerList.ServerInfo serverInfo) {
        CURouter.serverInfo = serverInfo;
    }

    public static ServerList.ServerInfo getServerInfo() {
        return serverInfo;
    }

    public static String getApiHost() {
        return String.format("%1$s/api/%2$s", serverInfo.getRailsHost(), serverInfo.getApiVersion());
    }

    public static String getRtmpHost() {
        return serverInfo.getRtmpHost();
    }

    public static String getSummonerDetailURL(String summonerName) {
        return String.format("%1$s/summoners/%2$s", serverInfo.getRailsHost(), summonerName);
    }

    public static String getChampionGuideURL(int championId) {
        return String.format("%1$s/champions/%2$d/guides", serverInfo.getRailsHost(), championId);
    }

    public static String getFeedbackURL() {
        return "https://www.facebook.com/lol.carryyou";
    }
}
