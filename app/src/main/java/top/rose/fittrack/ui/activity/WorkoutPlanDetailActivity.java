package top.rose.fittrack.ui.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import top.rose.fittrack.R;
import top.rose.fittrack.database.DatabaseManager;
import top.rose.fittrack.database.entity.WorkoutPlan;

public class WorkoutPlanDetailActivity extends AppCompatActivity {
    
    public static final String EXTRA_WORKOUT_PLAN_ID = "workout_plan_id";
    
    private TextView tvPlanName;
    private TextView tvDescription;
    private TextView tvDifficulty;
    private TextView tvDuration;
    private TextView tvFrequency;
    private TextView tvTargetMuscles;
    private TextView tvExercises;
    private TextView tvCreatedAt;
    private TextView tvUpdatedAt;
    
    private DatabaseManager databaseManager;
    private WorkoutPlan workoutPlan;
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_plan_detail);
        
        databaseManager = DatabaseManager.getInstance(this);
        
        initViews();
        setupToolbar();
        loadWorkoutPlan();
    }
    
    private void initViews() {
        tvPlanName = findViewById(R.id.tv_plan_name);
        tvDescription = findViewById(R.id.tv_description);
        tvDifficulty = findViewById(R.id.tv_difficulty);
        tvDuration = findViewById(R.id.tv_duration);
        tvFrequency = findViewById(R.id.tv_frequency);
        tvTargetMuscles = findViewById(R.id.tv_target_muscles);
        tvExercises = findViewById(R.id.tv_exercises);
        tvCreatedAt = findViewById(R.id.tv_created_at);
        tvUpdatedAt = findViewById(R.id.tv_updated_at);
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("训练计划详情");
        }
    }
    
    private void loadWorkoutPlan() {
        int planId = getIntent().getIntExtra(EXTRA_WORKOUT_PLAN_ID, -1);
        if (planId == -1) {
            Toast.makeText(this, "计划不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        databaseManager.executeInBackground(() -> {
            workoutPlan = databaseManager.getDatabase()
                    .workoutPlanDao()
                    .getWorkoutPlanById(planId);
            
            runOnUiThread(() -> {
                if (workoutPlan != null) {
                    displayWorkoutPlan();
                } else {
                    Toast.makeText(this, "计划不存在", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }
    
    private void displayWorkoutPlan() {
        tvPlanName.setText(workoutPlan.getName());
        tvDescription.setText(workoutPlan.getDescription());
        tvDifficulty.setText(getDifficultyText(workoutPlan.getDifficulty()));
        tvDuration.setText(workoutPlan.getEstimatedDurationMinutes() + "分钟");
        tvFrequency.setText("每周" + workoutPlan.getWeeklyFrequency() + "次");
        
        if (workoutPlan.getTargetMuscleGroups() != null && !workoutPlan.getTargetMuscleGroups().isEmpty()) {
            tvTargetMuscles.setText(workoutPlan.getTargetMuscleGroups());
        } else {
            tvTargetMuscles.setText("未设置");
        }
        
        if (workoutPlan.getExercises() != null && !workoutPlan.getExercises().isEmpty()) {
            tvExercises.setText(workoutPlan.getExercises());
        } else {
            tvExercises.setText("未设置");
        }
        
        tvCreatedAt.setText(dateTimeFormat.format(new Date(workoutPlan.getCreatedAt())));
        tvUpdatedAt.setText(dateTimeFormat.format(new Date(workoutPlan.getUpdatedAt())));
        
        // 设置难度颜色
        int difficultyColor = getDifficultyColor(workoutPlan.getDifficulty());
        tvDifficulty.setTextColor(difficultyColor);
    }
    
    private String getDifficultyText(String difficulty) {
        switch (difficulty) {
            case "BEGINNER":
                return "初级";
            case "INTERMEDIATE":
                return "中级";
            case "ADVANCED":
                return "高级";
            default:
                return "未知";
        }
    }
    
    private int getDifficultyColor(String difficulty) {
        switch (difficulty) {
            case "BEGINNER":
                return 0xFF4CAF50; // 绿色
            case "INTERMEDIATE":
                return 0xFFFF9800; // 橙色
            case "ADVANCED":
                return 0xFFF44336; // 红色
            default:
                return 0xFF757575; // 灰色
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}