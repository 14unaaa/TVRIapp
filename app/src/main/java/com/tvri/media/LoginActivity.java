package com.tvri.media;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity{
    private EditText editEmail, editPassword;
    private Button btnLogin, btnRegister;
    private SignInButton btnGoogle;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editEmail=findViewById(R.id.email);
        editPassword=findViewById(R.id.password);
        btnLogin=findViewById(R.id.btn_login);
        btnRegister=findViewById(R.id.btn_register);
        btnGoogle=findViewById(R.id.btn_google);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Silahkan tunggu!");
        progressDialog.setCancelable(false);

        btnRegister.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        });
        btnLogin.setOnClickListener(view -> {
            if (editEmail.getText().length()>0 && editPassword.getText().length()>0){
                login(editEmail.getText().toString(),editPassword.getText().toString());
            }else {
                Toast.makeText(getApplicationContext(), "Data anda tidak lengkap!", Toast.LENGTH_SHORT).show();
            }
        });
        btnGoogle.setOnClickListener(view -> {
            googleSignIn();
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("844616732094-gfp2t9csui1gnoju369dmh7oslnlp2gh.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    private void googleSignIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 100);
    }
    private void login(String email, String password) {
        ///LOGIN CODE
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful() && task.getResult()!=null){
                    if (task.getResult().getUser()!=null){
                        reload();
                    }else {
                        Toast.makeText(getApplicationContext(),"Login gagal!", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"Login gagal!", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }
    private void reload(){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100){
            Task<GoogleSignInAccount>task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("GOOGLE SIGN IN", "firebaseAuthWithGoogle:" +account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            }catch (ApiException e){
                Log.w("GOOGLE SIGN IN","Google sign in failed", e);
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.d("GOOGLE SIGN IN", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        }else {
                            Log.w("GOOGLE SIGN IN", "signInWithCredential:failure", task.getException());
                        }
                        reload();
                    }
                });
    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            reload();
        }
    }}
