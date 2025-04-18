package dbproject.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages database operations
 */
public class DatabaseManager {
    private Map<String, Table> tables;
    private Map<String, String> tableFiles;
    private String databaseFile;
    
    public DatabaseManager() {
        this.tables = new HashMap<>();
        this.tableFiles = new HashMap<>();
    }
    
    /**
     * Opens a database from a file
     */
    public void openDatabase(String filePath) throws IOException {
        // Clear current data
        tables.clear();
        tableFiles.clear();
        
        // Load database from file
        this.databaseFile = filePath;
        tableFiles = FileHandler.loadDatabaseCatalog(filePath);
        
        // Load all tables
        for (Map.Entry<String, String> entry : tableFiles.entrySet()) {
            String tableName = entry.getKey();
            String tableFile = entry.getValue();
            Table table = FileHandler.loadTable(tableName, tableFile);
            tables.put(tableName, table);
        }
    }
    
    /**
     * Saves the database to the current file
     */
    public void saveDatabase() throws IOException {
        if (databaseFile != null) {
            FileHandler.saveDatabaseCatalog(databaseFile, tableFiles);
            
            // Save all tables
            for (Map.Entry<String, String> entry : tableFiles.entrySet()) {
                String tableName = entry.getKey();
                String tableFile = entry.getValue();
                Table table = tables.get(tableName);
                FileHandler.saveTable(table, tableFile);
            }
        } else {
            throw new IllegalStateException("No database file specified");
        }
    }
    
    /**
     * Saves the database to a new file
     */
    public void saveAsDatabase(String filePath) throws IOException {
        this.databaseFile = filePath;
        saveDatabase();
    }
    
    /**
     * Imports a table from a file
     */
    public void importTable(String filePath) throws IOException {
        // Extract table name from file path
        String fileName = new File(filePath).getName();
        String tableName = fileName.substring(0, fileName.lastIndexOf('.'));
        
        if (tables.containsKey(tableName)) {
            throw new IllegalArgumentException("Table with name '" + tableName + "' already exists");
        }
        
        // Load table from file
        Table table = FileHandler.loadTable(tableName, filePath);
        tables.put(tableName, table);
        tableFiles.put(tableName, filePath);
    }
    
    /**
     * Exports a table to a file
     */
    public void exportTable(String tableName, String filePath) throws IOException {
        Table table = getTable(tableName);
        FileHandler.saveTable(table, filePath);
    }
    
    /**
     * Gets a list of all table names
     */
    public List<String> getTableNames() {
        return new ArrayList<>(tables.keySet());
    }
    
    /**
     * Gets a table by name
     */
    public Table getTable(String name) {
        Table table = tables.get(name);
        if (table == null) {
            throw new IllegalArgumentException("Table with name '" + name + "' doesn't exist");
        }
        return table;
    }
    
    /**
     * Adds a new table
     */
    public void addTable(Table table) {
        if (tables.containsKey(table.getName())) {
            throw new IllegalArgumentException("Table with name '" + table.getName() + "' already exists");
        }
        tables.put(table.getName(), table);
        
        // Create a default file path for the table
        String tableFile = table.getName() + ".tbl";
        tableFiles.put(table.getName(), tableFile);
    }
    
    /**
     * Renames a table
     */
    public void renameTable(String oldName, String newName) {
        if (!tables.containsKey(oldName)) {
            throw new IllegalArgumentException("Table with name '" + oldName + "' doesn't exist");
        }
        if (tables.containsKey(newName)) {
            throw new IllegalArgumentException("Table with name '" + newName + "' already exists");
        }
        
        Table table = tables.remove(oldName);
        table.setName(newName);
        tables.put(newName, table);
        
        // Update file mapping
        String tableFile = tableFiles.remove(oldName);
        tableFiles.put(newName, tableFile);
    }
    
    /**
     * Performs an inner join on two tables
     */
    public Table innerJoin(String table1Name, int columnIndex1, String table2Name, int columnIndex2) {
        Table table1 = getTable(table1Name);
        Table table2 = getTable(table2Name);
        
        // Create a new table with combined columns
        String newTableName = table1.getName() + "_" + table2.getName() + "_join";
        Table result = new Table(newTableName);
        
        // Add columns from first table
        for (Column column : table1.getColumns()) {
            result.addColumn(table1.getName() + "." + column.getName(), column.getType());
        }
        
        // Add columns from second table
        for (Column column : table2.getColumns()) {
            result.addColumn(table2.getName() + "." + column.getName(), column.getType());
        }
        
        // Perform the inner join
        for (Row row1 : table1.getRows()) {
            Cell cell1 = row1.getCell(columnIndex1);
            
            for (Row row2 : table2.getRows()) {
                Cell cell2 = row2.getCell(columnIndex2);
                
                // If the join columns match
                if (cell1.equals(cell2)) {
                    Row newRow = new Row();
                    
                    // Add cells from first table
                    for (int i = 0; i < row1.size(); i++) {
                        newRow.addCell(new Cell(row1.getCell(i).getValue(), row1.getCell(i).getType()));
                    }
                    
                    // Add cells from second table
                    for (int i = 0; i < row2.size(); i++) {
                        newRow.addCell(new Cell(row2.getCell(i).getValue(), row2.getCell(i).getType()));
                    }
                    
                    result.addRow(newRow);
                }
            }
        }
        
        addTable(result);
        return result;
    }
}
