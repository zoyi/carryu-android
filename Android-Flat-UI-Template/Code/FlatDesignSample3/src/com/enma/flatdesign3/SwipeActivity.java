package com.enma.flatdesign3;

import java.util.ArrayList;

import com.enma.flatdesign3.adapter.SwipeAdapter;
import com.enma.flatdesign3.model.VideoItem;
import com.enma.flatdesign3.model.VideoList;
import com.enma.utils.Utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SwipeActivity extends Fragment implements OnClickListener,
		OnItemClickListener {

	ListView listView;
	int lastIndex = -1;
	ArrayList<VideoItem> lstVideos;
	View vw_layout;
	CellAnimationOut cellAnimationOutListener = new CellAnimationOut();
	CellAnimationIn cellAnimationInListener = new CellAnimationIn();
	private Animation mCellSlideInRight;
	private Animation mCellSlideOutRight;

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
		vw_layout = inflater.inflate(R.layout.activity_swipe, container, false);

		mCellSlideOutRight = AnimationUtils.loadAnimation(getActivity(),
				R.anim.cell_right_out);
		mCellSlideInRight = AnimationUtils.loadAnimation(getActivity(),
				R.anim.cell_right_in);
		mCellSlideOutRight.setAnimationListener(cellAnimationOutListener);
		mCellSlideInRight.setAnimationListener(cellAnimationInListener);

		// get list view
		listView = (ListView) this.vw_layout.findViewById(R.id.lst_videos);
		lstVideos = VideoList.getVideoList();

		listView.setAdapter(new SwipeAdapter(getActivity(), lstVideos, this));

		this.listView.setOnItemClickListener(this);

		Utils.setFontAllView((ViewGroup)vw_layout);
		return vw_layout;
	}

	@Override
	public void onItemClick(AdapterView<?> adp, View listview, int position,
			long id) {

		if (adp != null && adp.getAdapter() instanceof SwipeAdapter) {
			SwipeAdapter swipeAdp = (SwipeAdapter) adp.getAdapter();
			VideoItem itm = swipeAdp.getItem(position);
			itm.set_selected(!itm.get_selected());

			int currentIndex = position - listView.getFirstVisiblePosition()
					- listView.getHeaderViewsCount();

			lastIndex = lastIndex - listView.getFirstVisiblePosition()
					- listView.getHeaderViewsCount();
			if (lastIndex < 0 || lastIndex >= listView.getChildCount())
				cellAnimationOutListener.setPreviousView(null);

			View _v = listView.getChildAt(currentIndex);

			View vwLayer1 = _v.findViewById(R.id.view_layer1);
			View vwLayer2 = _v.findViewById(R.id.view_layer2);

			cellAnimationOutListener.setCurrentView(_v);
			vwLayer1.startAnimation(mCellSlideOutRight);

			int height = vwLayer1.getHeight();
			int width = vwLayer1.getWidth();
			vwLayer2.setLayoutParams(new RelativeLayout.LayoutParams(width,
					height));
			vwLayer2.setVisibility(View.VISIBLE);

			lastIndex = position;
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_love) {
			Integer pos = (Integer) v.getTag();
			Toast.makeText(getActivity(),
					"Love button clicked at position " + pos,
					Toast.LENGTH_SHORT).show();
		}

		if (v.getId() == R.id.btn_like) {
			Integer pos = (Integer) v.getTag();
			Toast.makeText(getActivity(),
					"Like button clicked at position " + pos,
					Toast.LENGTH_SHORT).show();

		}

		if (v.getId() == R.id.btn_reload) {
			Integer pos = (Integer) v.getTag();
			Toast.makeText(getActivity(),
					"Reload button clicked at position " + pos,
					Toast.LENGTH_SHORT).show();

		}

	}

	public class CellAnimationOut implements Animation.AnimationListener {
		private View previousView;
		private View currentView;

		public CellAnimationOut() {

		}

		public View getPreviousView() {
			return previousView;
		}

		public void setPreviousView(View previousView) {
			this.previousView = previousView;
		}

		public View getCurrentView() {
			return currentView;
		}

		public void setCurrentView(View currentView) {
			this.currentView = currentView;
		}

		@Override
		public void onAnimationStart(Animation arg0) {

			if (previousView != null) {
				View layer1 = previousView.findViewById(R.id.view_layer1);
				cellAnimationInListener.setCurrentView(previousView);
				layer1.startAnimation(mCellSlideInRight);
				previousView = null;

			}

			if (currentView != null) {
				View layer2 = currentView.findViewById(R.id.view_layer2);
				layer2.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onAnimationRepeat(Animation arg0) {
		}

		@Override
		public void onAnimationEnd(Animation arg0) {

			if (currentView != null) {
				View textView = currentView.findViewById(R.id.view_layer1);
				textView.setVisibility(View.GONE);
				previousView = currentView;
			}
		}
	}

	public class CellAnimationIn implements Animation.AnimationListener {

		private View currentView;

		public CellAnimationIn() {

		}

		public View getCurrentView() {
			return currentView;
		}

		public void setCurrentView(View currentView) {
			this.currentView = currentView;
		}

		@Override
		public void onAnimationStart(Animation arg0) {

		}

		@Override
		public void onAnimationRepeat(Animation arg0) {
		}

		@Override
		public void onAnimationEnd(Animation arg0) {
			if (currentView != null) {
				View layer1 = currentView.findViewById(R.id.view_layer1);
				View layer2 = currentView.findViewById(R.id.view_layer2);

				layer1.setVisibility(View.VISIBLE);
				layer2.setVisibility(View.INVISIBLE);

			}

		}
	}

}
