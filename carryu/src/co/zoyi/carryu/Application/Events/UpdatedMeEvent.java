package co.zoyi.carryu.Application.Events;

import co.zoyi.carryu.Application.Datas.Models.Summoner;

public class UpdatedMeEvent extends Event {
    private Summoner me;

    public Summoner getMe() {
        return me;
    }

    public UpdatedMeEvent(Summoner me) {
        this.me = me;
    }
}
