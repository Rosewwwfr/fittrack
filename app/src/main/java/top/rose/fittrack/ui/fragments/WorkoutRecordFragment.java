package top.rose.fittrack.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import top.rose.fittrack.R;
import top.rose.fittrack.database.DatabaseManager;
import top.rose.fittrack.auth.AuthManager;
import top.rose.fittrack.database.entity.WorkoutRecord;
import top.rose.fittrack.ui.activity.AddWorkoutRecordActivity;
import top.rose.fittrack.ui.adapter.WorkoutRecordAdapter;

import java.util.ArrayList;
import java.util.List;

public class WorkoutRecordFragment extends Fragment {
    
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddRecord;
    private TextView tvEmptyState;
    private DatabaseManager databaseManager;
    private AuthManager authManager;
    private WorkoutRecordAdapter adapter;
    private List<WorkoutRecord> workoutRecords;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseManager = DatabaseManager.getInstance(requireContext());
        authManager = AuthManager.getInstance(requireContext());
        workoutRecords = new ArrayList<>();
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout_record, container, false);
        
        initViews(view);
        setupRecyclerView();
        setupFab();
        
        return view;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        loadWorkoutRecords();
    }
    
    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.rv_workout_records);
        fabAddRecord = view.findViewById(R.id.fab_add_record);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);
    }
    
    private void setupRecyclerView() {
        adapter = new WorkoutRecordAdapter(workoutRecords);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        
        adapter.setOnItemClickListener(new WorkoutRecordAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(WorkoutRecord workoutRecord) {
                // TODO: 打开训练记录详情
            }
            
            @Override
            public void onItemLongClick(WorkoutRecord workoutRecord) {
                // TODO: 显示删除/编辑选项
            }
        });
    }
    
    private void setupFab() {
        fabAddRecord.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddWorkoutRecordActivity.class);
            startActivity(intent);
        });
    }
    
    private void loadWorkoutRecords() {
        databaseManager.executeInBackground(() -> {
            List<WorkoutRecord> records = databaseManager.getDatabase()
                    .workoutRecordDao()
                    .getAllWorkoutRecordsByUser(authManager.getCurrentUserId());
            
            requireActivity().runOnUiThread(() -> {
                workoutRecords.clear();
                workoutRecords.addAll(records);
                adapter.updateData(workoutRecords);
                
                if (workoutRecords.isEmpty()) {
                    showEmptyState();
                } else {
                    hideEmptyState();
                }
            });
        });
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