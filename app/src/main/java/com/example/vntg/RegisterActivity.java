package com.example.vntg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText registerUsername,registerPhone,registerPassword;
    private Button registerBtn;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerUsername=(EditText) findViewById(R.id.register_username_ed);
        registerPhone=(EditText) findViewById(R.id.register_phone_ed);
        registerPassword=(EditText) findViewById(R.id.register_password_ed);
        registerBtn=(Button) findViewById(R.id.register_btn);
        loadingBar=new ProgressDialog(this);


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        String username=registerUsername.getText().toString();
        String phone=registerPhone.getText().toString();
        String password=registerPassword.getText().toString();

        if(TextUtils.isEmpty(username))
        {
            Toast.makeText(this,"Введите имя",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this,"Введите телефон",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Введите пароль",Toast.LENGTH_SHORT).show();
        }
        else
        {
           loadingBar.setTitle("Создание аккаунта");
           loadingBar.setMessage("Пожалуйста подождите");
           loadingBar.setCanceledOnTouchOutside(false);
           loadingBar.show();
           
           ValidatePhone(username,phone,password);
        }
    }

    private void ValidatePhone(String username,String phone,String password)
    {
        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!(snapshot.child("Users").child(phone).exists())) {
                    HashMap<String,Object> userDataMap=new HashMap<>();
                    userDataMap.put("phone",phone);
                    userDataMap.put("username",username);
                    userDataMap.put("password",password);

                    RootRef.child("Users").child(phone).updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                loadingBar.dismiss();
                                Toast.makeText(RegisterActivity.this, "Регистраиция прошла успешно", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                            } else {
                                loadingBar.dismiss();
                                Toast.makeText(RegisterActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Номер + " + phone + " уже существует", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}