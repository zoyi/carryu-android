package co.zoyi.carryu.Application.Datas.Models;

import com.google.gson.annotations.SerializedName;

public abstract class Model {
    public static interface Rooted<T extends Model> {
        public T getObject();
    }

    public int getId() {
        return id;
    }

    @SerializedName("id")

    private int id;
}
