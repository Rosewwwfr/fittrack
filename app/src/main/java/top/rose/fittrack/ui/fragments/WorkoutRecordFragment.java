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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import top.rose.fittrack.R;
import top.rose.fittrack.database.DatabaseManager;
import top.rose.fittrack.auth.AuthManager;
import top.rose.fittrack.database.entity.WorkoutRecord;
import top.rose.fittrack.ui.activity.AddWorkoutRecordActivity;
import top.rose.fittrack.ui.activity.EditWorkoutRecordActivity;
import top.rose.fittrack.ui.activity.WorkoutRecordDetailActivity;
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
    
    private static final int REQUEST_CODE_DETAIL = 1002;
    
    private void setupRecyclerView() {
        adapter = new WorkoutRecordAdapter(workoutRecords);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        
        adapter.setOnItemClickListener(new WorkoutRecordAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(WorkoutRecord workoutRecord) {
                // 打开详情页面
                Intent intent = new Intent(getContext(), WorkoutRecordDetailActivity.class);
                intent.putExtra(WorkoutRecordDetailActivity.EXTRA_WORKOUT_RECORD_ID, workoutRecord.getId());
                startActivityForResult(intent, REQUEST_CODE_DETAIL);
            }
            
            @Override
            public void onItemLongClick(WorkoutRecord workoutRecord) {
                // 显示操作选项对话框
                showActionDialog(workoutRecord);
            }
        });
    }
    
    private void showActionDialog(WorkoutRecord workoutRecord) {
        String[] options = {"查看详情", "编辑", "删除"};
        
        new AlertDialog.Builder(requireContext())
                .setTitle(workoutRecord.getExerciseName())
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // 查看详情
                            Intent detailIntent = new Intent(getContext(), WorkoutRecordDetailActivity.class);
                            detailIntent.putExtra(WorkoutRecordDetailActivity.EXTRA_WORKOUT_RECORD_ID, workoutRecord.getId());
                            startActivityForResult(detailIntent, REQUEST_CODE_DETAIL);
                            break;
                        case 1: // 编辑
                            Intent editIntent = new Intent(getContext(), EditWorkoutRecordActivity.class);
                            editIntent.putExtra(EditWorkoutRecordActivity.EXTRA_WORKOUT_RECORD_ID, workoutRecord.getId());
                            startActivityForResult(editIntent, REQUEST_CODE_DETAIL);
                            break;
                        case 2: // 删除
                            showDeleteConfirmDialog(workoutRecord);
                            break;
                    }
                })
                .show();
    }
    
    private void showDeleteConfirmDialog(WorkoutRecord workoutRecord) {
        new AlertDialog.Builder(requireContext())
                .setTitle("删除训练记录")
                .setMessage("确定要删除 \"" + workoutRecord.getExerciseName() + "\" 这条训练记录吗？此操作不可撤销。")
                .setPositiveButton("删除", (dialog, which) -> deleteWorkoutRecord(workoutRecord))
                .setNegativeButton("取消", null)
                .show();
    }
    
    private void deleteWorkoutRecord(WorkoutRecord workoutRecord) {
        databaseManager.executeInBackground(() -> {
            databaseManager.getDatabase().workoutRecordDao().deleteWorkoutRecord(workoutRecord);
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "训练记录已删除", Toast.LENGTH_SHORT).show();
                loadWorkoutRecords(); // 重新加载数据
            });
        });
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DETAIL && resultCode == Activity.RESULT_OK) {
            // 重新加载数据
            loadWorkoutRecords();
        }
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