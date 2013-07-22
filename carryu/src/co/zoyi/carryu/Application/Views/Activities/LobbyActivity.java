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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby_activity);
    }

    public void onEventMainThread(ChatStatusChangeEvent event) {
        super.onEventMainThread(event);
        if (event.getStatus() == ChatService.Status.IN_QUEUE) {
            TextView.class.cast(findViewById(R.id.current_state_message)).setText("Finding the room");
        } else if (event.getStatus() == ChatService.Status.OUT_OF_GAME){
            TextView.class.cast(findViewById(R.id.current_state_message)).setText("Waiting for the game");
        }
    }
}
