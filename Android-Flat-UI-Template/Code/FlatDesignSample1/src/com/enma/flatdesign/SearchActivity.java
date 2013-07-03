package com.enma.flatdesign;

import com.enma.utils.Utils;
import android.os.Bundle;
import android.app.Activity;
import android.view.ViewGroup;

public class SearchActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		ViewGroup vg = (ViewGroup) findViewById(R.id.root);
		Utils.setFontAllView(vg);
	}
}
