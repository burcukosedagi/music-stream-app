package com.example.app1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class InnovationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_innovations);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.innavationPage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void btnBackToSettingOnClick(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);


        String userId = getIntent().getStringExtra("userId");
        intent.putExtra("userId",userId);

        startActivity(intent);

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}