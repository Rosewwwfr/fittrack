package top.rose.fittrack.auth;

import android.content.Context;
import android.content.SharedPreferences;
import top.rose.fittrack.database.DatabaseManager;
import top.rose.fittrack.database.entity.User;
import top.rose.fittrack.utils.PasswordUtils;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class AuthManager {
    
    private static final String PREFS_NAME = "FitTrackAuth";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    
    private static volatile AuthManager INSTANCE;
    private final SharedPreferences prefs;
    private final DatabaseManager databaseManager;
    private final ExecutorService executor;
    
    private AuthManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        databaseManager = DatabaseManager.getInstance(context);
        executor = databaseManager.getDatabaseWriteExecutor();
    }
    
    public static AuthManager getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AuthManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AuthManager(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }
    
    /**
     * 用户注册
     */
    public CompletableFuture<AuthResult> register(String username, String email, String password, String name) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 检查用户名是否已存在
                if (databaseManager.getDatabase().userDao().checkUsernameExists(username) > 0) {
                    return new AuthResult(false, "用户名已存在", null);
                }
                
                // 检查邮箱是否已存在
                if (databaseManager.getDatabase().userDao().checkEmailExists(email) > 0) {
                    return new AuthResult(false, "邮箱已被注册", null);
                }
                
                // 创建新用户
                User user = new User(username, email, PasswordUtils.createPasswordHash(password), name);
                long userId = databaseManager.getDatabase().userDao().insertUser(user);
                
                if (userId > 0) {
                    user.setId((int) userId);
                    saveUserSession(user);
                    return new AuthResult(true, "注册成功", user);
                } else {
                    return new AuthResult(false, "注册失败，请重试", null);
                }
                
            } catch (Exception e) {
                return new AuthResult(false, "注册失败：" + e.getMessage(), null);
            }
        }, executor);
    }
    
    /**
     * 用户登录
     */
    public CompletableFuture<AuthResult> login(String username, String password) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                User user = databaseManager.getDatabase().userDao().getUserByUsername(username);
                
                if (user == null) {
                    return new AuthResult(false, "用户不存在", null);
                }
                
                if (PasswordUtils.verifyPasswordFromHash(password, user.getPasswordHash())) {
                    saveUserSession(user);
                    return new AuthResult(true, "登录成功", user);
                } else {
                    return new AuthResult(false, "密码错误", null);
                }
                
            } catch (Exception e) {
                return new AuthResult(false, "登录失败：" + e.getMessage(), null);
            }
        }, executor);
    }
    
    /**
     * 用户登出
     */
    public void logout() {
        prefs.edit()
                .remove(KEY_USER_ID)
                .remove(KEY_USERNAME)
                .putBoolean(KEY_IS_LOGGED_IN, false)
                .apply();
    }
    
    /**
     * 检查是否已登录
     */
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    /**
     * 获取当前用户ID
     */
    public int getCurrentUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }
    
    /**
     * 获取当前用户名
     */
    public String getCurrentUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }
    
    /**
     * 保存用户会话
     */
    private void saveUserSession(User user) {
        prefs.edit()
                .putInt(KEY_USER_ID, user.getId())
                .putString(KEY_USERNAME, user.getUsername())
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .apply();
    }
    
    /**
     * 认证结果类
     */
    public static class AuthResult {
        private final boolean success;
        private final String message;
        private final User user;
        
        public AuthResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public User getUser() { return user; }
    }
}