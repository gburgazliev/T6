package dbproject.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a database table
 */
public class Table {
    private String name;
    private List<Column> columns;
    private List<Row> rows;
    
    public Table(String name) {
        this.name = name;
        this.columns = new ArrayList<>();
        this.rows = new ArrayList<>();
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Adds a new column to the table
     */
    public void addColumn(String name, DataType type) {
        columns.add(new Column(name, type));
        // Add NULL cells to all existing rows for this new column
        for (Row row : rows) {
            row.addNullCell();
        }
    }
    
    /**
     * Adds a new row to the table
     */
    public void addRow(Row row) {
        if (row.size() != columns.size()) {
            throw new IllegalArgumentException("Row size doesn't match the number of columns");
        }
        rows.add(row);
    }
    
    public List<Column> getColumns() {
        return Collections.unmodifiableList(columns);
    }
    
    public List<Row> getRows() {
        return Collections.unmodifiableList(rows);
    }
    
    public int getColumnCount() {
        return columns.size();
    }
    
    public int getRowCount() {
        return rows.size();
    }
    
    /**
     * Selects rows that match a specific value in a column
     */
    public List<Row> select(int columnIndex, String value) {
        List<Row> result = new ArrayList<>();
        for (Row row : rows) {
            if (columnIndex >= 0 && columnIndex < row.size() && 
                row.getCell(columnIndex).toString().equals(value)) {
                result.add(row);
            }
        }
        return result;
    }
    
    /**
     * Updates rows in the table based on search criteria
     */
    public void update(int searchColumnIndex, String searchValue, int targetColumnIndex, String targetValue) {
        for (Row row : rows) {
            if (searchColumnIndex >= 0 && searchColumnIndex < row.size() && 
                row.getCell(searchColumnIndex).toString().equals(searchValue)) {
                
                if (targetColumnIndex >= 0 && targetColumnIndex < row.size()) {
                    DataType targetType = columns.get(targetColumnIndex).getType();
                    Cell newCell = Cell.parseCell(targetValue, targetType);
                    row.getCell(targetColumnIndex).setValue(newCell.getValue());
                }
            }
        }
    }
    
    /**
     * Deletes rows that match a specific value in a column
     */
    public void delete(int searchColumnIndex, String searchValue) {
        rows.removeIf(row -> 
            searchColumnIndex >= 0 && searchColumnIndex < row.size() && 
            row.getCell(searchColumnIndex).toString().equals(searchValue)
        );
    }
    
    /**
     * Counts rows that match a specific value in a column
     */
    public int count(int searchColumnIndex, String searchValue) {
        int count = 0;
        for (Row row : rows) {
            if (searchColumnIndex >= 0 && searchColumnIndex < row.size() && 
                row.getCell(searchColumnIndex).toString().equals(searchValue)) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Performs an aggregate operation on a numeric column for rows that match a search criteria
     */
    public Object aggregate(int searchColumnIndex, String searchValue, int targetColumnIndex, String operation) {
        List<Row> matchingRows = select(searchColumnIndex, searchValue);
        
        if (matchingRows.isEmpty()) {
            return null;
        }
        
        DataType columnType = columns.get(targetColumnIndex).getType();
        if (columnType != DataType.INTEGER && columnType != DataType.FLOAT) {
            throw new IllegalArgumentException("Aggregate operations can only be performed on numeric columns");
        }
        
        switch (operation.toLowerCase()) {
            case "sum":
                double sum = 0;
                for (Row row : matchingRows) {
                    Cell cell = row.getCell(targetColumnIndex);
                    if (cell.getValue() != null) {
                        sum += ((Number) cell.getValue()).doubleValue();
                    }
                }
                return sum;
                
            case "product":
                double product = 1;
                for (Row row : matchingRows) {
                    Cell cell = row.getCell(targetColumnIndex);
                    if (cell.getValue() != null) {
                        product *= ((Number) cell.getValue()).doubleValue();
                    }
                }
                return product;
                
            case "maximum":
                double max = Double.NEGATIVE_INFINITY;
                for (Row row : matchingRows) {
                    Cell cell = row.getCell(targetColumnIndex);
                    if (cell.getValue() != null) {
                        double value = ((Number) cell.getValue()).doubleValue();
                        if (value > max) {
                            max = value;
                        }
                    }
                }
                return max == Double.NEGATIVE_INFINITY ? null : max;
                
            case "minimum":
                double min = Double.POSITIVE_INFINITY;
                for (Row row : matchingRows) {
                    Cell cell = row.getCell(targetColumnIndex);
                    if (cell.getValue() != null) {
                        double value = ((Number) cell.getValue()).doubleValue();
                        if (value < min) {
                            min = value;
                        }
                    }
                }
                return min == Double.POSITIVE_INFINITY ? null : min;
                
            default:
                throw new IllegalArgumentException("Unsupported aggregate operation: " + operation);
        }
    }
}
