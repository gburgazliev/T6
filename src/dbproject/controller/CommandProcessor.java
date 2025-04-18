package dbproject.controller;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

// Model imports
import dbproject.model.DatabaseManager;
import dbproject.model.Table;
import dbproject.model.Row;
import dbproject.model.Column;
import dbproject.model.Cell;
import dbproject.model.DataType;

// View imports
import dbproject.view.TablePrinter;
/**
 * Processes user commands
 */
public class CommandProcessor {
    private DatabaseManager dbManager;
    private Scanner scanner;
    
    public CommandProcessor() {
        this.dbManager = new DatabaseManager();
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Runs the command processor
     */
    public void run() {
        boolean running = true;
        System.out.println("Database Management System");
        System.out.println("Type 'help' for a list of commands");
        
        while (running) {
            System.out.print("> ");
            String command = scanner.nextLine().trim();
            
            try {
                running = processCommand(command);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    
    /**
     * Processes a command
     */
    private boolean processCommand(String command) throws IOException {
        if (command.isEmpty()) {
            return true;
        }
        
        String[] parts = command.split("\\s+");
        String cmd = parts[0].toLowerCase();
        
        switch (cmd) {
            case "open":
                if (parts.length < 2) {
                    System.out.println("Usage: open <file name>");
                    return true;
                }
                dbManager.openDatabase(parts[1]);
                System.out.println("Database opened: " + parts[1]);
                return true;
                
            case "close":
                dbManager = new DatabaseManager();
                System.out.println("Database closed");
                return true;
                
            case "save":
                dbManager.saveDatabase();
                System.out.println("Database saved");
                return true;
                
            case "saveas":
                if (parts.length < 2) {
                    System.out.println("Usage: saveas <file name>");
                    return true;
                }
                dbManager.saveAsDatabase(parts[1]);
                System.out.println("Database saved as: " + parts[1]);
                return true;
                
            case "help":
                printHelp();
                return true;
                
            case "exit":
                return false;
                
            case "import":
                if (parts.length < 2) {
                    System.out.println("Usage: import <file name>");
                    return true;
                }
                dbManager.importTable(parts[1]);
                System.out.println("Table imported from: " + parts[1]);
                return true;
                
            case "showtables":
                List<String> tableNames = dbManager.getTableNames();
                if (tableNames.isEmpty()) {
                    System.out.println("No tables in the database");
                } else {
                    System.out.println("Tables:");
                    for (String name : tableNames) {
                        System.out.println("- " + name);
                    }
                }
                return true;
                
            case "describe":
                if (parts.length < 2) {
                    System.out.println("Usage: describe <table name>");
                    return true;
                }
                Table descTable = dbManager.getTable(parts[1]);
                System.out.println("Table: " + descTable.getName());
                System.out.println("Columns:");
                List<Column> columns = descTable.getColumns();
                for (int i = 0; i < columns.size(); i++) {
                    System.out.println(i + ": " + columns.get(i).getName() + " (" + columns.get(i).getType() + ")");
                }
                return true;
                
            case "print":
                if (parts.length < 2) {
                    System.out.println("Usage: print <table name>");
                    return true;
                }
                Table printTable = dbManager.getTable(parts[1]);
                TablePrinter.printTable(printTable, scanner);
                return true;
                
            case "export":
                if (parts.length < 3) {
                    System.out.println("Usage: export <table name> <file name>");
                    return true;
                }
                dbManager.exportTable(parts[1], parts[2]);
                System.out.println("Table exported to: " + parts[2]);
                return true;
                
            case "select":
                if (parts.length < 4) {
                    System.out.println("Usage: select <column-n> <value> <table name>");
                    return true;
                }
                int selectColumnIndex = Integer.parseInt(parts[1]);
                String selectValue = parts[2];
                String selectTableName = parts[3];
                
                Table selectTable = dbManager.getTable(selectTableName);
                List<Row> selectedRows = selectTable.select(selectColumnIndex, selectValue);
                
                // Create a temporary table with the selected rows
                Table tempTable = new Table(selectTableName + "_selected");
                for (Column column : selectTable.getColumns()) {
                    tempTable.addColumn(column.getName(), column.getType());
                }
                for (Row row : selectedRows) {
                    Row newRow = new Row();
                    for (int i = 0; i < row.size(); i++) {
                        newRow.addCell(new Cell(row.getCell(i).getValue(), row.getCell(i).getType()));
                    }
                    tempTable.addRow(newRow);
                }
                
                TablePrinter.printTable(tempTable, scanner);
                return true;
                
            case "addcolumn":
                if (parts.length < 4) {
                    System.out.println("Usage: addcolumn <table name> <column name> <column type>");
                    return true;
                }
                String addColumnTableName = parts[1];
                String addColumnName = parts[2];
                DataType addColumnType = DataType.valueOf(parts[3].toUpperCase());
                
                Table addColumnTable = dbManager.getTable(addColumnTableName);
                addColumnTable.addColumn(addColumnName, addColumnType);
                System.out.println("Column added: " + addColumnName);
                return true;
                
            case "update":
                if (parts.length < 6) {
                    System.out.println("Usage: update <table name> <search column n> <search value> <target column n> <target value>");
                    return true;
                }
                String updateTableName = parts[1];
                int searchColumnIndex = Integer.parseInt(parts[2]);
                String searchValue = parts[3];
                int targetColumnIndex = Integer.parseInt(parts[4]);
                String targetValue = parts[5];
                
                Table updateTable = dbManager.getTable(updateTableName);
                updateTable.update(searchColumnIndex, searchValue, targetColumnIndex, targetValue);
                System.out.println("Rows updated");
                return true;
                
            case "delete":
                if (parts.length < 4) {
                    System.out.println("Usage: delete <table name> <search column n> <search value>");
                    return true;
                }
                String deleteTableName = parts[1];
                int deleteColumnIndex = Integer.parseInt(parts[2]);
                String deleteValue = parts[3];
                
                Table deleteTable = dbManager.getTable(deleteTableName);
                deleteTable.delete(deleteColumnIndex, deleteValue);
                System.out.println("Rows deleted");
                return true;
                
            case "insert":
                if (parts.length < 2) {
                    System.out.println("Usage: insert <table name> <column 1> ... <column n>");
                    return true;
                }
                String insertTableName = parts[1];
                Table insertTable = dbManager.getTable(insertTableName);
                
                if (parts.length - 2 != insertTable.getColumnCount()) {
                    System.out.println("Error: Number of values doesn't match column count");
                    return true;
                }
                
                Row newRow = new Row();
                List<Column> insertColumns = insertTable.getColumns();
                
                for (int i = 0; i < insertColumns.size(); i++) {
                    DataType columnType = insertColumns.get(i).getType();
                    String value = parts[i + 2];
                    Cell cell = Cell.parseCell(value, columnType);
                    newRow.addCell(cell);
                }
                
                insertTable.addRow(newRow);
                System.out.println("Row inserted");
                return true;
                
            case "innerjoin":
                if (parts.length < 5) {
                    System.out.println("Usage: innerjoin <table 1> <column n1> <table 2> <column n2>");
                    return true;
                }
                String table1Name = parts[1];
                int column1Index = Integer.parseInt(parts[2]);
                String table2Name = parts[3];
                int column2Index = Integer.parseInt(parts[4]);
                
                Table joinedTable = dbManager.innerJoin(table1Name, column1Index, table2Name, column2Index);
                System.out.println("Joined table created: " + joinedTable.getName());
                return true;
                
            case "rename":
                if (parts.length < 3) {
                    System.out.println("Usage: rename <old name> <new name>");
                    return true;
                }
                String oldName = parts[1];
                String newName = parts[2];
                
                dbManager.renameTable(oldName, newName);
                System.out.println("Table renamed from '" + oldName + "' to '" + newName + "'");
                return true;
                
            case "count":
                if (parts.length < 4) {
                    System.out.println("Usage: count <table name> <search column n> <search value>");
                    return true;
                }
                String countTableName = parts[1];
                int countColumnIndex = Integer.parseInt(parts[2]);
                String countValue = parts[3];
                
                Table countTable = dbManager.getTable(countTableName);
                int count = countTable.count(countColumnIndex, countValue);
                System.out.println("Count: " + count);
                return true;
                
            case "aggregate":
                if (parts.length < 6) {
                    System.out.println("Usage: aggregate <table name> <search column n> <search value> <target column n> <operation>");
                    return true;
                }
                String aggregateTableName = parts[1];
                int aggregateSearchColumnIndex = Integer.parseInt(parts[2]);
                String aggregateSearchValue = parts[3];
                int aggregateTargetColumnIndex = Integer.parseInt(parts[4]);
                String aggregateOperation = parts[5];
                
                Table aggregateTable = dbManager.getTable(aggregateTableName);
                Object result = aggregateTable.aggregate(
                    aggregateSearchColumnIndex, 
                    aggregateSearchValue, 
                    aggregateTargetColumnIndex, 
                    aggregateOperation
                );
                
                System.out.println("Result of " + aggregateOperation + ": " + result);
                return true;
                
            default:
                System.out.println("Unknown command: " + cmd);
                return true;
        }
    }
    
    /**
     * Prints the help message
     */
    private void printHelp() {
        System.out.println("Available commands:");
        System.out.println("open <file name> - Open a database from a file");
        System.out.println("close - Close the current database");
        System.out.println("save - Save the database");
        System.out.println("saveas <file name> - Save the database to a new file");
        System.out.println("exit - Exit the program");
        System.out.println("help - Show this help message");
        System.out.println("import <file name> - Import a table from a file");
        System.out.println("showtables - Show all tables in the database");
        System.out.println("describe <name> - Show information about a table");
        System.out.println("print <name> - Show all rows from a table");
        System.out.println("export <name> <file name> - Export a table to a file");
        System.out.println("select <column-n> <value> <table name> - Select rows from a table");
        System.out.println("addcolumn <table name> <column name> <column type> - Add a new column to a table");
        System.out.println("update <table name> <search column n> <search value> <target column n> <target value> - Update rows in a table");
        System.out.println("delete <table name> <search column n> <search value> - Delete rows from a table");
        System.out.println("insert <table name> <column 1> ... <column n> - Insert a new row into a table");
        System.out.println("innerjoin <table 1> <column n1> <table 2> <column n2> - Join two tables");
        System.out.println("rename <old name> <new name> - Rename a table");
        System.out.println("count <table name> <search column n> <search value> - Count rows in a table");
        System.out.println("aggregate <table name> <search column n> <search value> <target column n> <operation> - Perform an aggregation");
    }
}
