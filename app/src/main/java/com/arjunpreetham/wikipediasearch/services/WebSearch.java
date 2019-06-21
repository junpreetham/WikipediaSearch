package com.arjunpreetham.wikipediasearch.services;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebSearch {

    public void startWebSearch(HttpResponse response, String term){
        new HttpRequest(response).execute(term);
    }

    public class HttpRequest extends AsyncTask<String, Void, String>{

        HttpResponse httpResponse;
        HttpRequest(HttpResponse httpResponse){
            this.httpResponse = httpResponse;
        }

        @Override
        protected String doInBackground(String... args) {
            String searchText = args[0];

            if(searchText.trim().contains(" ")){
                searchText = searchText.replace(" ", "%20");
            }

            String URL = "https://en.wikipedia.org/w/api.php?action=query&formatversion=2&generator=prefixsearch&prop=pageimages%7Cpageterms&piprop=thumbnail&format=json&pithumbsize=300&wbptterms=description&gpssearch=" + searchText;
            String str = "";
            try{
                java.net.URL url = new URL(URL);

                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();

                InputStream is = urlConn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                String line = "";

                while((line = br.readLine()) != null){
                    str += line;
                }
            }catch (Exception e){
                Log.d("DEBUG", e.getMessage());
            }finally {
                try {
                    JSONObject responseJson = new JSONObject(str);

                    Log.d("DEBUG", responseJson.toString());
                    httpResponse.onHttpResponse(responseJson);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }

}
