package com.arjunpreetham.wikipediasearch.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.arjunpreetham.wikipediasearch.data.DataRepository;
import com.arjunpreetham.wikipediasearch.models.DataRequestModel;
import com.arjunpreetham.wikipediasearch.models.SearchResultsDataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SearchResultsViewModel extends ViewModel {

    ObservableEmitter<DataRequestModel> dataRequestModelEmitter = null;
    ObservableEmitter<ArrayList<SearchResultsDataModel>> searchResultModelEmitter = null;
    Observable<JSONObject> searchResultsObservable;

    public void init(){
        DataRepository dataRepository = new DataRepository();

        searchResultsObservable = dataRepository.getSearchResultsObservable();

        //Initializing Data Repository stream to push data to
        dataRepository.initSearchRequestDataStream(getRequestObservable());

        //Initializing stream to listen to events from Data Repository
        initDataRepositoryStream(searchResultsObservable);
    }

    //Data coming in from DataRespository
    public void initDataRepositoryStream(Observable<JSONObject> searchResultsObservable){
        searchResultsObservable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(JSONObject searchResultsJSON) {
                        //Process and get data
                        ArrayList<SearchResultsDataModel> searchResultsDataModels = getSearchResultsDataModelArray(searchResultsJSON);

                        //Push list to View
                        searchResultModelEmitter.onNext(searchResultsDataModels);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //Initializing observable for Search Request Stream
    private Observable<DataRequestModel> getRequestObservable(){
        return Observable.create(new ObservableOnSubscribe<DataRequestModel>() {
            @Override
            public void subscribe(ObservableEmitter<DataRequestModel> emitter) throws Exception {
                //Emit request to DataRepository
                dataRequestModelEmitter = emitter;
            }
        });
    }

    //To initialize emitter which pushes data to View
    public Observable<ArrayList<SearchResultsDataModel>> getSearchViewModelStream(){
        return Observable.create(new ObservableOnSubscribe<ArrayList<SearchResultsDataModel>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<SearchResultsDataModel>> emitter) throws Exception {
                searchResultModelEmitter = emitter;
            }
        });
    }

    //Data request from view
    public void sendRequest(DataRequestModel dataRequestModel){
        //Pushing request to data repository
        dataRequestModelEmitter.onNext(dataRequestModel);
    }

    //Method to process data
    private ArrayList<SearchResultsDataModel> getSearchResultsDataModelArray(JSONObject json){
        ArrayList<SearchResultsDataModel> searchResultList = new ArrayList<>();

        try{

            JSONArray pagesJson = json.getJSONObject("query").getJSONArray("pages");

            for(int i = 0; i < pagesJson.length(); i++){
                JSONObject responseMetaData = pagesJson.getJSONObject(i);

                String title = responseMetaData.getString("title");

                String description = "";

                if(responseMetaData.has("terms")){
                    description = responseMetaData.getJSONObject("terms").getJSONArray("description").getString(0);

                    //Making first letter capital
                    description = description.substring(0,1).toUpperCase() + description.substring(1);
                }else{
                    description = "NONE";
                }

                String imageURL = "";

                if(responseMetaData.has("thumbnail")){
                    imageURL = responseMetaData.getJSONObject("thumbnail").getString("source");
                }else{
                    imageURL = "NONE";
                }

                String pageID = responseMetaData.getString("pageid");

                SearchResultsDataModel model = new SearchResultsDataModel(title, description, imageURL, pageID);
                searchResultList.add(model);
            }

        }catch (JSONException e){
            Log.d("DEBUG", "ERR: " + e.getLocalizedMessage());
        }

        return searchResultList;
    }

}
