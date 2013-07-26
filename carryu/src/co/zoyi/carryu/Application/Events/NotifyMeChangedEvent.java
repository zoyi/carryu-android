package co.zoyi.carryu.Application.Events;

import co.zoyi.carryu.Application.Datas.Models.Summoner;

public class NotifyMeChangedEvent extends Event {
    private Summoner me;

    public Summoner getMe() {
        return me;
    }

    public NotifyMeChangedEvent(Summoner me) {
        this.me = me;
    }
}
