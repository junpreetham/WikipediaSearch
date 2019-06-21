package com.arjunpreetham.wikipediasearch.data;

import android.content.Context;
import android.util.Log;

import com.arjunpreetham.wikipediasearch.models.DataRequestModel;
import com.arjunpreetham.wikipediasearch.models.SavedPageDataModel;
import com.arjunpreetham.wikipediasearch.services.HttpResponse;
import com.arjunpreetham.wikipediasearch.services.MainDB;
import com.arjunpreetham.wikipediasearch.services.WebSearch;

import org.json.JSONObject;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DataRepository implements HttpResponse {

    ObservableEmitter<JSONObject> searchResultsDataEmitter = null;
    ObservableEmitter<ArrayList<SavedPageDataModel>> saveListDataEmitter = null;

    //Data coming in from View Model
    public void initSearchRequestDataStream(Observable<DataRequestModel> SearchRequestDataStream){
        SearchRequestDataStream.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<DataRequestModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(DataRequestModel dataRequestModel) {
                        //What type of data do I need to retrieve?
                        String dataRequestType = dataRequestModel.getDataRequestType();

                        Context context = dataRequestModel.getContext();

                        if(dataRequestType.equals(DataRequestModel.webSearch)){ //Request is websearch
                            String searchTerm = dataRequestModel.getSearchTerm();

                            //Beginning web search
                            WebSearch webSearch = new WebSearch();
                            webSearch.startWebSearch(DataRepository.this, searchTerm);
                        }else if(dataRequestType.equals(DataRequestModel.savedItemsRetrieval)){ //Retrieve data locally

                            //Retrieving saved pages
                            MainDB db = new MainDB(context);

                            ArrayList<SavedPageDataModel> model = db.getSavedPages();

                            if(saveListDataEmitter != null){
                                saveListDataEmitter.onNext(model);
                            }
                        }else if(dataRequestType.equals(DataRequestModel.deletePage)){
                            SavedPageDataModel page = dataRequestModel.getSavedPageDataModel();

                            MainDB db = new MainDB(context);
                            db.deletePage(page);

                            Log.d("DEBUG", "Deleting page");

                            ArrayList<SavedPageDataModel> model = db.getSavedPages();

                            if(saveListDataEmitter != null){
                                saveListDataEmitter.onNext(model);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //Data going out to Search Results View Model
    public Observable<JSONObject> getSearchResultsObservable(){
        return Observable.create(new ObservableOnSubscribe<JSONObject>() {
            @Override
            public void subscribe(ObservableEmitter<JSONObject> emitter) throws Exception {
                searchResultsDataEmitter = emitter;
            }
        });
    }

    //Data going out to Saved List View Model
    public Observable<ArrayList<SavedPageDataModel>> getSavedListObservable(){
        return Observable.create(new ObservableOnSubscribe<ArrayList<SavedPageDataModel>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<SavedPageDataModel>> emitter) throws Exception {
                saveListDataEmitter = emitter;
            }
        });
    }

    //Callback when http response is received
    @Override
    public void onHttpResponse(JSONObject responseJSON) {
        //Push data to SearchResultsViewModel
        searchResultsDataEmitter.onNext(responseJSON);
    }
}
