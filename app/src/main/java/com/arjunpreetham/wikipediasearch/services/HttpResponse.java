package com.arjunpreetham.wikipediasearch.services;

import org.json.JSONObject;

public interface HttpResponse {
    void onHttpResponse(JSONObject responseJSON);
}
