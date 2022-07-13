package com.example.lada.activities;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.lada.R;

import androidx.appcompat.app.AppCompatActivity;
import meow.bottomnavigation.MeowBottomNavigation;

public class SampleActivity extends AppCompatActivity {

    ImageView img1, img2;


    private final int CODE_IMG_GALLERY = 1;
    private final int ID_HOME = 3;
    private final int ID_MESSAGE = 4;
    private final int ID_NOTIFICATION = 5;
    private final int ID_ACCOUNT = 6;
    private final int CODE_MULTIPLE_IMG_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MeowBottomNavigation bottomNavigation = findViewById(R.id.bottom_navigation);

        bottomNavigation.add(new MeowBottomNavigation.Model(ID_HOME,R.drawable.ic_city));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_MESSAGE,R.drawable.ic_mail));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_NOTIFICATION,R.drawable.ic_notifications));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_ACCOUNT,R.drawable.ic_person));
        img1 = findViewById(R.id.image1);
        img2 = findViewById(R.id.image2);



        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(Intent.createChooser(new Intent().setAction(Intent.ACTION_GET_CONTENT).setType("image/*"), "Select Image"), CODE_IMG_GALLERY);
            }
        });

        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select images"), CODE_MULTIPLE_IMG_GALLERY);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODE_IMG_GALLERY && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            if (imageUri !=null){
                img1.setImageURI(imageUri);
            }

        }else if (requestCode == CODE_MULTIPLE_IMG_GALLERY && resultCode == RESULT_OK){
            ClipData clipData = data.getClipData();

            if (clipData !=null){
                img1.setImageURI(clipData.getItemAt(0).getUri());
                img2.setImageURI(clipData.getItemAt(1).getUri());

                for (int i =0; i < clipData.getItemCount(); i++){
                    ClipData.Item item = clipData.getItemAt(i);
                    Uri uri = item.getUri();
                    Log.e("Images :", uri.toString());
                }
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
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
