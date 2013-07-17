package co.zoyi.carryu.Application.Views.Activities;

import android.os.Bundle;
import android.widget.TextView;
import co.zoyi.carryu.Application.Etc.ActivityDelegate;
import co.zoyi.carryu.Application.Etc.CUCroutonStyle;
import co.zoyi.carryu.Application.Events.Chat.ChatStatusChangeEvent;
import co.zoyi.carryu.Application.Registries.Registry;
import co.zoyi.Chat.Services.ChatService;
import co.zoyi.carryu.R;
import de.keyboardsurfer.android.widget.crouton.Crouton;

public class LobbyActivity extends CUActivity {
    private ChatService.Status status;

    private void setStatus(ChatService.Status status) {
        this.status = status;

        if (status == ChatService.Status.IN_QUEUE) {
            TextView.class.cast(findViewById(R.id.current_state_message)).setText("Finding the room");
        } else if (status == ChatService.Status.OUT_OF_GAME){
            TextView.class.cast(findViewById(R.id.current_state_message)).setText("Waiting for the game");
        } else if (status == ChatService.Status.CONNECTION_CLOSED){
            ActivityDelegate.openLoginActivityWithConnectionClosedCrouton(this);
        } else if (status == ChatService.Status.CHAMPION_SELECT) {
            ActivityDelegate.openChampionSelectActivity(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setStatus(Registry.getChatService().getStatus());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ActivityDelegate.openChampionSelectActivity(this);
        setContentView(R.layout.lobby_activity);
    }

    public void onEventMainThread(ChatStatusChangeEvent event) {
        Crouton.makeText(this, event.getStatus().toString(), CUCroutonStyle.INFO).show();
        setStatus(event.getStatus());
    }
}
