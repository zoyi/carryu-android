package com.enma.flatdesign;

import com.enma.utils.Utils;
import android.os.Bundle;
import android.app.Activity;
import android.view.ViewGroup;

public class SettingActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		ViewGroup vg = (ViewGroup) findViewById(R.id.root);
		Utils.setFontAllView(vg);

	}

}
