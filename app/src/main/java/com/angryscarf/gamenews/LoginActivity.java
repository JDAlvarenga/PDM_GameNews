package com.angryscarf.gamenews;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.angryscarf.gamenews.Data.GameNewsRepository;
import com.angryscarf.gamenews.Model.GameNewsViewModel;

import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

public class LoginActivity extends AppCompatActivity {

    private GameNewsViewModel viewModel;

    private EditText user, pass;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(this).get(GameNewsViewModel.class);

        if (viewModel.isLoggedIn()) {
            onLoggedIn();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user = findViewById(R.id.login_edit_username);
        pass = findViewById(R.id.login_edit_password);

        login = findViewById(R.id.login_button_login);
        login.setOnClickListener(view -> {
            logIn(user.getText().toString(), pass.getText().toString());
        });



    }

    public void logIn(String user, String password) {
        if (user.isEmpty() || password.isEmpty())  onFailedLogIn(new InvalidCredentialsException());
        viewModel.login(user, password)
        .subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
                login.setClickable(false);
            }

            @Override
            public void onComplete() {
                onLoggedIn();
            }

            @Override
            public void onError(Throwable e) {
                login.setClickable(true);
                if(e instanceof HttpException) {
                    if (((HttpException) e).code() == 401) {
                        onFailedLogIn(new InvalidCredentialsException());
                    }
                }
                else {
                    onFailedLogIn(e);
                }
            }

        });
    }

    public void onLoggedIn() {
        Log.d("LOGIN", "DEBUG: Called onLogedIn");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onFailedLogIn(Throwable e) {
        if ((e instanceof GameNewsRepository.NoConnectionException)) {
            Toast.makeText(this, R.string.no_connection_message, Toast.LENGTH_SHORT).show();
        }
        else if (e instanceof InvalidCredentialsException) {
            Toast.makeText(this, R.string.wrong_credentials_message, Toast.LENGTH_SHORT).show();
        }
    }

    public class InvalidCredentialsException extends Throwable {}
}
