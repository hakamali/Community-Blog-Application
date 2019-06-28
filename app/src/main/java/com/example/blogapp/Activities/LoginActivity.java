package com.example.blogapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.blogapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText useremail,userpassword;
    Button  buttonlogin;
    ProgressBar loginprogress;
    private FirebaseAuth mAuth;
    private Intent HomeActivity;
    ImageView loginnnphoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        useremail=findViewById(R.id.loginMail);
        userpassword=findViewById(R.id.loginPassword);
        buttonlogin=findViewById(R.id.btnlogin);
        loginprogress=findViewById(R.id.loginprogressbar);
        loginnnphoto=findViewById(R.id.loginphoto);
        HomeActivity=new Intent(this, com.example.blogapp.Activities.Home.class);
        mAuth=FirebaseAuth.getInstance();

        loginprogress.setVisibility(View.INVISIBLE);
        loginnnphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        buttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginprogress.setVisibility(View.VISIBLE);
                buttonlogin.setVisibility(View.INVISIBLE);
                final String mail=useremail.getText().toString();
                final String password=userpassword.getText().toString();

                if (mail.isEmpty() || password.isEmpty())
                {
                    showMessage("Please verify all fields");
                    buttonlogin.setVisibility(View.VISIBLE);
                    loginprogress.setVisibility(View.INVISIBLE);
                }
                else
                {
                    signIn(mail,password);
                }
            }
        });
    }

    private void signIn(String mail, String password)
    {

       mAuth.signInWithEmailAndPassword(mail,password)
               .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        loginprogress.setVisibility(View.INVISIBLE);
                        buttonlogin.setVisibility(View.VISIBLE);

                        updateUI();
                    }
                    else {

                        showMessage(task.getException().getMessage());
                        buttonlogin.setVisibility(View.VISIBLE);
                        loginprogress.setVisibility(View.INVISIBLE);
                    }
                   }
               });


    }

    private void updateUI()
    {
          startActivity(HomeActivity);
          finish();
    }

    private void showMessage(String text) {

        Toast.makeText(this, text, Toast.LENGTH_LONG).show();



    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user= mAuth.getCurrentUser();
        if (user !=null)
        {
            updateUI();
        }
    }
}
