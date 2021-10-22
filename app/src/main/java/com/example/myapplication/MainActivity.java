package com.example.myapplication;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ui.AppBarConfiguration;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    //persists accross config changes
    DataVM myVM;
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv=findViewById(R.id.imageView1);
        setSupportActionBar(findViewById(R.id.toolbar));

        // Create a ViewModel the first time the system calls an activity's
        // onCreate() method.  Re-created activities receive the same
        // MyViewModel instance created by the first activity.
        myVM = new ViewModelProvider(this).get(DataVM.class);

        // Create the observer which updates the UI.
        final Observer<Bitmap> bmpObserver = new Observer<Bitmap>() {
            @Override
            public void onChanged(@Nullable final Bitmap newbmp) {
                // Update the UI, in this case, a TextView.
                iv.setImageBitmap(newbmp);
             }
        };
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        myVM.getbmp().observe(this,bmpObserver);

        // Create the observer which updates the UI.
        final Observer<String> resultObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String result) {
                // Update the UI, in this case, a TextView.
                Toast.makeText(MainActivity.this,result,Toast.LENGTH_SHORT).show();
            }
        };
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        myVM.getresult().observe(this,resultObserver);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url=myVM.links[myVM.currentLink++%myVM.links.length];
                myVM.getImage(url);
            }
        });

        findViewById(R.id.fabgetjson).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myVM.getJSON();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}