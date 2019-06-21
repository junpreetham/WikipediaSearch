package com.arjunpreetham.wikipediasearch.views;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.arjunpreetham.wikipediasearch.models.SavedPageDataModel;
import com.arjunpreetham.wikipediasearch.R;
import com.arjunpreetham.wikipediasearch.services.MainDB;

import java.util.ArrayList;

public class WikipediaArticleView extends AppCompatActivity {

    TextView tvLoadingStatus;
    Button saveButton;
    boolean isArticleSaved = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wikipedia_article_view);

        ArrayList<String> articleData = getIntent().getExtras().getBundle("articleData").getStringArrayList("arrayList");

        initSaveButton(articleData);
        initWebView(articleData.get(3));

        isArticleSaved = getIntent().getExtras().getBoolean("isSaved");

        if(isArticleSaved){
            saveButton.setBackground(getResources().getDrawable(R.drawable.saved_icon));
        }else{
            saveButton.setBackground(getResources().getDrawable(R.drawable.not_saved_icon));
        }
    }

    private void initSaveButton(final ArrayList<String> articleData){
        saveButton = (Button) findViewById(R.id.article_save_button);

        final MainDB db = new MainDB(this);

        final String title = articleData.get(0);
        final String description = articleData.get(1);
        final String imageURL = articleData.get(2);
        final String pageID = articleData.get(3);

        final SavedPageDataModel page = new SavedPageDataModel(title, description, imageURL, pageID);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isArticleSaved){
                    isArticleSaved = false;

                    db.deletePage(page);

                    //Change icon
                    saveButton.setBackground(getResources().getDrawable(R.drawable.not_saved_icon));
                }else{
                    isArticleSaved = true;

                    db.addPageToDB(page);

                    //Change icon
                    saveButton.setBackground(getResources().getDrawable(R.drawable.saved_icon));

                }
            }
        });
    }

    private void initWebView(String pageId){
        String url = "https://en.m.wikipedia.org/?curid=" + pageId;

        WebView wikiWebView = (WebView) findViewById(R.id.wiki_webview);

        tvLoadingStatus = (TextView) findViewById(R.id.tv_load_status);

        wikiWebView.getSettings().setJavaScriptEnabled(true);
        wikiWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                tvLoadingStatus.setText("Loading webpage...");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                tvLoadingStatus.setVisibility(View.GONE);
            }
        });
        wikiWebView.loadUrl(url);
    }
}
