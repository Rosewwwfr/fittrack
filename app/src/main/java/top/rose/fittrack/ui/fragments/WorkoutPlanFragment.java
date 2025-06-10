package top.rose.fittrack.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import top.rose.fittrack.R;
import top.rose.fittrack.database.DatabaseManager;
import top.rose.fittrack.auth.AuthManager;

public class WorkoutPlanFragment extends Fragment {
    
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddPlan;
    private TextView tvEmptyState;
    private DatabaseManager databaseManager;
    private AuthManager authManager;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseManager = DatabaseManager.getInstance(requireContext());
        authManager = AuthManager.getInstance(requireContext());
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout_plan, container, false);
        
        initViews(view);
        setupRecyclerView();
        setupFab();
        loadWorkoutPlans();
        
        return view;
    }
    
    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.rv_workout_plans);
        fabAddPlan = view.findViewById(R.id.fab_add_plan);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);
    }
    
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        // TODO: 设置适配器
    }
    
    private void setupFab() {
        fabAddPlan.setOnClickListener(v -> {
            // TODO: 打开创建训练计划的Activity或Dialog
        });
    }
    
    private void loadWorkoutPlans() {
        // TODO: 从数据库加载训练计划
        // 暂时显示空状态
        showEmptyState();
    }
    
    private void showEmptyState() {
        recyclerView.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.VISIBLE);
    }
    
    private void hideEmptyState() {
        recyclerView.setVisibility(View.VISIBLE);
        tvEmptyState.setVisibility(View.GONE);
    }
}