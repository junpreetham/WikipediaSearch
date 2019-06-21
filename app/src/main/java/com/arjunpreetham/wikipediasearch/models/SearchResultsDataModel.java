package com.arjunpreetham.wikipediasearch.models;

public class SearchResultsDataModel {

    String title;
    String description;
    String imageURL;
    String pageID;

    public SearchResultsDataModel(String title, String description, String imageURL, String pageID){
        this.title = title;
        this.description = description;
        this.imageURL = imageURL;
        this.pageID = pageID;
    }

    public String getTitle(){
        return title;
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
