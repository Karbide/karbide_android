package com.karbide.bluoh.datatypes;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrafficData {

    @SerializedName("activity")
    @Expose
    private String activity;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("info")
    @Expose
    private String info;

    /**
    *
    * @return
    * The activity
    */
    public String getActivity() {
    return activity;
    }

    /**
    *
    * @param activity
    * The activity
    */
    public void setActivity(String activity) {
    this.activity = activity;
    }

    /**
    *
    * @return
    * The type
    */
    public String getType() {
    return type;
    }

    /**
    *
    * @param type
    * The type
    */
    public void setType(String type) {
    this.type = type;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}