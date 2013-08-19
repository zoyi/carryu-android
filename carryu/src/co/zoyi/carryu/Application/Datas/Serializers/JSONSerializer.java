package co.zoyi.carryu.Application.Datas.Serializers;

import co.zoyi.carryu.Application.Datas.Models.Model;
import com.google.gson.Gson;

public class JSONSerializer<T extends Serializable, RT extends Model.Rooted> {
    private static final Gson gsonInstance = new Gson();

    public static Gson getGsonInstance() {
        return gsonInstance;
    }

    public T toObject(String jsonString, Class<T> cls) {
        return gsonInstance.fromJson(jsonString, cls);
    }

    public String toJson(T object) {
        return gsonInstance.toJson(object);
    }

    public T toObjectFromRooted(String jsonString, Class<RT> cls) {
        return (T) gsonInstance.fromJson(jsonString, cls).getObject();
    }
}
