package com.karbide.bluoh.dao.core;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class Feedback {

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
private String number;
@SerializedName("subject")
@Expose
private String subject;

    public Feedback(String content, String email, String name, String number, String subject) {
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
public String getNumber() {
return number;
}

/**
* 
* @param number
* The number
*/
public void setNumber(String number) {
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