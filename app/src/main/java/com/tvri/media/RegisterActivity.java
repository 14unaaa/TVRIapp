package com.tvri.media;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {
    private EditText editName, editEmail, editPhone, editPassword, editPasswordconf;
    private Button btnRegister, btnLogin;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editName = findViewById(R.id.name);
        editEmail = findViewById(R.id.email);
        editPhone = findViewById(R.id.phone);
        editPassword = findViewById(R.id.password);
        editPasswordconf = findViewById(R.id.passwordconf);
        btnRegister = findViewById(R.id.btn_register);
        btnLogin = findViewById(R.id.btn_login) ;

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Silahkan tunggu!");
        progressDialog.setCancelable(false);

        btnLogin.setOnClickListener(view -> {
            finish();
        });
        btnRegister.setOnClickListener(view -> {
            if (editName.getText().length()>0 && editEmail.getText().length()>0 && editPhone.getText().length()>0 && editPassword.getText().length()>0 && editPasswordconf.getText().length()>0){
                if (editPassword.getText().toString().equals(editPasswordconf.getText().toString())){
                    register(editName.getText().toString(),editEmail.getText().toString(), editPhone.getText().toString(), editPassword.getText().toString());
                }else {
                    Toast.makeText(getApplicationContext(), "Silahkan masukkan password yang sama!", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(getApplicationContext(), "Silahkan isi semua data!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void register(String name, String email, String phone, String password) {
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    FirebaseUser firebaseUser = task.getResult().getUser();
                    if (firebaseUser != null) {
                        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();
                        firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                reload();
                            }
                        });
                        UserProfileChangeRequest request2 = new UserProfileChangeRequest.Builder()
                                .setDisplayName(phone)
                                .build();
                        firebaseUser.updateProfile(request2).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                reload();
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Register gagal!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    private void reload(){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            reload();
        }
    }
}