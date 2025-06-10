package top.rose.fittrack.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import top.rose.fittrack.R;
import top.rose.fittrack.database.entity.WorkoutPlan;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorkoutPlanAdapter extends RecyclerView.Adapter<WorkoutPlanAdapter.ViewHolder> {
    
    private List<WorkoutPlan> workoutPlans;
    private OnItemClickListener onItemClickListener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    
    public interface OnItemClickListener {
        void onItemClick(WorkoutPlan workoutPlan);
        void onItemLongClick(WorkoutPlan workoutPlan);
    }
    
    public WorkoutPlanAdapter(List<WorkoutPlan> workoutPlans) {
        this.workoutPlans = workoutPlans;
    }
    
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout_plan, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkoutPlan plan = workoutPlans.get(position);
        
        holder.tvPlanName.setText(plan.getName());
        holder.tvDescription.setText(plan.getDescription());
        holder.tvDifficulty.setText(getDifficultyText(plan.getDifficulty()));
        holder.tvDuration.setText(plan.getEstimatedDurationMinutes() + "分钟");
        holder.tvFrequency.setText("每周" + plan.getWeeklyFrequency() + "次");
        holder.tvCreatedAt.setText(dateFormat.format(new Date(plan.getCreatedAt())));
        
        // 设置难度颜色
        int difficultyColor = getDifficultyColor(plan.getDifficulty());
        holder.tvDifficulty.setTextColor(difficultyColor);
        
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(plan);
            }
        });
        
        holder.itemView.setOnLongClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemLongClick(plan);
            }
            return true;
        });
    }
    
    @Override
    public int getItemCount() {
        return workoutPlans.size();
    }
    
    public void updateData(List<WorkoutPlan> newPlans) {
        this.workoutPlans = newPlans;
        notifyDataSetChanged();
    }
    
    private String getDifficultyText(String difficulty) {
        switch (difficulty) {
            case "BEGINNER":
                return "初级";
            case "INTERMEDIATE":
                return "中级";
            case "ADVANCED":
                return "高级";
            default:
                return "未知";
        }
    }
    
    private int getDifficultyColor(String difficulty) {
        switch (difficulty) {
            case "BEGINNER":
                return 0xFF4CAF50; // 绿色
            case "INTERMEDIATE":
                return 0xFFFF9800; // 橙色
            case "ADVANCED":
                return 0xFFF44336; // 红色
            default:
                return 0xFF757575; // 灰色
        }
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvPlanName;
        TextView tvDescription;
        TextView tvDifficulty;
        TextView tvDuration;
        TextView tvFrequency;
        TextView tvCreatedAt;
        
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            tvPlanName = itemView.findViewById(R.id.tv_plan_name);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvDifficulty = itemView.findViewById(R.id.tv_difficulty);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            tvFrequency = itemView.findViewById(R.id.tv_frequency);
            tvCreatedAt = itemView.findViewById(R.id.tv_created_at);
        }
    }
}