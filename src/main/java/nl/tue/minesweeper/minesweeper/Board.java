package nl.tue.minesweeper.minesweeper;

import java.util.Random;

public class Board {
    private final int rows;
    private final int cols;
    private final int numberOfMines;
    private final Cell[][] cells;
    private final Random random = new Random();
    private GameStatus status = GameStatus.PLAYING;
    private boolean minesPlaced = false;
    private boolean shieldActive = false;

    public Board(int rows, int cols, int numberOfMines) {
        if (rows < 1 || cols < 1) {
            throw new IllegalArgumentException("Board must have at least 1 row and 1 column");
        }
        if (numberOfMines < 0 || numberOfMines > rows * cols - 1) {
            throw new IllegalArgumentException("Mines must be between 0 and rows * cols - 1");
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
    }

    private void placeMines(int safeRow, int safeCol) {
        int placed = 0;
        while (placed < numberOfMines) {
            int r = random.nextInt(rows);
            int c = random.nextInt(cols);
            boolean isSafeCell = (r == safeRow && c == safeCol);
            if (!isSafeCell && !cells[r][c].isMine()) {
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
    public GameStatus getStatus() { return status; }
    public boolean isShieldActive() { return shieldActive; }

    public void activateShield() {
        if (status == GameStatus.PLAYING) {
            shieldActive = true;
        }
    }

    public void revealCell(int row, int col) {
        if (status != GameStatus.PLAYING) {
            return;
        }

        Cell cell = cells[row][col];

        if (cell.getState() != Cell.State.COVERED) {
            return;
        }

        if (!minesPlaced) {
            placeMines(row, col);
            calculateNeighbourMines();
            minesPlaced = true;
        }

        revealInternal(row, col);

        shieldActive = false;

        if (status == GameStatus.PLAYING) {
            checkWin();
        }
    }

    private void revealInternal(int row, int col) {
        Cell cell = cells[row][col];

        if (cell.getState() != Cell.State.COVERED) {
            return;
        }

        if (cell.isMine()) {
            if (shieldActive) {
                return;
            }
            cell.setState(Cell.State.UNCOVERED);
            status = GameStatus.LOST;
            return;
        }

        cell.setState(Cell.State.UNCOVERED);

        if (cell.getNeighbourMines() == 0) {
            floodFill(row, col);
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

    private void checkWin() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = cells[r][c];
                if (!cell.isMine() && cell.getState() != Cell.State.UNCOVERED) {
                    return;
                }
            }
        }
        status = GameStatus.WON;
    }

    public void chordCell(int row, int col) {
        if (status != GameStatus.PLAYING) {
            return;
        }
        if (cells[row][col].getState() != Cell.State.UNCOVERED) {
            return;
        }
        if (cells[row][col].getNeighbourMines() == 0) {
            return;
        }
        int count = 0;
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

                if (neighbour.getState() == Cell.State.FLAGGED) {
                    count++;
                }
            }
        }
        if (count != cells[row][col].getNeighbourMines()) {
            return;
        }

        boolean attemptedReveal = false;

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

                if (cells[nr][nc].getState() == Cell.State.COVERED) {
                    attemptedReveal = true;
                    revealInternal(nr, nc);
                }
                if (status != GameStatus.PLAYING) {
                    shieldActive = false;
                    return;
                }
            }
        }

        if (attemptedReveal) {
            shieldActive = false;
            checkWin();
        }
    }

    public int scanArea(int row, int col) {
        if (status != GameStatus.PLAYING || !minesPlaced) {
            return -1;
        }

        int count = 0;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int nr = row + dr;
                int nc = col + dc;

                boolean onBoard = nr >= 0 && nr < rows && nc >= 0 && nc < cols;
                if (onBoard && cells[nr][nc].isMine()) {
                    count++;
                }
            }
        }
        return count;
    }

    public void useHint() {
        if (status != GameStatus.PLAYING) {
            return;
        }
        int[][] safeCells = new int[rows*cols][2];
        int safeCount = 0;
        for (int r = 0; r < getRows(); r++) {
            for (int c = 0; c < getCols(); c++) {
                if (cells[r][c].getState() == Cell.State.FLAGGED || cells[r][c].getState() == Cell.State.UNCOVERED) {
                    continue;
                }
                if (!cells[r][c].isMine()) {
                    safeCells[safeCount][0] = r;
                    safeCells[safeCount][1] = c;
                    safeCount++;
                }
            }
        }
        if (safeCount == 0) {
            return;
        }
        int randomIndex = random.nextInt(safeCount);
        int[] selected = safeCells[randomIndex];
        int srow = selected[0];
        int scol = selected[1];
        revealCell(srow,scol);
    }
}