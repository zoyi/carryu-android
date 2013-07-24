package co.zoyi.carryu.Application.Etc.API;

import android.content.Context;
import co.zoyi.carryu.Application.Datas.Models.ActiveGame;
import co.zoyi.carryu.Application.Datas.Models.Summoner;
import co.zoyi.carryu.Application.Datas.Serializers.JSONSerializer;
import co.zoyi.carryu.Application.Datas.ValueObjects.ServerList;
import co.zoyi.carryu.Application.Etc.CUUtil;
import co.zoyi.carryu.Application.Registries.Registry;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class HttpRequestDelegate {
    private static final String BASE_URL_FORMAT = "http://%s.carryu.co/api/v1";
    private static final String BASE_RTMP_URL_FORMAT = "http://%s.rtmp.carryu.co";
    private static final AsyncHttpClient client = new AsyncHttpClient();
    private static String baseUrl = String.format(BASE_URL_FORMAT, "na");
    private static String baseRtmpUrl = String.format(BASE_RTMP_URL_FORMAT, "na");

    private static class HttpResponseHandler extends AsyncHttpResponseHandler {
        private DataCallback cb;

        private HttpResponseHandler(DataCallback cb) {
            super();
            this.cb = cb;
        }

        @Override
        public void onStart() {
            cb.onStart();
            super.onStart();
        }

        @Override
        public void onFinish() {
            super.onFinish();
            cb.onFinish();
        }

        @Override
        public void onSuccess(String s) {
            super.onSuccess(s);
            CUUtil.log("[RECV] " + s);
        }

        @Override
        public void onFailure(Throwable throwable, String s) {
            super.onFailure(throwable, s);
            cb.onError(throwable, s);
            CUUtil.log("[ERR] " + s);
        }
    }

    public static void setRegion(String region) {
        baseUrl = String.format(BASE_URL_FORMAT, region);
        baseRtmpUrl = String.format(BASE_RTMP_URL_FORMAT, region);
    }

    public static void get(String url, RequestParams params, HttpResponseHandler responseHandler) {
        CUUtil.log("REQ[GET] " + baseUrl + url);
        client.get(baseUrl + url, params, responseHandler);
    }

    public static void getRtmp(String url, RequestParams params, HttpResponseHandler responseHandler) {
        CUUtil.log("REQ[RTMP_GET] " + baseRtmpUrl + url);
        client.get(baseRtmpUrl + url, params, responseHandler);
    }

    public static void fetchServerInfo(final Context context, final DataCallback<ServerList> cb) {
        client.get("http://lol-gist.wudi.me", null, new HttpResponseHandler(cb) {
            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                Gson gson = new Gson();
                ServerList serverList = gson.fromJson(s, ServerList.class);
                cb.onSuccess(serverList);
            }
        });
    }

    private static String getEncodedSummonerName(Summoner summoner) throws UnsupportedEncodingException {
        return URLEncoder.encode(summoner.getName(), "UTF-8").replace("+", "%20");
    }

    public static void fetchSummoner(final Summoner summoner, final DataCallback<Summoner> cb) {
        try {
            get("/summoners/" + getEncodedSummonerName(summoner), null, new HttpResponseHandler(cb){
                @Override
                public void onSuccess(String s) {
                    super.onSuccess(s);
                    cb.onSuccess(Registry.getSummonerSerializer().toObjectFromRooted(s, Summoner.Rooted.class));
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void fetchSummoner(final String userId, final DataCallback<Summoner> cb) {
        get("/summoners/" + userId + "?by=id", null, new HttpResponseHandler(cb){
            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                cb.onSuccess(Registry.getSummonerSerializer().toObjectFromRooted(s, Summoner.Rooted.class));
            }
        });
    }

    public static void fetchActiveGame(final Summoner me, final DataCallback<ActiveGame> cb) {
        try {
            getRtmp("/active_game/" + getEncodedSummonerName(me), null, new HttpResponseHandler(cb) {
                @Override
                public void onSuccess(String s) {
                    super.onSuccess(s);
                    ActiveGame activeGame = Registry.getActiveGameSerializer().toObject(s, ActiveGame.class);
                    cb.onSuccess(activeGame);
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void fetchActiveGameSample(final DataCallback<ActiveGame> cb) {
        get("/active_game/sample", null, new HttpResponseHandler(cb) {
            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                ActiveGame activeGame = Registry.getActiveGameSerializer().toObject(s, ActiveGame.class);
                cb.onSuccess(activeGame);
            }
        });
    }
}
