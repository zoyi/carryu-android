package com.enma.flatdesign;

import com.enma.adapters.MyColorArrayAdapter;
import com.enma.utils.Utils;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

public class MainActivity extends Activity implements OnClickListener,OnItemClickListener {

	// 
	ImageButton btnSettings,btnSearch;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ViewGroup vg = (ViewGroup) findViewById(R.id.root);
		Utils.setFontAllView(vg);

		
		btnSearch = (ImageButton) findViewById(R.id.btnImg_Search);
		btnSettings = (ImageButton) findViewById(R.id.btnImg_Settings);
		
		btnSearch.setOnClickListener(this);
		btnSettings.setOnClickListener(this);
		
		ListView listView = (ListView) findViewById(R.id.list_colors);
		String[] values = new String[] { "Green", "Blue", "Lilac", "Red",
				"Orange", "Purple", "Yellow" };
		Integer[] images = { R.drawable.lst_green, R.drawable.lst_blue,
				R.drawable.lst_lilac, R.drawable.lst_red,
				R.drawable.lst_orange, R.drawable.lst_purple,
				R.drawable.lst_yellow };

		// Create Color Adapter
		MyColorArrayAdapter adapter = new MyColorArrayAdapter(this, values,
				images);

		// Assign adapter to ListView
		listView.setAdapter(adapter);

		// Add Listener to handle click on list row
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnImg_Search){
			Intent intent = new Intent(this, SearchActivity.class);
			startActivity(intent);
		}
		if (v.getId() == R.id.btnImg_Settings){
			Intent intent = new Intent(this, SettingActivity.class);
			startActivity(intent);
		}

	}
	
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		Intent intent = new Intent(this, SubActivity.class);
		intent.putExtra("id", v.getId());
		startActivity(intent);
	}

}
