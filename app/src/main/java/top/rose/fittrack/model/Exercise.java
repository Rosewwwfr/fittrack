package top.rose.fittrack.model;

public class Exercise {
    private String name;
    private String type;
    private String muscleGroup;
    private String equipment;
    private String instructions;
    
    public Exercise() {}
    
    public Exercise(String name, String type, String muscleGroup) {
        this.name = name;
        this.type = type;
        this.muscleGroup = muscleGroup;
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getMuscleGroup() { return muscleGroup; }
    public void setMuscleGroup(String muscleGroup) { this.muscleGroup = muscleGroup; }
    
    public String getEquipment() { return equipment; }
    public void setEquipment(String equipment) { this.equipment = equipment; }
    
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
}