package com.karbide.bluoh.dao;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResponse {

@SerializedName("id")
@Expose
private String id;
@SerializedName("username")
@Expose
private String username;
@SerializedName("password")
@Expose
private String password;
@SerializedName("createdTime")
@Expose
private String createdTime;
@SerializedName("modifiedTime")
@Expose
private String modifiedTime;
@SerializedName("role")
@Expose
private Integer role;

/**
* 
* @return
* The id
*/
public String getId() {
return id;
}

/**
* 
* @param id
* The id
*/
public void setId(String id) {
this.id = id;
}

/**
* 
* @return
* The username
*/
public String getUsername() {
return username;
}

/**
* 
* @param username
* The username
*/
public void setUsername(String username) {
this.username = username;
}

/**
* 
* @return
* The password
*/
public String getPassword() {
return password;
}

/**
* 
* @param password
* The password
*/
public void setPassword(String password) {
this.password = password;
}

/**
* 
* @return
* The createdTime
*/
public String getCreatedTime() {
return createdTime;
}

/**
* 
* @param createdTime
* The createdTime
*/
public void setCreatedTime(String createdTime) {
this.createdTime = createdTime;
}

/**
* 
* @return
* The modifiedTime
*/
public String getModifiedTime() {
return modifiedTime;
}

/**
* 
* @param modifiedTime
* The modifiedTime
*/
public void setModifiedTime(String modifiedTime) {
this.modifiedTime = modifiedTime;
}

/**
* 
* @return
* The role
*/
public Integer getRole() {
return role;
}

/**
* 
* @param role
* The role
*/
public void setRole(Integer role) {
this.role = role;
}

}