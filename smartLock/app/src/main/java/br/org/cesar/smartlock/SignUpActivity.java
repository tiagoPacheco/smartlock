package br.org.cesar.smartlock;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import at.abraxas.amarino.Amarino;
import br.org.cesar.smartlock.interfaces.IAmarinoCommand;
import br.org.cesar.smartlock.utils.AmarinoUtil;
import br.org.cesar.smartlock.utils.Utils;

import static android.content.Context.*;

public class SignUpActivity extends AppCompatActivity {

    private AppCompatButton mBtnCreate;
    private EditText mEditTextUser;
    private EditText mEditTextPassword;
    private EditText mEditTextConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mBtnCreate = (AppCompatButton) findViewById(R.id.btn_create);
        mEditTextUser = (EditText) findViewById(R.id.input_user);
        mEditTextPassword = (EditText) findViewById(R.id.input_password);
        mEditTextConfirmPassword = (EditText) findViewById(R.id.input_confirm_password);

        mBtnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAdmin();
            }
        });
    }

    private void CreateNewAdmin() {
        String user = mEditTextUser.getText().toString();
        String password = mEditTextPassword.getText().toString();
        String passwordConfirm = mEditTextConfirmPassword.getText().toString();
        String macAddress = Utils.getMac(this);

        if (!validateEntry(user, password, passwordConfirm)) return;

        final String[] data = { user, password, macAddress };

        Amarino.sendDataToArduino(this, AmarinoUtil.Address, AmarinoUtil.SignUpFlag, data);

        Intent intent = new Intent(SignUpActivity.this, SmartLockActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean validateEntry(String user, String password, String passwordConfirm) {
        boolean valid = true;

        if (user.isEmpty()) {
            mEditTextUser.setError("Enter a valid user name");
            valid = false;
        }
        if (password.isEmpty()) {
            mEditTextPassword.setError("Enter a valid password");
            valid = false;
        }
        if (passwordConfirm.isEmpty()) {
            mEditTextConfirmPassword.setError("Enter a valid password");
            valid = false;
        }
        if (!passwordConfirm.equals(password)) {
            mEditTextConfirmPassword.setError("Passwords do not match");
            valid = false;
        }
        if (user.equals("adm")) {
            mEditTextUser.setError("Use a different name than 'adm'");
            valid = false;
        }

        return valid;
    }
}
