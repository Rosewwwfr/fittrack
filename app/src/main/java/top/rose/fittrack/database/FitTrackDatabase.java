package top.rose.fittrack.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import top.rose.fittrack.database.dao.UserDao;
import top.rose.fittrack.database.dao.WorkoutPlanDao;
import top.rose.fittrack.database.dao.WorkoutRecordDao;
import top.rose.fittrack.database.entity.User;
import top.rose.fittrack.database.entity.WorkoutPlan;
import top.rose.fittrack.database.entity.WorkoutRecord;

@Database(
    entities = {User.class, WorkoutPlan.class, WorkoutRecord.class},
    version = 1,
    exportSchema = false
)
public abstract class FitTrackDatabase extends RoomDatabase {
    
    private static final String DATABASE_NAME = "fittrack_database";
    private static volatile FitTrackDatabase INSTANCE;
    
    // 抽象方法获取DAO
    public abstract UserDao userDao();
    public abstract WorkoutPlanDao workoutPlanDao();
    public abstract WorkoutRecordDao workoutRecordDao();
    
    // 单例模式获取数据库实例
    public static FitTrackDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (FitTrackDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            FitTrackDatabase.class,
                            DATABASE_NAME
                    )
                    .fallbackToDestructiveMigration() // 开发阶段使用，生产环境需要proper migration
                    .build();
                }
            }
        }
        return INSTANCE;
    }
    
    // 数据库迁移示例（未来版本升级时使用）
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // 执行数据库结构变更的SQL语句
            // 例如：database.execSQL("ALTER TABLE users ADD COLUMN avatar_url TEXT");
        }
    };
    
    // 清理数据库实例（用于测试）
    public static void destroyInstance() {
        INSTANCE = null;
    }
}