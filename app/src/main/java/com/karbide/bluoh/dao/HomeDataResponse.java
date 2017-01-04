package com.karbide.bluoh.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.karbide.bluoh.dao.core.Deck;

import java.util.ArrayList;

public class HomeDataResponse {
    @SerializedName("content")
    @Expose
    private ArrayList<Deck> deck = new ArrayList<Deck>();
    @SerializedName("last")
    @Expose
    private Boolean last;
    @SerializedName("totalPages")
    @Expose
    private Integer totalPages;
    @SerializedName("totalElements")
    @Expose
    private Integer totalElements;
    @SerializedName("sort")
    @Expose
    private Object sort;
    @SerializedName("numberOfElements")
    @Expose
    private Integer numberOfElements;
    @SerializedName("first")
    @Expose
    private Boolean first;
    @SerializedName("size")
    @Expose
    private Integer size;
    @SerializedName("number")
    @Expose
    private Integer number;

    /**
     *
     * @return
     * The content
     */
    public ArrayList<Deck> getDeck() {
        return deck;
    }

    /**
     *
     * @param deck
     * The content
     */
    public void setDeck(ArrayList<Deck> deck) {
        this.deck = deck;
    }

    /**
     *
     * @return
     * The last
     */
    public Boolean getLast() {
        return last;
    }

    /**
     *
     * @param last
     * The last
     */
    public void setLast(Boolean last) {
        this.last = last;
    }

    /**
     *
     * @return
     * The totalPages
     */
    public Integer getTotalPages() {
        return totalPages;
    }

    /**
     *
     * @param totalPages
     * The totalPages
     */
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    /**
     *
     * @return
     * The totalElements
     */
    public Integer getTotalElements() {
        return totalElements;
    }

    /**
     *
     * @param totalElements
     * The totalElements
     */
    public void setTotalElements(Integer totalElements) {
        this.totalElements = totalElements;
    }

    /**
     *
     * @return
     * The sort
     */
    public Object getSort() {
        return sort;
    }

    /**
     *
     * @param sort
     * The sort
     */
    public void setSort(Object sort) {
        this.sort = sort;
    }

    /**
     *
     * @return
     * The numberOfElements
     */
    public Integer getNumberOfElements() {
        return numberOfElements;
    }

    /**
     *
     * @param numberOfElements
     * The numberOfElements
     */
    public void setNumberOfElements(Integer numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    /**
     *
     * @return
     * The first
     */
    public Boolean getFirst() {
        return first;
    }

    /**
     *
     * @param first
     * The first
     */
    public void setFirst(Boolean first) {
        this.first = first;
    }

    /**
     *
     * @return
     * The size
     */
    public Integer getSize() {
        return size;
    }

    /**
     *
     * @param size
     * The size
     */
    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     *
     * @return
     * The number
     */
    public Integer getNumber() {
        return number;
    }

    /**
     *
     * @param number
     * The number
     */
    public void setNumber(Integer number) {
        this.number = number;
    }

}
