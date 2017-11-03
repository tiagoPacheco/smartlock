package br.org.cesar.smartlock;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import at.abraxas.amarino.Amarino;
import br.org.cesar.smartlock.interfaces.IAmarinoCommand;
import br.org.cesar.smartlock.utils.AmarinoUtil;
import br.org.cesar.smartlock.utils.Utils;

public class LoginActivity extends AppCompatActivity {

    private AppCompatButton mBtnLogin;
    private EditText mEditTextUser;
    private EditText mEditTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Amarino.connect(LoginActivity.this, AmarinoUtil.Address);
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
        String macAddress = Utils.getMac(this);

        final String[] data = { user, password, macAddress };

        AmarinoUtil.sendDataToArduinoWithReturn(data, new IAmarinoCommand(){
            @Override
            public void callback(String dataReturned) {
                switch (dataReturned){
                    case AmarinoUtil.InvalidUserOrPassword:
                        Toast.makeText(LoginActivity.this, "Username or password is incorrect", Toast.LENGTH_SHORT).show();
                        break;
                    case AmarinoUtil.InvalidMac:
                        Toast.makeText(LoginActivity.this, "Your device is not authorized", Toast.LENGTH_SHORT).show();
                        break;
                    case AmarinoUtil.SignIn:
                        Intent intentSmartLock = new Intent(LoginActivity.this, SmartLockActivity.class);
                        startActivity(intentSmartLock);
                        break;
                    case AmarinoUtil.SignUp:
                        Intent intentSignUp = new Intent(LoginActivity.this, SignUpActivity.class);
                        startActivity(intentSignUp);
                        break;
                }
            }
        }, this, AmarinoUtil.LoginFlag);
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
