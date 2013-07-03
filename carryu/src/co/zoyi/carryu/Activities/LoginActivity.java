package co.zoyi.carryu.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import co.zoyi.carryu.Etc.CUUtil;
import co.zoyi.carryu.R;

public class LoginActivity extends CUActivity {
    private EditText userIdTextEdit, userPasswordTextEdit;
    private Button loginButton;

    private View.OnClickListener onLoginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            login();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity);

        userIdTextEdit = (EditText) findViewById(R.id.user_id);
        userPasswordTextEdit = (EditText) findViewById(R.id.user_password);
        loginButton = (Button) findViewById(R.id.login);

        loginButton.setOnClickListener(onLoginClickListener);
    }

    public void login() {
        Toast.makeText(this, EditText.class.cast(findViewById(R.id.user_id)).getText().toString(), Toast.LENGTH_LONG);
        Log.d("zoyi", EditText.class.cast(findViewById(R.id.user_id)).getText().toString());
        CUUtil.log(this, EditText.class.cast(findViewById(R.id.user_id)).getText().toString());
    }
}
