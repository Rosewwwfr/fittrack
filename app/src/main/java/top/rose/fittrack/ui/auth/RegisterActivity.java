package top.rose.fittrack.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import top.rose.fittrack.MainActivity;
import top.rose.fittrack.R;
import top.rose.fittrack.auth.AuthManager;

public class RegisterActivity extends AppCompatActivity {
    
    private EditText etUsername, etEmail, etPassword, etConfirmPassword, etName;
    private Button btnRegister;
    private TextView tvLogin;
    private ProgressBar progressBar;
    private AuthManager authManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        initViews();
        initAuthManager();
        setupClickListeners();
    }
    
    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etName = findViewById(R.id.et_name);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);
        progressBar = findViewById(R.id.progress_bar);
    }
    
    private void initAuthManager() {
        authManager = AuthManager.getInstance(this);
    }
    
    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> performRegister());
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
    
    private void performRegister() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String name = etName.getText().toString().trim();
        
        if (validateInput(username, email, password, confirmPassword, name)) {
            showLoading(true);
            
            authManager.register(username, email, password, name)
                    .thenAccept(result -> runOnUiThread(() -> {
                        showLoading(false);
                        
                        if (result.isSuccess()) {
                            Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, result.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }))
                    .exceptionally(throwable -> {
                        runOnUiThread(() -> {
                            showLoading(false);
                            Toast.makeText(this, "注册失败，请重试", Toast.LENGTH_LONG).show();
                        });
                        return null;
                    });
        }
    }
    
    private boolean validateInput(String username, String email, String password, String confirmPassword, String name) {
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("请输入用户名");
            etUsername.requestFocus();
            return false;
        }
        
        if (username.length() < 3) {
            etUsername.setError("用户名至少3位");
            etUsername.requestFocus();
            return false;
        }
        
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("请输入邮箱");
            etEmail.requestFocus();
            return false;
        }
        
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("请输入有效的邮箱地址");
            etEmail.requestFocus();
            return false;
        }
        
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("请输入密码");
            etPassword.requestFocus();
            return false;
        }
        
        if (password.length() < 6) {
            etPassword.setError("密码至少6位");
            etPassword.requestFocus();
            return false;
        }
        
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("两次密码输入不一致");
            etConfirmPassword.requestFocus();
            return false;
        }
        
        if (TextUtils.isEmpty(name)) {
            etName.setError("请输入姓名");
            etName.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!show);
    }
}