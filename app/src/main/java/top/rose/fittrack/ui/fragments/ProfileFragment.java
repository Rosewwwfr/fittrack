package top.rose.fittrack.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;

import top.rose.fittrack.R;
import top.rose.fittrack.auth.AuthManager;
import top.rose.fittrack.ui.auth.LoginActivity;

public class ProfileFragment extends Fragment {
    
    private TextView tvUsername;
    private TextView tvEmail;
    private MaterialCardView cardPersonalInfo;
    private MaterialCardView cardSettings;
    private MaterialCardView cardStatistics;
    private Button btnLogout;
    private AuthManager authManager;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authManager = AuthManager.getInstance(requireContext());
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        initViews(view);
        setupUserInfo();
        setupClickListeners();
        
        return view;
    }
    
    private void initViews(View view) {
        tvUsername = view.findViewById(R.id.tv_username);
        tvEmail = view.findViewById(R.id.tv_email);
        cardPersonalInfo = view.findViewById(R.id.card_personal_info);
        cardSettings = view.findViewById(R.id.card_settings);
        cardStatistics = view.findViewById(R.id.card_statistics);
        btnLogout = view.findViewById(R.id.btn_logout);
    }
    
    private void setupUserInfo() {
        String username = authManager.getCurrentUsername();
        if (username != null) {
            tvUsername.setText(username);
            tvEmail.setText("用户邮箱"); // TODO: 从数据库获取邮箱
        }
    }
    
    private void setupClickListeners() {
        cardPersonalInfo.setOnClickListener(v -> {
            // TODO: 打开个人信息编辑页面
        });
        
        cardSettings.setOnClickListener(v -> {
            // TODO: 打开设置页面
        });
        
        cardStatistics.setOnClickListener(v -> {
            // TODO: 打开统计页面
        });
        
        btnLogout.setOnClickListener(v -> {
            logout();
        });
    }
    
    private void logout() {
        authManager.logout();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}