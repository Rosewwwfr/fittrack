package top.rose.fittrack.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import top.rose.fittrack.R;
import top.rose.fittrack.database.entity.WorkoutRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorkoutRecordAdapter extends RecyclerView.Adapter<WorkoutRecordAdapter.ViewHolder> {
    
    private List<WorkoutRecord> workoutRecords;
    private OnItemClickListener onItemClickListener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM月dd日 HH:mm", Locale.getDefault());
    
    public interface OnItemClickListener {
        void onItemClick(WorkoutRecord workoutRecord);
        void onItemLongClick(WorkoutRecord workoutRecord);
    }
    
    public WorkoutRecordAdapter(List<WorkoutRecord> workoutRecords) {
        this.workoutRecords = workoutRecords;
    }
    
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout_record, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkoutRecord record = workoutRecords.get(position);
        holder.bind(record);
    }
    
    @Override
    public int getItemCount() {
        return workoutRecords.size();
    }
    
    public void updateData(List<WorkoutRecord> newRecords) {
        this.workoutRecords = newRecords;
        notifyDataSetChanged();
    }
    
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvExerciseName;
        private TextView tvExerciseType;
        private TextView tvDetails;
        private TextView tvDate;
        private TextView tvDuration;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExerciseName = itemView.findViewById(R.id.tv_exercise_name);
            tvExerciseType = itemView.findViewById(R.id.tv_exercise_type);
            tvDetails = itemView.findViewById(R.id.tv_details);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            
            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(workoutRecords.get(getAdapterPosition()));
                }
            });
            
            itemView.setOnLongClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemLongClick(workoutRecords.get(getAdapterPosition()));
                }
                return true;
            });
        }
        
        public void bind(WorkoutRecord record) {
            tvExerciseName.setText(record.getExerciseName());
            tvExerciseType.setText(getExerciseTypeText(record.getExerciseType()));
            tvDate.setText(dateFormat.format(new Date(record.getRecordedAt())));
            
            // 根据运动类型显示不同的详细信息
            String details = buildDetailsText(record);
            tvDetails.setText(details);
            
            if (record.getDurationMinutes() > 0) {
                tvDuration.setText(record.getDurationMinutes() + "分钟");
                tvDuration.setVisibility(View.VISIBLE);
            } else {
                tvDuration.setVisibility(View.GONE);
            }
        }
        
        private String getExerciseTypeText(String type) {
            switch (type) {
                case "STRENGTH": return "力量训练";
                case "CARDIO": return "有氧运动";
                case "FLEXIBILITY": return "柔韧性";
                case "SPORTS": return "运动";
                default: return type;
            }
        }
        
        private String buildDetailsText(WorkoutRecord record) {
            StringBuilder details = new StringBuilder();
            
            if (record.getSets() > 0 && record.getReps() > 0) {
                details.append(record.getSets()).append("组 × ").append(record.getReps()).append("次");
            }
            
            if (record.getWeight() > 0) {
                if (details.length() > 0) details.append(" • ");
                details.append(record.getWeight()).append("kg");
            }
            
            if (record.getDistance() > 0) {
                if (details.length() > 0) details.append(" • ");
                details.append(record.getDistance()).append("km");
            }
            
            if (record.getCaloriesBurned() > 0) {
                if (details.length() > 0) details.append(" • ");
                details.append(record.getCaloriesBurned()).append("卡路里");
            }
            
            return details.toString();
        }
    }
}