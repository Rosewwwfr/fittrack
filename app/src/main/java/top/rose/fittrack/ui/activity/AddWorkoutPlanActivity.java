package top.rose.fittrack.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import top.rose.fittrack.R;
import top.rose.fittrack.auth.AuthManager;
import top.rose.fittrack.database.DatabaseManager;
import top.rose.fittrack.database.entity.WorkoutPlan;

public class AddWorkoutPlanActivity extends AppCompatActivity {
    
    private EditText etPlanName;
    private EditText etDescription;
    private Spinner spinnerDifficulty;
    private SeekBar seekBarDuration;
    private TextView tvDuration;
    private SeekBar seekBarFrequency;
    private TextView tvFrequency;
    private EditText etTargetMuscles;
    private EditText etExercises;
    private Button btnSave;
    
    private DatabaseManager databaseManager;
    private AuthManager authManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout_plan);
        
        databaseManager = DatabaseManager.getInstance(this);
        authManager = AuthManager.getInstance(this);
        
        initViews();
        setupToolbar();
        setupSpinner();
        setupSeekBars();
        setupListeners();
    }
    
    private void initViews() {
        etPlanName = findViewById(R.id.et_plan_name);
        etDescription = findViewById(R.id.et_description);
        spinnerDifficulty = findViewById(R.id.spinner_difficulty);
        seekBarDuration = findViewById(R.id.seekbar_duration);
        tvDuration = findViewById(R.id.tv_duration);
        seekBarFrequency = findViewById(R.id.seekbar_frequency);
        tvFrequency = findViewById(R.id.tv_frequency);
        etTargetMuscles = findViewById(R.id.et_target_muscles);
        etExercises = findViewById(R.id.et_exercises);
        btnSave = findViewById(R.id.btn_save);
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("创建训练计划");
        }
    }
    
    private void setupSpinner() {
        String[] difficulties = {"初级", "中级", "高级"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, difficulties);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(adapter);
    }
    
    private void setupSeekBars() {
        // 训练时长 SeekBar (15-120分钟)
        seekBarDuration.setMax(105); // 120-15
        seekBarDuration.setProgress(45); // 默认60分钟
        tvDuration.setText("60分钟");
        
        seekBarDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int duration = progress + 15;
                tvDuration.setText(duration + "分钟");
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        // 训练频率 SeekBar (1-7次/周)
        seekBarFrequency.setMax(6); // 7-1
        seekBarFrequency.setProgress(2); // 默认3次/周
        tvFrequency.setText("每周3次");
        
        seekBarFrequency.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int frequency = progress + 1;
                tvFrequency.setText("每周" + frequency + "次");
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
    
    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveWorkoutPlan());
    }
    
    private void saveWorkoutPlan() {
        String planName = etPlanName.getText().toString().trim();
        if (TextUtils.isEmpty(planName)) {
            etPlanName.setError("请输入计划名称");
            return;
        }
        
        String description = etDescription.getText().toString().trim();
        if (TextUtils.isEmpty(description)) {
            etDescription.setError("请输入计划描述");
            return;
        }
        
        String difficulty = getDifficultyValue(spinnerDifficulty.getSelectedItemPosition());
        int duration = seekBarDuration.getProgress() + 15;
        int frequency = seekBarFrequency.getProgress() + 1;
        String targetMuscles = etTargetMuscles.getText().toString().trim();
        String exercises = etExercises.getText().toString().trim();
        
        int userId = authManager.getCurrentUserId();
        if (userId == -1) {
            Toast.makeText(this, "用户未登录", Toast.LENGTH_SHORT).show();
            return;
        }
        
        WorkoutPlan workoutPlan = new WorkoutPlan(
                userId, planName, description, difficulty
        );
        workoutPlan.setEstimatedDurationMinutes(duration);
        workoutPlan.setWeeklyFrequency(frequency);
        workoutPlan.setTargetMuscleGroups(targetMuscles);
        workoutPlan.setExercises(exercises);
        workoutPlan.setActive(true);
        workoutPlan.setCreatedAt(System.currentTimeMillis());
        workoutPlan.setUpdatedAt(System.currentTimeMillis());
        
        databaseManager.executeInBackground(() -> {
            long planId = databaseManager.getDatabase()
                    .workoutPlanDao()
                    .insertWorkoutPlan(workoutPlan);
            
            runOnUiThread(() -> {
                if (planId > 0) {
                    Toast.makeText(this, "训练计划创建成功", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, "创建失败，请重试", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    
    private String getDifficultyValue(int position) {
        switch (position) {
            case 0:
                return "BEGINNER";
            case 1:
                return "INTERMEDIATE";
            case 2:
                return "ADVANCED";
            default:
                return "BEGINNER";
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