package com.example.vntg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private EditText loginPhone,loginPassword;
    private Button loginBtn,fastClient,fastAdmin;
    private ProgressDialog loadingBar;
    private Button adminLink,notAdminLink;

    private String parentDbName="Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fastAdmin=(Button) findViewById(R.id.fastAdmin);
        fastClient=(Button) findViewById(R.id.fastClient);
        loginPhone=(EditText) findViewById(R.id.login_phone_ed);
        loginPassword=(EditText) findViewById(R.id.login_password_ed);
        loginBtn=(Button) findViewById(R.id.login_btn);
        loadingBar=new ProgressDialog(this);
        adminLink=(Button) findViewById(R.id.login_admin_panel_btn);
        notAdminLink=(Button) findViewById(R.id.login_client_panel_btn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adminLink.setVisibility(View.INVISIBLE);
                notAdminLink.setVisibility(View.VISIBLE);
                loginBtn.setText("Вход для админа");
                parentDbName="Admin";
            }
        });
        notAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adminLink.setVisibility(View.VISIBLE);
                notAdminLink.setVisibility(View.INVISIBLE);
                loginBtn.setText("Вход");
                parentDbName="Users";
            }
        });

        fastClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,PrimaryActivity.class);
                startActivity(intent);
            }
        });
        fastAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,AdminActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        String phone=loginPhone.getText().toString();
        String password=loginPassword.getText().toString();

        if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(LoginActivity.this,"Введите номер",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(LoginActivity.this,"Введите пароль",Toast.LENGTH_SHORT).show();
        }else
        {
            loadingBar.setTitle("Вход в аккаунт");
            loadingBar.setMessage("Пожалуйста подождите");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidatePhone(phone,password);
        }
    }

    private void ValidatePhone(String phone, String password) {
       final DatabaseReference RootRef;
       RootRef=FirebaseDatabase.getInstance().getReference();

       RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.child(parentDbName).exists())
               {
                   Users userData=snapshot.child(parentDbName).child(phone).getValue(Users.class);

                   if(userData.getPhone().equals(phone))
                   {
                       if(userData.getPassword().equals(password))
                       {
                           if(parentDbName.equals("Users"))
                           {
                               loadingBar.dismiss();
                               Toast.makeText(LoginActivity.this,"Успешный вход",Toast.LENGTH_SHORT).show();
                               Intent homeIntent=new Intent(LoginActivity.this,HomeActivity.class);
                               startActivity(homeIntent);
                           }else if(parentDbName.equals("Admin"))
                           {
                               loadingBar.dismiss();
                               Toast.makeText(LoginActivity.this,"Успешный вход",Toast.LENGTH_SHORT).show();
                               Intent homeIntent=new Intent(LoginActivity.this,AdminActivity.class);
                               startActivity(homeIntent);
                           }

                       }else
                           Toast.makeText(LoginActivity.this,"Неверный пароль",Toast.LENGTH_SHORT).show();
                   }

               }else
               {
                   loadingBar.dismiss();
                   Toast.makeText(LoginActivity.this,"Аккаунт с номером +"+phone+" не существует",Toast.LENGTH_SHORT).show();
                   Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                   startActivity(intent);
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });

    }
}