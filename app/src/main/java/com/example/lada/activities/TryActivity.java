package com.example.lada.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.lada.R;
import com.example.lada.adapters.RecyclerAdapter;
import com.zhihu.matisse.Matisse;

import java.util.ArrayList;

public class TryActivity extends AppCompatActivity implements View.OnClickListener{

    ArrayList<Uri> uri = new ArrayList<>();
    RecyclerAdapter adapter;
    RecyclerView recyclerView;
    ImageView imageView;
    ImageButton addProductBtn;
    TextView photoNo;
    private static final int READ_STORAGE_PERMISSION = 4000;
    private static final int LIMIT = 5;
    private static final int REQUEST_CODE_CHOOSE = 23;

    private static final int Read_Permission = 101;
    static final int OPEN_MEDIA_PICKER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_try);

        recyclerView = findViewById(R.id.recyclerView);
        addProductBtn = findViewById(R.id.add_pic_btna);
        photoNo = findViewById(R.id.add_photo_info);
        imageView = findViewById(R.id.image_view);

        adapter = new RecyclerAdapter(uri);
        recyclerView.setLayoutManager(new GridLayoutManager(TryActivity.this, 3));
        recyclerView.setAdapter(adapter);

        if (ContextCompat.checkSelfPermission(TryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(TryActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_CHOOSE);
        }


//        List<SlideModel> slideModels = new ArrayList<>();
//        slideModels.add(new SlideModel(R.drawable.ecommerce,"1"));
//        slideModels.add(new SlideModel(R.drawable.ic_mail,"2"));
//        slideModels.add(new SlideModel(R.drawable.ic_city,"3"));
//        slideModels.add(new SlideModel(R.drawable.ic_person,"4"));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            Log.e("OnActivityResult ", String.valueOf(Matisse.obtainOriginalState(data)));
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void onClick(final View v) {

    }

//    private void startAction(View v) {
//        switch (v.getId()) {
//            case R.id.add_pic_btn:
//                Matisse.from(TryActivity.this)
//                        .choose(MimeType.ofImage(), false)
//                        .countable(true)
//                        .capture(true)
//                        .captureStrategy(
//                                new CaptureStrategy(true, "com.zhihu.matisse.sample.fileprovider", "test"))
//                        .maxSelectable(9)
//                        .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
//                        .gridExpectedSize(
//                                getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
//                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
//                        .thumbnailScale(0.85f)
//                        .imageEngine(new GlideEngine())
//                        .setOnSelectedListener((uriList, pathList) -> {
//                            Log.e("onSelected", "onSelected: pathList=" + pathList);
//                        })
//                        .showSingleMediaType(true)
//                        .originalEnable(true)
//                        .maxOriginalSize(10)
//                        .autoHideToolbarOnSingleTap(true)
//                        .setOnCheckedListener(isChecked -> {
//                            Log.e("isChecked", "onCheck: isChecked=" + isChecked);
//                        })
//                        .forResult(REQUEST_CODE_CHOOSE);
//                break;
//            default:
//                break;
//        }
//    }
}