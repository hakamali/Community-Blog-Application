package com.example.blogapp.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.blogapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {
     ImageView imageuserphoto;
     static int PReqCode =1;
    static int REQUESCODE =1;
    Uri pickedImgUri;

    private EditText useremail,userpasssword,userpassword2,username;
    private ProgressBar loadingprogress;
    private Button regBtn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        useremail=findViewById(R.id.regMail);
        userpasssword=findViewById(R.id.regPassword);
        userpassword2=findViewById(R.id.regPassword2);
        username=findViewById(R.id.regName);
        loadingprogress=findViewById(R.id.regprogressBar);
        regBtn=findViewById(R.id.btnregister);

        loadingprogress.setVisibility(View.INVISIBLE);
        mAuth=FirebaseAuth.getInstance();
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regBtn.setVisibility(View.INVISIBLE);
                loadingprogress.setVisibility(View.VISIBLE);
                 final String email=useremail.getText().toString();
                 final String password=userpasssword.getText().toString();
                 final String password2=userpassword2.getText().toString();
                 final  String name=username.getText().toString();
                 if (email.isEmpty() || name.isEmpty() || password.isEmpty() || !password.equals(password2))
                 {
                   showMessage("Please verify all fields");
                   regBtn.setVisibility(View.VISIBLE);
                   loadingprogress.setVisibility(View.INVISIBLE);
                 }
                 else
                 {
                     createUserAccount(email,name,password);
                 }

            }
        });


        imageuserphoto=findViewById(R.id.registeruserphoto);

        imageuserphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >=22)
                {
                    checkAndRequestForPermission();
                }
                else
                {
                    openGallery();
                }
            }
        });
    }

    private void createUserAccount(String email, final String name, String password)
    {
      mAuth.createUserWithEmailAndPassword(email,password)
              .addOnCompleteListener( this,new OnCompleteListener<AuthResult>() {
                  @Override
                  public void onComplete(@NonNull Task<AuthResult> task) {
                      if (task.isSuccessful())
                      {
                          showMessage("Account Created");

                          updateUserInfo(name,pickedImgUri,mAuth.getCurrentUser());
                      }
                    else
                      {
                          showMessage("Account Created Failed"+task.getException().getMessage());
                          regBtn.setVisibility(View.VISIBLE);
                          loadingprogress.setVisibility(View.INVISIBLE);
                      }
                  }
              });
    }

    private void updateUserInfo(final String name, Uri pickedImgUri, final FirebaseUser currentUser)
    {
        StorageReference mStorage= FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imagefilePath=mStorage.child(pickedImgUri.getLastPathSegment());
        imagefilePath.putFile(pickedImgUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       imagefilePath.getDownloadUrl()
                               .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                   @Override
                                   public void onSuccess(Uri uri) {
                                       UserProfileChangeRequest profileUpdate= new UserProfileChangeRequest.Builder()
                                               .setDisplayName(name)
                                               .setPhotoUri(uri)
                                               .build();
                                       currentUser.updateProfile(profileUpdate)
                                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                   @Override
                                                   public void onComplete(@NonNull Task<Void> task) {
                                                      if (task.isSuccessful())
                                                      {
                                                          showMessage("Register Complete");
                                                          updateUI();
                                                      }
                                                   }
                                               });
                                   }
                               });
                    }
                });
    }

    private void updateUI()
    {
        Intent intent=new Intent(RegisterActivity.this,Home.class);
        startActivity(intent);
        finish();
    }

    private void showMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void openGallery()
    {

        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,REQUESCODE);
    }

    private void checkAndRequestForPermission()
    {
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
             != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE))

            {
                Toast.makeText(this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
            }

            else
            {
                ActivityCompat.requestPermissions(RegisterActivity.this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                             PReqCode);
            }
        }
        else
        openGallery();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode== RESULT_OK && requestCode==REQUESCODE && data !=null)
        {
          pickedImgUri=data.getData();
          imageuserphoto.setImageURI(pickedImgUri);
        }
    }
}
