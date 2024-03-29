package org.khmeracademy.NetworkRequest;

/**
 * Created by PC1 on 1/18/2016.
 */

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;

public class GsonObjectRequest extends JsonObjectRequest {

    public GsonObjectRequest(int method, String url, JSONObject params, Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        super(method, url, params, reponseListener,errorListener);
    }

    public GsonObjectRequest(int method, String url, Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        super(method, url, reponseListener,errorListener);
    }

    // Header for API Access
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String,String> header = new HashMap<>();
        header.put(API.API_KEY, API.API_CODE);
        return header;
    }

    // Increase Time Out Duration
    @Override
    public RetryPolicy getRetryPolicy() {
        //return new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        return new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }
}