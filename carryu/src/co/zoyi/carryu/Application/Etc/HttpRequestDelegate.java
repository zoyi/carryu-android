package co.zoyi.carryu.Application.Etc;

import android.content.Context;
import android.util.Log;
import co.zoyi.carryu.Application.Datas.Models.Summoner;
import co.zoyi.carryu.Application.Datas.ValueObjects.ServerList;
import co.zoyi.carryu.Application.Events.HttpResponses.CUHttpResponseEvent;
import co.zoyi.carryu.Application.Events.HttpResponses.FetchServerInfoEvent;
import co.zoyi.carryu.Application.Events.HttpResponses.FetchSummonerEvent;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import de.greenrobot.event.EventBus;

public class HttpRequestDelegate {
    private static String baseUrl;
    private static final String BASE_URL_FORMAT = "http://%s.carryu.co/api/v1";
    private static final AsyncHttpClient client = new AsyncHttpClient();

    public static class CUHttpResponseHandler extends AsyncHttpResponseHandler {
        @Override
        public void onSuccess(String s) {
            super.onSuccess(s);
            Log.d("zoyi", "onSuccess: " + s);
        }

        @Override
        public void onFailure(Throwable throwable, String s) {
            super.onFailure(throwable, s);
            Log.d("zoyi", "onFailure: " + s);
        }
    }

    public static void setRegion(String region) {
        baseUrl = String.format(BASE_URL_FORMAT, region);
    }

    public static void get(String url, RequestParams params, CUHttpResponseHandler responseHandler) {
        Log.d("zoyi", String.format("GET %s", url));
        client.get(url, params, responseHandler);
    }

    public static void fetchServerInfo(final Context context) {
        get("http://lol-gist.wudi.me", null, new CUHttpResponseHandler() {
            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                Gson gson = new Gson();
                ServerList serverList = gson.fromJson(s, ServerList.class);
                EventBus.getDefault().post(new FetchServerInfoEvent(serverList));
            }
        });
    }

    public static void fetchSummoner(final Summoner summoner) {
        get(baseUrl + "/summoners/" + summoner.getName(), null, new CUHttpResponseHandler(){
            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                EventBus.getDefault().post(new FetchSummonerEvent(new Gson().fromJson(s, Summoner.class)));
            }
        });
    }
}
