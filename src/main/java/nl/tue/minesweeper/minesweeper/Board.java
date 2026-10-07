package nl.tue.minesweeper.minesweeper;

import java.util.Random;

public class Board {
    private final int rows;
    private final int cols;
    private final int numberOfMines;
    private final Cell[][] cells;
    private final Random random = new Random();

    public Board(int rows, int cols, int numberOfMines) {
        if (rows < 1 || cols < 1) {
            throw new IllegalArgumentException("Board must have at least 1 row and 1 column");
        }
        if (numberOfMines < 0 || numberOfMines > rows * cols) {
            throw new IllegalArgumentException("Mines must be between 0 and rows * cols");
        }

        this.rows = rows;
        this.cols = cols;
        this.numberOfMines = numberOfMines;
        this.cells = new Cell[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = new Cell();
            }
        }

        placeMines();
    }

    private void placeMines() {
        int placed = 0;
        while (placed < numberOfMines) {
            int r = random.nextInt(rows);
            int c = random.nextInt(cols);
            if (!cells[r][c].isMine()) {
                cells[r][c].setMine(true);
                placed++;
            }
        }
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getNumberOfMines() { return numberOfMines; }
    public Cell getCell(int r, int c) { return cells[r][c]; }
}