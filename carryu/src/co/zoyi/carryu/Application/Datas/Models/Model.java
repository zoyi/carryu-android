package co.zoyi.carryu.Application.Datas.Models;

import co.zoyi.carryu.Application.Datas.Serializers.Serializable;
import com.google.gson.annotations.SerializedName;

public abstract class Model implements Serializable {
    public static interface Rooted<T extends Model> {
        public T getObject();
    }

    public int getId() {
        return id;
    }

    @SerializedName("id")
    private int id;
}
