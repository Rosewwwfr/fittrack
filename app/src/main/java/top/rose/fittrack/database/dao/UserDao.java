package top.rose.fittrack.database.dao;

import androidx.room.*;
import top.rose.fittrack.database.entity.User;
import java.util.List;

@Dao
public interface UserDao {
    
    @Insert
    long insertUser(User user);
    
    @Update
    void updateUser(User user);
    
    @Delete
    void deleteUser(User user);
    
    @Query("SELECT * FROM users WHERE id = :userId")
    User getUserById(int userId);
    
    @Query("SELECT * FROM users WHERE username = :username")
    User getUserByUsername(String username);
    
    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmail(String email);
    
    @Query("SELECT * FROM users WHERE username = :username AND passwordHash = :passwordHash")
    User authenticateUser(String username, String passwordHash);
    
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    List<User> getAllUsers();
    
    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    int checkUsernameExists(String username);
    
    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    int checkEmailExists(String email);
    
    @Query("UPDATE users SET weight = :weight, updatedAt = :updatedAt WHERE id = :userId")
    void updateUserWeight(int userId, float weight, long updatedAt);
    
    @Query("UPDATE users SET height = :height, updatedAt = :updatedAt WHERE id = :userId")
    void updateUserHeight(int userId, float height, long updatedAt);
}