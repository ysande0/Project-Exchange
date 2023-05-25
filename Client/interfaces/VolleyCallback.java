package com.syncadapters.czar.exchange.interfaces;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface VolleyCallback {

    void network_response(JSONObject json_response);
    void network_error(VolleyError error);


}

