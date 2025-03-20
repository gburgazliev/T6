package dbproject.model;

import java.util.ArrayList;
import java.util.List;

public class Row {
    private List<Cell> cells;

    public Row() {
        this.cells = new ArrayList<>();
    }

    public void addCell(Cell cell) {
        cells.add(cell);
    }

    public Cell getCell(int columnIndex) {
        if (columnIndex >= 0 && columnIndex < cells.size()) {
            return cells.get(columnIndex);
        }
        throw new IndexOutOfBoundsException("Invalid column index: " + columnIndex);
    }

    public int size() {
        return cells.size();
    }

    /**
     * Adds a NULL cell when a new column is added to a table
     */
    public void addNullCell() {
        cells.add(new Cell(null, DataType.NULL));
    }
}
