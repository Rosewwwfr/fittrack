package top.rose.fittrack.database.dao;

import androidx.room.*;
import top.rose.fittrack.database.entity.WorkoutRecord;
import java.util.List;

@Dao
public interface WorkoutRecordDao {
    
    @Insert
    long insertWorkoutRecord(WorkoutRecord workoutRecord);
    
    @Update
    void updateWorkoutRecord(WorkoutRecord workoutRecord);
    
    @Delete
    void deleteWorkoutRecord(WorkoutRecord workoutRecord);
    
    @Query("SELECT * FROM workout_records WHERE id = :recordId")
    WorkoutRecord getWorkoutRecordById(int recordId);
    
    @Query("SELECT * FROM workout_records WHERE userId = :userId ORDER BY recordedAt DESC")
    List<WorkoutRecord> getAllWorkoutRecordsByUser(int userId);
    
    @Query("SELECT * FROM workout_records WHERE userId = :userId AND planId = :planId ORDER BY recordedAt DESC")
    List<WorkoutRecord> getWorkoutRecordsByPlan(int userId, int planId);
    
    @Query("SELECT * FROM workout_records WHERE userId = :userId AND exerciseType = :exerciseType ORDER BY recordedAt DESC")
    List<WorkoutRecord> getWorkoutRecordsByType(int userId, String exerciseType);
    
    @Query("SELECT * FROM workout_records WHERE userId = :userId AND recordedAt BETWEEN :startTime AND :endTime ORDER BY recordedAt DESC")
    List<WorkoutRecord> getWorkoutRecordsByDateRange(int userId, long startTime, long endTime);
    
    @Query("SELECT * FROM workout_records WHERE userId = :userId AND exerciseName = :exerciseName ORDER BY recordedAt DESC LIMIT :limit")
    List<WorkoutRecord> getRecentRecordsForExercise(int userId, String exerciseName, int limit);
    
    @Query("SELECT SUM(caloriesBurned) FROM workout_records WHERE userId = :userId AND recordedAt BETWEEN :startTime AND :endTime")
    int getTotalCaloriesBurnedInPeriod(int userId, long startTime, long endTime);
    
    @Query("SELECT COUNT(*) FROM workout_records WHERE userId = :userId AND recordedAt BETWEEN :startTime AND :endTime")
    int getWorkoutCountInPeriod(int userId, long startTime, long endTime);
    
    @Query("SELECT SUM(durationMinutes) FROM workout_records WHERE userId = :userId AND recordedAt BETWEEN :startTime AND :endTime")
    int getTotalWorkoutTimeInPeriod(int userId, long startTime, long endTime);
    
    @Query("DELETE FROM workout_records WHERE userId = :userId")
    void deleteAllRecordsByUser(int userId);
    
    @Query("SELECT DISTINCT exerciseName FROM workout_records WHERE userId = :userId ORDER BY exerciseName")
    List<String> getUniqueExerciseNames(int userId);
}