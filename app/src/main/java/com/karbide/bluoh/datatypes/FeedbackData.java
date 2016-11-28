package com.karbide.bluoh.datatypes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class FeedbackData {

@SerializedName("content")
@Expose
private String content;
@SerializedName("email")
@Expose
private String email;
@SerializedName("name")
@Expose
private String name;
@SerializedName("number")
@Expose
private Integer number;
@SerializedName("subject")
@Expose
private String subject;

    public FeedbackData(String content, String email, String name, Integer number, String subject) {
        this.content = content;
        this.email = email;
        this.name = name;
        this.number = number;
        this.subject = subject;
    }

    /**
* 
* @return
* The content
*/
public String getContent() {
return content;
}

/**
* 
* @param content
* The content
*/
public void setContent(String content) {
this.content = content;
}

/**
* 
* @return
* The email
*/
public String getEmail() {
return email;
}

/**
* 
* @param email
* The email
*/
public void setEmail(String email) {
this.email = email;
}

/**
* 
* @return
* The name
*/
public String getName() {
return name;
}

/**
* 
* @param name
* The name
*/
public void setName(String name) {
this.name = name;
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

/**
* 
* @return
* The subject
*/
public String getSubject() {
return subject;
}

/**
* 
* @param subject
* The subject
*/
public void setSubject(String subject) {
this.subject = subject;
}

}