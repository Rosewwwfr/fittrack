package top.rose.fittrack.database.dao;

import androidx.room.*;
import top.rose.fittrack.database.entity.WorkoutPlan;
import java.util.List;

@Dao
public interface WorkoutPlanDao {
    
    @Insert
    long insertWorkoutPlan(WorkoutPlan workoutPlan);
    
    @Update
    void updateWorkoutPlan(WorkoutPlan workoutPlan);
    
    @Delete
    void deleteWorkoutPlan(WorkoutPlan workoutPlan);
    
    @Query("SELECT * FROM workout_plans WHERE id = :planId")
    WorkoutPlan getWorkoutPlanById(int planId);
    
    @Query("SELECT * FROM workout_plans WHERE userId = :userId AND isActive = 1 ORDER BY createdAt DESC")
    List<WorkoutPlan> getActiveWorkoutPlansByUser(int userId);
    
    @Query("SELECT * FROM workout_plans WHERE userId = :userId ORDER BY createdAt DESC")
    List<WorkoutPlan> getAllWorkoutPlansByUser(int userId);
    
    @Query("SELECT * FROM workout_plans WHERE userId = :userId AND difficulty = :difficulty AND isActive = 1")
    List<WorkoutPlan> getWorkoutPlansByDifficulty(int userId, String difficulty);
    
    @Query("UPDATE workout_plans SET isActive = :isActive, updatedAt = :updatedAt WHERE id = :planId")
    void updatePlanActiveStatus(int planId, boolean isActive, long updatedAt);
    
    @Query("SELECT COUNT(*) FROM workout_plans WHERE userId = :userId AND isActive = 1")
    int getActivePlanCount(int userId);
    
    @Query("DELETE FROM workout_plans WHERE userId = :userId")
    void deleteAllPlansByUser(int userId);
}