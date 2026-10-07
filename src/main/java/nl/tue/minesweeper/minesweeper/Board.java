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
        calculateNeighbourMines();
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

    public void calculateNeighbourMines() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int count = 0;

                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        if (dr == 0 && dc == 0) {
                            continue;
                        }
                        int nr = r + dr;
                        int nc = c + dc;
                        boolean onBoard = nr >= 0 && nr < rows && nc >= 0 && nc < cols;
                        if (onBoard && cells[nr][nc].isMine()) {
                            count++;
                        }
                    }
                }

                cells[r][c].setNeighbourMines(count);
            }
        }
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getNumberOfMines() { return numberOfMines; }
    public Cell getCell(int r, int c) { return cells[r][c]; }

    public void revealCell(int row, int col) {
        Cell cell = cells[row][col];

        if (cell.getState() == Cell.State.COVERED) {
            cell.setState(Cell.State.UNCOVERED);

            if (!cell.isMine() && cell.getNeighbourMines() == 0) {
                floodFill(row, col);
            }
        }
    }

    private void floodFill(int row, int col) {
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) {
                    continue;
                }

                int nr = row + dr;
                int nc = col + dc;

                boolean onBoard = nr >= 0 && nr < rows && nc >= 0 && nc < cols;
                if (!onBoard) {
                    continue;
                }

                Cell neighbour = cells[nr][nc];

                if (neighbour.isMine() || neighbour.getState() != Cell.State.COVERED) {
                    continue;
                }

                neighbour.setState(Cell.State.UNCOVERED);

                if (neighbour.getNeighbourMines() == 0) {
                    floodFill(nr, nc);
                }
            }
        }
    }
}