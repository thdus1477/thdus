package com.example.geofence_prac;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddLocationActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_location);

        String title = "";
        String address = "";

        Bundle extras = getIntent().getExtras();

        if (extras == null) {
            title = "일정 추가 실패";
        }
        else {

            title = extras.getString("title");
            address = extras.getString("address");
        }

        TextView textView = (TextView) findViewById(R.id.list_todo_extra_textView);

        String str = title + '\n' + address + '\n';
        textView.setText(str);
    }
}
