package top.rose.fittrack.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import top.rose.fittrack.R;
import top.rose.fittrack.auth.AuthManager;
import top.rose.fittrack.database.DatabaseManager;
import top.rose.fittrack.database.entity.WorkoutRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WorkoutRecordDetailActivity extends AppCompatActivity {
    
    public static final String EXTRA_WORKOUT_RECORD_ID = "workout_record_id";
    public static final int REQUEST_CODE_EDIT = 1001;
    
    private TextView tvExerciseName, tvExerciseType, tvDateTime;
    private TextView tvSets, tvReps, tvWeight;
    private TextView tvDuration, tvDistance, tvCalories;
    private TextView tvNotes;
    
    private DatabaseManager databaseManager;
    private AuthManager authManager;
    private WorkoutRecord workoutRecord;
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault());
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_record_detail);
        
        databaseManager = DatabaseManager.getInstance(this);
        authManager = AuthManager.getInstance(this);
        
        initViews();
        setupToolbar();
        loadWorkoutRecord();
    }
    
    private void initViews() {
        tvExerciseName = findViewById(R.id.tv_exercise_name);
        tvExerciseType = findViewById(R.id.tv_exercise_type);
        tvDateTime = findViewById(R.id.tv_date_time);
        tvSets = findViewById(R.id.tv_sets);
        tvReps = findViewById(R.id.tv_reps);
        tvWeight = findViewById(R.id.tv_weight);
        tvDuration = findViewById(R.id.tv_duration);
        tvDistance = findViewById(R.id.tv_distance);
        tvCalories = findViewById(R.id.tv_calories);
        tvNotes = findViewById(R.id.tv_notes);
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("训练记录详情");
        }
    }
    
    private void loadWorkoutRecord() {
        int recordId = getIntent().getIntExtra(EXTRA_WORKOUT_RECORD_ID, -1);
        if (recordId == -1) {
            Toast.makeText(this, "记录不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        databaseManager.executeInBackground(() -> {
            workoutRecord = databaseManager.getDatabase()
                    .workoutRecordDao()
                    .getWorkoutRecordById(recordId);
            
            runOnUiThread(() -> {
                if (workoutRecord != null) {
                    displayWorkoutRecord();
                } else {
                    Toast.makeText(this, "记录不存在", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }
    
    private void displayWorkoutRecord() {
        tvExerciseName.setText(workoutRecord.getExerciseName());
        tvExerciseType.setText(getExerciseTypeText(workoutRecord.getExerciseType()));
        tvDateTime.setText(dateTimeFormat.format(new Date(workoutRecord.getRecordedAt())));
        
        // 力量训练参数
        if (workoutRecord.getSets() > 0) {
            tvSets.setText(String.valueOf(workoutRecord.getSets()));
        } else {
            tvSets.setText("-");
        }
        
        if (workoutRecord.getReps() > 0) {
            tvReps.setText(String.valueOf(workoutRecord.getReps()));
        } else {
            tvReps.setText("-");
        }
        
        if (workoutRecord.getWeight() > 0) {
            tvWeight.setText(workoutRecord.getWeight() + " kg");
        } else {
            tvWeight.setText("-");
        }
        
        // 有氧运动参数
        if (workoutRecord.getDurationMinutes() > 0) {
            tvDuration.setText(workoutRecord.getDurationMinutes() + " 分钟");
        } else {
            tvDuration.setText("-");
        }
        
        if (workoutRecord.getDistance() > 0) {
            tvDistance.setText(workoutRecord.getDistance() + " km");
        } else {
            tvDistance.setText("-");
        }
        
        if (workoutRecord.getCaloriesBurned() > 0) {
            tvCalories.setText(workoutRecord.getCaloriesBurned() + " 卡路里");
        } else {
            tvCalories.setText("-");
        }
        
        // 备注
        if (workoutRecord.getNotes() != null && !workoutRecord.getNotes().trim().isEmpty()) {
            tvNotes.setText(workoutRecord.getNotes());
        } else {
            tvNotes.setText("无备注");
        }
    }
    
    private String getExerciseTypeText(String type) {
        switch (type) {
            case "STRENGTH": return "力量训练";
            case "CARDIO": return "有氧运动";
            case "FLEXIBILITY": return "柔韧性";
            case "SPORTS": return "运动";
            default: return type;
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_workout_record_detail, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_edit) {
            editWorkoutRecord();
            return true;
        } else if (id == R.id.action_delete) {
            showDeleteConfirmDialog();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void editWorkoutRecord() {
        Intent intent = new Intent(this, EditWorkoutRecordActivity.class);
        intent.putExtra(EditWorkoutRecordActivity.EXTRA_WORKOUT_RECORD_ID, workoutRecord.getId());
        startActivityForResult(intent, REQUEST_CODE_EDIT);
    }
    
    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("删除训练记录")
                .setMessage("确定要删除这条训练记录吗？此操作不可撤销。")
                .setPositiveButton("删除", (dialog, which) -> deleteWorkoutRecord())
                .setNegativeButton("取消", null)
                .show();
    }
    
    private void deleteWorkoutRecord() {
        databaseManager.executeInBackground(() -> {
            databaseManager.getDatabase().workoutRecordDao().deleteWorkoutRecord(workoutRecord);
            runOnUiThread(() -> {
                Toast.makeText(this, "训练记录已删除", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            });
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK) {
            // 重新加载数据
            loadWorkoutRecord();
            setResult(RESULT_OK);
        }
    }
}