package com.syncadapters.czar.exchange.interfaces;

import com.android.volley.VolleyError;

import org.json.JSONArray;

public interface VolleyArrayCallback {

    void network_response(JSONArray json_response);
    void network_error(VolleyError error);
}
