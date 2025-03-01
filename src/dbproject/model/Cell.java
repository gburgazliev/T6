package dbproject.model;

public class Cell {
    private Object value;
    private DataType type;
    
    public Cell(Object value, DataType type) {
        this.value = value;
        this.type = type;
    }
    
    public Object getValue() {
        return value;
    }
    
    public void setValue(Object value) {
        this.value = value;
    }
    
    public DataType getType() {
        return type;
    }
    
    /**
     * Parses a string into a Cell with the appropriate value and type
     */
    public static Cell parseCell(String value, DataType type) {
        if (value.equals("NULL")) {
            return new Cell(null, DataType.NULL);
        }
        
        switch (type) {
            case INTEGER:
                try {
                    
                    if (value.startsWith("+")) {
                        value = value.substring(1);
                    }
                    return new Cell(Integer.parseInt(value), DataType.INTEGER);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid integer format: " + value);
                }
            case FLOAT:
                try {
                    
                    if (value.startsWith("+")) {
                        value = value.substring(1);
                    }
                    return new Cell(Double.parseDouble(value), DataType.FLOAT);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid float format: " + value);
                }
            case STRING:
                // Handle quotes and escape characters
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    String content = value.substring(1, value.length() - 1);
                    // Process escape sequences
                    StringBuilder processed = new StringBuilder();
                    boolean escaped = false;
                    
                    for (int i = 0; i < content.length(); i++) {
                        char c = content.charAt(i);
                        if (escaped) {
                            if (c == '\"' || c == '\\') {
                                processed.append(c);
                            } else {
                                // Invalid escape sequence
                                processed.append('\\').append(c);
                            }
                            escaped = false;
                        } else if (c == '\\') {
                            escaped = true;
                        } else {
                            processed.append(c);
                        }
                    }
                    
                    return new Cell(processed.toString(), DataType.STRING);
                }
                throw new IllegalArgumentException("Invalid string format: " + value);
            default:
                throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }
    
    /**
     * Converts the cell value to a string representation
     */
    @Override
    public String toString() {
        if (value == null) {
            return "NULL";
        }
        
        switch (type) {
            case STRING:
                String str = (String) value;
                // Escape quotes and backslashes
                str = str.replace("\\", "\\\\").replace("\"", "\\\"");
                return "\"" + str + "\"";
            default:
                return value.toString();
        }
    }
    
    /**
     * Check if two cells are equal based on their values
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Cell other = (Cell) obj;
        if (value == null) return other.value == null;
        return value.equals(other.value);
    }
}
