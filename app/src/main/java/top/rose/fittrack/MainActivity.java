package top.rose.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import top.rose.fittrack.auth.AuthManager;
import top.rose.fittrack.ui.auth.LoginActivity;
import top.rose.fittrack.ui.fragments.ProfileFragment;
import top.rose.fittrack.ui.fragments.WorkoutPlanFragment;
import top.rose.fittrack.ui.fragments.WorkoutRecordFragment;

public class MainActivity extends AppCompatActivity {
    
    private AuthManager authManager;
    private BottomNavigationView bottomNavigation;
    private FragmentManager fragmentManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        authManager = AuthManager.getInstance(this);
        
        // 检查用户是否已登录
        if (!authManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        
        setContentView(R.layout.activity_main);
        
        initViews();
        setupToolbar();
        setupBottomNavigation();
        
        // 默认显示训练记录页面
        if (savedInstanceState == null) {
            loadFragment(new WorkoutRecordFragment());
        }
    }
    
    private void initViews() {
        bottomNavigation = findViewById(R.id.bottom_navigation);
        fragmentManager = getSupportFragmentManager();
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("FitTrack");
        }
    }
    
    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_workout_record) {
                fragment = new WorkoutRecordFragment();
            } else if (itemId == R.id.nav_workout_plan) {
                fragment = new WorkoutPlanFragment();
            } else if (itemId == R.id.nav_profile) {
                fragment = new ProfileFragment();
            }
            
            return loadFragment(fragment);
        });
    }
    
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();
            return true;
        }
        return false;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void logout() {
        authManager.logout();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}