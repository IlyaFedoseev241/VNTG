package com.example.vntg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> listData;
    private List<Product> listTemp;
    private DatabaseReference myDataBase;
    private String parentDbName="Products";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Init();
        getDataFromDB();
        setOnClickItem();
    }

    private void Init() {
        listView=findViewById(R.id.homeListView);
        listData=new ArrayList<>();
        listTemp=new ArrayList<>();
        adapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,listData);
        listView.setAdapter(adapter);
        myDataBase= FirebaseDatabase.getInstance().getReference(parentDbName);
    }
    private void getDataFromDB(){
        ValueEventListener vListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(listData.size()>0)listData.clear();
                if(listTemp.size()>0)listTemp.clear();
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    Product product=ds.getValue(Product.class);
                    assert product !=null;
                    listData.add(product.name);
                    listTemp.add(product);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        myDataBase.addValueEventListener(vListener);
    }
    private void setOnClickItem()
    {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Product product=listTemp.get(i);
                Intent intent=new Intent(HomeActivity.this,ShowActivity.class);
                intent.putExtra("product_name",product.name);
                intent.putExtra("product_price",product.price);
                intent.putExtra("product_description",product.description);
                intent.putExtra("product_image",product.image);
                startActivity(intent);
            }
        });
    }
}