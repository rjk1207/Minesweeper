
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

    private int countMinesInScanArea(Board board, int row, int col) {
        int count = 0;

        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {

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

    private Cell.State[][] snapshot(Board board) {
        Cell.State[][] states =
                new Cell.State[board.getRows()][board.getCols()];

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                states[r][c] = board.getCell(r, c).getState();
            }
        }

        return states;
    }

    private void assertSnapshot(
            Board board, Cell.State[][] states) {

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                assertEquals(
                        states[r][c],
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

        assertAllUncovered(board);
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
    @Test
    public void hintDoesNotRevealFlaggedCell() {
        Board board = new Board(3, 3, 7);

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
    @Test
    public void hintWithNoEligibleCellsDoesNothing() {
        Board board = new Board(3, 3, 7);

        board.revealCell(0, 0);

        int[] otherSafe = findOtherSafeCell(board, 0, 0);

        assertNotNull(otherSafe);

        board.getCell(otherSafe[0], otherSafe[1])
                .setState(Cell.State.FLAGGED);

        Cell.State[][] before = snapshot(board);

        board.useHint();

        assertSnapshot(board, before);

        assertEquals(
                GameStatus.PLAYING,
                board.getStatus()
        );
    }

    // Test 50
    @Test
    public void hintAfterWinDoesNothing() {
        Board board = new Board(3, 3, 8);

        board.revealCell(0, 0);

        assertEquals(GameStatus.WON, board.getStatus());

        Cell.State[][] before = snapshot(board);

        board.useHint();

        assertSnapshot(board, before);
        assertEquals(GameStatus.WON, board.getStatus());
    }

    // Test 51
    @Test
    public void hintAfterLossDoesNothing() {
        Board board = new Board(3, 3, 7);

        board.revealCell(0, 0);

        int[] mine = findMine(board);

        assertNotNull(mine);

        board.revealCell(mine[0], mine[1]);

        assertEquals(GameStatus.LOST, board.getStatus());

        Cell.State[][] before = snapshot(board);

        board.useHint();

        assertSnapshot(board, before);
        assertEquals(GameStatus.LOST, board.getStatus());
    }

    // Test 52
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

    // =========================================================
    // TESTS 53-66: SHIELD SYSTEM
    // =========================================================

    // Test 53
    @Test
    public void shieldInitiallyInactive() {
        Board board = new Board(3, 3, 2);

        assertFalse(board.isShieldActive());
    }

    // Test 54
    @Test
    public void activateShieldMakesShieldActive() {
        Board board = new Board(3, 3, 2);

        board.activateShield();

        assertTrue(board.isShieldActive());
    }

    // Test 55
    @Test
    public void repeatedShieldActivationDoesNotStack() {
        Board board = new Board(3, 3, 7);

        board.revealCell(1, 1);

        board.activateShield();
        board.activateShield();
        board.activateShield();

        assertTrue(board.isShieldActive());

        int[] safe = findOtherSafeCell(board, 1, 1);

        assertNotNull(safe);

        board.revealCell(safe[0], safe[1]);

        assertFalse(board.isShieldActive());
    }

    // Test 56
    @Test
    public void shieldExpiresAfterSafeReveal() {
        Board board = new Board(3, 3, 7);

        board.revealCell(1, 1);

        int[] safe = findOtherSafeCell(board, 1, 1);

        assertNotNull(safe);

        board.activateShield();

        board.revealCell(safe[0], safe[1]);

        assertEquals(
                Cell.State.UNCOVERED,
                board.getCell(safe[0], safe[1]).getState()
        );

        assertFalse(board.isShieldActive());
    }

    // Test 57
    @Test
    public void shieldPreventsMineRevealAndExpires() {
        Board board = new Board(3, 3, 7);

        board.revealCell(1, 1);

        int[] mine = findMine(board);

        assertNotNull(mine);

        board.activateShield();

        board.revealCell(mine[0], mine[1]);

        assertEquals(
                Cell.State.COVERED,
                board.getCell(mine[0], mine[1]).getState()
        );

        assertEquals(
                GameStatus.PLAYING,
                board.getStatus()
        );

        assertFalse(board.isShieldActive());
    }

    // Test 58
    @Test
    public void mineWithoutShieldLosesGame() {
        Board board = new Board(3, 3, 7);

        board.revealCell(1, 1);

        int[] mine = findMine(board);

        assertNotNull(mine);
        assertFalse(board.isShieldActive());

        board.revealCell(mine[0], mine[1]);

        assertEquals(
                GameStatus.LOST,
                board.getStatus()
        );
    }

    // Test 59
    @Test
    public void shieldRemainsAfterFlaggedRevealAttempt() {
        Board board = new Board(3, 3, 7);

        board.revealCell(1, 1);

        int[] mine = findMine(board);

        assertNotNull(mine);

        board.getCell(mine[0], mine[1])
                .setState(Cell.State.FLAGGED);

        board.activateShield();

        board.revealCell(mine[0], mine[1]);

        assertEquals(
                Cell.State.FLAGGED,
                board.getCell(mine[0], mine[1]).getState()
        );

        assertTrue(board.isShieldActive());
    }

    // Test 60
    @Test
    public void shieldRemainsAfterUncoveredRevealAttempt() {
        Board board = new Board(3, 3, 7);

        board.revealCell(1, 1);

        board.activateShield();

        board.revealCell(1, 1);

        assertEquals(
                Cell.State.UNCOVERED,
                board.getCell(1, 1).getState()
        );

        assertTrue(board.isShieldActive());
    }

    // Test 61
    @Test
    public void shieldRemainsAfterInvalidChord() {
        Board board = new Board(3, 3, 7);

        board.revealCell(1, 1);

        board.activateShield();

        board.chordCell(1, 1);

        assertTrue(board.isShieldActive());

        assertEquals(
                GameStatus.PLAYING,
                board.getStatus()
        );
    }

    // Test 62
    @Test
    public void shieldRemainsWhenChordHasNoCoveredNeighbours() {
        Board board = new Board(3, 3, 7);

        board.revealCell(1, 1);

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (r == 1 && c == 1) {
                    continue;
                }

                board.getCell(r, c).setState(
                        Cell.State.FLAGGED
                );
            }
        }

        board.activateShield();

        board.chordCell(1, 1);

        assertTrue(board.isShieldActive());

        assertEquals(
                GameStatus.PLAYING,
                board.getStatus()
        );

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
    }

    // Test 63
    @Test
    public void shieldExpiresAfterValidChord() {
        Board board = new Board(3, 3, 7);

        board.revealCell(1, 1);

        int[] safe = findOtherSafeCell(board, 1, 1);

        assertNotNull(safe);

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board.getCell(r, c).isMine()) {
                    board.getCell(r, c).setState(
                            Cell.State.FLAGGED
                    );
                }
            }
        }

        board.activateShield();

        assertTrue(board.isShieldActive());

        board.chordCell(1, 1);

        assertEquals(
                Cell.State.UNCOVERED,
                board.getCell(safe[0], safe[1]).getState()
        );

        assertEquals(
                GameStatus.WON,
                board.getStatus()
        );

        assertFalse(board.isShieldActive());
    }

    // Test 64
    @Test
    public void shieldProtectsMinesDuringChord() {
        Board board = new Board(3, 3, 7);

        board.revealCell(1, 1);

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

        board.activateShield();

        board.chordCell(1, 1);

        assertEquals(
                Cell.State.COVERED,
                board.getCell(
                        unflaggedMine[0],
                        unflaggedMine[1]
                ).getState()
        );

        assertEquals(
                GameStatus.PLAYING,
                board.getStatus()
        );

        assertFalse(board.isShieldActive());
    }

    // Test 65
    @Test
    public void shieldCannotActivateAfterGameEnds() {

        Board wonBoard = new Board(3, 3, 8);

        wonBoard.revealCell(1, 1);

        assertEquals(
                GameStatus.WON,
                wonBoard.getStatus()
        );

        wonBoard.activateShield();

        assertFalse(wonBoard.isShieldActive());

        Board lostBoard = new Board(3, 3, 7);

        lostBoard.revealCell(1, 1);

        int[] mine = findMine(lostBoard);

        assertNotNull(mine);

        lostBoard.revealCell(mine[0], mine[1]);

        assertEquals(
                GameStatus.LOST,
                lostBoard.getStatus()
        );

        lostBoard.activateShield();

        assertFalse(lostBoard.isShieldActive());
    }

    // Test 66
    @Test
    public void shieldExpiresAfterHintRevealsCell() {
        Board board = new Board(3, 3, 7);

        board.revealCell(1, 1);

        int[] safe = findOtherSafeCell(board, 1, 1);

        assertNotNull(safe);

        assertEquals(
                Cell.State.COVERED,
                board.getCell(safe[0], safe[1]).getState()
        );

        board.activateShield();

        assertTrue(board.isShieldActive());

        board.useHint();

        assertEquals(
                Cell.State.UNCOVERED,
                board.getCell(safe[0], safe[1]).getState()
        );

        assertFalse(board.isShieldActive());

        assertNotEquals(
                GameStatus.LOST,
                board.getStatus()
        );
    }

    // =========================================================
    // TESTS 67-76: SCAN SYSTEM
    // =========================================================

    // Test 67
    // Scan area containing no mines returns zero
    @Test
    public void scanAreaWithNoMinesReturnsZero() {
        Board board = new Board(5, 5, 1);

        board.revealCell(2, 2);

        if (board.getStatus() == GameStatus.WON) {
            assertEquals(-1, board.scanArea(0, 0));
            return;
        }

        // Find a 3x3 area that contains no mines.
        int[] emptyArea = null;

        for (int r = 0; r < 5 && emptyArea == null; r++) {
            for (int c = 0; c < 5 && emptyArea == null; c++) {
                if (countMinesInScanArea(board, r, c) == 0) {
                    emptyArea = new int[]{r, c};
                }
            }
        }

        assertNotNull(emptyArea);

        assertEquals(
                0,
                board.scanArea(emptyArea[0], emptyArea[1])
        );
    }

    // Test 68
    // Scan area containing multiple mines returns correct count
    @Test
    public void scanAreaWithMultipleMinesReturnsCorrectCount() {
        Board board = new Board(3, 3, 7);

        board.revealCell(1, 1);

        assertEquals(
                7,
                board.scanArea(1, 1)
        );
    }

    // Test 69
    // Corner scan counts only valid board positions
    @Test
    public void scanCornerCountsOnlyValidPositions() {
        Board board = new Board(3, 3, 7);

        board.revealCell(1, 1);

        int expected = countMinesInScanArea(board, 0, 0);

        assertEquals(
                expected,
                board.scanArea(0, 0)
        );
    }

    // Test 70
    // Edge scan counts only valid board positions
    @Test
    public void scanEdgeCountsOnlyValidPositions() {
        Board board = new Board(3, 3, 7);

        board.revealCell(1, 1);

        int expected = countMinesInScanArea(board, 0, 1);

        assertEquals(
                expected,
                board.scanArea(0, 1)
        );
    }

    // Test 71
    // Centre scan checks all nine positions
    @Test
    public void scanCentreChecksAllNinePositions() {
        Board board = new Board(3, 3, 7);

        board.revealCell(1, 1);

        int expected = countMinesInScanArea(board, 1, 1);

        assertEquals(7, expected);

        assertEquals(
                expected,
                board.scanArea(1, 1)
        );
    }

    // Test 72
    // Flagged mines are still counted during scanning
    @Test
    public void scanCountsFlaggedMines() {
        Board board = new Board(3, 3, 7);

        board.revealCell(1, 1);

        int flaggedMines = 0;

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board.getCell(r, c).isMine()) {
                    board.getCell(r, c).setState(
                            Cell.State.FLAGGED
                    );
                    flaggedMines++;
                }
            }
        }

        assertEquals(7, flaggedMines);

        assertEquals(
                7,
                board.scanArea(1, 1)
        );
    }

    // Test 73
    // Scanning must not consume an active shield
    @Test
    public void scanDoesNotConsumeShield() {
        Board board = new Board(3, 3, 7);

        board.revealCell(1, 1);

        board.activateShield();

        assertTrue(board.isShieldActive());

        board.scanArea(1, 1);

        assertTrue(board.isShieldActive());

        assertEquals(
                GameStatus.PLAYING,
                board.getStatus()
        );
    }

    // Test 74
    // Scanning must not reveal or change any cell
    @Test
    public void scanDoesNotChangeCellStates() {
        Board board = new Board(3, 3, 7);

        board.revealCell(1, 1);

        Cell.State[][] before = snapshot(board);

        int expected = countMinesInScanArea(board, 1, 1);

        assertEquals(
                expected,
                board.scanArea(1, 1)
        );

        assertSnapshot(board, before);

        assertEquals(
                GameStatus.PLAYING,
                board.getStatus()
        );
    }

    // Test 75
    // Scanning before mines are initialized returns -1
    @Test
    public void scanBeforeFirstRevealReturnsMinusOne() {
        Board board = new Board(3, 3, 2);

        assertEquals(0, countMines(board));

        assertEquals(
                -1,
                board.scanArea(1, 1)
        );

        assertEquals(0, countMines(board));

        assertEquals(
                GameStatus.PLAYING,
                board.getStatus()
        );
    }

    // Test 76
    // Scanning after winning or losing returns -1
    @Test
    public void scanAfterGameEndsReturnsMinusOne() {

        // Check scanning after winning.
        Board wonBoard = new Board(3, 3, 8);

        wonBoard.revealCell(1, 1);

        assertEquals(
                GameStatus.WON,
                wonBoard.getStatus()
        );

        Cell.State[][] wonBefore = snapshot(wonBoard);

        assertEquals(
                -1,
                wonBoard.scanArea(1, 1)
        );

        assertSnapshot(wonBoard, wonBefore);

        assertEquals(
                GameStatus.WON,
                wonBoard.getStatus()
        );

        // Check scanning after losing.
        Board lostBoard = new Board(3, 3, 7);

        lostBoard.revealCell(1, 1);

        int[] mine = findMine(lostBoard);

        assertNotNull(mine);

        lostBoard.revealCell(mine[0], mine[1]);

        assertEquals(
                GameStatus.LOST,
                lostBoard.getStatus()
        );

        Cell.State[][] lostBefore = snapshot(lostBoard);

        assertEquals(
                -1,
                lostBoard.scanArea(1, 1)
        );

        assertSnapshot(lostBoard, lostBefore);

        assertEquals(
                GameStatus.LOST,
                lostBoard.getStatus()
        );
    }
}
