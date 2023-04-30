package com.syncadapters.czar.exchange.http;

import android.content.Context;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.syncadapters.czar.exchange.datamodels.UserSettings;
import com.syncadapters.czar.exchange.interfaces.VolleyArrayCallback;
import com.syncadapters.czar.exchange.interfaces.VolleyCallback;
import com.syncadapters.czar.exchange.interfaces.VolleyStringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class Volley {

    private static final String TAG = "MSG";
    private JSONObject json_object;
    private JSONArray  json_array;
    private Request.Priority priority;
   // private final Context context;
    private final WeakReference<Context> context_weak_reference;
    private static final int TIME_OUT = 20000;
    private final String url;
    private final int http_method;

    public Volley(Context context, int http_method,  String url, JSONObject json_object){

        //this.context = context;
        context_weak_reference = new WeakReference<>(context);
        this.http_method = http_method;
        this.url = url;
        this.json_object = json_object;

    }


    public Volley( Context context, int http_method, String url, JSONArray json_array){

       // this.context = context;
        context_weak_reference = new WeakReference<>(context);
        this.http_method = http_method;
        this.url = url;
        this.json_array = json_array;

    }

    public Volley(Context context, int http_method, String url){

       // this.context = context;
        context_weak_reference = new WeakReference<>(context);
        this.http_method = http_method;
        this.url = url;

    }

    public void set_priority(Request.Priority priority){

        this.priority = priority;
    }


    public void Execute(final VolleyCallback volley_callback){

            Log.d(TAG, "Executing network call... " + this.json_object.toString());
           Log.d(TAG, "To URL: " + this.url);

        JsonObjectRequest json_request = new JsonObjectRequest(this.http_method, this.url, json_object, response -> {

            Log.d(TAG, "Network Call Success: " + response.toString());
            volley_callback.network_response(response);

        }, error -> {
            error.printStackTrace();
            Log.d(TAG, "Network Error Volley: " + error.toString());

            volley_callback.network_error(error);
        }) {

            @Override
            public Map<String, String> getHeaders() {

                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", UserSettings.get_user_token(context_weak_reference.get()));
                params.put("Accept", "application/json");

                return params;
            }

            @Override
            public void addMarker(String tag) {
                super.addMarker(tag);

                Log.d(TAG, "[JsonObject] Volley State: " + tag);

            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }

            @Override
            public Priority getPriority() {
                return priority != null ? priority : Priority.NORMAL;
            }
        };

        json_request.setRetryPolicy(new DefaultRetryPolicy(TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyRequestQueue.getInstance(this.context_weak_reference).add_request_queue(json_request);
    }


    public void Execute(final VolleyStringCallback volley_string_callback){

      //  Log.d(TAG, "To URL: " + url.toString());
        // Log.d(TAG, "Parsing Network Response");
        //  Log.d(TAG, "Soft Time: " + soft_expire);
        //  Log.d(TAG, "Hard Time: " + ttl);
        //  Log.d(TAG, "Server Date: " + cache_entry.serverDate);
        // Log.d(TAG, "[String] Cached Data: " + data);
        // Log.d(TAG, "[StringObject] Volley State: " + tag);
        StringRequest string_request = new StringRequest(this.http_method, url, volley_string_callback::network_response, volley_string_callback::network_error) {

            @Override
            public Map<String, String> getHeaders() {

                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", UserSettings.get_user_token(context_weak_reference.get()));

                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {

                // Log.d(TAG, "Parsing Network Response");

                Cache.Entry cache_entry = HttpHeaderParser.parseCacheHeaders(response);
                if (cache_entry == null)
                    cache_entry = new Cache.Entry();

                long cache_hit_but_refreshed = 0;
                long cache_expired = 0;


                String header_value = response.headers.get("Cache-Control");
                if (header_value != null) {

                    cache_expired = Long.parseLong(header_value.substring(8));
                    cache_hit_but_refreshed = cache_expired - 82800000;

                }


                long now = System.currentTimeMillis();
                final long soft_expire = now + cache_hit_but_refreshed;
                final long ttl = now + cache_expired;

                //  Log.d(TAG, "Soft Time: " + soft_expire);
                //  Log.d(TAG, "Hard Time: " + ttl);

                cache_entry.data = response.data;
                cache_entry.softTtl = soft_expire;
                cache_entry.ttl = ttl;

                header_value = response.headers.get("Date");
                if (header_value != null) {

                    cache_entry.serverDate = HttpHeaderParser.parseDateAsEpoch(header_value);
                    //  Log.d(TAG, "Server Date: " + cache_entry.serverDate);
                }

                header_value = response.headers.get("Last-Modified");

                if (header_value != null) {

                    cache_entry.lastModified = HttpHeaderParser.parseDateAsEpoch(header_value);
                }

                cache_entry.responseHeaders = response.headers;

                String data = null;
                try {

                    data = new String(cache_entry.data, HttpHeaderParser.parseCharset(response.headers));
                    // Log.d(TAG, "[String] Cached Data: " + data);

                    Calendar calender = Calendar.getInstance();
                    if (get_minute_difference(cache_entry.serverDate, calender.getTimeInMillis()) >= 30) {
                        Log.d(TAG, "30 Min has passed. Invalidate cache");
                        VolleyRequestQueue.getInstance(context_weak_reference).get_request_queue().getCache().invalidate(url, true);
                    }

                    if (get_minute_difference(cache_entry.serverDate, calender.getTimeInMillis()) >= 60) {

                        Log.d(TAG, "60 Min has passed. Delete cache");
                        VolleyRequestQueue.getInstance(context_weak_reference).get_request_queue().getCache().clear();

                    }


                } catch (UnsupportedEncodingException error) {
                    error.printStackTrace();
                }

                return Response.success(data, cache_entry);
            }

            @Override
            public void addMarker(String tag) {
                super.addMarker(tag);

                // Log.d(TAG, "[StringObject] Volley State: " + tag);

            }
        };

        string_request.setRetryPolicy(new DefaultRetryPolicy(TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyRequestQueue.getInstance(this.context_weak_reference).add_request_queue(string_request);
    }

    public void Execute(final VolleyArrayCallback volley_array_callback){

        Log.d(TAG, "Executing network call... " + this.json_array.toString());
        Log.d(TAG, "To URL: " + this.url);

        JsonArrayRequest json_array_request = new JsonArrayRequest(this.http_method, this.url, json_array, volley_array_callback::network_response, volley_array_callback::network_error) {

            @Override
            public Map<String, String> getHeaders() {

                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", UserSettings.get_user_token(context_weak_reference.get()));

                return params;
            }

            @Override
            public void addMarker(String tag) {
                super.addMarker(tag);

                Log.d(TAG, "[JsonArray] Volley State: " + tag);

            }
        };

        json_array_request.setRetryPolicy(new DefaultRetryPolicy(TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyRequestQueue.getInstance(this.context_weak_reference).add_request_queue(json_array_request);

    }

    private static long get_minute_difference(long time_start, long time_stop){

        long difference_milliseconds = time_stop - time_start;

        return difference_milliseconds / (60 * 1000);
    }


}
