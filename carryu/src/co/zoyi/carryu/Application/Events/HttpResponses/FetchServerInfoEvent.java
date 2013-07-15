package co.zoyi.carryu.Application.Events.HttpResponses;

import co.zoyi.carryu.Application.Datas.ValueObjects.ServerList;

public class FetchServerInfoEvent extends CUHttpResponseEvent {
    private ServerList serverList;

    public FetchServerInfoEvent(ServerList serverList) {
        this.serverList = serverList;
    }

    public ServerList getServerList() {

        return serverList;
    }
}
