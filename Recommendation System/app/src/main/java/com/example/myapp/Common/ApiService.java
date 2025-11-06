package com.example.myapp.Common;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ApiService {

    private static final String TAG = ApiService.class.getSimpleName();

    // Define the base URL
    private static final String AI_BASE_URL = "http://192.168.1.35:3000";
    public static String AI_URL_PREDICT = AI_BASE_URL+"/predict";
    public static String AI_URL_PREDICT_AGAIN = AI_BASE_URL+"/predict_again";

    private static final String WEATHER_BASE_URL = "http://api.openweathermap.org/data/2.5/weather";
    private static final String WEATHER_API_KEY = "e7704bc895b4a8d2dfd4a29d404285b6"; // Replace with your actual API key


    private final Context context;
    private final RequestQueue requestQueue;

    public ApiService(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public void postAIUrl(JSONObject requestBody, final ApiCallback callback) {
        Log.d(TAG, requestBody.toString());

        String jsonStringMale = "{ \"data\": [ { \"label\": \"Top\", \"value\": \"Lorem Ippsum\" }, { \"label\": \"Bottom\", \"value\": \"Lorem Ippsum\" }, { \"label\": \"Shoes\", \"value\": \"Lorem Ippsum\" }, { \"label\": \"Accessories\", \"value\": \"Lorem Ippsum\" } ] }";
        String jsonStringFemale = "{ \"data\": [ { \"label\": \"Top\", \"value\": \"Lorem Ippsum\" }, { \"label\": \"Bottom\", \"value\": \"Lorem Ippsum\" }, { \"label\": \"Duppata Fabric\", \"value\": \"Lorem Ippsum\" }, { \"label\": \"Shoes\", \"value\": \"Lorem Ippsum\" }, { \"label\": \"Accessories\", \"value\": \"Lorem Ippsum\" } ] }";



        new Handler().postDelayed(() -> {
            // Assume we fetched the user data successfully
            try {

                JSONObject response = new JSONObject(jsonStringFemale);
                callback.onSuccess(response);
            } catch (JSONException e) {
                callback.onError(e.toString());
            }
        }, 4000); // Simulated delay of 4 seconds

    }

    public void postData(String url, JSONObject requestBody, final ApiCallback callback) {
        Log.d(TAG, url);
        Log.d(TAG, "here"+requestBody.toString());
        // Convert JSONObject to Map<String, String>
        final Map<String, String> formData = new HashMap<>();
        try {
            Iterator<String> keys = requestBody.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = requestBody.getString(key);
                formData.put(key, value);
            }
        } catch (JSONException e) {
            callback.onError(e.toString());
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the response
                        Log.d(TAG, "response : "+response);
//                        JSONObject jsonResponse = null;
                        try {
                            String mockResponse = "{\"data\": [{\"label\": \"Top\", \"value\": \"kurta\"}, {\"label\": \"Top Fabric\", \"value\": \"organza\"}, {\"label\": \"Top Color\", \"value\": \"grey\"}, {\"label\": \"Top Decor\", \"value\": \"embroidery , sequins\"}, {\"label\": \"Bottom\", \"value\": \"wide-leg trouser\"}, {\"label\": \"Bottom Fabric\", \"value\": \"satin\"}, {\"label\": \"Bottom Color\", \"value\": \"grey\"}, {\"label\": \"Bottom Decor\", \"value\": \"plain\"}, {\"label\": \"Dupatta Fabric\", \"value\": \"organza\"}, {\"label\": \"Dupatta Color\", \"value\": \"grey\"}, {\"label\": \"Dupatta Decor\", \"value\": \"fringed lace\"}, {\"label\": \"Hijab Color\", \"value\": \"grey\"}, {\"label\": \"Shoes\", \"value\": \"heels\"}, {\"label\": \"Shoes Color\", \"value\": \"silver\"}, {\"label\": \"Accessories\", \"value\": \"clutch\"}]}";
                            JSONObject jsonResponse = new JSONObject(response);

//                            JSONObject js = new JSONObject(response);
                            callback.onSuccess(jsonResponse);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error
                        callback.onError(error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                return formData;
            }
        };

        // Add the request to the queue
        request.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(request);
    }

    public void getTodayTemperature(String city, final ApiCallback callback) {
        String url = String.format("%s?q=%s&appid=%s", WEATHER_BASE_URL, city, WEATHER_API_KEY);
        Log.d(TAG,"API_URL : "+url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject mainObject = response.getJSONObject("main");
                            double temperatureKelvin = mainObject.getDouble("temp");
                            double temperatureCelsius = temperatureKelvin - 273.15; // Convert to Celsius

                            callback.onSuccess(response);
                        } catch (JSONException e) {
                            callback.onError("Error parsing JSON");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG,error.toString());
                        callback.onError(error.toString());
                    }
                });

        // Add the request to the queue
        requestQueue.add(request);
    }

    // Callback interface to handle API response
    public interface ApiCallback {
        void onSuccess(JSONObject response) throws JSONException;

        void onError(String errorMessage);
    }
}
