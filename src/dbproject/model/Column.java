package dbproject.model;

public class Column {
    private String name;
    private DataType type;
    
    public Column(String name, DataType type) {
        this.name = name;
        this.type = type;
    }
    
    public String getName() {
        return name;
    }
    
    public DataType getType() {
        return type;
    }
}
