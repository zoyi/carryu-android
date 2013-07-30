package co.zoyi.carryu.Application.Views.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import co.zoyi.carryu.Application.Etc.ActivityDelegate;
import co.zoyi.Chat.Services.ChatService;
import co.zoyi.carryu.Application.Views.Dialogs.ConfirmDialog;
import co.zoyi.carryu.R;

public class LobbyActivity extends CUActivity {
    private View.OnClickListener searchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ActivityDelegate.openSearchSummonerActivity(LobbyActivity.this);
        }
    };

    private View.OnClickListener sampleClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ActivityDelegate.openSampleInGameActivity(LobbyActivity.this);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.lobby_activity);

        findViewById(R.id.search).setOnClickListener(searchClickListener);
        Button.class.cast(findViewById(R.id.sample)).setOnClickListener(sampleClickListener);
    }

    @Override
    protected void processChatStatus(ChatService.Status status) {
        if (status == ChatService.Status.IN_QUEUE) {
//            TextView.class.cast(findViewById(R.id.current_state_message)).setText(getString(R.string.searching_for_match));
            Button.class.cast(findViewById(R.id.sample)).setVisibility(View.GONE);
            TextView.class.cast(findViewById(R.id.current_state_message)).setText("");
            ImageView.class.cast(findViewById(R.id.current_state_image)).setImageResource(R.drawable.searching_match);
        } else if (status == ChatService.Status.OUT_OF_GAME){
            TextView.class.cast(findViewById(R.id.current_state_message)).setText(getString(R.string.welcome_message));
            ImageView.class.cast(findViewById(R.id.current_state_image)).setImageResource(R.drawable.summon_game);
            Button.class.cast(findViewById(R.id.sample)).setVisibility(View.VISIBLE);
        } else {
            super.processChatStatus(status);
        }
    }
}
