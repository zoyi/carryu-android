package com.enma.flatdesign2.adapter;

import java.util.List;

import com.enma.flatdesign2.R;
import com.enma.flatdesign2.model.VideoItem;
import com.enma.utils.Utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MasterDetailAdapter extends ArrayAdapter<VideoItem> {

	private List<VideoItem> _list;
	private final Activity _context;
	private static LayoutInflater _inflater = null;

	public MasterDetailAdapter(Activity context, List<VideoItem> lst) {
		super(context, R.layout.row_masterdetail, lst);
		this._context = context;
		this._list = lst;

		_inflater = this._context.getLayoutInflater();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (convertView == null) {
			view = _inflater.inflate(R.layout.row_masterdetail, null);
		}

		Utils.setFontAllView(parent);

		VideoItem vidItem = _list.get(position);
		Integer id = vidItem.get_id();

		view.setId(id);
		TextView tvTitle = (TextView) view.findViewById(R.id.text_name);
		TextView tvPrice = (TextView) view.findViewById(R.id.text_price);
		TextView tvDesc = (TextView) view.findViewById(R.id.text_desc);
		ImageView iv = (ImageView) view.findViewById(R.id.image);

		view.setId(id);
		tvTitle.setText(vidItem.get_title());
		tvPrice.setText("$" + vidItem.get_price());
		tvDesc.setText(vidItem.get_desc());

		Bitmap bmp = Utils.GetImageFromAssets(this._context, "images/"
				+ vidItem.get_image());
		iv.setImageBitmap(bmp);

		return view;
	}
}
