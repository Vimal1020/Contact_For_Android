package com.example.backendless;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.local.UserIdStorageFactory;

public class Login extends AppCompatActivity {

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;
    EditText etmaill,etpassl;
    Button login,register;
   TextView forgotpas;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);
        etmaill=findViewById(R.id.emailet);
        etpassl = findViewById(R.id.passwordet);
        login=findViewById(R.id.butlog);
        register=findViewById(R.id.butreg);
        forgotpas=findViewById(R.id.forgpass);

        showProgress(true);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etmaill.getText().toString().isEmpty() || etpassl.getText().toString().isEmpty()){

                    Toast.makeText(Login.this,"Please Enter the all fields ",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    String email=etmaill.getText().toString().trim();
                    String password=etpassl.getText().toString().trim();
                    showProgress(true);
                    tvLoad.setText("Busy Logging you in..");
                    Backendless.UserService.login(email, password, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser response) {
                            ApplicationClass.user=response;
                            Toast.makeText(Login.this,"Logged in Successfully",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this,MainActivity.class));
                            Login.this.finish();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(Login.this,"Error :"+fault.getMessage(),Toast.LENGTH_SHORT).show();
                           showProgress(false);
                        }
                    }, true);

                }

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Login.this,Register.class));
            }
        });

        forgotpas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etmaill.getText().toString().isEmpty()) {
                    Toast.makeText(Login.this,"Please Enter Email to reset",Toast.LENGTH_SHORT).show();
                }
                else{
                    String email=etmaill.getText().toString().trim();
                    showProgress(true);
                    tvLoad.setText("Busy sending instruction details on your mail..");
                    Backendless.UserService.restorePassword(email, new AsyncCallback<Void>() {
                        @Override
                        public void handleResponse(Void response) {
                             Toast.makeText(Login.this,"Instruction has sent to your email address",Toast.LENGTH_SHORT).show();
                             showProgress(false);
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(Login.this,"Error "+ fault.getMessage(),Toast.LENGTH_SHORT).show();
                            showProgress(false);

                        }
                    });
                }

            }
        });
        tvLoad.setText("Checking the credentials please wait..");
        Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
            @Override
            public void handleResponse(Boolean response) {
                if(response){
                    String userObjectId= UserIdStorageFactory.instance().getStorage().get();
                    Backendless.Data.of(BackendlessUser.class).findById(userObjectId, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser response) {

                            ApplicationClass.user=response;
                            tvLoad.setText("Logging you in..Welcome Back.");
                            startActivity(new Intent(Login.this,MainActivity.class));
                            Login.this.finish();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(Login.this,"Error :"+fault.getMessage(),Toast.LENGTH_SHORT);
                            showProgress(false);

                        }
                    });
                    showProgress(false);
                }
                else {
                    showProgress(false);
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(Login.this,"Error "+fault.getMessage(),Toast.LENGTH_SHORT).show();
                showProgress(false);

            }
        });
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
        private void showProgress(final boolean show) {
            // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
            // for very easy animations. If available, use these APIs to fade-in
            // the progress spinner.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                        show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                    }
                });

                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                mProgressView.animate().setDuration(shortAnimTime).alpha(
                        show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });

                tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
                tvLoad.animate().setDuration(shortAnimTime).alpha(
                        show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });
            } else {
                // The ViewPropertyAnimator APIs are not available, so simply show
                // and hide the relevant UI components.
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        }




    }
