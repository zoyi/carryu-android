package co.zoyi.carryu.Application.Datas.ValueObjects;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class ServerStatus extends ValueObject {
    @SerializedName("errors")
    private List<Map<String, String>> errors;

    public List<Map<String, String>> getErrors() {
        return errors;
    }
}
