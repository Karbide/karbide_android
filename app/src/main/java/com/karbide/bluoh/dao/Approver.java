
package com.karbide.bluoh.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Approver {

    @SerializedName("liveDays")
    @Expose
    private Integer liveDays;
    @SerializedName("ranking")
    @Expose
    private String ranking;
    @SerializedName("liveOnDate")
    @Expose
    private String liveOnDate;
    @SerializedName("comments")
    @Expose
    private String comments;

    /**
     * 
     * @return
     *     The liveDays
     */
    public Integer getLiveDays() {
        return liveDays;
    }

    /**
     * 
     * @param liveDays
     *     The liveDays
     */
    public void setLiveDays(Integer liveDays) {
        this.liveDays = liveDays;
    }

    /**
     * 
     * @return
     *     The ranking
     */
    public String getRanking() {
        return ranking;
    }

    /**
     * 
     * @param ranking
     *     The ranking
     */
    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    /**
     * 
     * @return
     *     The liveOnDate
     */
    public String getLiveOnDate() {
        return liveOnDate;
    }

    /**
     * 
     * @param liveOnDate
     *     The liveOnDate
     */
    public void setLiveOnDate(String liveOnDate) {
        this.liveOnDate = liveOnDate;
    }

    /**
     * 
     * @return
     *     The comments
     */
    public String getComments() {
        return comments;
    }

    /**
     * 
     * @param comments
     *     The comments
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

}
