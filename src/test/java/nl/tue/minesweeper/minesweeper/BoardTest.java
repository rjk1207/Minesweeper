package nl.tue.minesweeper.minesweeper;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class BoardTest {

    // =========================================================
    // HELPER METHODS
    // =========================================================

    private int countMines(Board board) {
        int count = 0;

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                if (board.getCell(r, c).isMine()) {
                    count++;
                }
            }
        }

        return count;
    }

    private int countNeighbourMines(Board board, int row, int col) {
        int count = 0;

        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) {
                    continue;
                }

                int nr = row + dr;
                int nc = col + dc;

                if (nr >= 0 && nr < board.getRows()
                        && nc >= 0 && nc < board.getCols()) {
                    if (board.getCell(nr, nc).isMine()) {
                        count++;
                    }
                }
            }
        }

        return count;
    }

    private int[] findMine(Board board) {
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                if (board.getCell(r, c).isMine()) {
                    return new int[]{r, c};
                }
            }
        }

        return null;
    }

    private int[] findCoveredCell(Board board) {
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                if (board.getCell(r, c).getState()
                        == Cell.State.COVERED) {
                    return new int[]{r, c};
                }
            }
        }

        return null;
    }

    private int[] findOtherSafeCell(Board board, int row, int col) {
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                if ((r != row || c != col)
                        && !board.getCell(r, c).isMine()) {
                    return new int[]{r, c};
                }
            }
        }

        return null;
    }

    private void assertAllUncovered(Board board) {
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                assertEquals(
                        Cell.State.UNCOVERED,
                        board.getCell(r, c).getState()
                );
            }
        }
    }

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
    @Test
    public void boardHasCorrectNumberOfMines() {
        Board board = new Board(3, 4, 3);

        assertEquals(3, board.getNumberOfMines());
    }

    // Test 7
    @Test
    public void boardContainsExactlyThreeMines() {
        Board board = new Board(3, 4, 3);

        board.revealCell(0, 0);

        assertEquals(3, countMines(board));
    }

    // Test 8
    @Test
    public void boardContainsZeroMines() {
        Board board = new Board(3, 4, 0);

        assertEquals(0, countMines(board));
    }

    // Test 9
    @Test
    public void maximumMinesArePlacedAndFirstCellIsSafe() {
        Board board = new Board(2, 2, 3);

        board.revealCell(0, 0);

        assertEquals(3, countMines(board));
        assertFalse(board.getCell(0, 0).isMine());
    }

    // Test 10
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

        board.revealCell(2, 2);

        assertEquals(
                countNeighbourMines(board, 0, 0),
                board.getCell(0, 0).getNeighbourMines()
        );
    }

    // Test 12
    @Test
    public void edgeCellHasCorrectNeighbourMineCount() {
        Board board = new Board(3, 3, 3);

        board.revealCell(2, 2);

        assertEquals(
                countNeighbourMines(board, 0, 1),
                board.getCell(0, 1).getNeighbourMines()
        );
    }

    // Test 13
    @Test
    public void centreCellHasCorrectNeighbourMineCount() {
        Board board = new Board(3, 3, 3);

        board.revealCell(0, 0);

        assertEquals(
                countNeighbourMines(board, 1, 1),
                board.getCell(1, 1).getNeighbourMines()
        );
    }

    // Test 14
    @Test
    public void boardWithNoMinesHasZeroNeighbourMines() {
        Board board = new Board(3, 3, 0);

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

        assertAllUncovered(board);
    }

    // Test 20
    @Test
    public void floodFillFromCornerRevealsAllCells() {
        Board board = new Board(3, 3, 0);

        board.revealCell(0, 0);

        assertAllUncovered(board);
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

        assertAllUncovered(board);
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
    @Test
    public void revealingMineLosesGame() {
        Board board = new Board(3, 3, 7);

        board.revealCell(0, 0);

        int[] mine = findMine(board);

        assertNotNull(mine);

        board.revealCell(mine[0], mine[1]);

        assertEquals(
                GameStatus.LOST,
                board.getStatus()
        );
    }

    // Test 27
    @Test
    public void revealedMineIsUncovered() {
        Board board = new Board(3, 3, 7);

        board.revealCell(0, 0);

        int[] mine = findMine(board);

        assertNotNull(mine);

        board.revealCell(mine[0], mine[1]);

        assertEquals(
                Cell.State.UNCOVERED,
                board.getCell(mine[0], mine[1]).getState()
        );
    }

    // Test 28
    @Test
    public void cannotPlayAfterLoss() {
        Board board = new Board(3, 3, 7);

        board.revealCell(0, 0);

        int[] mine = findMine(board);

        assertNotNull(mine);

        board.revealCell(mine[0], mine[1]);

        assertEquals(
                GameStatus.LOST,
                board.getStatus()
        );

        int[] covered = findCoveredCell(board);

        assertNotNull(covered);

        board.revealCell(covered[0], covered[1]);

        assertEquals(
                Cell.State.COVERED,
                board.getCell(covered[0], covered[1]).getState()
        );

        assertEquals(
                GameStatus.LOST,
                board.getStatus()
        );
    }

    // Test 29
    @Test
    public void canWinGame() {
        Board board = new Board(3, 3, 8);

        board.revealCell(0, 0);

        assertFalse(board.getCell(0, 0).isMine());

        assertEquals(
                GameStatus.WON,
                board.getStatus()
        );
    }

    // Test 30
    @Test
    public void cannotPlayAfterWin() {
        Board board = new Board(2, 2, 3);

        board.revealCell(0, 0);

        assertEquals(
                GameStatus.WON,
                board.getStatus()
        );

        int[] mine = findMine(board);

        assertNotNull(mine);

        board.revealCell(mine[0], mine[1]);

        assertEquals(
                GameStatus.WON,
                board.getStatus()
        );

        assertEquals(
                Cell.State.COVERED,
                board.getCell(mine[0], mine[1]).getState()
        );
    }

    // =========================================================
    // TESTS 31-36: FIRST-CLICK-SAFE MINE PLACEMENT
    // =========================================================

    // Test 31
    @Test
    public void noMinesBeforeFirstReveal() {
        Board board = new Board(3, 3, 2);

        assertEquals(0, countMines(board));
    }

    // Test 32
    @Test
    public void minesPlacedAfterFirstReveal() {
        Board board = new Board(3, 3, 2);

        board.revealCell(1, 1);

        assertEquals(2, countMines(board));
    }

    // Test 33
    @Test
    public void firstClickedCellIsSafe() {
        Board board = new Board(3, 3, 8);

        board.revealCell(1, 1);

        assertFalse(board.getCell(1, 1).isMine());
    }

    // Test 34
    @Test
    public void firstClickAtCornerIsSafe() {
        Board board = new Board(3, 3, 8);

        board.revealCell(0, 0);

        assertFalse(board.getCell(0, 0).isMine());

        assertEquals(
                Cell.State.UNCOVERED,
                board.getCell(0, 0).getState()
        );
    }

    // Test 35
    @Test
    public void minesAreNotPlacedAgain() {
        Board board = new Board(4, 4, 5);

        board.revealCell(0, 0);

        boolean[][] originalMines = new boolean[4][4];

        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                originalMines[r][c] =
                        board.getCell(r, c).isMine();
            }
        }

        int[] nextSafe = null;

        for (int r = 0; r < 4 && nextSafe == null; r++) {
            for (int c = 0; c < 4 && nextSafe == null; c++) {

                Cell cell = board.getCell(r, c);

                if (!cell.isMine()
                        && cell.getState() == Cell.State.COVERED) {
                    nextSafe = new int[]{r, c};
                }
            }
        }

        if (nextSafe != null) {
            board.revealCell(nextSafe[0], nextSafe[1]);
        }

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
    @Test
    public void fullBoardOfMinesIsRejected() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Board(2, 2, 4)
        );
    }

    // =========================================================
    // TESTS 37-44: CHORDING
    // =========================================================

    // Test 37
    @Test
    public void chordCoveredCellDoesNothing() {
        Board board = new Board(3, 3, 2);

        board.chordCell(0, 0);

        assertEquals(
                Cell.State.COVERED,
                board.getCell(0, 0).getState()
        );

        assertEquals(0, countMines(board));
        assertEquals(GameStatus.PLAYING, board.getStatus());
    }

    // Test 38
    @Test
    public void chordFlaggedCellDoesNothing() {
        Board board = new Board(3, 3, 2);

        board.getCell(0, 0).setState(Cell.State.FLAGGED);

        board.chordCell(0, 0);

        assertEquals(
                Cell.State.FLAGGED,
                board.getCell(0, 0).getState()
        );

        assertEquals(0, countMines(board));
        assertEquals(GameStatus.PLAYING, board.getStatus());
    }

    // Test 39
    @Test
    public void chordZeroNumberedCellDoesNothing() {
        Board board = new Board(3, 3, 0);

        board.revealCell(0, 0);

        assertEquals(
                0,
                board.getCell(0, 0).getNeighbourMines()
        );

        board.chordCell(0, 0);

        assertAllUncovered(board);
        assertEquals(GameStatus.WON, board.getStatus());
    }

    // Test 40
    @Test
    public void chordWithTooFewFlagsDoesNothing() {
        Board board = new Board(3, 3, 7);

        board.revealCell(1, 1);

        assertEquals(
                7,
                board.getCell(1, 1).getNeighbourMines()
        );

        int flags = 0;

        for (int r = 0; r < 3 && flags < 6; r++) {
            for (int c = 0; c < 3 && flags < 6; c++) {
                if (board.getCell(r, c).isMine()) {
                    board.getCell(r, c).setState(
                            Cell.State.FLAGGED
                    );
                    flags++;
                }
            }
        }

        assertEquals(6, flags);

        int[] safe = findOtherSafeCell(board, 1, 1);

        assertNotNull(safe);

        assertEquals(
                Cell.State.COVERED,
                board.getCell(safe[0], safe[1]).getState()
        );

        board.chordCell(1, 1);

        assertEquals(
                Cell.State.COVERED,
                board.getCell(safe[0], safe[1]).getState()
        );

        assertEquals(GameStatus.PLAYING, board.getStatus());
    }

    // Test 41
    @Test
    public void chordWithTooManyFlagsDoesNothing() {
        Board board = new Board(3, 3, 7);

        board.revealCell(1, 1);

        assertEquals(
                7,
                board.getCell(1, 1).getNeighbourMines()
        );

        int flags = 0;

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (r == 1 && c == 1) {
                    continue;
                }

                board.getCell(r, c).setState(
                        Cell.State.FLAGGED
                );
                flags++;
            }
        }

        assertEquals(8, flags);

        board.chordCell(1, 1);

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (r == 1 && c == 1) {
                    continue;
                }

                assertEquals(
                        Cell.State.FLAGGED,
                        board.getCell(r, c).getState()
                );
            }
        }

        assertEquals(GameStatus.PLAYING, board.getStatus());
    }

    // Test 42
    @Test
    public void chordWithCorrectFlagsRevealsNeighbours() {
        Board board = new Board(3, 3, 7);

        board.revealCell(1, 1);

        assertEquals(
                7,
                board.getCell(1, 1).getNeighbourMines()
        );

        int[] safe = findOtherSafeCell(board, 1, 1);

        assertNotNull(safe);

        assertEquals(
                Cell.State.COVERED,
                board.getCell(safe[0], safe[1]).getState()
        );

        int flags = 0;

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board.getCell(r, c).isMine()) {
                    board.getCell(r, c).setState(
                            Cell.State.FLAGGED
                    );
                    flags++;
                }
            }
        }

        assertEquals(7, flags);

        board.chordCell(1, 1);

        assertEquals(
                Cell.State.UNCOVERED,
                board.getCell(safe[0], safe[1]).getState()
        );

        assertEquals(GameStatus.WON, board.getStatus());
    }

    // Test 43
    @Test
    public void chordWithIncorrectFlagsLosesGame() {
        Board board = new Board(3, 3, 7);

        board.revealCell(1, 1);

        assertEquals(
                7,
                board.getCell(1, 1).getNeighbourMines()
        );

        int[] safe = findOtherSafeCell(board, 1, 1);

        assertNotNull(safe);

        board.getCell(safe[0], safe[1]).setState(
                Cell.State.FLAGGED
        );

        int mineFlags = 0;

        for (int r = 0; r < 3 && mineFlags < 6; r++) {
            for (int c = 0; c < 3 && mineFlags < 6; c++) {
                if (board.getCell(r, c).isMine()) {
                    board.getCell(r, c).setState(
                            Cell.State.FLAGGED
                    );
                    mineFlags++;
                }
            }
        }

        assertEquals(6, mineFlags);

        int[] unflaggedMine = null;

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board.getCell(r, c).isMine()
                        && board.getCell(r, c).getState()
                        == Cell.State.COVERED) {
                    unflaggedMine = new int[]{r, c};
                }
            }
        }

        assertNotNull(unflaggedMine);

        board.chordCell(1, 1);

        assertEquals(GameStatus.LOST, board.getStatus());

        assertEquals(
                Cell.State.UNCOVERED,
                board.getCell(
                        unflaggedMine[0],
                        unflaggedMine[1]
                ).getState()
        );
    }

    // Test 44
    @Test
    public void chordAfterGameEndedDoesNothing() {
        Board board = new Board(3, 3, 8);

        board.revealCell(1, 1);

        assertEquals(GameStatus.WON, board.getStatus());

        board.chordCell(1, 1);

        assertEquals(GameStatus.WON, board.getStatus());

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board.getCell(r, c).isMine()) {
                    assertEquals(
                            Cell.State.COVERED,
                            board.getCell(r, c).getState()
                    );
                }
            }
        }
    }

    // =========================================================
    // TESTS 45-52: HINT SYSTEM
    // =========================================================

    // Test 45
    // First hint places mines and uncovers a safe cell
    @Test
    public void firstHintInitializesMines() {
        Board board = new Board(3, 3, 2);

        assertEquals(0, countMines(board));

        board.useHint();

        assertEquals(2, countMines(board));

        int uncoveredSafeCells = 0;

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                Cell cell = board.getCell(r, c);

                if (!cell.isMine()
                        && cell.getState() == Cell.State.UNCOVERED) {
                    uncoveredSafeCells++;
                }
            }
        }

        assertTrue(uncoveredSafeCells >= 1);
        assertNotEquals(GameStatus.LOST, board.getStatus());
    }

    // Test 46
    // Hint must never uncover a mine
    @Test
    public void hintAlwaysSelectsSafeCell() {
        Board board = new Board(3, 3, 7);

        board.revealCell(0, 0);

        board.useHint();

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                Cell cell = board.getCell(r, c);

                if (cell.isMine()) {
                    assertNotEquals(
                            Cell.State.UNCOVERED,
                            cell.getState()
                    );
                }
            }
        }

        assertNotEquals(GameStatus.LOST, board.getStatus());
    }

    // Test 47
    // Hint must not uncover a flagged safe cell
    @Test
    public void hintDoesNotRevealFlaggedCell() {
        Board board = new Board(3, 3, 6);

        board.revealCell(1, 1);

        int[] safeCell = findOtherSafeCell(board, 1, 1);

        assertNotNull(safeCell);

        board.getCell(safeCell[0], safeCell[1])
                .setState(Cell.State.FLAGGED);

        board.useHint();

        assertEquals(
                Cell.State.FLAGGED,
                board.getCell(safeCell[0], safeCell[1]).getState()
        );
    }

    // Test 48
    // Hint must choose a covered cell rather than an uncovered one
    @Test
    public void hintDoesNotSelectAlreadyUncoveredCell() {
        Board board = new Board(3, 3, 7);

        board.revealCell(0, 0);

        assertEquals(
                Cell.State.UNCOVERED,
                board.getCell(0, 0).getState()
        );

        board.useHint();

        assertEquals(
                Cell.State.UNCOVERED,
                board.getCell(0, 0).getState()
        );

        int[] otherSafe = findOtherSafeCell(board, 0, 0);

        assertNotNull(otherSafe);

        assertEquals(
                Cell.State.UNCOVERED,
                board.getCell(otherSafe[0], otherSafe[1]).getState()
        );
    }

    // Test 49
    // No eligible covered safe cells means nothing changes
    @Test
    public void hintWithNoEligibleCellsDoesNothing() {
        Board board = new Board(3, 3, 7);

        board.revealCell(0, 0);

        int[] otherSafe = findOtherSafeCell(board, 0, 0);

        assertNotNull(otherSafe);

        board.getCell(otherSafe[0], otherSafe[1])
                .setState(Cell.State.FLAGGED);

        board.useHint();

        assertEquals(
                Cell.State.FLAGGED,
                board.getCell(otherSafe[0], otherSafe[1]).getState()
        );

        assertEquals(GameStatus.PLAYING, board.getStatus());

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board.getCell(r, c).isMine()) {
                    assertEquals(
                            Cell.State.COVERED,
                            board.getCell(r, c).getState()
                    );
                }
            }
        }
    }

    // Test 50
    // Hint does nothing after winning
    @Test
    public void hintAfterWinDoesNothing() {
        Board board = new Board(3, 3, 8);

        board.revealCell(0, 0);

        assertEquals(GameStatus.WON, board.getStatus());

        board.useHint();

        assertEquals(GameStatus.WON, board.getStatus());

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                Cell cell = board.getCell(r, c);

                if (cell.isMine()) {
                    assertEquals(
                            Cell.State.COVERED,
                            cell.getState()
                    );
                } else {
                    assertEquals(
                            Cell.State.UNCOVERED,
                            cell.getState()
                    );
                }
            }
        }
    }

    // Test 51
    // Hint does nothing after losing
    @Test
    public void hintAfterLossDoesNothing() {
        Board board = new Board(3, 3, 7);

        board.revealCell(0, 0);

        int[] mine = findMine(board);

        assertNotNull(mine);

        board.revealCell(mine[0], mine[1]);

        assertEquals(GameStatus.LOST, board.getStatus());

        Cell.State[][] previousStates = new Cell.State[3][3];

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                previousStates[r][c] =
                        board.getCell(r, c).getState();
            }
        }

        board.useHint();

        assertEquals(GameStatus.LOST, board.getStatus());

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                assertEquals(
                        previousStates[r][c],
                        board.getCell(r, c).getState()
                );
            }
        }
    }

    // Test 52
    // Hints should choose eligible cells at different coordinates
    @Test
    public void hintUsesRandomSelection() {
        boolean[][] selectedPositions = new boolean[4][4];

        int differentPositions = 0;

        for (int attempt = 0; attempt < 100; attempt++) {
            Board board = new Board(4, 4, 5);

            board.useHint();

            int selectedRow = -1;
            int selectedCol = -1;

            for (int r = 0; r < 4; r++) {
                for (int c = 0; c < 4; c++) {
                    Cell cell = board.getCell(r, c);

                    if (cell.getState() == Cell.State.UNCOVERED) {
                        assertFalse(cell.isMine());

                        if (selectedRow == -1) {
                            selectedRow = r;
                            selectedCol = c;
                        }
                    }
                }
            }

            assertTrue(selectedRow != -1);

            if (!selectedPositions[selectedRow][selectedCol]) {
                selectedPositions[selectedRow][selectedCol] = true;
                differentPositions++;
            }
        }

        assertTrue(
                differentPositions > 1,
                "Hints should not always select the same position"
        );
    }
}
