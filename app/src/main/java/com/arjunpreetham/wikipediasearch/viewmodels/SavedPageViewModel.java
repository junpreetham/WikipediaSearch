package com.arjunpreetham.wikipediasearch.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.arjunpreetham.wikipediasearch.data.DataRepository;
import com.arjunpreetham.wikipediasearch.models.DataRequestModel;
import com.arjunpreetham.wikipediasearch.models.SavedPageDataModel;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SavedPageViewModel extends ViewModel {

    ObservableEmitter<DataRequestModel> dataRequestModelEmitter = null;
    ObservableEmitter<ArrayList<SavedPageDataModel>> savedListModelEmitter = null;
    public void init(){
        DataRepository dataRepository = new DataRepository();

        Observable<ArrayList<SavedPageDataModel>> savedListObservable = dataRepository.getSavedListObservable();

        //Initializing Data Repository stream to push data to
        dataRepository.initSearchRequestDataStream(getRequestObservable());

        //Initializing stream to listen to events from Data Repository
        initDataRepositoryStream(savedListObservable);
    }

    //Data coming in from DataRespository
    public void initDataRepositoryStream(Observable<ArrayList<SavedPageDataModel>> searchResultsObservable){
        searchResultsObservable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<ArrayList<SavedPageDataModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<SavedPageDataModel> savedListDataModels) {
                        //Push list to View
                        Log.d("DEBUG", "ON NEXT");
                        savedListModelEmitter.onNext(savedListDataModels);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //To initialize emitter which pushes data to Data Repository
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
    public Observable<ArrayList<SavedPageDataModel>> getSavedListViewModelStream(){
        return Observable.create(new ObservableOnSubscribe<ArrayList<SavedPageDataModel>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<SavedPageDataModel>> emitter) throws Exception {
                savedListModelEmitter = emitter;
            }
        });
    }

    //Data request from view; data pushed to DataRepository
    public void sendRequest(DataRequestModel dataRequestModel){
        //Pushing request to data repository
        dataRequestModelEmitter.onNext(dataRequestModel);
    }

}
