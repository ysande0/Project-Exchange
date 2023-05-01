package com.syncadapters.czar.exchange.interfaces;

import com.android.volley.VolleyError;

public interface VolleyStringCallback {

    void network_response(String json_response);
    void network_error(VolleyError error);

}
