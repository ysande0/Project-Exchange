package com.syncadapters.czar.exchange.http;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.lang.ref.WeakReference;

public class VolleyRequestQueue {

    private final WeakReference<Context> context_weak_reference;
    private static VolleyRequestQueue volley_request_queue; // <--- 1) Could be the problem
    private RequestQueue request_queue; // 2

    private VolleyRequestQueue(Context context){

        this.context_weak_reference = new WeakReference<>(context.getApplicationContext());
        request_queue = get_request_queue();
    }


    public static synchronized VolleyRequestQueue getInstance(WeakReference<Context> context_weak_reference){


        if(volley_request_queue == null){

            volley_request_queue = new VolleyRequestQueue(context_weak_reference.get().getApplicationContext());
        }

        return volley_request_queue;
    }

    public RequestQueue get_request_queue(){

        if(request_queue == null)
            request_queue =  Volley.newRequestQueue(this.context_weak_reference.get().getApplicationContext());

        return request_queue;
    }

    public <T> void add_request_queue(Request<T> request_queue){

       get_request_queue().add(request_queue);
    }

}
