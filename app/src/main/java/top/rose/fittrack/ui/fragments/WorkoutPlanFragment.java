package top.rose.fittrack.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import top.rose.fittrack.R;
import top.rose.fittrack.auth.AuthManager;
import top.rose.fittrack.database.DatabaseManager;
import top.rose.fittrack.database.entity.WorkoutPlan;
import top.rose.fittrack.ui.activity.AddWorkoutPlanActivity;
import top.rose.fittrack.ui.activity.EditWorkoutPlanActivity;
import top.rose.fittrack.ui.activity.WorkoutPlanDetailActivity;
import top.rose.fittrack.ui.adapter.WorkoutPlanAdapter;

public class WorkoutPlanFragment extends Fragment implements WorkoutPlanAdapter.OnItemClickListener {
    
    private static final int REQUEST_ADD_PLAN = 1001;
    private static final int REQUEST_EDIT_PLAN = 1002;
    
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddPlan;
    private TextView tvEmptyState;
    private DatabaseManager databaseManager;
    private AuthManager authManager;
    private WorkoutPlanAdapter adapter;
    private List<WorkoutPlan> workoutPlans;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseManager = DatabaseManager.getInstance(requireContext());
        authManager = AuthManager.getInstance(requireContext());
        workoutPlans = new ArrayList<>();
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
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        adapter = new WorkoutPlanAdapter(workoutPlans);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }
    
    private void setupFab() {
        fabAddPlan.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddWorkoutPlanActivity.class);
            startActivityForResult(intent, REQUEST_ADD_PLAN);
        });
    }
    
    private void loadWorkoutPlans() {
        int userId = authManager.getCurrentUserId();
        if (userId == -1) {
            showEmptyState();
            return;
        }
        
        databaseManager.executeInBackground(() -> {
            List<WorkoutPlan> plans = databaseManager.getDatabase()
                    .workoutPlanDao()
                    .getActiveWorkoutPlansByUser(userId);
            
            requireActivity().runOnUiThread(() -> {
                workoutPlans.clear();
                workoutPlans.addAll(plans);
                adapter.updateData(workoutPlans);
                
                if (workoutPlans.isEmpty()) {
                    showEmptyState();
                } else {
                    hideEmptyState();
                }
            });
        });
    }
    
    @Override
    public void onItemClick(WorkoutPlan workoutPlan) {
        Intent intent = new Intent(getContext(), WorkoutPlanDetailActivity.class);
        intent.putExtra(WorkoutPlanDetailActivity.EXTRA_WORKOUT_PLAN_ID, workoutPlan.getId());
        startActivity(intent);
    }
    
    @Override
    public void onItemLongClick(WorkoutPlan workoutPlan) {
        new AlertDialog.Builder(requireContext())
                .setTitle("操作选择")
                .setItems(new String[]{"编辑", "删除"}, (dialog, which) -> {
                    if (which == 0) {
                        // 编辑训练计划
                        editWorkoutPlan(workoutPlan);
                    } else {
                        // 删除训练计划
                        deleteWorkoutPlan(workoutPlan);
                    }
                })
                .show();
    }
    
    private void editWorkoutPlan(WorkoutPlan workoutPlan) {
        Intent intent = new Intent(getContext(), EditWorkoutPlanActivity.class);
        intent.putExtra(EditWorkoutPlanActivity.EXTRA_WORKOUT_PLAN_ID, workoutPlan.getId());
        startActivityForResult(intent, REQUEST_EDIT_PLAN);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_ADD_PLAN || requestCode == REQUEST_EDIT_PLAN) 
                && resultCode == Activity.RESULT_OK) {
            loadWorkoutPlans();
        }
    }
    
    private void deleteWorkoutPlan(WorkoutPlan workoutPlan) {
        new AlertDialog.Builder(requireContext())
                .setTitle("确认删除")
                .setMessage("确定要删除训练计划 \"" + workoutPlan.getName() + "\" 吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    databaseManager.executeInBackground(() -> {
                        // 软删除：设置为非活跃状态
                        workoutPlan.setActive(false);
                        workoutPlan.setUpdatedAt(System.currentTimeMillis());
                        databaseManager.getDatabase()
                                .workoutPlanDao()
                                .updateWorkoutPlan(workoutPlan);
                        
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                            loadWorkoutPlans();
                        });
                    });
                })
                .setNegativeButton("取消", null)
                .show();
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