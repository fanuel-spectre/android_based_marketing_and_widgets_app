package com.example.lada.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.lada.R;
import com.example.lada.adapters.RecyclerAdapter;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.zfdang.multiple_images_selector.ImagesSelectorActivity;
import com.zfdang.multiple_images_selector.SelectorSettings;

import java.util.ArrayList;

public class DoodleActivity extends AppCompatActivity {

    ChipNavigationBar chipNavigationBar;
    RecyclerView recyclerView;
    TextView textView;
    Button pick;
    ImageView img1, img2, img3;

    ArrayList<Uri> uri = new ArrayList<>();
    RecyclerAdapter adapter;

    private static final int REQUEST_CODE = 123;
    private ArrayList<String> mResults = new ArrayList<>();
    ArrayList<Uri> ImageList = new ArrayList<Uri>();
    private Uri image_uri;

    private static final int Read_Permission = 101;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doodle);
        Fresco.initialize(getApplicationContext());

        textView = findViewById(R.id.total);
        recyclerView = findViewById(R.id.recyclerView);
        pick = findViewById(R.id.link);
        img1 = findViewById(R.id.image1);
        img2 = findViewById(R.id.image2);
        img3 = findViewById(R.id.image3);

        adapter = new RecyclerAdapter(uri);
        recyclerView.setLayoutManager(new GridLayoutManager(DoodleActivity.this, 4));
        recyclerView.setAdapter(adapter);

        if (ContextCompat.checkSelfPermission(DoodleActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(DoodleActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},Read_Permission);
        }

        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // start multiple photos selector
                Intent intent = new Intent(DoodleActivity.this, ImagesSelectorActivity.class);
// max number of images to be selected
                intent.putExtra(SelectorSettings.SELECTOR_MAX_IMAGE_NUMBER, 5);

// min size of image which will be shown; to filter tiny images (mainly icons)
                intent.putExtra(SelectorSettings.SELECTOR_MIN_IMAGE_SIZE, 100000);
// show camera or not
                intent.putExtra(SelectorSettings.SELECTOR_SHOW_CAMERA, true);
// pass current selected images as the initial value
                intent.putStringArrayListExtra(SelectorSettings.SELECTOR_INITIAL_SELECTED_LIST, mResults);
// start the selector
                startActivityForResult(intent, REQUEST_CODE);
               intent.setAction(Intent.ACTION_GET_CONTENT);

            }
        });


//        chipNavigationBar = findViewById(R.id.bottom_menu);
//        bottomMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // get selected images from selector
        if(requestCode == REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                mResults = data.getStringArrayListExtra(SelectorSettings.SELECTOR_RESULTS);
                assert mResults != null;
                ClipData clipData = data.getClipData();

                if (clipData !=null){
                    int countClipData = data.getClipData().getItemCount();

                    img1.setImageURI(clipData.getItemAt(0).getUri());
                    img2.setImageURI(clipData.getItemAt(1).getUri());
                    img3.setImageURI(clipData.getItemAt(2).getUri());


                    int currentImageSelect = 0;
                    while (currentImageSelect < countClipData){
                        image_uri = data.getClipData().getItemAt(currentImageSelect).getUri();

                        ImageList.add(image_uri);
                        currentImageSelect = currentImageSelect +1;
                    }

                }


                // show results in textview
                StringBuffer sb = new StringBuffer();
                sb.append(String.format("Totally %d images selected:", mResults.size())).append("\n");
                for(String result : mResults) {
                    sb.append(result).append("\n");
                }
                textView.setText(sb.toString());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
//    private void bottomMenu() {
//
//        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(int i) {
//                Activity activity = null;
//                switch (i){
//                    case R.id.bottom_nav_home:
//                        startActivity(new Intent(DoodleActivity.this, ForgotPasswordActivity.class));
//                        break;
//
//                    case R.id.bottom_nav_add:
//                        activity = new LoginActivity();
//                        break;
//
//                    case R.id.bottom_nav_settings:
//                        activity = new SellerEditProfileActivity();
//                        break;
//
//                }
//            }
//        });
//
//    }
}