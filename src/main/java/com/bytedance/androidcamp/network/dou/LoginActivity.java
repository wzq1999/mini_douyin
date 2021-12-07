package com.bytedance.androidcamp.network.dou;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    Button sentLoginBtn;
    EditText editStudentId;
    EditText editStudentName;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bindButtons();


    }
   private void bindButtons(){
        sentLoginBtn=findViewById(R.id.sentLogin);
        editStudentId=findViewById(R.id.userStudentIdEnter);
        editStudentName=findViewById(R.id.userNameEnter);
        sentLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLoginData();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });
    }
   private void setLoginData(){
        String editedStudentId;
        String editedStudentName;
       editedStudentId=editStudentId.getText().toString();
       editedStudentName=editStudentName.getText().toString();
       //SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);
       SharedPreferences.Editor editor=getSharedPreferences("user_info", MODE_PRIVATE).edit();
       editor.putInt("loginbool", 1);
       editor.putString("studentId",editedStudentId );
       editor.putString("studentName", editedStudentName);
       editor.commit();
   }
}
