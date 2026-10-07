package nl.tue.minesweeper.minesweeper;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class BoardTest {

    // =========================================================
    // TESTS 1-5: BOARD CREATION
    // =========================================================

    // Test 1
    @Test
    public void boardHasThreeRows() {
        Board board = new Board(3, 4, 0);

        assertEquals(3, board.getRows());
    }

    // Test 2
    @Test
    public void boardHasFourColumns() {
        Board board = new Board(3, 4, 0);

        assertEquals(4, board.getCols());
    }

    // Test 3
    @Test
    public void firstCellIsNotNull() {
        Board board = new Board(3, 4, 0);

        assertNotNull(board.getCell(0, 0));
    }

    // Test 4
    @Test
    public void lastCellIsNotNull() {
        Board board = new Board(3, 4, 0);

        assertNotNull(board.getCell(2, 3));
    }

    // Test 5
    @Test
    public void allCellsAreNotNull() {
        Board board = new Board(3, 4, 0);

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 4; c++) {
                assertNotNull(board.getCell(r, c));
            }
        }
    }


    // =========================================================
    // TESTS 6-10: MINE PLACEMENT
    // =========================================================

    // Test 6
    // Number of requested mines is remembered before placement
    @Test
    public void boardHasCorrectNumberOfMines() {
        Board board = new Board(3, 4, 3);

        assertEquals(3, board.getNumberOfMines());
    }

    // Test 7
    // Mines are generated after first reveal
    @Test
    public void boardContainsExactlyThreeMines() {
        Board board = new Board(3, 4, 3);

        board.revealCell(0, 0);

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

    // Test 8
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

    // Test 9
    // Maximum valid number of mines on 2x2 is 3
    @Test
    public void maximumMinesArePlacedAndFirstCellIsSafe() {
        Board board = new Board(2, 2, 3);

        board.revealCell(0, 0);

        int mineCount = 0;

        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 2; c++) {
                if (board.getCell(r, c).isMine()) {
                    mineCount++;
                }
            }
        }

        assertEquals(3, mineCount);
        assertFalse(board.getCell(0, 0).isMine());
    }

    // Test 10
    // 2x2 board cannot have 4 mines
    @Test
    public void tooManyMinesThrowsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Board(2, 2, 4)
        );
    }


    // =========================================================
    // TESTS 11-14: NEIGHBOUR MINE COUNTS
    // =========================================================

    // Test 11
    @Test
    public void cornerCellHasCorrectNeighbourMineCount() {
        Board board = new Board(3, 3, 3);

        // Trigger mine placement
        board.revealCell(2, 2);

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

    // Test 12
    @Test
    public void edgeCellHasCorrectNeighbourMineCount() {
        Board board = new Board(3, 3, 3);

        // Trigger mine placement
        board.revealCell(2, 2);

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

    // Test 13
    @Test
    public void centreCellHasCorrectNeighbourMineCount() {
        Board board = new Board(3, 3, 3);

        // Trigger mine placement
        board.revealCell(0, 0);

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

    // Test 14
    @Test
    public void boardWithNoMinesHasZeroNeighbourMines() {
        Board board = new Board(3, 3, 0);

        // Trigger official board initialization
        board.revealCell(0, 0);

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                assertEquals(
                        0,
                        board.getCell(r, c).getNeighbourMines()
                );
            }
        }
    }


    // =========================================================
    // TESTS 15-18: REVEALING CELLS
    // =========================================================

    // Test 15
    @Test
    public void revealCellMakesCellUncovered() {
        Board board = new Board(3, 3, 0);

        board.revealCell(1, 1);

        assertEquals(
                Cell.State.UNCOVERED,
                board.getCell(1, 1).getState()
        );
    }

    // Test 16
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

    // Test 17
    @Test
    public void selectedCellIsUncovered() {
        Board board = new Board(3, 3, 0);

        board.revealCell(1, 1);

        assertEquals(
                Cell.State.UNCOVERED,
                board.getCell(1, 1).getState()
        );
    }

    // Test 18
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


    // =========================================================
    // TESTS 19-24: FLOOD FILL
    // =========================================================

    // Test 19
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

    // Test 20
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

    // Test 21
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

    // Test 22
    @Test
    public void revealSingleCellBoard() {
        Board board = new Board(1, 1, 0);

        board.revealCell(0, 0);

        assertEquals(
                Cell.State.UNCOVERED,
                board.getCell(0, 0).getState()
        );
    }

    // Test 23
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

    // Test 24
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


    // =========================================================
    // TESTS 25-30: GAME STATUS
    // =========================================================

    // Test 25
    @Test
    public void newBoardStartsPlaying() {
        Board board = new Board(3, 3, 2);

        assertEquals(
                GameStatus.PLAYING,
                board.getStatus()
        );
    }

    // Test 26
    // First reveal initializes mines.
    // Then reveal an actual mine.
    @Test
    public void revealingMineLosesGame() {
        Board board = new Board(3, 3, 2);

        // Guaranteed safe first click
        board.revealCell(0, 0);

        int mineRow = -1;
        int mineCol = -1;

        for (int r = 0; r < 3 && mineRow == -1; r++) {
            for (int c = 0; c < 3 && mineRow == -1; c++) {

                if (board.getCell(r, c).isMine()) {
                    mineRow = r;
                    mineCol = c;
                }
            }
        }

        assertTrue(mineRow != -1);

        board.revealCell(mineRow, mineCol);

        assertEquals(
                GameStatus.LOST,
                board.getStatus()
        );
    }

    // Test 27
    @Test
    public void revealedMineIsUncovered() {
        Board board = new Board(3, 3, 2);

        // Initialize mines
        board.revealCell(0, 0);

        int mineRow = -1;
        int mineCol = -1;

        for (int r = 0; r < 3 && mineRow == -1; r++) {
            for (int c = 0; c < 3 && mineRow == -1; c++) {

                if (board.getCell(r, c).isMine()) {
                    mineRow = r;
                    mineCol = c;
                }
            }
        }

        assertTrue(mineRow != -1);

        board.revealCell(mineRow, mineCol);

        assertEquals(
                Cell.State.UNCOVERED,
                board.getCell(mineRow, mineCol).getState()
        );
    }

    // Test 28
    @Test
    public void cannotPlayAfterLoss() {
        Board board = new Board(3, 3, 2);

        // Initialize mines
        board.revealCell(0, 0);

        int mineRow = -1;
        int mineCol = -1;

        // Find a mine
        for (int r = 0; r < 3 && mineRow == -1; r++) {
            for (int c = 0; c < 3 && mineRow == -1; c++) {

                if (board.getCell(r, c).isMine()) {
                    mineRow = r;
                    mineCol = c;
                }
            }
        }

        assertTrue(mineRow != -1);

        // Lose
        board.revealCell(mineRow, mineCol);

        assertEquals(
                GameStatus.LOST,
                board.getStatus()
        );

        int coveredRow = -1;
        int coveredCol = -1;

        // Find another covered cell
        for (int r = 0; r < 3 && coveredRow == -1; r++) {
            for (int c = 0; c < 3 && coveredRow == -1; c++) {

                if (board.getCell(r, c).getState()
                        == Cell.State.COVERED) {

                    coveredRow = r;
                    coveredCol = c;
                }
            }
        }

        assertTrue(coveredRow != -1);

        // Try to reveal after losing
        board.revealCell(coveredRow, coveredCol);

        assertEquals(
                Cell.State.COVERED,
                board.getCell(coveredRow, coveredCol).getState()
        );

        assertEquals(
                GameStatus.LOST,
                board.getStatus()
        );
    }

    // Test 29
    // 8 mines means only first-clicked cell can be safe
    @Test
    public void canWinGame() {
        Board board = new Board(3, 3, 8);

        board.revealCell(0, 0);

        assertFalse(board.getCell(0, 0).isMine());

        assertEquals(
                Cell.State.UNCOVERED,
                board.getCell(0, 0).getState()
        );

        assertEquals(
                GameStatus.WON,
                board.getStatus()
        );
    }

    // Test 30
    @Test
    public void cannotPlayAfterWin() {
        Board board = new Board(2, 2, 3);

        // First click is guaranteed safe and therefore wins
        board.revealCell(0, 0);

        assertEquals(
                GameStatus.WON,
                board.getStatus()
        );

        int mineRow = -1;
        int mineCol = -1;

        // Mines now exist, so find one
        for (int r = 0; r < 2 && mineRow == -1; r++) {
            for (int c = 0; c < 2 && mineRow == -1; c++) {

                if (board.getCell(r, c).isMine()) {
                    mineRow = r;
                    mineCol = c;
                }
            }
        }

        assertTrue(mineRow != -1);

        assertEquals(
                Cell.State.COVERED,
                board.getCell(mineRow, mineCol).getState()
        );

        // Try to reveal mine after winning
        board.revealCell(mineRow, mineCol);

        assertEquals(
                GameStatus.WON,
                board.getStatus()
        );

        assertEquals(
                Cell.State.COVERED,
                board.getCell(mineRow, mineCol).getState()
        );
    }


    // =========================================================
    // TESTS 31-36: FIRST-CLICK-SAFE MINE PLACEMENT
    // =========================================================

    // Test 31
    // No mines should exist before the first valid reveal
    @Test
    public void noMinesBeforeFirstReveal() {
        Board board = new Board(3, 3, 2);

        int mineCount = 0;

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {

                if (board.getCell(r, c).isMine()) {
                    mineCount++;
                }
            }
        }

        assertEquals(0, mineCount);
    }

    // Test 32
    // First valid reveal should cause mines to be placed
    @Test
    public void minesPlacedAfterFirstReveal() {
        Board board = new Board(3, 3, 2);

        board.revealCell(1, 1);

        int mineCount = 0;

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {

                if (board.getCell(r, c).isMine()) {
                    mineCount++;
                }
            }
        }

        assertEquals(2, mineCount);
    }

    // Test 33
    // First clicked cell must never contain a mine
    @Test
    public void firstClickedCellIsSafe() {
        Board board = new Board(3, 3, 8);

        board.revealCell(1, 1);

        assertFalse(
                board.getCell(1, 1).isMine()
        );
    }

    // Test 34
    // First-click safety should also work in a corner
    @Test
    public void firstClickAtCornerIsSafe() {
        Board board = new Board(3, 3, 8);

        board.revealCell(0, 0);

        assertFalse(
                board.getCell(0, 0).isMine()
        );

        assertEquals(
                Cell.State.UNCOVERED,
                board.getCell(0, 0).getState()
        );
    }

    // Test 35
    // Mine locations must not change after they are first placed
    @Test
    public void minesAreNotPlacedAgain() {
        Board board = new Board(4, 4, 5);

        // First reveal initializes the mines
        board.revealCell(0, 0);

        boolean[][] originalMines = new boolean[4][4];

        // Save all mine locations
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                originalMines[r][c] =
                        board.getCell(r, c).isMine();
            }
        }

        // Find a covered safe cell for another valid reveal
        int safeRow = -1;
        int safeCol = -1;

        for (int r = 0; r < 4 && safeRow == -1; r++) {
            for (int c = 0; c < 4 && safeRow == -1; c++) {

                if (!board.getCell(r, c).isMine()
                        && board.getCell(r, c).getState()
                        == Cell.State.COVERED) {

                    safeRow = r;
                    safeCol = c;
                }
            }
        }

        // There may be no covered safe cell if flood fill
        // already revealed all safe cells and won the game.
        if (safeRow != -1) {
            board.revealCell(safeRow, safeCol);
        }

        // Compare every mine position with saved positions
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {

                assertEquals(
                        originalMines[r][c],
                        board.getCell(r, c).isMine()
                );
            }
        }
    }

    // Test 36
    // Entire board cannot consist of mines because the
    // first clicked cell must always be safe
    @Test
    public void fullBoardOfMinesIsRejected() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Board(2, 2, 4)
        );
    }
}