package co.zoyi.carryu.Application.Events.HttpResponses;

import co.zoyi.carryu.Application.Datas.Models.Summoner;

public class FetchSummonerEvent extends CUHttpResponseEvent {
    private Summoner summoner;

    public Summoner getSummoner() {
        return summoner;
    }

    public FetchSummonerEvent(Summoner summoner) {
        this.summoner = summoner;
    }
}
