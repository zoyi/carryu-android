package com.enma.flatdesign3;

import java.util.ArrayList;

import com.enma.flatdesign3.adapter.CheckListAdapter;

import com.enma.flatdesign3.model.VideoItem;
import com.enma.flatdesign3.model.VideoList;
import com.enma.utils.Utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CheckListActivity extends Fragment implements OnItemClickListener {

	ListView listView;
	ArrayList<VideoItem> lstVideos;
	View vw_layout;

	private Animation mCellSFadeIn;
	private Animation mCellSSlideUp;
	private Animation mScale;
	private TextView tvtTotal;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (container == null) {
			// We have different layouts, and in one of them this
			// fragment's containing frame doesn't exist. The fragment
			// may still be created from its saved state, but there is
			// no reason to try to create its view hierarchy because it
			// won't be displayed. Note this is not needed -- we could
			// just run the code below, where we would create and return
			// the view hierarchy; it would just never be used.
			return null;
		}
		vw_layout = inflater.inflate(R.layout.activity_checklist, container,
				false);

		mCellSFadeIn = AnimationUtils.loadAnimation(getActivity(),
				R.anim.fade_in);
		mCellSSlideUp = AnimationUtils.loadAnimation(getActivity(),
				R.anim.slide_up);
		mScale = AnimationUtils.loadAnimation(getActivity(), R.anim.scale);

		// get total price label
		tvtTotal = (TextView) this.vw_layout.findViewById(R.id.lbl_total);
		
		// get list view
		listView = (ListView) this.vw_layout.findViewById(R.id.lst_videos);

		lstVideos = VideoList.getVideoList();

		listView.setAdapter(new CheckListAdapter(getActivity(), lstVideos));
		listView.setOnItemClickListener(this);
		
		this.CalcTotalPrice();
		Utils.setFontAllView((ViewGroup) vw_layout);
		return vw_layout;
	}

	public void onItemClick(AdapterView<?> adp, View listview, int position,
			long id) {

		if (adp != null && adp.getAdapter() instanceof CheckListAdapter) {
			CheckListAdapter newsAdp = (CheckListAdapter) adp.getAdapter();
			VideoItem itm = newsAdp.getItem(position);
			itm.set_selected(!itm.get_selected());

			int currentIndex = position - listView.getFirstVisiblePosition()
					- listView.getHeaderViewsCount();

			View _v = listView.getChildAt(currentIndex);
			TextView tvPrice = (TextView) _v.findViewById(R.id.text_price);
			ImageView im = (ImageView) _v.findViewById(R.id.image);

			if (itm.get_selected()) {
				im.setImageResource(R.drawable.rb_checked_blue);
				tvPrice.setBackgroundResource(R.color.blue);

			} else {
				im.setImageResource(R.drawable.rb_hover_blue);
				tvPrice.setBackgroundResource(R.color.transparent);

			}

			if ((position % 2) == 0) {
				_v.startAnimation(mCellSSlideUp);
			} else {
				_v.startAnimation(mCellSFadeIn);
			}
			this.CalcTotalPrice();
		}
	}

	private void CalcTotalPrice() {
		int total = 0;
		for (int i = 0; i < lstVideos.size(); i++) {
			if (lstVideos.get(i).get_selected()) {
				total += lstVideos.get(i).get_price();
			}
		}

		tvtTotal.setText("Total: $ " + total);
		tvtTotal.clearAnimation();
		tvtTotal.startAnimation(mScale);
	}

}
