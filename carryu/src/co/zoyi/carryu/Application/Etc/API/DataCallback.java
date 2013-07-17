package co.zoyi.carryu.Application.Etc.API;

public class DataCallback<T> {
    public void onStart(){}
    public void onSuccess(T object){}
    public void onFinish(){}

    public boolean onError(Throwable throwable, String s){
        throwable.printStackTrace();
        return true;
    }

    public boolean onError(String s){
        return true;
    }
}
