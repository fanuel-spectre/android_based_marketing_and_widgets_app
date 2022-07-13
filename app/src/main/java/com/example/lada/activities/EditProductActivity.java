package com.example.lada.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lada.Constants;
import com.example.lada.R;
import com.example.lada.adapters.RecyclerAdapter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class EditProductActivity extends AppCompatActivity {

    private String productId;
    ArrayList<Uri> ImageList = new ArrayList<Uri>();
    RecyclerAdapter adapter;
    RecyclerView recyclerView;
    ImageButton backBtn,addProductPhotoBtn, addMultipleProductPhoto, picCancel1, picCancel2, picCancel3;
    TextInputLayout titleProduct, proBrand, proType, proGender, proColor, proDescription, proCondition, discountPrice, discountRate, adderName, adderPhone;
    EditText categoriesProduct, productPrice;
    TextView photoNo, multiplePhotoNo;
    AppCompatButton updateProductBtn;
    ImageView img1, img2, img3;
    RelativeLayout relativeImg1, relativeImg2, relativeImg3;
    SwitchCompat discountSwitch, negotiableSwitch;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private Uri image_uri;
    private Uri image_Uri;
    private String myUri = "";
    private StorageTask uploadTask;
    private int upload_count = 0;

    private final int CODE_IMG_GALLERY = 1;
    private final int CODE_MULTIPLE_IMG_GALLERY = 2;
    private static final int Read_Permission = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_edit_product);

        //get id of the product from intent
        productId = getIntent().getStringExtra("productId");

        addProductPhotoBtn = findViewById(R.id.add_pic_btn);
        addMultipleProductPhoto = findViewById(R.id.add_multiple_pic_btn);
        photoNo = findViewById(R.id.add_photo_info);
        multiplePhotoNo = findViewById(R.id.add_multiple_photo_info);
        backBtn = findViewById(R.id.back_btn);
        categoriesProduct = findViewById(R.id.categories);
        img1 = findViewById(R.id.image1);
        img2 = findViewById(R.id.image2);
        img3 = findViewById(R.id.image3);
        relativeImg1 = findViewById(R.id.img1_relative);
        relativeImg2 = findViewById(R.id.img2_relative);
        relativeImg3 = findViewById(R.id.img3_relative);
        picCancel1 = findViewById(R.id.cancel_add_photo);
        picCancel2 = findViewById(R.id.cancel_add_photo2);
        picCancel3 = findViewById(R.id.cancel_add_photo3);
        titleProduct = findViewById(R.id.title);
        proBrand = findViewById(R.id.brand);
        proType = findViewById(R.id.type);
        proGender = findViewById(R.id.gender);
        proColor = findViewById(R.id.color);
        proDescription = findViewById(R.id.description);
        proCondition = findViewById(R.id.condition);
        discountPrice = findViewById(R.id.Discount_price);
        productPrice = findViewById(R.id.price);
        discountRate = findViewById(R.id.Discount_note);
        adderName = findViewById(R.id.adder_name);
        adderPhone = findViewById(R.id.adder_phone);
        discountSwitch = findViewById(R.id.discount_switch);
        negotiableSwitch = findViewById(R.id.negotiation_switch);
        updateProductBtn = findViewById(R.id.update_product_btn);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("pleas wait");
        progressDialog.setCanceledOnTouchOutside(false);

        loadProductsDetails();

        if (ContextCompat.checkSelfPermission(EditProductActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(EditProductActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},Read_Permission);
        }




        discountSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){

                    discountPrice.setVisibility(View.VISIBLE);
                    discountRate.setVisibility(View.VISIBLE);

                }
                else{

                    discountPrice.setVisibility(View.GONE);
                    discountRate.setVisibility(View.GONE);
                }
            }
        });
        relativeImg1.setVisibility(View.GONE);
        relativeImg2.setVisibility(View.GONE);
        relativeImg3.setVisibility(View.GONE);
        discountPrice.setVisibility(View.GONE);
        discountRate.setVisibility(View.GONE);

        addProductPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(Intent.createChooser(new Intent().setAction(Intent.ACTION_GET_CONTENT).setType("image/*"), "Select Image"), CODE_IMG_GALLERY);
                relativeImg1.setVisibility(View.VISIBLE);
            }
        });

        addMultipleProductPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select images"), CODE_MULTIPLE_IMG_GALLERY);

                relativeImg1.setVisibility(View.VISIBLE);
                relativeImg2.setVisibility(View.VISIBLE);
                relativeImg3.setVisibility(View.VISIBLE);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        categoriesProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialog();
            }
        });

        updateProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1, input data
                //2, validate data
                //3, update data to db
                inputData();
            }
        });

        picCancel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                img1.setImageResource(android.R.color.transparent);
                relativeImg1.setVisibility(View.GONE);

            }
        });
        picCancel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                img2.setImageResource(android.R.color.transparent);
                relativeImg2.setVisibility(View.GONE);


            }
        });
        picCancel3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img3.setImageResource(android.R.color.transparent);
                relativeImg3.setVisibility(View.GONE);
            }
        });

    }

    private void loadProductsDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products").child(productId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //get data
                        String productId = ""+ dataSnapshot.child("productId").getValue();
                        String productTitle = ""+ dataSnapshot.child("productTitle").getValue();
                        String productImage = ""+ dataSnapshot.child("productImage").getValue();
                        String productCategory = ""+ dataSnapshot.child("productCategory").getValue();
                        String productBrand = ""+ dataSnapshot.child("productBrand").getValue();
                        String productColor = ""+ dataSnapshot.child("productColor").getValue();
                        String productCondition = ""+ dataSnapshot.child("productCondition").getValue();
                        String productType = ""+ dataSnapshot.child("productType").getValue();
                        String productDescription = ""+ dataSnapshot.child("productDescription").getValue();
                        String productGender = ""+ dataSnapshot.child("productGender").getValue();
                        String productDiscountPrice = ""+ dataSnapshot.child("productDiscountPrice").getValue();
                        String originalPrice = ""+ dataSnapshot.child("originalPrice").getValue();
                        String productDiscountRate = ""+ dataSnapshot.child("productDiscountRate").getValue();
                        String productAdderName = ""+ dataSnapshot.child("productAdderName").getValue();
                        String discountAvailable = ""+ dataSnapshot.child("discountAvailable").getValue();
                        String negotiationAvailable = ""+ dataSnapshot.child("negotiationAvailable").getValue();
                        String uid = ""+ dataSnapshot.child("uid").getValue();

                        //set data to views

                        if (discountAvailable.equals("true")){
                            discountSwitch.setChecked(true);

                            discountPrice.setVisibility(View.VISIBLE);
                            discountRate.setVisibility(View.VISIBLE);
                        }
                        else{
                            discountSwitch.setChecked(false);

                            discountPrice.setVisibility(View.GONE);
                            discountRate.setVisibility(View.GONE);
                        }

                        if (negotiationAvailable.equals("true")){
                            negotiableSwitch.setChecked(true);

                        }
                        else{
                            discountSwitch.setChecked(false);

                        }

                        titleProduct.getEditText().setText(productTitle);
                        proBrand.getEditText().setText(productBrand);
                        proColor.getEditText().setText(productColor);
                        proCondition.getEditText().setText(productCondition);
                        proDescription.getEditText().setText(productDescription);
                        proGender.getEditText().setText(productGender);
                        proType.getEditText().setText(productType);
                        discountRate.getEditText().setText(productDiscountRate);
                        discountPrice.getEditText().setText(productDiscountPrice);
                        categoriesProduct.setText(productCategory);
                        productPrice.setText(originalPrice);

                        try{
                            Picasso.get().load(productImage).placeholder(R.drawable.ic_dashboard).into(img1);
                        }
                        catch (Exception e){
                            img1.setImageResource(R.drawable.ic_dashboard);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void categoryDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Products Categories")
                .setItems(Constants.productCategories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String category = Constants.productCategories[which];
                        categoriesProduct.setText(category);

                    }
                })
                .show();
    }


    private String productTitle, productCategory, productBrand, productType, productGender, productColor, productDescription, productCondition, productDiscountPrice, originalPrice, productDiscountRate, productAdderName, productAdderPhoneNo;
    private boolean discountAvailable =false;
    private boolean negotiationAvailable=false;
    private void inputData() {

        productTitle = titleProduct.getEditText().getText().toString().trim();
        productCategory = categoriesProduct.getText().toString().trim();
        productBrand = proBrand.getEditText().getText().toString().trim();
        productColor = proColor.getEditText().getText().toString().trim();
        productType = proType.getEditText().getText().toString().trim();
        productGender = proGender.getEditText().getText().toString().trim();
        productDescription = proDescription.getEditText().getText().toString().trim();
        productCondition = proCondition.getEditText().getText().toString().trim();
        productDiscountPrice = discountPrice.getEditText().getText().toString().trim();
        productDiscountRate = discountRate.getEditText().getText().toString().trim();
        originalPrice = productPrice.getText().toString().trim();
        productAdderName = adderName.getEditText().getText().toString().trim();
        productAdderPhoneNo = adderPhone.getEditText().getText().toString().trim();
        discountAvailable = discountSwitch.isChecked();
        negotiationAvailable = negotiableSwitch.isChecked();

        if (TextUtils.isEmpty(productTitle)){
            Toast.makeText(this, "Title is Required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(productCategory)){
            Toast.makeText(this, "Category is Required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(productBrand)){
            Toast.makeText(this, "Brand is Required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(productColor)){
            Toast.makeText(this, "Color is Required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(productType)){
            Toast.makeText(this, "Type is Required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(productGender)){
            Toast.makeText(this, "Gender is Required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(productDescription)){
            Toast.makeText(this, "Description is Required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(productCondition)){
            Toast.makeText(this, "Condition is Required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(originalPrice)){
            Toast.makeText(this, "Price is Required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (discountAvailable){
            productDiscountPrice = discountPrice.getEditText().getText().toString().trim();
            productDiscountRate = discountRate.getEditText().getText().toString().trim();
            if (TextUtils.isEmpty(productDiscountPrice)){
                Toast.makeText(this, "Discount Price is Required", Toast.LENGTH_SHORT).show();
                return;
            }

        }
        else {
            productDiscountPrice = "0";
            productDiscountRate = "";
        }
        updateProduct();
    }

    private void updateProduct() {
        progressDialog.setMessage("Updating product...");
        progressDialog.show();


        String timestamp = ""+System.currentTimeMillis();

        if (image_uri == null){

            String filePathAndName = "product_images/" + ""+timestamp;

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            uploadTask = storageReference.putFile(image_Uri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return storageReference.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful())
                            {

                                Uri downloadUri = task.getResult();
                                myUri = downloadUri.toString();

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("uid",""+firebaseAuth.getUid());
                                hashMap.put("productTitle", ""+productTitle);
                                hashMap.put("productCategory", ""+productCategory);
                                hashMap.put("productBrand", ""+productBrand);
                                hashMap.put("productColor", ""+productColor);
                                hashMap.put("productCondition", ""+productCondition);
                                hashMap.put("productType", ""+productType);
                                hashMap.put("productDescription", ""+productDescription);
                                hashMap.put("productGender", ""+productGender);
                                hashMap.put("productDiscountPrice", ""+productDiscountPrice);
                                hashMap.put("originalPrice", ""+originalPrice);
                                hashMap.put("productDiscountRate", ""+productDiscountRate);
                                hashMap.put("productAdderName", ""+productAdderName);
                                hashMap.put("productImage", myUri);
                                hashMap.put("discountAvailable", ""+discountAvailable);
                                hashMap.put("negotiationAvailable", ""+negotiationAvailable);

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseAuth.getUid()).child("Products").child(productId).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(EditProductActivity.this, "Product Sent to Admin For Checking", Toast.LENGTH_LONG).show();


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(EditProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();


                                            }
                                        });

                            }
                            else {
                                Toast.makeText(EditProductActivity.this, "Please Select image", Toast.LENGTH_SHORT).show();
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            progressDialog.dismiss();
                            Toast.makeText(EditProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else {

            String filePathAndName = "product_images/" + ""+ productId; //override previous image using id
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            for (upload_count = 0; upload_count < ImageList.size(); upload_count++){

                Uri IndividualImage = ImageList.get(upload_count);
                StorageReference ImageName = storageReference.child("Products"+IndividualImage.getLastPathSegment());

                ImageName.putFile(IndividualImage)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                ImageName.getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String url =String.valueOf(uri);

                                                StoreLink(url);
                                            }
                                        });
                            }
                        });


            }


        }

    }

    private void StoreLink(String url) {

        String timestamp = ""+System.currentTimeMillis();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        HashMap<String, Object> hashMap= new HashMap<>();
        hashMap.put("productTitle", ""+productTitle);
        hashMap.put("productCategory", ""+productCategory);
        hashMap.put("productBrand", ""+productBrand);
        hashMap.put("productColor", ""+productColor);
        hashMap.put("productCondition", ""+productCondition);
        hashMap.put("productType", ""+productType);
        hashMap.put("productDescription", ""+productDescription);
        hashMap.put("productGender", ""+productGender);
        hashMap.put("productDiscountPrice", ""+productDiscountPrice);
        hashMap.put("originalPrice", ""+originalPrice);
        hashMap.put("productDiscountRate", ""+productDiscountRate);
        hashMap.put("productAdderName", ""+productAdderName);
        hashMap.put("discountAvailable", ""+discountAvailable);
        hashMap.put("negotiationAvailable", ""+negotiationAvailable);
        hashMap.put("uid", ""+firebaseAuth.getUid());
        hashMap.put("productImage", url);
        reference.child(firebaseAuth.getUid()).child("Products").child(productId).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(EditProductActivity.this, "Product Sent to Admin For Checking", Toast.LENGTH_LONG).show();
                        clearData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(EditProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();


                    }
                });

    }

    private void clearData(){

        titleProduct.getEditText().setText("");
        proBrand.getEditText().setText("");
        proType.getEditText().setText("");
        proGender.getEditText().setText("");
        proCondition.getEditText().setText("");
        proColor.getEditText().setText("");
        proDescription.getEditText().setText("");
        img1.setImageResource(R.drawable.picture);
        img2.setImageResource(R.drawable.picture);
        img3.setImageResource(R.drawable.picture);
        image_uri = null;


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODE_IMG_GALLERY && resultCode == RESULT_OK){
            image_Uri = data.getData();
            if (image_Uri !=null){
                img1.setImageURI(image_Uri);
            }

        }else if (requestCode == CODE_MULTIPLE_IMG_GALLERY && resultCode == RESULT_OK){
            ClipData clipData = data.getClipData();

            if (clipData !=null){
                int countClipData = data.getClipData().getItemCount();

                img1.setImageURI(clipData.getItemAt(0).getUri());
                img2.setImageURI(clipData.getItemAt(1).getUri());
                img3.setImageURI(clipData.getItemAt(2).getUri());

                for (int i =0; i < clipData.getItemCount(); i++){
                    ClipData.Item item = clipData.getItemAt(i);
                    Uri uri = item.getUri();
                    Log.e("Images :", uri.toString());

                }


                int currentImageSelect = 0;
                while (currentImageSelect < countClipData){
                    image_uri = data.getClipData().getItemAt(currentImageSelect).getUri();

                    ImageList.add(image_uri);
                    currentImageSelect = currentImageSelect +1;
                }


                if (ImageList.size() > 3){
                    Toast.makeText(this, "You can only select maximum of 3 images", Toast.LENGTH_SHORT).show();
                    multiplePhotoNo.setText("You Have Selected 3 Images");

                }else{
                    multiplePhotoNo.setText("You Have Selected"+ ImageList.size() +" Images");
                }
            }

        }
    }
}