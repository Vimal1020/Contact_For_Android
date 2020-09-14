package com.example.backendless;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.List;

public class ContactEdit extends AppCompatActivity {

    TextView tvch, tvname;
    EditText etname, etmail, etphone;
    ImageView ivcall, ivmail, ivedit, ivdelete;
    Button submitbut;
    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;

    boolean edit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_edit);

        tvch = findViewById(R.id.tvchar);
        tvname = findViewById(R.id.tvname);
        etname = findViewById(R.id.etname);
        etphone = findViewById(R.id.etphone);
        etmail = findViewById(R.id.etmail);
        ivcall = findViewById(R.id.ivcall);
        ivmail = findViewById(R.id.ivmail);
        ivdelete = findViewById(R.id.deleteivv);
        ivedit = findViewById(R.id.ivedit);
        submitbut = findViewById(R.id.btnsub);

        etname.setVisibility(View.GONE);
        etmail.setVisibility(View.GONE);
        etphone.setVisibility(View.GONE);
        submitbut.setVisibility(View.GONE);

        final int index = getIntent().getIntExtra("index", 0);

        etname.setText(ApplicationClass.contacts.get(index).getName());
        etmail.setText(ApplicationClass.contacts.get(index).getEmail());
        etphone.setText(ApplicationClass.contacts.get(index).getNumber());

        tvch.setText(ApplicationClass.contacts.get(index).getName().toUpperCase().charAt(0) + "");
        tvname.setText(ApplicationClass.contacts.get(index).getName());


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);

        ivcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               String uri="tel:"+ApplicationClass.contacts.get(index).getNumber();
               Intent intent=new Intent(Intent.ACTION_DIAL);
               intent.setData(Uri.parse(uri));
               startActivity(intent);


            }
        });
        ivedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit=!edit;

                if(edit)
                {
                    etname.setVisibility(View.VISIBLE);
                    etmail.setVisibility(View.VISIBLE);
                    etphone.setVisibility(View.VISIBLE);
                    submitbut.setVisibility(View.VISIBLE);


                }
                else{
                    etname.setVisibility(View.GONE);
                    etmail.setVisibility(View.GONE);
                    etphone.setVisibility(View.GONE);
                    submitbut.setVisibility(View.GONE);


                }

            }
        });
        ivdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               final AlertDialog.Builder dailog =new AlertDialog.Builder(ContactEdit.this);

               dailog.setMessage("Are you sure you want to delete the contact ?");
               dailog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {

                       showProgress(true);
                       tvLoad.setText("deleting the contact please wait....");
                      Backendless.Persistence.of(Contact.class).remove(ApplicationClass.contacts.get(index), new AsyncCallback<Long>() {
                          @Override
                          public void handleResponse(Long response) {
                              ApplicationClass.contacts.remove(index);
                              Toast.makeText(ContactEdit.this,"Successfully Deleted the contact ",Toast.LENGTH_SHORT).show();
                              setResult(RESULT_OK);
                              ContactEdit.this.finish();

                              showProgress(false);
                          }

                          @Override
                          public void handleFault(BackendlessFault fault) {

                         Toast.makeText(ContactEdit.this,"Error :"+fault.getMessage(),Toast.LENGTH_SHORT);
                           showProgress(false);
                          }
                      });
                   }
               });

               dailog.setNegativeButton("NO ,Thanks", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {

                       setResult(RESULT_CANCELED);
                       showProgress(false);

                   }
               });
            dailog.show();

            }
        });
        ivmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL,ApplicationClass.contacts.get(index).getEmail());
                startActivity(intent.createChooser(intent,"Send Mail To :"+ApplicationClass.contacts.get(index).getEmail()));

            }
        });
        submitbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etname.getText().toString().isEmpty()||etmail.getText().toString().isEmpty()||etphone.getText().toString().isEmpty()){
                    Toast.makeText(ContactEdit.this,"Please fill all fields",Toast.LENGTH_SHORT).show();

                }
                else {

                    ApplicationClass.contacts.get(index).setName(etname.getText().toString().trim());
                    ApplicationClass.contacts.get(index).setEmail(etmail.getText().toString().trim());
                    ApplicationClass.contacts.get(index).setNumber(etphone.getText().toString().trim());

                    showProgress(true);
                    tvLoad.setText("Updateing the data.. wait..");

                    Backendless.Persistence.save(ApplicationClass.contacts, new AsyncCallback<List<Contact>>() {
                        @Override
                        public void handleResponse(List<Contact> response) {

                            tvch.setText(ApplicationClass.contacts.get(index).getName().toUpperCase().charAt(0)+"");
                            tvname.setText(ApplicationClass.contacts.get(index).getName());
                            Toast.makeText(ContactEdit.this,"Successfully Updated ",Toast.LENGTH_SHORT).show();
                            showProgress(false);


                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(ContactEdit.this, "Error :" + fault.getMessage(), Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        }
                    });
                }
            }
        });

    }
    /**
     * Shows the progress UI and hides the login form.
     */
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