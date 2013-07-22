package co.zoyi.Chat.Services;

import java.util.List;

public interface FetchOurTeamNamesListener {
    public void onCompleted(List<String> teamNames);
    public void onFailed();
}
