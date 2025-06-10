package top.rose.fittrack.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Index;
// 在类的开头添加 import
import androidx.room.Ignore;

@Entity(tableName = "workout_plans",
        foreignKeys = @ForeignKey(entity = User.class,
                                parentColumns = "id",
                                childColumns = "userId",
                                onDelete = ForeignKey.CASCADE),
        indices = {@Index("userId")})
public class WorkoutPlan {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private int userId;
    private String name;
    private String description;
    private String difficulty; // BEGINNER, INTERMEDIATE, ADVANCED
    private int estimatedDurationMinutes;
    private String targetMuscleGroups; // JSON格式存储目标肌肉群
    private String exercises; // JSON格式存储练习列表
    private int weeklyFrequency; // 每周训练频率
    private boolean isActive;
    private long createdAt;
    private long updatedAt;
    
    // 构造函数
    public WorkoutPlan() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.isActive = true;
    }

    // 在有参构造函数前添加 @Ignore 注解
    @Ignore
    public WorkoutPlan(int userId, String name, String description) {
        this();
        this.userId = userId;
        this.name = name;
        this.description = description;
    }

    @Ignore
    public WorkoutPlan(int userId, String name, String description, String difficulty) {
        this();
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.difficulty = difficulty;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    
    public int getEstimatedDurationMinutes() { return estimatedDurationMinutes; }
    public void setEstimatedDurationMinutes(int estimatedDurationMinutes) { 
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.updatedAt = System.currentTimeMillis();
    }
    
    public String getTargetMuscleGroups() { return targetMuscleGroups; }
    public void setTargetMuscleGroups(String targetMuscleGroups) { 
        this.targetMuscleGroups = targetMuscleGroups;
        this.updatedAt = System.currentTimeMillis();
    }
    
    public String getExercises() { return exercises; }
    public void setExercises(String exercises) { 
        this.exercises = exercises;
        this.updatedAt = System.currentTimeMillis();
    }
    
    public int getWeeklyFrequency() { return weeklyFrequency; }
    public void setWeeklyFrequency(int weeklyFrequency) { 
        this.weeklyFrequency = weeklyFrequency;
        this.updatedAt = System.currentTimeMillis();
    }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { 
        isActive = active;
        this.updatedAt = System.currentTimeMillis();
    }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}