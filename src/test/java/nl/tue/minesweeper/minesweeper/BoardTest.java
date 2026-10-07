package nl.tue.minesweeper.minesweeper;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class BoardTest {

    // ---------------------------------------------------------
    // Test 1: Board should have 3 rows
    // ---------------------------------------------------------
    @Test
    public void boardHasThreeRows() {
        Board board = new Board(3, 4, 0);

        assertEquals(3, board.getRows());
    }


    // ---------------------------------------------------------
    // Test 2: Board should have 4 columns
    // ---------------------------------------------------------
    @Test
    public void boardHasFourColumns() {
        Board board = new Board(3, 4, 0);

        assertEquals(4, board.getCols());
    }


    // ---------------------------------------------------------
    // Test 3: Cell at (0,0) should not be null
    // ---------------------------------------------------------
    @Test
    public void firstCellIsNotNull() {
        Board board = new Board(3, 4, 0);

        assertNotNull(board.getCell(0, 0));
    }


    // ---------------------------------------------------------
    // Test 4: Cell at (2,3) should not be null
    // ---------------------------------------------------------
    @Test
    public void lastCellIsNotNull() {
        Board board = new Board(3, 4, 0);

        assertNotNull(board.getCell(2, 3));
    }


    // ---------------------------------------------------------
    // Test 5: Every cell should not be null
    // ---------------------------------------------------------
    @Test
    public void allCellsAreNotNull() {
        Board board = new Board(3, 4, 0);

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 4; c++) {
                assertNotNull(board.getCell(r, c));
            }
        }
    }


    // ---------------------------------------------------------
    // Test 6: Board should store the correct number of mines
    // ---------------------------------------------------------
    @Test
    public void boardHasCorrectNumberOfMines() {
        Board board = new Board(3, 4, 3);

        assertEquals(3, board.getNumberOfMines());
    }


    // ---------------------------------------------------------
    // Test 7: Board should actually contain exactly 3 mines
    // ---------------------------------------------------------
    @Test
    public void boardContainsExactlyThreeMines() {
        Board board = new Board(3, 4, 3);

        int mineCount = 0;

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 4; c++) {

                if (board.getCell(r, c).isMine()) {
                    mineCount++;
                }
            }
        }

        assertEquals(3, mineCount);
    }


    // ---------------------------------------------------------
    // Test 8: Board with 0 mines should contain no mines
    // ---------------------------------------------------------
    @Test
    public void boardContainsZeroMines() {
        Board board = new Board(3, 4, 0);

        int mineCount = 0;

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 4; c++) {

                if (board.getCell(r, c).isMine()) {
                    mineCount++;
                }
            }
        }

        assertEquals(0, mineCount);
    }


    // ---------------------------------------------------------
    // Test 9: A 2x2 board with 4 mines should contain 4 mines
    // ---------------------------------------------------------
    @Test
    public void fullBoardContainsFourMines() {
        Board board = new Board(2, 2, 4);

        int mineCount = 0;

        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 2; c++) {

                if (board.getCell(r, c).isMine()) {
                    mineCount++;
                }
            }
        }

        assertEquals(4, mineCount);
    }


    // ---------------------------------------------------------
    // Test 10: Too many mines should throw an exception
    // ---------------------------------------------------------
    @Test
    public void tooManyMinesThrowsException() {

        assertThrows(IllegalArgumentException.class, () -> {
            new Board(3, 4, 13);
        });
    }


    // ---------------------------------------------------------
    // Test 11: Corner cell (0,0) has correct neighbour mine count
    // ---------------------------------------------------------
    @Test
    public void cornerCellHasCorrectNeighbourMineCount() {
        Board board = new Board(3, 3, 3);

        int actualMines = 0;

        // The three possible neighbours of (0,0)
        if (board.getCell(0, 1).isMine()) {
            actualMines++;
        }

        if (board.getCell(1, 0).isMine()) {
            actualMines++;
        }

        if (board.getCell(1, 1).isMine()) {
            actualMines++;
        }

        assertEquals(
                actualMines,
                board.getCell(0, 0).getNeighbourMines()
        );
    }


    // ---------------------------------------------------------
    // Test 12: Edge cell (0,1) has correct neighbour mine count
    // ---------------------------------------------------------
    @Test
    public void edgeCellHasCorrectNeighbourMineCount() {
        Board board = new Board(3, 3, 3);

        int actualMines = 0;

        // The five possible neighbours of (0,1)
        if (board.getCell(0, 0).isMine()) {
            actualMines++;
        }

        if (board.getCell(0, 2).isMine()) {
            actualMines++;
        }

        if (board.getCell(1, 0).isMine()) {
            actualMines++;
        }

        if (board.getCell(1, 1).isMine()) {
            actualMines++;
        }

        if (board.getCell(1, 2).isMine()) {
            actualMines++;
        }

        assertEquals(
                actualMines,
                board.getCell(0, 1).getNeighbourMines()
        );
    }


    // ---------------------------------------------------------
    // Test 13: Centre cell (1,1) has correct neighbour mine count
    // ---------------------------------------------------------
    @Test
    public void centreCellHasCorrectNeighbourMineCount() {
        Board board = new Board(3, 3, 3);

        int actualMines = 0;

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {

                // Do not count the centre cell itself
                if (r == 1 && c == 1) {
                    continue;
                }

                if (board.getCell(r, c).isMine()) {
                    actualMines++;
                }
            }
        }

        assertEquals(
                actualMines,
                board.getCell(1, 1).getNeighbourMines()
        );
    }


    // ---------------------------------------------------------
    // Test 14: Board with no mines should have 0 neighbour mines
    // for every cell
    // ---------------------------------------------------------
    @Test
    public void boardWithNoMinesHasZeroNeighbourMines() {
        Board board = new Board(3, 3, 0);

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {

                assertEquals(
                        0,
                        board.getCell(r, c).getNeighbourMines()
                );
            }
        }
    }
}