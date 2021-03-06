
package com.karbide.bluoh.dao.core;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Category {

    @SerializedName("category")
    @Expose
    private List<String> category = new ArrayList<String>();

    /**
     * 
     * @return
     *     The category
     */
    public List<String> getCategory() {
        return category;
    }

    /**
     * 
     * @param category
     *     The category
     */
    public void setCategory(List<String> category) {
        this.category = category;
    }

}
