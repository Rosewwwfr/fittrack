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
import top.rose.fittrack.database.DatabaseManager;
import top.rose.fittrack.database.entity.WorkoutPlan;

public class EditWorkoutPlanActivity extends AppCompatActivity {
    
    public static final String EXTRA_WORKOUT_PLAN_ID = "workout_plan_id";
    
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
    private WorkoutPlan workoutPlan;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_workout_plan);
        
        databaseManager = DatabaseManager.getInstance(this);
        
        initViews();
        setupToolbar();
        setupSpinner();
        setupSeekBars();
        setupListeners();
        loadWorkoutPlan();
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
            getSupportActionBar().setTitle("编辑训练计划");
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
                    populateFields();
                } else {
                    Toast.makeText(this, "计划不存在", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }
    
    private void populateFields() {
        etPlanName.setText(workoutPlan.getName());
        etDescription.setText(workoutPlan.getDescription());
        
        // 设置难度
        int difficultyPosition = getDifficultyPosition(workoutPlan.getDifficulty());
        spinnerDifficulty.setSelection(difficultyPosition);
        
        // 设置时长
        int duration = workoutPlan.getEstimatedDurationMinutes();
        seekBarDuration.setProgress(duration - 15);
        tvDuration.setText(duration + "分钟");
        
        // 设置频率
        int frequency = workoutPlan.getWeeklyFrequency();
        seekBarFrequency.setProgress(frequency - 1);
        tvFrequency.setText("每周" + frequency + "次");
        
        // 设置目标肌肉群和训练动作
        if (workoutPlan.getTargetMuscleGroups() != null) {
            etTargetMuscles.setText(workoutPlan.getTargetMuscleGroups());
        }
        if (workoutPlan.getExercises() != null) {
            etExercises.setText(workoutPlan.getExercises());
        }
    }
    
    private int getDifficultyPosition(String difficulty) {
        switch (difficulty) {
            case "BEGINNER":
                return 0;
            case "INTERMEDIATE":
                return 1;
            case "ADVANCED":
                return 2;
            default:
                return 0;
        }
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
        
        // 更新训练计划信息
        workoutPlan.setName(planName);
        workoutPlan.setDescription(description);
        workoutPlan.setDifficulty(difficulty);
        workoutPlan.setEstimatedDurationMinutes(duration);
        workoutPlan.setWeeklyFrequency(frequency);
        workoutPlan.setTargetMuscleGroups(targetMuscles);
        workoutPlan.setExercises(exercises);
        workoutPlan.setUpdatedAt(System.currentTimeMillis());
        
        databaseManager.executeInBackground(() -> {
            databaseManager.getDatabase()
                    .workoutPlanDao()
                    .updateWorkoutPlan(workoutPlan);
            
            runOnUiThread(() -> {
                Toast.makeText(this, "训练计划更新成功", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
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