package com.mx.sireco;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mx.sireco.databinding.ActivityLoginBinding;
import com.mx.sireco.util.Config;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        String  user = (String) Config.getInstance().getKey(Config.USER_BEAN);
        TextView us = findViewById(R.id.textView2);
        us.setText(user == null ? "Uknown" : user);

        final Button button = findViewById(R.id.printTicketButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toPrint();
            }
        });
    }


    public void toPrint(){
        startActivity(new Intent(this, PrintReceiptCustom.class));
    }
}