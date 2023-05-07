package com.example.vntg;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class AdminActivity extends AppCompatActivity {
    private EditText adminProductName,adminProductPrice,adminProductDescription;
    private Button adminSaveBtn,adminReadBtn,adminChooseImageBtn;
    private ImageView adminCameraIcon;
    private DatabaseReference myDataBase;
    private String parentDbName="Products";
    private StorageReference mStorageRef;
    private Uri uploadUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Init();

    }

    private void Init() {
        mStorageRef= FirebaseStorage.getInstance().getReference("ImageDB");
        adminCameraIcon=(ImageView) findViewById(R.id.admin_camera_icon);
        adminProductDescription=(EditText) findViewById(R.id.admin_description_et);
        adminProductName=(EditText) findViewById(R.id.admin_named_et);
        adminProductPrice=(EditText) findViewById(R.id.admin_price_et);
        adminSaveBtn=(Button) findViewById(R.id.admin_save_btn);
        adminReadBtn=(Button) findViewById(R.id.admin_read_btn);
        adminChooseImageBtn=(Button) findViewById(R.id.admin_choose_btn);
        myDataBase= FirebaseDatabase.getInstance().getReference(parentDbName);
    }
    private void saveUser(){
        String id=myDataBase.push().getKey();
        String name=adminProductName.getText().toString();
        String price=adminProductPrice.getText().toString();
        String description=adminProductDescription.getText().toString();

        Product newProduct=new Product(id,name,price,description,uploadUri.toString());
        if(!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(price)&&!TextUtils.isEmpty(description)) {
            if(id!=null)myDataBase.child(id).setValue(newProduct);
            Toast.makeText(AdminActivity.this, "Товар добавлен", Toast.LENGTH_SHORT).show();
        }else
            Toast.makeText(AdminActivity.this,"Пустое поле",Toast.LENGTH_SHORT).show();
    }
    public void onClickSave(View view){
       uploadImage();
    }
    public void onClickRead(View view){
        Intent intent=new Intent(AdminActivity.this,HomeActivity.class);
        startActivity(intent);
    }
    public void onClickChooseImage(View view)
    {
        getImage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==1&&data!=null&&data.getData()!=null)
        {
            if(resultCode==RESULT_OK)
            {
                Log.d("MyLog","Image URI : "+data.getData());
                adminCameraIcon.setImageURI(data.getData());

            }
        }
    }
    private void uploadImage(){
        Bitmap bitmap=((BitmapDrawable)adminCameraIcon.getDrawable()).getBitmap();
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] byteArray=baos.toByteArray();
        final StorageReference mRef=mStorageRef.child(System.currentTimeMillis()+"my_image");
        UploadTask up=mRef.putBytes(byteArray);
        Task<Uri> task=up.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return mRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                uploadUri=task.getResult();
                saveUser();
            }
        });
    }
    private void getImage()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

}