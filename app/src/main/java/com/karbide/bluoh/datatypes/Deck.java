
package com.karbide.bluoh.datatypes;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Deck {

    @SerializedName("deckId")
    @Expose
    private Integer deckId;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("cards")
    @Expose
    private List<Card> cards = new ArrayList<Card>();

    /**
     * 
     * @return
     *     The deckId
     */
    public Integer getDeckId() {
        return deckId;
    }

    /**
     * 
     * @param deckId
     *     The deckId
     */
    public void setDeckId(Integer deckId) {
        this.deckId = deckId;
    }

    /**
     * 
     * @return
     *     The type
     */
    public String getType() {
        return type;
    }

    /**
     * 
     * @param type
     *     The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 
     * @return
     *     The cards
     */
    public List<Card> getCards() {
        return cards;
    }

    /**
     * 
     * @param cards
     *     The cards
     */
    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

}
