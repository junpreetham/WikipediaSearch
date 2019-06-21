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

import com.arjunpreetham.wikipediasearch.models.SearchResultsDataModel;
import com.arjunpreetham.wikipediasearch.R;
import com.arjunpreetham.wikipediasearch.views.WikipediaArticleView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.Arrays;

public class SearchItemsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<SearchResultsDataModel> pageList;

    LayoutInflater inflater;
    public SearchItemsListAdapter(Context context, ArrayList<SearchResultsDataModel> data){
        this.context = context;
        this.pageList = data;

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
        SearchResultsDataModel savedPageDataModel = pageList.get(i);

        final String imageURL = savedPageDataModel.getImageURL();
        final String headingText = savedPageDataModel.getTitle();
        final String descriptionText = savedPageDataModel.getDescription();
        final String pageID = savedPageDataModel.getPageID();

        ItemHolder itemHolder = (ItemHolder) viewHolder;

        if(imageURL.equals("NONE")){
            itemHolder.ivDisplayPicture.setImageResource(R.drawable.ic_launcher_background);
        }else{
            Glide.with(context).load(imageURL).diskCacheStrategy(DiskCacheStrategy.ALL).into(itemHolder.ivDisplayPicture);
        }

        if(descriptionText.equals("NONE")){
            itemHolder.tvDescription.setText("No Description Available");
        }else{
            itemHolder.tvDescription.setText(descriptionText);
        }

        itemHolder.tvHeading.setText(headingText);

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
                    context.startActivity(in);
                }else{
                    showAlertDialog(context);
                }
            }
        });

        itemHolder.saveButton.setVisibility(View.INVISIBLE);

    }

    @Override
    public int getItemCount() {
        return pageList.size();
    }

    public void setNewData(ArrayList<SearchResultsDataModel> data){
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

    private class ItemHolder extends RecyclerView.ViewHolder{

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
