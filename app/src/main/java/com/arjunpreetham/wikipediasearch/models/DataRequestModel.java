package com.arjunpreetham.wikipediasearch.models;

import android.content.Context;

public class DataRequestModel {

    private String dataRequestType;
    private String searchTerm;

    SavedPageDataModel savedPageDataModel;

    Context context;

    public static final String webSearch = "webSearch";
    public static final String savedItemsRetrieval = "savedItemsRetrieval";
    public static final String deletePage = "deletePage";

    public DataRequestModel(Context context, String dataRequestType, String searchTerm){
        this.dataRequestType = dataRequestType;
        this.searchTerm = searchTerm;

        this.context = context;
    }

    public DataRequestModel(Context context, String dataRequestType, SavedPageDataModel savedPageDataModel){
        this.dataRequestType = dataRequestType;
        this.savedPageDataModel = savedPageDataModel;

        this.context = context;
    }

    public DataRequestModel(Context context,String dataRequestType){
        this.dataRequestType = dataRequestType;

        this.context = context;
    }

    public String getDataRequestType(){
        return dataRequestType;
    }

    public String getSearchTerm(){
        return searchTerm;
    }

    public Context getContext(){
        return context;
    }

    public SavedPageDataModel getSavedPageDataModel() {
        return savedPageDataModel;
    }
}
