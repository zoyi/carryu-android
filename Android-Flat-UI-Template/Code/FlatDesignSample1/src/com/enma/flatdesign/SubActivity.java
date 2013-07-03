package com.enma.flatdesign;

import com.enma.utils.Utils;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class SubActivity extends Activity implements OnClickListener {

	Integer[] layouts = { R.layout.activity_sub_green,
			R.layout.activity_sub_blue, R.layout.activity_sub_lilac,
			R.layout.activity_sub_red, R.layout.activity_sub_orange,
			R.layout.activity_sub_purple, R.layout.activity_sub_yellow };
	ImageButton btnBack, btnLike;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Integer id = getIntent().getIntExtra("id", -1);
		if (id == -1)
			return;

		setContentView(layouts[id]);
		ViewGroup vg = (ViewGroup) findViewById(R.id.root);
		Utils.setFontAllView(vg);

		btnBack = (ImageButton) findViewById(R.id.btnImg_Back);
		btnLike = (ImageButton) findViewById(R.id.btnImg_Like);

		btnBack.setOnClickListener(this);
		btnLike.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnImg_Back) {
			finish();
		}
		if (v.getId() == R.id.btnImg_Like) {

		}

	}

}
