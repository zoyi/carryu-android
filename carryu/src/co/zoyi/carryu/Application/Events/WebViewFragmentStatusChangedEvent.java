package co.zoyi.carryu.Application.Events;

import android.support.v4.app.Fragment;

public class WebViewFragmentStatusChangedEvent extends Event {
    public enum Status {
        STARTED, FINISHED
    }

    private Status status;
    private Fragment fragment;

    public Fragment getFragment() {
        return fragment;
    }

    public Status getStatus() {
        return status;

    }

    public WebViewFragmentStatusChangedEvent(Fragment fragment, Status status) {
        super();
        this.status = status;
        this.fragment = fragment;
    }
}
