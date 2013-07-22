package co.zoyi.carryu.Application.Etc.API;

public class DataCallback<T> {
    public void onStart(){}
    public void onSuccess(T object){}
    public void onFinish(){}
    public void onError(String s){}
    public void onError(Throwable throwable, String s){
        throwable.printStackTrace();
    }
}
