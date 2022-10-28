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

    public void getJSON(String url){
        //run the task
        GetTextThread myThread = new GetTextThread(url);
        myThread.start();
    }

    public void getImage(String url){
        //run the task
        GetImageThread myThread = new GetImageThread(url);
        myThread.start();
    }

    public class GetTextThread extends Thread{
        private String  url;
        public GetTextThread(String url) {
            this.url=url;
        }
        public void run() {
            //run the task
            Download_https mytask = new Download_https(this.url);
            result.postValue(mytask.get_text());
        }
    }
    public class GetImageThread extends Thread {
        private String url;
        public GetImageThread(String url) {
            this.url = url;
        }

        public void run() {
            //run the task
            Download_https mytask = new Download_https(this.url);
            bmp.postValue(mytask.get_Bitmap());
        }
    }
}
