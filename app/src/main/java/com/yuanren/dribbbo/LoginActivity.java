package com.yuanren.dribbbo;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.yuanren.dribbbo.R;
import com.yuanren.dribbbo.dribbble.Dribbble;
import com.yuanren.dribbbo.dribbble.auth.Auth;
import com.yuanren.dribbbo.dribbble.auth.AuthActivity;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.activity_login_btn) TextView loginBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        Dribbble.init(this);

        if (Dribbble.isLoggedIn()){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Auth.openAuthActivity(LoginActivity.this);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Auth.REQ_CODE) {
            final String authCode = data.getStringExtra(AuthActivity.KEY_CODE);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String token = Auth.fetchAccessToken(authCode);
                        Dribbble.login(LoginActivity.this, token);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }
}
