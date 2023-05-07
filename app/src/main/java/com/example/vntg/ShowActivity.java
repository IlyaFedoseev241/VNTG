package com.example.vntg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class ShowActivity extends AppCompatActivity {
    private TextView tvName,tvPrice,tvDescription;
    private ImageView imBD;
    private Button basketBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        Init();
        getIntentMain();
    }

    private void Init() {
        imBD=(ImageView) findViewById(R.id.show_image);
        tvName=(TextView) findViewById(R.id.show_name_tv);
        tvPrice=(TextView) findViewById(R.id.show_price_tv);
        tvDescription=(TextView) findViewById(R.id.show_description_tv);
        basketBtn=(Button) findViewById(R.id.show_basket_btn);
    }
    private void getIntentMain(){
        Intent intent=getIntent();
        if(intent!=null){

            Picasso.get().load(intent.getStringExtra("product_image")).into(imBD);
            tvName.setText(intent.getStringExtra("product_name"));
            tvPrice.setText(intent.getStringExtra("product_price"));
            tvDescription.setText(intent.getStringExtra("product_description"));

            basketBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent basketIntent=new Intent(ShowActivity.this,BasketActivity.class);
                    basketIntent.putExtra("product_name",intent);
                    basketIntent.putExtra("product_price",intent);
                    basketIntent.putExtra("product_description",intent);
                    basketIntent.putExtra("product_image",intent);
                    Toast.makeText(ShowActivity.this,"Товар добавлен в корзину",Toast.LENGTH_SHORT).show();
                    startActivity(basketIntent);

                }
            });
        }
    }

}