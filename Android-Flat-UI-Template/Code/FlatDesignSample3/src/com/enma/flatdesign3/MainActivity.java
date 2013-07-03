package com.enma.flatdesign3;

import com.enma.utils.Utils;
import com.slidingmenu.lib.SlidingMenu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MainActivity extends FragmentActivity implements OnClickListener {

	private SlidingMenu menu;
	private Fragment mContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContent = new SwipeActivity();

		// set the Above View
		setContentView(R.layout.activity_main);
		ViewGroup vg = (ViewGroup)findViewById(R.id.main_root);
		
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mContent).commit();

		// configure the SlidingMenu
		menu = new SlidingMenu(this);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new MenuFragment()).commit();

		ImageButton btnList = (ImageButton) findViewById(R.id.btn_list);
		ImageButton btnCompose = (ImageButton) findViewById(R.id.btn_compose);

		btnList.setOnClickListener(this);
		btnCompose.setOnClickListener(this);
		
		Utils.setFontAllView(vg);
	}

	@Override
	public void onBackPressed() {
		if (menu.isMenuShowing()) {
			menu.showContent();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_list) {
			if (menu.isMenuShowing()) {
				menu.showContent();
			} else {
				menu.showMenu();
			}
		}
		if (v.getId() == R.id.btn_compose) {

		}

	}

	public void switchContent(Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
		menu.showContent();
	}

}
