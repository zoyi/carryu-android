package co.zoyi.carryu.Application.Events;

import co.zoyi.carryu.Application.Views.Fragments.CUFragment;

public class NeedRefreshFragmentEvent extends Event {
    private CUFragment fragment;

    public CUFragment getFragment() {
        return fragment;
    }

    public NeedRefreshFragmentEvent(CUFragment fragment) {
        this.fragment = fragment;
    }
}
