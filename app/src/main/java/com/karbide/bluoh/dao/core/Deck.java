package com.karbide.bluoh.dao.core;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Deck {

    @SerializedName("deckId")
    @Expose
    private Integer deckId;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("authorImage")
    @Expose
    private String authorImage;

    @SerializedName("authorHandle")
    @Expose
    private String displayName;
    @SerializedName("categories")
    @Expose
    private List<String> categories = new ArrayList<String>();
    @SerializedName("tags")
    @Expose
    private List<String> tags = new ArrayList<String>();
    @SerializedName("approver")
    @Expose
    private Approver approver;
    @SerializedName("deckStatus")
    @Expose
    private String status;
    @SerializedName("likes")
    @Expose
    private Integer likes;
    @SerializedName("dislikes")
    @Expose
    private Integer dislikes;
    @SerializedName("cards")
    @Expose
    private List<Card> cards = new ArrayList<Card>();

    /**
     *
     * @return
     * The deckId
     */
    public Integer getDeckId() {
        return deckId;
    }

    /**
     *
     * @param deckId
     * The deckId
     */
    public void setDeckId(Integer deckId) {
        this.deckId = deckId;
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

    /**
     *
     * @return
     * The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     * The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     * The author
     */
    public String getAuthor() {
        return author;
    }

    /**
     *
     * @param author
     * The author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     *
     * @return
     * The author
     */
    public String getAuthorImage() {
        return authorImage;
    }

    /**
     *
     * @param authorImage
     * The author
     */
    public void setAuthorImage(String authorImage) {
        this.authorImage = authorImage;
    }

    /**
     *
     * @return
     * The displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     *
     * @param displayName
     * The displayName
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     *
     * @return
     * The categories
     */
    public List<String> getCategories() {
        return categories;
    }

    /**
     *
     * @param categories
     * The categories
     */
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    /**
     *
     * @return
     * The tags
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     *
     * @param tags
     * The tags
     */
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /**
     *
     * @return
     * The approver
     */
    public Approver getApprover() {
        return approver;
    }

    /**
     *
     * @param approver
     * The approver
     */
    public void setApprover(Approver approver) {
        this.approver = approver;
    }

    /**
     *
     * @return
     * The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The likes
     */
    public Integer getLikes() {
        return likes;
    }

    /**
     *
     * @param likes
     * The likes
     */
    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    /**
     *
     * @return
     * The dislikes
     */
    public Integer getDislikes() {
        return dislikes;
    }

    /**
     *
     * @param dislikes
     * The dislikes
     */
    public void setDislikes(Integer dislikes) {
        this.dislikes = dislikes;
    }

    /**
     *
     * @return
     * The cards
     */
    public List<Card> getCards() {
        return cards;
    }

    /**
     *
     * @param cards
     * The cards
     */
    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

}