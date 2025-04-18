package dbproject.model;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles file I/O operations for database and table files
 */
public class FileHandler {
    /**
     * Saves the database catalog to a file
     */
    public static void saveDatabaseCatalog(String filePath, Map<String, String> tableFiles) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Map.Entry<String, String> entry : tableFiles.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
        }
    }
    
    /**
     * Loads the database catalog from a file
     */
    public static Map<String, String> loadDatabaseCatalog(String filePath) throws IOException {
        Map<String, String> tableFiles = new HashMap<>();
        File file = new File(filePath);
        
        if (!file.exists()) {
            // Create an empty database file
            file.createNewFile();
            return tableFiles;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    tableFiles.put(parts[0], parts[1]);
                }
            }
        }
        return tableFiles;
    }
    
    /**
     * Saves a table to a file
     */
    public static void saveTable(Table table, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write column definitions
            for (Column column : table.getColumns()) {
                writer.write(column.getName() + "," + column.getType());
                writer.newLine();
            }
            
            // Separator between columns and data
            writer.write("---");
            writer.newLine();
            
            // Write data rows
            for (Row row : table.getRows()) {
                StringBuilder rowData = new StringBuilder();
                for (int i = 0; i < row.size(); i++) {
                    if (i > 0) {
                        rowData.append(",");
                    }
                    rowData.append(row.getCell(i).toString());
                }
                writer.write(rowData.toString());
                writer.newLine();
            }
        }
    }
    
    /**
     * Loads a table from a file
     */
    public static Table loadTable(String tableName, String filePath) throws IOException {
        Table table = new Table(tableName);
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            boolean readingColumns = true;
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("---")) {
                    readingColumns = false;
                    continue;
                }
                
                if (readingColumns) {
                    // Parse column definition
                    String[] parts = line.split(",", 2);
                    if (parts.length == 2) {
                        String columnName = parts[0];
                        DataType columnType = DataType.valueOf(parts[1]);
                        table.addColumn(columnName, columnType);
                    }
                } else {
                    // Parse data row
                    List<String> cellValues = parseCsvLine(line);
                    if (cellValues.size() == table.getColumnCount()) {
                        Row row = new Row();
                        for (int i = 0; i < cellValues.size(); i++) {
                            DataType columnType = table.getColumns().get(i).getType();
                            Cell cell = Cell.parseCell(cellValues.get(i), columnType);
                            row.addCell(cell);
                        }
                        table.addRow(row);
                    }
                }
            }
        }
        return table;
    }
    
    /**
     * Parses a CSV line, handling quoted strings with commas
     */
    private static List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder currentValue = new StringBuilder();
        boolean inQuotes = false;
        boolean escapeNext = false;
        
        for (char c : line.toCharArray()) {
            if (escapeNext) {
                currentValue.append(c);
                escapeNext = false;
            } else if (c == '\\' && inQuotes) {
                escapeNext = true;
            } else if (c == '"') {
                inQuotes = !inQuotes;
                currentValue.append(c);
            } else if (c == ',' && !inQuotes) {
                result.add(currentValue.toString());
                currentValue = new StringBuilder();
            } else {
                currentValue.append(c);
            }
        }
        
        result.add(currentValue.toString());
        return result;
    }
}
