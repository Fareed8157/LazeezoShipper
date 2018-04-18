package com.example.fareed.lazeezoshipper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.fareed.lazeezoshipper.Common.Common;
import com.example.fareed.lazeezoshipper.Model.Shipper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import info.hoang8f.widget.FButton;

public class MainActivity extends AppCompatActivity {

    FButton signIn;
    AutoCompleteTextView phone,pass;

    FirebaseDatabase database;
    DatabaseReference shippers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signIn=(FButton)findViewById(R.id.signIn);

        phone=(AutoCompleteTextView)findViewById(R.id.phone);
        pass=(AutoCompleteTextView)findViewById(R.id.pass);

        database=FirebaseDatabase.getInstance();
        shippers=database.getReference(Common.SHIPPER_TABLE);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(phone.getText().toString(),pass.getText().toString());
            }
        });

    }

    private void login(String s, final String password) {
        shippers.child(s)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Shipper shipper=dataSnapshot.getValue(Shipper.class);
                            if(shipper.getPassword().equals(password)){
                                startActivity(new Intent(MainActivity.this,HomeActivity.class));
                                Common.currentShipper=shipper;
                                finish();
                            }
                        }else {
                            Toast.makeText(MainActivity.this, "Shipper does not Exist", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

}
