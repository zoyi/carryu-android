package co.zoyi.carryu.Chat.Events;

import java.util.ArrayList;

public class TeamIdListUpdatedEvent extends CUChatServiceEvent {
    private ArrayList<String> teamIdList;

    public TeamIdListUpdatedEvent(ArrayList<String> teamIdList) {
        this.teamIdList = teamIdList;
    }

    public ArrayList<String> getTeamIdList() {
        return teamIdList;
    }
}
