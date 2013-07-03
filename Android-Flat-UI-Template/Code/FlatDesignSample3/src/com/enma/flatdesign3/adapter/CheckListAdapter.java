package com.enma.flatdesign3.adapter;

import java.util.List;

import com.enma.flatdesign3.R;
import com.enma.flatdesign3.model.VideoItem;
import com.enma.utils.Utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CheckListAdapter extends ArrayAdapter<VideoItem> {

	private List<VideoItem> _list;
	private final Activity _context;
	private static LayoutInflater _inflater = null;

	public CheckListAdapter(Activity context, List<VideoItem> lst) {
		super(context, R.layout.row_checklist, lst);
		this._context = context;
		this._list = lst;

		_inflater = this._context.getLayoutInflater();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (convertView == null) {
			view = _inflater.inflate(R.layout.row_checklist, null);
		}

		Utils.setFontAllView(parent);

		VideoItem vidItem = _list.get(position);
		Integer id = vidItem.get_id();

		view.setId(id);
		TextView tvTitle = (TextView) view.findViewById(R.id.text_name);
		TextView tvPrice = (TextView) view.findViewById(R.id.text_price);
		ImageView im = (ImageView) view.findViewById(R.id.image);

		view.setId(id);
		tvTitle.setText(vidItem.get_title());
		tvPrice.setText("$" + vidItem.get_price());
		
		if (vidItem.get_selected()){
			im.setImageResource(R.drawable.rb_checked_blue);
			tvPrice.setBackgroundResource(R.color.blue);
		}else{
			im.setImageResource(R.drawable.rb_hover_blue);
			tvPrice.setBackgroundResource(R.color.transparent);
		}


		return view;
	}
}
