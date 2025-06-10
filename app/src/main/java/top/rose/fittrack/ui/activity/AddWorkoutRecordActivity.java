package top.rose.fittrack.ui.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import top.rose.fittrack.R;
import top.rose.fittrack.auth.AuthManager;
import top.rose.fittrack.database.DatabaseManager;
import top.rose.fittrack.database.entity.WorkoutRecord;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddWorkoutRecordActivity extends AppCompatActivity {
    
    private TextInputEditText etExerciseName;
    private Spinner spinnerExerciseType;
    private TextInputEditText etSets, etReps, etWeight;
    private TextInputEditText etDuration, etDistance, etCalories;
    private TextInputEditText etDurationOther, etCaloriesOther;
    private TextInputEditText etNotes;
    private TextView tvSelectedDateTime;
    private MaterialButton btnSelectDateTime, btnSave;
    
    // 参数区域布局
    private LinearLayout layoutStrengthParams;
    private LinearLayout layoutCardioParams;
    private LinearLayout layoutOtherParams;
    
    private DatabaseManager databaseManager;
    private AuthManager authManager;
    private Calendar selectedDateTime;
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault());
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout_record);
        
        databaseManager = DatabaseManager.getInstance(this);
        authManager = AuthManager.getInstance(this);
        selectedDateTime = Calendar.getInstance();
        
        initViews();
        setupToolbar();
        setupSpinner();
        setupListeners();
        updateDateTimeDisplay();
    }
    
    private void initViews() {
        etExerciseName = findViewById(R.id.et_exercise_name);
        spinnerExerciseType = findViewById(R.id.spinner_exercise_type);
        etSets = findViewById(R.id.et_sets);
        etReps = findViewById(R.id.et_reps);
        etWeight = findViewById(R.id.et_weight);
        etDuration = findViewById(R.id.et_duration);
        etDistance = findViewById(R.id.et_distance);
        etCalories = findViewById(R.id.et_calories);
        etDurationOther = findViewById(R.id.et_duration_other);
        etCaloriesOther = findViewById(R.id.et_calories_other);
        etNotes = findViewById(R.id.et_notes);
        tvSelectedDateTime = findViewById(R.id.tv_selected_datetime);
        btnSelectDateTime = findViewById(R.id.btn_select_datetime);
        btnSave = findViewById(R.id.btn_save);
        
        // 参数区域布局
        layoutStrengthParams = findViewById(R.id.layout_strength_params);
        layoutCardioParams = findViewById(R.id.layout_cardio_params);
        layoutOtherParams = findViewById(R.id.layout_other_params);
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("添加训练记录");
        }
    }
    
    private void setupSpinner() {
        String[] exerciseTypes = {"力量训练", "有氧运动", "柔韧性", "运动"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, exerciseTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExerciseType.setAdapter(adapter);
        
        // 添加选择监听器
        spinnerExerciseType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateParameterVisibility(position);
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 默认显示力量训练参数
                updateParameterVisibility(0);
            }
        });
    }
    
    private void updateParameterVisibility(int exerciseTypePosition) {
        // 隐藏所有参数区域
        layoutStrengthParams.setVisibility(View.GONE);
        layoutCardioParams.setVisibility(View.GONE);
        layoutOtherParams.setVisibility(View.GONE);
        
        // 根据选择的运动类型显示相应的参数区域
        switch (exerciseTypePosition) {
            case 0: // 力量训练
                layoutStrengthParams.setVisibility(View.VISIBLE);
                break;
            case 1: // 有氧运动
                layoutCardioParams.setVisibility(View.VISIBLE);
                break;
            case 2: // 柔韧性
            case 3: // 运动
                layoutOtherParams.setVisibility(View.VISIBLE);
                break;
        }
    }
    
    private void setupListeners() {
        btnSelectDateTime.setOnClickListener(v -> showDateTimePicker());
        btnSave.setOnClickListener(v -> saveWorkoutRecord());
    }
    
    private void showDateTimePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            this,
                            (timeView, hourOfDay, minute) -> {
                                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                selectedDateTime.set(Calendar.MINUTE, minute);
                                updateDateTimeDisplay();
                            },
                            selectedDateTime.get(Calendar.HOUR_OF_DAY),
                            selectedDateTime.get(Calendar.MINUTE),
                            true
                    );
                    timePickerDialog.show();
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
    
    private void updateDateTimeDisplay() {
        tvSelectedDateTime.setText(dateTimeFormat.format(selectedDateTime.getTime()));
    }
    
    private void saveWorkoutRecord() {
        String exerciseName = etExerciseName.getText().toString().trim();
        if (TextUtils.isEmpty(exerciseName)) {
            etExerciseName.setError("请输入运动名称");
            return;
        }
        
        String exerciseType = getExerciseTypeValue(spinnerExerciseType.getSelectedItemPosition());
        int exerciseTypePosition = spinnerExerciseType.getSelectedItemPosition();
        
        WorkoutRecord record = new WorkoutRecord(authManager.getCurrentUserId(), exerciseName, exerciseType);
        record.setRecordedAt(selectedDateTime.getTimeInMillis());
        
        // 根据运动类型设置相应的参数
        try {
            switch (exerciseTypePosition) {
                case 0: // 力量训练
                    setStrengthParameters(record);
                    break;
                case 1: // 有氧运动
                    setCardioParameters(record);
                    break;
                case 2: // 柔韧性
                case 3: // 运动
                    setOtherParameters(record);
                    break;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入有效的数字", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String notes = etNotes.getText().toString().trim();
        if (!TextUtils.isEmpty(notes)) {
            record.setNotes(notes);
        }
        
        // 保存到数据库
        databaseManager.executeInBackground(() -> {
            databaseManager.getDatabase().workoutRecordDao().insertWorkoutRecord(record);
            runOnUiThread(() -> {
                Toast.makeText(this, "训练记录已保存", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
    
    private void setStrengthParameters(WorkoutRecord record) {
        String setsStr = etSets.getText().toString().trim();
        if (!TextUtils.isEmpty(setsStr)) {
            record.setSets(Integer.parseInt(setsStr));
        }
        
        String repsStr = etReps.getText().toString().trim();
        if (!TextUtils.isEmpty(repsStr)) {
            record.setReps(Integer.parseInt(repsStr));
        }
        
        String weightStr = etWeight.getText().toString().trim();
        if (!TextUtils.isEmpty(weightStr)) {
            record.setWeight(Float.parseFloat(weightStr));
        }
    }
    
    private void setCardioParameters(WorkoutRecord record) {
        String durationStr = etDuration.getText().toString().trim();
        if (!TextUtils.isEmpty(durationStr)) {
            record.setDurationMinutes(Integer.parseInt(durationStr));
        }
        
        String distanceStr = etDistance.getText().toString().trim();
        if (!TextUtils.isEmpty(distanceStr)) {
            record.setDistance(Float.parseFloat(distanceStr));
        }
        
        String caloriesStr = etCalories.getText().toString().trim();
        if (!TextUtils.isEmpty(caloriesStr)) {
            record.setCaloriesBurned(Integer.parseInt(caloriesStr));
        }
    }
    
    private void setOtherParameters(WorkoutRecord record) {
        String durationStr = etDurationOther.getText().toString().trim();
        if (!TextUtils.isEmpty(durationStr)) {
            record.setDurationMinutes(Integer.parseInt(durationStr));
        }
        
        String caloriesStr = etCaloriesOther.getText().toString().trim();
        if (!TextUtils.isEmpty(caloriesStr)) {
            record.setCaloriesBurned(Integer.parseInt(caloriesStr));
        }
    }
    
    private String getExerciseTypeValue(int position) {
        switch (position) {
            case 0: return "STRENGTH";
            case 1: return "CARDIO";
            case 2: return "FLEXIBILITY";
            case 3: return "SPORTS";
            default: return "STRENGTH";
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