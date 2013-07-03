package com.enma.flatdesign3.adapter;

import java.util.List;

import com.enma.flatdesign3.R;
import com.enma.flatdesign3.model.MenuItem;
import com.enma.utils.Utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuAdapter extends ArrayAdapter<MenuItem> {

	private List<MenuItem> _list;
	private final Activity _context;
	private static LayoutInflater _inflater = null;

	public MenuAdapter(Activity context, List<MenuItem> lst) {
		super(context, R.layout.row_menu, lst);
		this._context = context;
		this._list = lst;

		_inflater = this._context.getLayoutInflater();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (convertView == null) {
			view = _inflater.inflate(R.layout.row_menu, null);
		}

		Utils.setFontAllView(parent);

		MenuItem mnuItem = _list.get(position);
		Integer id = mnuItem.get_id();

		view.setId(id);
		TextView tvTitle = (TextView) view.findViewById(R.id.text_name);
		ImageView im = (ImageView) view.findViewById(R.id.image);

		tvTitle.setText(mnuItem.get_titleRes());
		im.setImageResource(mnuItem.get_imageRes());

		return view;
	}
}
