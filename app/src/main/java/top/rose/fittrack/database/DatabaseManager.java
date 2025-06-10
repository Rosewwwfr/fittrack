package top.rose.fittrack.database;

import android.content.Context;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseManager {
    
    private static final int NUMBER_OF_THREADS = 4;
    private static volatile DatabaseManager INSTANCE;
    
    private final FitTrackDatabase database;
    private final ExecutorService databaseWriteExecutor;
    
    private DatabaseManager(Context context) {
        database = FitTrackDatabase.getInstance(context);
        databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    }
    
    public static DatabaseManager getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (DatabaseManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DatabaseManager(context);
                }
            }
        }
        return INSTANCE;
    }
    
    public FitTrackDatabase getDatabase() {
        return database;
    }
    
    public ExecutorService getDatabaseWriteExecutor() {
        return databaseWriteExecutor;
    }
    
    // 便捷方法执行数据库操作
    public void executeInBackground(Runnable runnable) {
        databaseWriteExecutor.execute(runnable);
    }
}