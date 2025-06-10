package top.rose.fittrack.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.Index;

@Entity(tableName = "workout_records",
        foreignKeys = {
            @ForeignKey(entity = User.class,
                       parentColumns = "id",
                       childColumns = "userId",
                       onDelete = ForeignKey.CASCADE),
            @ForeignKey(entity = WorkoutPlan.class,
                       parentColumns = "id",
                       childColumns = "planId",
                       onDelete = ForeignKey.SET_NULL)
        },
        indices = {@Index("userId"), @Index("planId"), @Index("recordedAt")})
public class WorkoutRecord {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private int userId;
    private Integer planId; // 可以为null，表示自由训练
    private String exerciseName;
    private String exerciseType; // STRENGTH, CARDIO, FLEXIBILITY, SPORTS
    private int sets;
    private int reps;
    private float weight; // 重量(kg)
    private int durationMinutes; // 持续时间
    private float distance; // 距离(km)，用于有氧运动
    private int caloriesBurned; // 消耗卡路里
    private String notes; // 备注
    private int difficultyRating; // 难度评分 1-10
    private long recordedAt; // 训练时间
    private long createdAt;
    
    // 构造函数
    public WorkoutRecord() {
        this.createdAt = System.currentTimeMillis();
        this.recordedAt = System.currentTimeMillis();
    }

    @Ignore
    public WorkoutRecord(int userId, String exerciseName, String exerciseType) {
        this();
        this.userId = userId;
        this.exerciseName = exerciseName;
        this.exerciseType = exerciseType;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public Integer getPlanId() { return planId; }
    public void setPlanId(Integer planId) { this.planId = planId; }
    
    public String getExerciseName() { return exerciseName; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }
    
    public String getExerciseType() { return exerciseType; }
    public void setExerciseType(String exerciseType) { this.exerciseType = exerciseType; }
    
    public int getSets() { return sets; }
    public void setSets(int sets) { this.sets = sets; }
    
    public int getReps() { return reps; }
    public void setReps(int reps) { this.reps = reps; }
    
    public float getWeight() { return weight; }
    public void setWeight(float weight) { this.weight = weight; }
    
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    
    public float getDistance() { return distance; }
    public void setDistance(float distance) { this.distance = distance; }
    
    public int getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(int caloriesBurned) { this.caloriesBurned = caloriesBurned; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public int getDifficultyRating() { return difficultyRating; }
    public void setDifficultyRating(int difficultyRating) { this.difficultyRating = difficultyRating; }
    
    public long getRecordedAt() { return recordedAt; }
    public void setRecordedAt(long recordedAt) { this.recordedAt = recordedAt; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}