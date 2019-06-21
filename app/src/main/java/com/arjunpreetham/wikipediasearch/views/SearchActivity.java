package com.arjunpreetham.wikipediasearch.views;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.arjunpreetham.wikipediasearch.adapters.SearchItemsListAdapter;
import com.arjunpreetham.wikipediasearch.models.DataRequestModel;
import com.arjunpreetham.wikipediasearch.models.SearchResultsDataModel;
import com.arjunpreetham.wikipediasearch.R;
import com.arjunpreetham.wikipediasearch.viewmodels.SearchResultsViewModel;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    //Views
    EditText etSearch;
    Button bSearchButton;
    RecyclerView rvSearchResultsList;
    ProgressDialog dialog;

    //View Models
    SearchResultsViewModel searchResultsViewModel;

    //Adapters
    SearchItemsListAdapter searchItemsListAdapter;

    //ArrayLists
    ArrayList<SearchResultsDataModel> searchedPages;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_view);

        initViews();
        initViewModels();
    }

    private void initViews(){
        //Initializing View Objects
        rvSearchResultsList = (RecyclerView) findViewById(R.id.rv_search_results_list);

        etSearch = (EditText) findViewById(R.id.et_search);

        bSearchButton = (Button) findViewById(R.id.search_button);

        //Initializing onClickListener
        bSearchButton.setOnClickListener(this);

        searchedPages = new ArrayList<>();

        //Initializing recycler views
        rvSearchResultsList.setLayoutManager(new LinearLayoutManager(this));
        searchItemsListAdapter = new SearchItemsListAdapter(this, searchedPages);
        rvSearchResultsList.setAdapter(searchItemsListAdapter);
    }

    private void initViewModels(){
        //Initializing View Models
        searchResultsViewModel = ViewModelProviders.of(this).get(SearchResultsViewModel.class);
        searchResultsViewModel.init();


        searchResultsViewModel.getSearchViewModelStream().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<SearchResultsDataModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<SearchResultsDataModel> data) {
                        dismissLoadingDialog();

                        //Update recycler view
                        searchItemsListAdapter.setNewData(data);
                        searchItemsListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void showLoadingDialog(){
        dialog = new ProgressDialog(SearchActivity.this);
        dialog.setMessage("Searching Wikipedia");
        dialog.show();
    }

    private void dismissLoadingDialog(){
        dialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_button:
                String searchData = etSearch.getText().toString();

                if(!searchData.isEmpty()){
                    showLoadingDialog();

                    DataRequestModel dataRequestModel = new DataRequestModel(v.getContext(), DataRequestModel.webSearch, searchData);
                    searchResultsViewModel.sendRequest(dataRequestModel);
                }
                break;
        }
    }
}
