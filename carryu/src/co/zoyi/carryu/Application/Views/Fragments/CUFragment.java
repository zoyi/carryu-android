package co.zoyi.carryu.Application.Views.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import co.zoyi.carryu.Application.Etc.CUUtil;
import de.greenrobot.event.EventBus;

public class CUFragment extends Fragment {
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CUUtil.setFontAllView((ViewGroup) getActivity().getWindow().getDecorView());
    }
}
