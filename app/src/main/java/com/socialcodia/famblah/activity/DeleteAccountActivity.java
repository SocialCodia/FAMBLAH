package com.socialcodia.famblah.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.tastytoast.TastyToast;
import com.socialcodia.famblah.R;
import com.socialcodia.famblah.api.Api;
import com.socialcodia.famblah.api.ApiClient;
import com.socialcodia.famblah.model.ModelUser;
import com.socialcodia.famblah.pojo.ResponseDefault;
import com.socialcodia.famblah.storage.SharedPrefHandler;
import com.socialcodia.famblah.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeleteAccountActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private Button btnCancel;
    private EditText inputPassword;
    private Button btnDeleteAccount;
    private SharedPrefHandler sp;
    private ModelUser mUser;
    private String token,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);
        init();
        btnCancel.setOnClickListener(v->sendToHome());
        btnDeleteAccount.setOnClickListener(v->validateData());
    }

    private void showDeleteAccountAlert()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(DeleteAccountActivity.this);
        builder.setTitle("Permanently Delete Account");
        builder.setMessage("Are you sure want to permanently delete your account");
        builder.setPositiveButton("Delete My Account", (dialogInterface, i) -> deleteAccountRequest()).setNegativeButton("Cancel", (dialogInterface, i) -> TastyToast.makeText(getApplicationContext(), "Account Deletion Canceled", TastyToast.LENGTH_LONG, TastyToast.SUCCESS));
        builder.show();
    }

    private void deleteAccountRequest()
    {
        if (Utils.isNetworkAvailable(getApplicationContext()))
        {
            TastyToast.makeText(getApplicationContext(),"Please wait...",TastyToast.LENGTH_LONG,TastyToast.DEFAULT);
            Call<ResponseDefault> call = ApiClient.getInstance().getApi().deleteAccountRequest(token,password);
            call.enqueue(new Callback<ResponseDefault>() {
                @Override
                public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                    if (response.isSuccessful())
                    {
                        ResponseDefault rd = response.body();
                        if (!rd.getError())
                        {
                            sendToDeleteAccountFinal();
                            TastyToast.makeText(getApplicationContext(),rd.getMessage(),TastyToast.LENGTH_LONG,TastyToast.SUCCESS);
                        }
                        else
                            TastyToast.makeText(getApplicationContext(),rd.getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                    }
                    else
                        TastyToast.makeText(getApplicationContext(),String.valueOf(R.string.SNR),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                }

                @Override
                public void onFailure(Call<ResponseDefault> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    private void sendToHome()
    {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finishAffinity();
    }

    private void init()
    {
        actionBar = getSupportActionBar();
        actionBar.setTitle("Delete Account");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        btnCancel = findViewById(R.id.btnCancel);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        inputPassword = findViewById(R.id.inputPassword);
        sp = SharedPrefHandler.getInstance(getApplicationContext());
        mUser = sp.getUser();
        token = mUser.getToken();
    }

    private void validateData()
    {
        password = inputPassword.getText().toString().trim();
        if (password.isEmpty())
        {
            inputPassword.setError("Enter Password");
            inputPassword.requestFocus();
            return;
        }
        if (password.length()<8)
        {
            inputPassword.setError("Password could not be less than 8 character");
            inputPassword.requestFocus();
        }
        else
        {
            showDeleteAccountAlert();
        }
    }

    private void sendToDeleteAccountFinal()
    {
        Intent intent = new Intent(getApplicationContext(),DeleteAccountFinalActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}