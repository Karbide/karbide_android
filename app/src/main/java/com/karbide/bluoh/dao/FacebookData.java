package com.karbide.bluoh.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FacebookData {

@SerializedName("name")
@Expose
private String name;
@SerializedName("gender")
@Expose
private String gender;
@SerializedName("email")
@Expose
private String email;
@SerializedName("picture")
@Expose
private Picture picture;
@SerializedName("id")
@Expose
private String id;
@SerializedName("gcm_key")
@Expose
private String gcmKey;

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
* The gender
*/
public String getGender() {
return gender;
}

/**
* 
* @param gender
* The gender
*/
public void setGender(String gender) {
this.gender = gender;
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
* The picture
*/
public Picture getPicture() {
return picture;
}

/**
* 
* @param picture
* The picture
*/
public void setPicture(Picture picture) {
this.picture = picture;
}

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

    public String getGcmKey() {
        return gcmKey;
    }

    public void setGcmKey(String gcmKey) {
        this.gcmKey = gcmKey;
    }

    public class Data {

        @SerializedName("height")
        @Expose
        private Integer height;
        @SerializedName("is_silhouette")
        @Expose
        private Boolean isSilhouette;
        @SerializedName("url")
        @Expose
        private String url;
        @SerializedName("width")
        @Expose
        private Integer width;

        /**
         *
         * @return
         * The height
         */
        public Integer getHeight() {
            return height;
        }

        /**
         *
         * @param height
         * The height
         */
        public void setHeight(Integer height) {
            this.height = height;
        }

        /**
         *
         * @return
         * The isSilhouette
         */
        public Boolean getIsSilhouette() {
            return isSilhouette;
        }

        /**
         *
         * @param isSilhouette
         * The is_silhouette
         */
        public void setIsSilhouette(Boolean isSilhouette) {
            this.isSilhouette = isSilhouette;
        }

        /**
         *
         * @return
         * The url
         */
        public String getUrl() {
            return url;
        }

        /**
         *
         * @param url
         * The url
         */
        public void setUrl(String url) {
            this.url = url;
        }

        /**
         *
         * @return
         * The width
         */
        public Integer getWidth() {
            return width;
        }

        /**
         *
         * @param width
         * The width
         */
        public void setWidth(Integer width) {
            this.width = width;
        }

    }


    public class Picture {

        @SerializedName("data")
        @Expose
        private Data data;

        /**
         *
         * @return
         * The data
         */
        public Data getData() {
            return data;
        }

        /**
         *
         * @param data
         * The data
         */
        public void setData(Data data) {
            this.data = data;
        }

    }
}
