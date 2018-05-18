package com.example.android.popularmovies;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

class MovieUtils {


   // Variable for logging messages
   private static final String LOG_TAG = MovieUtils.class.getSimpleName();
    private static final String HTTP_REQUEST ="GET";
    private static final int HTTP_CONNECTION_TIMEOUT=10000;
    private static final int HTTP_READ_TIMEOUT=15000;
    private static final int HTTP_SUCCESS_RESPONSE_CODE=200;



   //  Method to make an HTTP request and get a json string
    public static String httpRequest(URL url) throws IOException {

        String jsonString = "";
        //if url is null, return
        if (url == null) {
            return jsonString;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(HTTP_CONNECTION_TIMEOUT /* milliseconds */);
            urlConnection.setConnectTimeout(HTTP_READ_TIMEOUT /* milliseconds */);
            urlConnection.setRequestMethod(HTTP_REQUEST);
            urlConnection.connect();

            // If response code 200 then, then the request is successful and we can read the stream else log error message
            if (urlConnection.getResponseCode() == HTTP_SUCCESS_RESPONSE_CODE) {
                inputStream = urlConnection.getInputStream();
                jsonString = readStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error! response code: " + urlConnection.getResponseCode());
            }
        }
        catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving Movie Information", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonString;
    }


    // Read data from the inputStream and create the json String
    private static String readStream(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                line = reader.readLine();
            }
        }
        return stringBuilder.toString();
    }
}
