package com.arjunpreetham.wikipediasearch.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.arjunpreetham.wikipediasearch.models.DataRequestModel;
import com.arjunpreetham.wikipediasearch.models.SavedPageDataModel;
import com.arjunpreetham.wikipediasearch.R;
import com.arjunpreetham.wikipediasearch.viewmodels.SavedPageViewModel;
import com.arjunpreetham.wikipediasearch.views.WikipediaArticleView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.Arrays;

public class SavedItemsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<SavedPageDataModel> pageList;
    LayoutInflater inflater;

    SavedPageViewModel savedPageViewModel;

    public SavedItemsListAdapter(Context context, ArrayList<SavedPageDataModel> pageList, SavedPageViewModel savedPageViewModel){
        this.context = context;
        this.pageList = pageList;

        this.savedPageViewModel = savedPageViewModel;

        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = inflater.inflate(R.layout.list_item, parent, false);

        ItemHolder holder = new ItemHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        SavedPageDataModel savedPageDataModel = pageList.get(i);

        final String imageURL = savedPageDataModel.getImageURL();
        final String headingText = savedPageDataModel.getHeading();
        final String descriptionText = savedPageDataModel.getDescription();
        final String pageID = savedPageDataModel.getPageID();

        ItemHolder itemHolder = (ItemHolder) viewHolder;

        Glide.with(context).load(imageURL).diskCacheStrategy(DiskCacheStrategy.ALL).into(itemHolder.ivDisplayPicture);
        itemHolder.tvHeading.setText(headingText);
        itemHolder.tvDescription.setText(descriptionText);

        //Click on card to go to wikipedia article
        itemHolder.clItemBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isInterentConnectionAvailable()){
                    Intent in = new Intent(context, WikipediaArticleView.class);

                    ArrayList<String> data = new ArrayList<>(Arrays.asList(headingText, descriptionText, imageURL, pageID));

                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("arrayList", data);

                    in.putExtra("articleData", bundle);
                    in.putExtra("isSaved", true);
                    context.startActivity(in);
                }else{
                    showAlertDialog(context);
                }
            }
        });

        itemHolder.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataRequestModel model = new DataRequestModel(context, DataRequestModel.deletePage, pageList.get(i));

                savedPageViewModel.sendRequest(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pageList.size();
    }

    public void setNewData(ArrayList<SavedPageDataModel> data){
        pageList = data;
    }

    private boolean isInterentConnectionAvailable(){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    private void showAlertDialog(Context context){
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setMessage("Oops! You don't seem to have internet.\nCheck and try again")
                .setPositiveButton("OK", null)
                .show();
    }

    public class ItemHolder extends RecyclerView.ViewHolder{

        ImageView ivDisplayPicture;
        TextView tvHeading, tvDescription;
        ConstraintLayout clItemBackground;
        Button saveButton;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);

            ivDisplayPicture = (ImageView) itemView.findViewById(R.id.iv_page_display_picture);

            tvHeading = (TextView) itemView.findViewById(R.id.tv_page_title);
            tvDescription = (TextView) itemView.findViewById(R.id.tv_page_description);

            clItemBackground = (ConstraintLayout) itemView.findViewById(R.id.cl_item_background);

            saveButton = (Button) itemView.findViewById(R.id.b_save);
        }
    }
}
