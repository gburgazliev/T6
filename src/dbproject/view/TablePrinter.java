package dbproject.view;

import java.util.List;
import java.util.Scanner;
import dbproject.model.Table;
import dbproject.model.Row;
import dbproject.model.Column;

/**
 * Handles paginated display of table data
 */
public class TablePrinter {
    private static final int ROWS_PER_PAGE = 10; // Number of rows per page
    
    /**
     * Prints a table with pagination
     */
    public static void printTable(Table table, Scanner scanner) {
        List<Row> rows = table.getRows();
        int totalPages = (int) Math.ceil((double) rows.size() / ROWS_PER_PAGE);
        
        if (totalPages == 0) {
            System.out.println("Table is empty");
            return;
        }
        
        int currentPage = 0;
        boolean viewing = true;
        
        while (viewing) {
            printTablePage(table, rows, currentPage);
            System.out.println("Page " + (currentPage + 1) + " of " + totalPages);
            System.out.println("n: next page, p: previous page, q: quit");
            System.out.print("> ");
            
            String command = scanner.nextLine().trim().toLowerCase();
            switch (command) {
                case "n":
                    if (currentPage < totalPages - 1) {
                        currentPage++;
                    } else {
                        System.out.println("Already at the last page");
                    }
                    break;
                    
                case "p":
                    if (currentPage > 0) {
                        currentPage--;
                    } else {
                        System.out.println("Already at the first page");
                    }
                    break;
                    
                case "q":
                    viewing = false;
                    break;
                    
                default:
                    System.out.println("Unknown command: " + command);
                    break;
            }
        }
    }
    
    /**
     * Prints a single page of a table
     */
    private static void printTablePage(Table table, List<Row> rows, int page) {
        // Calculate range for current page
        int startRow = page * ROWS_PER_PAGE;
        int endRow = Math.min(startRow + ROWS_PER_PAGE, rows.size());
        
        // Print column headers
        List<Column> columns = table.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            System.out.print(i + ": " + columns.get(i).getName() + " (" + columns.get(i).getType() + ")\t");
        }
        System.out.println();
        
        // Print separator
        for (int i = 0; i < columns.size(); i++) {
            System.out.print("----------\t");
        }
        System.out.println();
        
        // Print rows for current page
        for (int i = startRow; i < endRow; i++) {
            Row row = rows.get(i);
            for (int j = 0; j < row.size(); j++) {
                System.out.print(row.getCell(j).toString() + "\t");
            }
            System.out.println();
        }
    }
}