package top.rose.fittrack.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Index;
import androidx.room.Ignore;

@Entity(tableName = "users",
        indices = {@Index(value = "username", unique = true),
                   @Index(value = "email", unique = true)})
public class User {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String username;
    private String email;
    private String passwordHash;
    private String name;
    private int age;
    private float weight; // 体重(kg)
    private float height; // 身高(cm)
    private String gender; // 性别
    private String fitnessGoal; // 健身目标
    private long createdAt;
    private long updatedAt;
    
    // Room 使用的无参构造函数
    public User() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }
    
    // 忽略这个构造函数，Room 不会使用它
    @Ignore
    public User(String username, String email, String passwordHash, String name) {
        this();
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    
    public float getWeight() { return weight; }
    public void setWeight(float weight) { 
        this.weight = weight;
        this.updatedAt = System.currentTimeMillis();
    }
    
    public float getHeight() { return height; }
    public void setHeight(float height) { 
        this.height = height;
        this.updatedAt = System.currentTimeMillis();
    }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public String getFitnessGoal() { return fitnessGoal; }
    public void setFitnessGoal(String fitnessGoal) { this.fitnessGoal = fitnessGoal; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}