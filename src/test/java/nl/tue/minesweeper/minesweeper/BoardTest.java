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
    // Test 6: Board should store correct number of mines
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
    // Test 10: Too many mines should throw exception
    // ---------------------------------------------------------
    @Test
    public void tooManyMinesThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Board(3, 4, 13);
        });
    }


    // ---------------------------------------------------------
    // Test 11: Corner cell neighbour mine count
    // ---------------------------------------------------------
    @Test
    public void cornerCellHasCorrectNeighbourMineCount() {
        Board board = new Board(3, 3, 3);

        int actualMines = 0;

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
    // Test 12: Edge cell neighbour mine count
    // ---------------------------------------------------------
    @Test
    public void edgeCellHasCorrectNeighbourMineCount() {
        Board board = new Board(3, 3, 3);

        int actualMines = 0;

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
    // Test 13: Centre cell neighbour mine count
    // ---------------------------------------------------------
    @Test
    public void centreCellHasCorrectNeighbourMineCount() {
        Board board = new Board(3, 3, 3);

        int actualMines = 0;

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {

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
    // Test 14: Zero-mine board should have zero neighbour mines
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


    // ---------------------------------------------------------
    // Test 15: Revealing a covered cell should uncover it
    // ---------------------------------------------------------
    @Test
    public void revealCellMakesCellUncovered() {
        Board board = new Board(3, 3, 0);

        board.revealCell(1, 1);

        assertEquals(
                Cell.State.UNCOVERED,
                board.getCell(1, 1).getState()
        );
    }


    // ---------------------------------------------------------
    // Test 16: Flagged cell should not be revealed
    // ---------------------------------------------------------
    @Test
    public void flaggedCellCannotBeRevealed() {
        Board board = new Board(3, 3, 0);

        board.getCell(1, 1).setState(Cell.State.FLAGGED);

        board.revealCell(1, 1);

        assertEquals(
                Cell.State.FLAGGED,
                board.getCell(1, 1).getState()
        );
    }


    // ---------------------------------------------------------
    // Test 17: Revealed cell should be uncovered
    //
    // NOTE:
    // With flood fill, other zero-mine cells may also be uncovered.
    // ---------------------------------------------------------
    @Test
    public void selectedCellIsUncovered() {
        Board board = new Board(3, 3, 0);

        board.revealCell(1, 1);

        assertEquals(
                Cell.State.UNCOVERED,
                board.getCell(1, 1).getState()
        );
    }


    // ---------------------------------------------------------
    // Test 18: Revealing same cell twice should be safe
    // ---------------------------------------------------------
    @Test
    public void revealSameCellTwice() {
        Board board = new Board(3, 3, 0);

        board.revealCell(1, 1);
        board.revealCell(1, 1);

        assertEquals(
                Cell.State.UNCOVERED,
                board.getCell(1, 1).getState()
        );
    }


    // ---------------------------------------------------------
    // Test 19: Revealing centre of empty 3x3 board should
    // uncover every cell
    // ---------------------------------------------------------
    @Test
    public void floodFillFromCentreRevealsAllCells() {
        Board board = new Board(3, 3, 0);

        board.revealCell(1, 1);

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                assertEquals(
                        Cell.State.UNCOVERED,
                        board.getCell(r, c).getState()
                );
            }
        }
    }


    // ---------------------------------------------------------
    // Test 20: Flood fill should also work from a corner
    // ---------------------------------------------------------
    @Test
    public void floodFillFromCornerRevealsAllCells() {
        Board board = new Board(3, 3, 0);

        board.revealCell(0, 0);

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                assertEquals(
                        Cell.State.UNCOVERED,
                        board.getCell(r, c).getState()
                );
            }
        }
    }


    // ---------------------------------------------------------
    // Test 21: Flood fill should not uncover a flagged cell
    // ---------------------------------------------------------
    @Test
    public void floodFillDoesNotRevealFlaggedCell() {
        Board board = new Board(3, 3, 0);

        board.getCell(2, 2).setState(Cell.State.FLAGGED);

        board.revealCell(0, 0);

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {

                if (r == 2 && c == 2) {
                    assertEquals(
                            Cell.State.FLAGGED,
                            board.getCell(r, c).getState()
                    );
                } else {
                    assertEquals(
                            Cell.State.UNCOVERED,
                            board.getCell(r, c).getState()
                    );
                }
            }
        }
    }


    // ---------------------------------------------------------
    // Test 22: Flood fill on a 1x1 board should work
    // without going outside board boundaries
    // ---------------------------------------------------------
    @Test
    public void revealSingleCellBoard() {
        Board board = new Board(1, 1, 0);

        board.revealCell(0, 0);

        assertEquals(
                Cell.State.UNCOVERED,
                board.getCell(0, 0).getState()
        );
    }


    // ---------------------------------------------------------
    // Test 23: Flood fill should continue recursively through
    // the entire 4x4 board
    // ---------------------------------------------------------
    @Test
    public void floodFillRevealsEntireFourByFourBoard() {
        Board board = new Board(4, 4, 0);

        board.revealCell(0, 0);

        int uncoveredCount = 0;

        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {

                if (board.getCell(r, c).getState()
                        == Cell.State.UNCOVERED) {

                    uncoveredCount++;
                }
            }
        }

        assertEquals(16, uncoveredCount);
    }


    // ---------------------------------------------------------
    // Test 24: Revealing an already flood-filled cell again
    // should be safe
    // ---------------------------------------------------------
    @Test
    public void revealAfterFloodFillDoesNothingWrong() {
        Board board = new Board(3, 3, 0);

        board.revealCell(1, 1);
        board.revealCell(1, 1);

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                assertEquals(
                        Cell.State.UNCOVERED,
                        board.getCell(r, c).getState()
                );
            }
        }
    }
}