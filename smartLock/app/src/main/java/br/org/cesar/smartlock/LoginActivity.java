package br.org.cesar.smartlock;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import at.abraxas.amarino.Amarino;
import br.org.cesar.smartlock.utils.AmarinoUtil;
import br.org.cesar.smartlock.utils.Utils;

public class LoginActivity extends AppCompatActivity {

    private AppCompatButton mBtnLogin;
    private EditText mEditTextUser;
    private EditText mEditTextPassword;
    private static ProgressDialog mProgressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Amarino.connect(LoginActivity.this, AmarinoUtil.Address);
        //AmarinoUtil.connect(this);
        AmarinoUtil.registerConnectionReceiver(LoginActivity.this);

        mBtnLogin = (AppCompatButton) findViewById(R.id.btn_login);
        mEditTextUser = (EditText) findViewById(R.id.input_user);
        mEditTextPassword = (EditText) findViewById(R.id.input_password);

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!AmarinoUtil.IsConnected){
                    Toast.makeText(LoginActivity.this, "You're not connected with the smart lock", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!validateEntry()) {
                    return;
                }

                signinOrSignup();
            }
        });
    }

    private void signinOrSignup(){
        String user = mEditTextUser.getText().toString();
        String password = mEditTextPassword.getText().toString();
        final String[] data = { user, password };

        new AsyncTask<Object, Object, String>() {
            @Override
            protected void onPreExecute() {
                mProgressDialog = Utils.createSimpleProgressDialog("Wait", "Authenticating...", LoginActivity.this);
            }

            @Override
            protected String doInBackground(Object... params) {

                Amarino.sendDataToArduino(LoginActivity.this, AmarinoUtil.Address, AmarinoUtil.LoginFlag, data);

                while (!AmarinoUtil.IsDataReceived);
                AmarinoUtil.IsDataReceived = false;

                return AmarinoUtil.DataReturned;
            }

            @Override
            protected void onPostExecute(String dataReturned) {
                mProgressDialog.dismiss();

                switch (dataReturned){
                    case AmarinoUtil.InvalidName:
                        mEditTextUser.setError("Enter a valid user name");
                        break;
                    case AmarinoUtil.InvalidPassword:
                        mEditTextPassword.setError("Enter a valid password");
                        break;
                    case AmarinoUtil.InvalidUserAndPassword:
                        mEditTextUser.setError("Enter a valid user name");
                        mEditTextPassword.setError("Enter a valid password");
                        break;
                    case AmarinoUtil.Success:
                        Intent intent = new Intent(LoginActivity.this, SmartLockActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                }

            }
        }.execute();
    }

    private boolean validateEntry() {
        boolean valid = true;
        String user = mEditTextUser.getText().toString();
        String password = mEditTextPassword.getText().toString();

        if (user.isEmpty()) {
            mEditTextUser.setError("Enter a valid user name");
            valid = false;
        }
        if (password.isEmpty()) {
            mEditTextPassword.setError("Enter a valid password");
            valid = false;
        }

        return valid;
    }

}
