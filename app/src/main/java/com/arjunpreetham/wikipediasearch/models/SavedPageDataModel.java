package com.arjunpreetham.wikipediasearch.models;

public class SavedPageDataModel {

    String headingText;
    String description;
    String imageURL;
    String pageID;

    public SavedPageDataModel(String headingText, String description, String imageURL, String pageID){
        this.headingText = headingText;
        this.description = description;
        this.imageURL = imageURL;
        this.pageID = pageID;
    }

    public String getHeading(){
        return headingText;
    }

    public String getDescription(){
        return description;
    }

    public String getImageURL(){
        return imageURL;
    }

    public String getPageID(){
        return pageID;
    }

}
