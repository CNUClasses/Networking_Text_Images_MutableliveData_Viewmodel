package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DataVM extends ViewModel {

    //gotta define this somewhere
    String links[] = {  "https://www.pcs.cnu.edu/~kperkins/pets/p33.png",
            "https://www.pcs.cnu.edu/~kperkins/pets/p44.png",
            "https://www.pcs.cnu.edu/~kperkins/pets/p55.png",
            "https://www.pcs.cnu.edu/~kperkins/pets/p22.png"};

    String jsonlink="https://www.pcs.cnu.edu/~kperkins/pets/pets.json";

    //this is the value that we want to keep track of through rotations
    int currentLink=0;

    GetImageThread myGetImageThread;
    GetTextThread myGetTextThread;

    //lets add some livedata
    //the bitmap we are looking for
    private MutableLiveData<Bitmap> bmp ;
    public MutableLiveData<Bitmap> getbmp() {
        if(bmp==null)
            bmp=new MutableLiveData<Bitmap>();
        return bmp;
    }

    //any communications from thread
    private MutableLiveData<String> result ;
    public MutableLiveData<String> getresult() {
        if(result==null)
            result=new MutableLiveData<String>();
        return result;
    }

    public void getJSON(){
        myGetTextThread=new GetTextThread(jsonlink);
        myGetTextThread.start();
    }
    public void getImage(String url){
        myGetImageThread=new GetImageThread(url);
        myGetImageThread.start();
    }

    public class GetTextThread extends Thread{
        private static final String     TAG = "GetTextThread";
        private static final int        DEFAULTBUFFERSIZE = 8096;
        private static final int        TIMEOUT = 1000;    // 1 second
        protected int                   statusCode = 0;
        private String                  url;

        public GetTextThread(String url) {
            this.url=url;
        }

        public void run() {
            try {
                URL url1 = new URL(url);

                // this does no network IO
                HttpURLConnection connection = (HttpURLConnection) url1.openConnection();

                // can further configure connection before getting data
                // cannot do this after connected
                connection.setRequestMethod("GET");
                connection.setReadTimeout(TIMEOUT);
                connection.setConnectTimeout(TIMEOUT);
                connection.setRequestProperty("Accept-Charset", "UTF-8");

                // wrap in finally so that stream bis is sure to close
                // and we disconnect the HttpURLConnection
                BufferedReader in = null;
                try {

                    // this opens a connection, then sends GET & headers
                    connection.connect();

                    // lets see what we got make sure its one of
                    // the 200 codes (there can be 100 of them
                    // http_status / 100 != 2 does integer div any 200 code will = 2
                    statusCode = connection.getResponseCode();
                    if (statusCode / 100 != 2) {
                        result.postValue("Failed! Statuscode returned is " + Integer.toString(statusCode));
                        return;
                    }

                    in = new BufferedReader(new InputStreamReader(connection.getInputStream()), DEFAULTBUFFERSIZE);

                    // the following buffer will grow as needed
                    String myData;
                    StringBuffer sb = new StringBuffer();

                    while ((myData = in.readLine()) != null) {
                        sb.append(myData);
                    }
                    result.postValue(sb.toString());
                } finally {
                    // close resource no matter what exception occurs
                    if(in != null)
                        in.close();
                    connection.disconnect();
                }
            } catch (Exception exc) {
                Log.d(TAG, exc.toString());
                result.postValue(exc.toString());
            }
        }

    }
    public class GetImageThread extends Thread{
        private static final String TAG = "GetImageThread";
        private static final int        DEFAULTBUFFERSIZE = 50;
        private static final int        NODATA = -1;
        private int                     statusCode=0;
        private String                  url;

        public GetImageThread(String url) {
            this.url=url;
        }

        public void run(){
            // note streams are left willy-nilly here because it declutters the
            // example
            try {
                URL url1 = new URL(url);

                // this does no network IO
                HttpURLConnection connection = (HttpURLConnection) url1.openConnection();

                // can further configure connection before getting data
                // cannot do this after connected
                // connection.setRequestMethod("GET");
                // connection.setReadTimeout(timeoutMillis);
                // connection.setConnectTimeout(timeoutMillis);

                // this opens a connection, then sends GET & headers
                connection.connect();

                // lets see what we got make sure its one of
                // the 200 codes (there can be 100 of them
                // http_status / 100 != 2 does integer div any 200 code will = 2
                int statusCode = connection.getResponseCode();

                if (statusCode / 100 != 2) {
                    result.postValue("Failed! Statuscode returned is " + Integer.toString(statusCode));
                    return;
                }

                // get our streams, a more concise implementation is
                // BufferedInputStream bis = new
                // BufferedInputStream(connection.getInputStream());
                InputStream is = connection.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);

                // the following buffer will grow as needed
                ByteArrayOutputStream baf = new ByteArrayOutputStream(DEFAULTBUFFERSIZE);
                int current = 0;

                // wrap in finally so that stream bis is sure to close
                try {
                    while ((current = bis.read()) != NODATA) {
                        baf.write((byte) current);
                    }

                    // convert to a bitmap
                    byte[] imageData = baf.toByteArray();
                    //some live data here
                    //can only postValue from background thread not setValue
                    bmp.postValue(BitmapFactory.decodeByteArray(imageData, 0, imageData.length));
                    result.postValue(url);

                } finally {
                    // close resource no matter what exception occurs
                    if(bis!= null)
                        bis.close();
                }
            } catch (Exception exc) {
                Log.d(TAG, exc.toString());
                result.postValue(exc.toString());
            }
        }

    }

}
