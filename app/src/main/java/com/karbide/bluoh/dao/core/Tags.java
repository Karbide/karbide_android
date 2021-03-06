
package com.karbide.bluoh.dao.core;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Tags {

    @SerializedName("tag")
    @Expose
    private List<String> tag = new ArrayList<String>();

    /**
     * 
     * @return
     *     The tag
     */
    public List<String> getTag() {
        return tag;
    }

    /**
     * 
     * @param tag
     *     The tag
     */
    public void setTag(List<String> tag) {
        this.tag = tag;
    }

}
