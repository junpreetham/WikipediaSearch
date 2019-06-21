package com.arjunpreetham.wikipediasearch.views;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.arjunpreetham.wikipediasearch.adapters.SavedItemsListAdapter;
import com.arjunpreetham.wikipediasearch.models.DataRequestModel;
import com.arjunpreetham.wikipediasearch.models.SavedPageDataModel;
import com.arjunpreetham.wikipediasearch.R;
import com.arjunpreetham.wikipediasearch.viewmodels.SavedPageViewModel;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Views
    RecyclerView rvSavedPagesList;
    TextView tvNoPagesSavedText;
    FloatingActionButton searchFAB;

    //ViewModels
    SavedPageViewModel savedPageViewModel;

    //Adapters
    SavedItemsListAdapter savedItemsListAdapter;

    //Array Lists
    ArrayList<SavedPageDataModel> savedPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initViewModels();
    }

    private void initViews(){
        //Initializing View Objects
        rvSavedPagesList = (RecyclerView) findViewById(R.id.rv_saved_items_list);

        tvNoPagesSavedText = (TextView) findViewById(R.id.tv_no_pages_saved);

        searchFAB = (FloatingActionButton) findViewById(R.id.search_fab);

        //Initializing onClickListener
        searchFAB.setOnClickListener(this);

        savedPages = new ArrayList<>();
    }

    private void initViewModels(){
        //Initializing View Models
        savedPageViewModel = ViewModelProviders.of(this).get(SavedPageViewModel.class);
        savedPageViewModel.init();

        //Getting saved list model stream from View Model
        savedPageViewModel.getSavedListViewModelStream().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<SavedPageDataModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        //Display saved pages
                        displaySavedPages();
                    }

                    @Override
                    public void onNext(ArrayList<SavedPageDataModel> data) {
                        //Check if anything is saved in database
                        if(data.size() == 0){
                            showNoDataToDisplayText();
                        }else{
                            hideNoDataToDisplayText();

                            //update recycler view
                            savedItemsListAdapter.setNewData(data);
                            savedItemsListAdapter.notifyDataSetChanged();
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

    private void initRecyclerView(){
        //Initializing recycler views
        rvSavedPagesList.setLayoutManager(new LinearLayoutManager(this));
        savedItemsListAdapter = new SavedItemsListAdapter(MainActivity.this, savedPages, savedPageViewModel);
        rvSavedPagesList.setAdapter(savedItemsListAdapter);
    }

    private void displaySavedPages(){
        initRecyclerView();

        //Creating request to receive saved pages
        DataRequestModel dataRequestModel = new DataRequestModel(this, DataRequestModel.savedItemsRetrieval);

        //Sending request
        savedPageViewModel.sendRequest(dataRequestModel);
    }

    private void showNoDataToDisplayText(){
        rvSavedPagesList.setVisibility(View.GONE);
        tvNoPagesSavedText.setVisibility(View.VISIBLE);
    }

    private void hideNoDataToDisplayText(){
        rvSavedPagesList.setVisibility(View.VISIBLE);
        tvNoPagesSavedText.setVisibility(View.GONE);
    }

    private boolean isInterentConnectionAvailable(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    private void showAlertDialog(Context context){
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setMessage("Oops! You don't seem to have internet.\nCheck and try again")
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_fab:
               if(isInterentConnectionAvailable()){
                   Intent in  = new Intent(v.getContext(), SearchActivity.class);
                   v.getContext().startActivity(in);
               }else{
                   showAlertDialog(v.getContext());
               }
            break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        displaySavedPages();
    }
}
