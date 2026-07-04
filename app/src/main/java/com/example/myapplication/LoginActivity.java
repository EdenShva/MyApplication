package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth auth;
    private boolean isRegisterMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            openMainActivity();
        }

        binding.btnAuth.setOnClickListener(v -> {
            if (isRegisterMode) {
                handleRegister();
            } else {
                handleLogin();
            }
        });

        binding.tvSwitchMode.setOnClickListener(v -> {
            isRegisterMode = !isRegisterMode;
            updateAuthMode();
        });
    }

    private void updateAuthMode() {
        if (isRegisterMode) {
            binding.tvAuthTitle.setText(R.string.register);
            binding.btnAuth.setText(R.string.register);
            binding.tvSwitchMode.setText(R.string.register_login_switch);
            binding.etFullName.setVisibility(View.VISIBLE);
            binding.etConfirmPassword.setVisibility(View.VISIBLE);
        } else {
            binding.tvAuthTitle.setText(R.string.login);
            binding.btnAuth.setText(R.string.login);
            binding.tvSwitchMode.setText(R.string.login_register_switch);
            binding.etFullName.setVisibility(View.GONE);
            binding.etConfirmPassword.setVisibility(View.GONE);
        }
    }

    private void handleLogin() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.enter_email_password, Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> openMainActivity())
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void handleRegister() {
        String fullName = binding.etFullName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, R.string.passwords_not_match, Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    Toast.makeText(this, R.string.account_created, Toast.LENGTH_SHORT).show();
                    openMainActivity();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void openMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}