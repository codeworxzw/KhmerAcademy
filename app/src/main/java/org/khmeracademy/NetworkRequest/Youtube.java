package org.khmeracademy.NetworkRequest;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.khmeracademy.CallBack.GetYoutubeDurationListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by PC1 on 1/25/2016.
 */
public class Youtube {
    public static void getYoutubeDuration(final String video_id, final GetYoutubeDurationListener callback){
        final String[] duration = {""};
        final String[] time = {""};
        String url = "https://www.googleapis.com/youtube/v3/videos?id=" + video_id + "&key=AIzaSyDYwPzLevXauI-kTSVXTLroLyHEONuF9Rw&part=snippet,contentDetails" ;
        JsonObjectRequest jObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONArray items = response.getJSONArray("items");
                            JSONObject contentDetails;
                            for (int i = 0; i < items.length(); i++) {
                                contentDetails = items.getJSONObject(i).getJSONObject("contentDetails");
                                duration[0] = contentDetails.getString("duration");
                            }

                            // Video Duration Formation H:M:S
                            Calendar c = new GregorianCalendar();
                            try {
                                DateFormat df = new SimpleDateFormat("'PT'mm'M'ss'S'");
                                Date d = df.parse(duration[0]);
                                c.setTime(d);
                            } catch (ParseException e) {
                                try {
                                    DateFormat df = new SimpleDateFormat("'PT'hh'H'mm'M'ss'S'");
                                    Date d = df.parse(duration[0]);
                                    c.setTime(d);
                                } catch (ParseException e1) {
                                    try {
                                        DateFormat df = new SimpleDateFormat("'PT'ss'S'");
                                        Date d = df.parse(duration[0]);
                                        c.setTime(d);
                                    } catch (ParseException e2) {
                                    }
                                }
                            }
                            c.setTimeZone(TimeZone.getDefault());


                            // test hour
                            if ( c.get(Calendar.HOUR) > 0 ) {
                                if ( String.valueOf(c.get(Calendar.HOUR)).length() == 1 ) {
                                    time[0] += "0" + c.get(Calendar.HOUR);
                                }
                                else {
                                    time[0] += c.get(Calendar.HOUR);
                                }
                                time[0] += ":";
                            }

                            // test minute
                            if ( String.valueOf(c.get(Calendar.MINUTE)).length() == 1 ) {
                                time[0] += "0" + c.get(Calendar.MINUTE);
                            }
                            else {
                                time[0] += c.get(Calendar.MINUTE);
                            }
                            time[0] += ":";
                            // test second

                            if ( String.valueOf(c.get(Calendar.SECOND)).length() == 1 ) {
                                time[0] += "0" + c.get(Calendar.SECOND);
                            }
                            else {
                                time[0] += c.get(Calendar.SECOND) - 1;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }finally {
                            callback.onSuccess(time[0]);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }
        };
        VolleySingleton.getsInstance().addToRequestQueue(jObjectRequest);
    }
}


