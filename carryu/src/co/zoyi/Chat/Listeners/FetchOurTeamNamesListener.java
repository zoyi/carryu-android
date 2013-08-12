package co.zoyi.Chat.Listeners;

import java.util.List;

public interface FetchOurTeamNamesListener {
    public void onCompleted(List<String> teamNames);
    public void onFailed();
}
