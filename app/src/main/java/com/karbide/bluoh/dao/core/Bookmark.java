package com.karbide.bluoh.dao.core;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bookmark extends BluohDAO {

@SerializedName("deckId")
@Expose
private Integer deckId;

@SerializedName("cardId")
@Expose
private String cardId;

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
* The cardId
*/
public String getCardId() {
return cardId;
}

/**
* 
* @param cardId
* The cardId
*/
public void setCardId(String cardId) {
this.cardId = cardId;
}

}