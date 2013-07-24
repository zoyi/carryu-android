package co.zoyi.carryu.Application.Datas.Models;

import com.google.gson.annotations.SerializedName;

public class Champion extends Model {
    @SerializedName("name")
    private String name;

    public String getChampionImageUrl() {
        return String.format("http://kr.carryu.co/champions/%d/image", getId());
    }
}
