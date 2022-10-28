package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Download_https {
    /**
     * usage ex- text
     * String url="https://somewhere/something.json"
     * Download_https myhttps = new Download_https(url);
     * String res = myhttps.get_text();
     *
     * usage ex- Bitmap
     * String url="https://somewhere/something.png"
     * Download_https myhttps = new Download_https(url);
     * Bitmap bmp = myhttps.get_Bitmap();
     */
    private static final String     TAG = "Download_https";
    private static final int        TIMEOUT = 1000;    // 1 second
    public static final int         BUFF_RD_SZ = 8096;

    protected int                   statusCode = 0;
    private static final int        DEFAULTBUFFERSIZE = 50;
    private static final int        NODATA = -1;

    private String                  myURL;  //https query string
    private HttpURLConnection       connection;

    /***
     * constructor
     * @param myURL  https url to connect to
     */
    public Download_https(String myURL) {
        this.myURL = myURL;
    }

    /**
     * @return see what http connection status is
     *         after call to get_text or get_Bitmap
     */
    public int getStatusCode(){
        return statusCode;
    }
    public String getMyURL() {
        return myURL;
    }

    /***
     * established http connection to myURL
     * @return  HttpURLConnection if connection succeeds (statusCode in 200s)
     *          otherwise null
     */
    private HttpURLConnection connect() {
        HttpURLConnection connection =null;
        try {
            URL url= new URL(myURL);
            // this does no network IO
            connection = (HttpURLConnection) url.openConnection();

         // can further configure connection before getting data
        // cannot do this after connected
//        connection.setRequestMethod("GET");
//        connection.setReadTimeout(TIMEOUT);
//        connection.setConnectTimeout(TIMEOUT);
//        connection.setRequestProperty("Accept-Charset", "UTF-8");

            // this opens a connection, then sends GET & headers
            connection.connect();

            //this gets the status of the open connection
            statusCode = connection.getResponseCode();
        } catch (MalformedURLException e) {
            Log.e(TAG, "doInBackground: " + e.toString());
            return null;
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: " + e.toString());
            return null;
        }

        // lets see what we got make sure its one of
        // the 200 codes (there can be 100 of them
        // http_status / 100 != 2 does integer div any 200 code will = 2
        if (statusCode / 100 != 2) {
            Log.e(TAG, "Error-connection.getResponseCode returned "
                    + Integer.toString(statusCode));
            return null;
        }
        return connection;
    }

    public String get_text() {
        HttpURLConnection connection = connect();
        if(connection == null)
            return null;

        String result=null;
        // wrap in finally so that stream bis is sure to close
        // and we disconnect the HttpURLConnection
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()), BUFF_RD_SZ);

            // the following buffer will grow as needed
            String myData;
            StringBuffer sb = new StringBuffer();

            while ((myData = in.readLine()) != null) {
                sb.append(myData);
            }
            result = sb.toString();

        } catch (IOException e) {
            Log.e(TAG, "doInBackground: " + e.toString());
            result = null;
        } finally {
            // close resource no matter what exception occurs
            try {
                in.close();
            } catch (IOException e) {
                Log.e(TAG, "doInBackground: " + e.toString());
            }
            connection.disconnect();
        }
        return result;
    }

    public Bitmap get_Bitmap() {
        HttpURLConnection connection = connect();
        if(connection == null)
            return null;
        Bitmap result=null;
        BufferedInputStream bis=null;
        try {
            // get our streams, a more concise implementation is
            // BufferedInputStream bis = new
            // BufferedInputStream(connection.getInputStream());
            InputStream is = connection.getInputStream();
            bis = new BufferedInputStream(is);

            // the following buffer will grow as needed
            ByteArrayOutputStream baf = new ByteArrayOutputStream(DEFAULTBUFFERSIZE);
            int current = 0;

            while ((current = bis.read()) != NODATA) {
                baf.write((byte) current);
            }

            // convert to a bitmap
            byte[] imageData = baf.toByteArray();
            result= BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: " + e.toString());
            result= null;
        }finally {
            // close resource no matter what exception occurs
            try {
                bis.close();
            } catch (IOException e) {
                Log.e(TAG, "get_Bitmap: " + e.toString());
            }
        }
        return result;
    }
//    /**
//     * @param name
//     * @param value
//     * @return this allows you to build a safe URL with all spaces and illegal
//     *         characters URLEncoded usage mytask.setnameValuePair("param1",
//     *         "value1").setnameValuePair("param2",
//     *         "value2").setnameValuePair("param3", "value3")....
//     */
//    public Download_https setnameValuePair(String name, String value) {
//        try {
//            if (name.length() != 0 && value.length() != 0) {
//
//                // if 1st pair that include ? otherwise use the joiner char &
//                if (myQuery.length() == 0)
//                    myQuery += "?";
//                else
//                    myQuery += "&";
//
//                myQuery += name + "=" + URLEncoder.encode(value, "utf-8");
//            }
//        } catch (UnsupportedEncodingException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return this;
//    }
}
