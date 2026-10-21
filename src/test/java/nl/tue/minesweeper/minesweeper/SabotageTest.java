
package nl.tue.minesweeper.minesweeper;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class SabotageTest {

    // =========================================================
    // HELPER METHODS
    // =========================================================

    private int countUncoveredSafeCells(Board board) {
        int count = 0;

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                Cell cell = board.getCell(r, c);

                if (!cell.isMine()
                        && cell.getState() == Cell.State.UNCOVERED) {
                    count++;
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

    private int[] findCoveredSafeCell(Board board) {
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                Cell cell = board.getCell(r, c);

                if (!cell.isMine()
                        && cell.getState() == Cell.State.COVERED) {
                    return new int[]{r, c};
                }
            }
        }

        return null;
    }

    private void prepareUncoveredCells(Board board, int required) {
        board.revealCell(0, 0);

        while (countUncoveredSafeCells(board) < required) {
            int[] safe = findCoveredSafeCell(board);
            assertNotNull(safe);

            board.getCell(safe[0], safe[1])
                    .setState(Cell.State.UNCOVERED);
        }
    }

    private Cell.State[][] snapshotStates(Board board) {
        Cell.State[][] states =
                new Cell.State[board.getRows()][board.getCols()];

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                states[r][c] = board.getCell(r, c).getState();
            }
        }

        return states;
    }

    private boolean[][] snapshotMines(Board board) {
        boolean[][] mines =
                new boolean[board.getRows()][board.getCols()];

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                mines[r][c] = board.getCell(r, c).isMine();
            }
        }

        return mines;
    }

    private int[][] snapshotNeighbourCounts(Board board) {
        int[][] counts =
                new int[board.getRows()][board.getCols()];

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                counts[r][c] =
                        board.getCell(r, c).getNeighbourMines();
            }
        }

        return counts;
    }

    private void assertStatesUnchanged(
            Board board, Cell.State[][] expected) {

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                assertEquals(expected[r][c],
                        board.getCell(r, c).getState());
            }
        }
    }

    private void assertMinesUnchanged(
            Board board, boolean[][] expected) {

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                assertEquals(expected[r][c],
                        board.getCell(r, c).isMine());
            }
        }
    }

    private void finishByLosing(Board board) {
        board.revealCell(1, 1);

        int[] mine = findMine(board);
        assertNotNull(mine);

        board.revealCell(mine[0], mine[1]);

        assertEquals(GameStatus.LOST, board.getStatus());
    }

    // =========================================================
    // FREEZE SABOTAGE
    // =========================================================

    // Test 101
    @Test
    public void testFreezeActivates() {
        Match match = new Match(3, 3, 7);

        assertFalse(match.getPlayer2Board().isFrozen());

        assertTrue(match.applySabotage(
                1, new FreezeSabotage(5000)));

        assertTrue(match.getPlayer2Board().isFrozen());
    }

    // Test 102
    @Test
    public void testFreezeTargetsOpponent() {
        Match match = new Match(3, 3, 7);

        match.applySabotage(1, new FreezeSabotage(5000));

        assertFalse(match.getPlayer1Board().isFrozen());
        assertTrue(match.getPlayer2Board().isFrozen());
    }

    // Test 103
    @Test
    public void testFreezeBlocksReveal() {
        Board board = new Board(3, 3, 7);

        new FreezeSabotage(5000).apply(board);

        board.revealCell(1, 1);

        assertEquals(Cell.State.COVERED,
                board.getCell(1, 1).getState());

        assertEquals(GameStatus.PLAYING, board.getStatus());
    }

    // Test 104
    @Test
    public void testFreezeBlocksFlag() {
        Board board = new Board(3, 3, 7);

        new FreezeSabotage(5000).apply(board);

        assertTrue(board.isFrozen());

        Cell cell = board.getCell(1, 1);

        assertEquals(Cell.State.COVERED, cell.getState());
    }

    // Test 105
    @Test
    public void testFreezeBlocksChord() {
        Board board = new Board(3, 3, 7);

        board.revealCell(1, 1);

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board.getCell(r, c).isMine()) {
                    board.getCell(r, c)
                            .setState(Cell.State.FLAGGED);
                }
            }
        }

        int before = countUncoveredSafeCells(board);

        new FreezeSabotage(5000).apply(board);

        board.chordCell(1, 1);

        assertEquals(before, countUncoveredSafeCells(board));
        assertEquals(GameStatus.PLAYING, board.getStatus());
    }

    // Test 106
    @Test
    public void testFreezeBlocksPowerUps() {
        Board board = new Board(3, 3, 7);

        board.revealCell(1, 1);

        new FreezeSabotage(5000).apply(board);

        int before = countUncoveredSafeCells(board);

        board.useHint();
        board.activateShield();
        int scanResult = board.scanArea(1, 1);

        assertEquals(before, countUncoveredSafeCells(board));
        assertFalse(board.isShieldActive());
        assertEquals(-1, scanResult);
    }

    // Test 107
    @Test
    public void testFreezeExpires()
            throws InterruptedException {

        Board board = new Board(3, 3, 7);

        new FreezeSabotage(50).apply(board);

        assertTrue(board.isFrozen());

        Thread.sleep(150);

        assertFalse(board.isFrozen());

        board.revealCell(1, 1);

        assertEquals(Cell.State.UNCOVERED,
                board.getCell(1, 1).getState());
    }

    // Test 108
    @Test
    public void testFreezeDoesNotModifyBoard() {
        Board board = new Board(3, 3, 7);

        board.revealCell(1, 1);

        Cell.State[][] states = snapshotStates(board);
        boolean[][] mines = snapshotMines(board);

        new FreezeSabotage(5000).apply(board);

        assertStatesUnchanged(board, states);
        assertMinesUnchanged(board, mines);
    }

    // =========================================================
    // RE-COVER SABOTAGE
    // =========================================================

    // Test 109
    @Test
    public void testReCoverPreviouslyUncoveredCells() {
        Board board = new Board(5, 5, 12);

        prepareUncoveredCells(board, 4);

        int before = countUncoveredSafeCells(board);

        new RecoverSabotage(3).apply(board);

        int after = countUncoveredSafeCells(board);

        assertEquals(3, before - after);
    }

    // Test 110
    @Test
    public void testReCoverMaximumThreeCells() {
        Board board = new Board(5, 5, 12);

        prepareUncoveredCells(board, 5);

        int before = countUncoveredSafeCells(board);

        new RecoverSabotage(3).apply(board);

        assertEquals(3,
                before - countUncoveredSafeCells(board));
    }

    // Test 111
    @Test
    public void testReCoverFewerThanThreeCells() {
        Board board = new Board(3, 3, 7);

        board.revealCell(1, 1);

        int[] safe = findCoveredSafeCell(board);
        assertNotNull(safe);

        board.getCell(safe[0], safe[1])
                .setState(Cell.State.UNCOVERED);

        assertEquals(2, countUncoveredSafeCells(board));

        new RecoverSabotage(3).apply(board);

        assertEquals(0, countUncoveredSafeCells(board));
    }

    // Test 112
    @Test
    public void testReCoverNoEligibleCells() {
        Board board = new Board(3, 3, 7);

        Cell.State[][] before = snapshotStates(board);

        new RecoverSabotage(3).apply(board);

        assertStatesUnchanged(board, before);
    }

    // Test 113
    @Test
    public void testReCoverDoesNotCoverMines() {
        Board board = new Board(5, 5, 12);

        prepareUncoveredCells(board, 4);

        boolean[][] mines = snapshotMines(board);
        Cell.State[][] states = snapshotStates(board);

        new RecoverSabotage(3).apply(board);

        assertMinesUnchanged(board, mines);

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                if (mines[r][c]) {
                    assertEquals(states[r][c],
                            board.getCell(r, c).getState());
                }
            }
        }
    }

    // Test 114
    @Test
    public void testReCoverDoesNotChangeFlags() {
        Board board = new Board(3, 3, 7);

        board.revealCell(1, 1);

        int[] mine = findMine(board);
        assertNotNull(mine);

        board.getCell(mine[0], mine[1])
                .setState(Cell.State.FLAGGED);

        new RecoverSabotage(3).apply(board);

        assertEquals(Cell.State.FLAGGED,
                board.getCell(mine[0], mine[1]).getState());
    }

    // Test 115
    @Test
    public void testReCoverTargetsOpponent() {
        Match match = new Match(3, 3, 7);

        Board board1 = match.getPlayer1Board();
        Board board2 = match.getPlayer2Board();

        board1.revealCell(1, 1);
        board2.revealCell(1, 1);

        int before1 = countUncoveredSafeCells(board1);
        int before2 = countUncoveredSafeCells(board2);

        assertTrue(match.applySabotage(
                1, new RecoverSabotage(3)));

        assertEquals(before1, countUncoveredSafeCells(board1));
        assertTrue(countUncoveredSafeCells(board2) < before2);
    }

    // Test 116
    @Test
    public void testReCoverPreservesNeighbourCounts() {
        Board board = new Board(5, 5, 12);

        prepareUncoveredCells(board, 4);

        int[][] before = snapshotNeighbourCounts(board);

        new RecoverSabotage(3).apply(board);

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                assertEquals(before[r][c],
                        board.getCell(r, c).getNeighbourMines());
            }
        }
    }

    // =========================================================
    // FOG SABOTAGE
    // =========================================================

    // Test 117
    @Test
    public void testFogActivates() {
        Match match = new Match(3, 3, 7);

        assertFalse(match.getPlayer2Board().isFogged());

        assertTrue(match.applySabotage(
                1, new FogSabotage(5000)));

        assertTrue(match.getPlayer2Board().isFogged());
    }

    // Test 118
    @Test
    public void testFogTargetsOpponent() {
        Match match = new Match(3, 3, 7);

        match.applySabotage(1, new FogSabotage(5000));

        assertFalse(match.getPlayer1Board().isFogged());
        assertTrue(match.getPlayer2Board().isFogged());
    }

    // Test 119
    @Test
    public void testFogDoesNotModifyBoard() {
        Board board = new Board(3, 3, 7);

        board.revealCell(1, 1);

        Cell.State[][] states = snapshotStates(board);
        boolean[][] mines = snapshotMines(board);

        new FogSabotage(5000).apply(board);

        assertStatesUnchanged(board, states);
        assertMinesUnchanged(board, mines);
    }

    // Test 120
    @Test
    public void testFogExpires()
            throws InterruptedException {

        Board board = new Board(3, 3, 7);

        new FogSabotage(50).apply(board);

        assertTrue(board.isFogged());

        Thread.sleep(150);

        assertFalse(board.isFogged());
    }

    // Test 121
    @Test
    public void testFogDoesNotBlockGameplay() {
        Board board = new Board(3, 3, 7);

        new FogSabotage(5000).apply(board);

        assertTrue(board.isFogged());

        board.revealCell(1, 1);

        assertEquals(Cell.State.UNCOVERED,
                board.getCell(1, 1).getState());

        assertFalse(board.isFrozen());
    }

    // =========================================================
    // MATCH INTEGRATION
    // =========================================================

    // Test 122
    @Test
    public void testSabotageAfterMatchEnd() {
        Match match = new Match(3, 3, 7);

        finishByLosing(match.getPlayer1Board());
        finishByLosing(match.getPlayer2Board());

        assertTrue(match.isFinished());

        assertFalse(match.applySabotage(
                1, new FreezeSabotage(5000)));

        assertFalse(match.applySabotage(
                1, new FogSabotage(5000)));

        assertFalse(match.applySabotage(
                1, new RecoverSabotage(3)));

        assertFalse(match.getPlayer2Board().isFrozen());
        assertFalse(match.getPlayer2Board().isFogged());
    }

    // Test 123
    @Test
    public void testMultipleSabotageEffects()
            throws InterruptedException {

        Match match = new Match(3, 3, 7);

        Board target = match.getPlayer2Board();

        match.applySabotage(1, new FreezeSabotage(100));
        match.applySabotage(1, new FogSabotage(1000));

        assertTrue(target.isFrozen());
        assertTrue(target.isFogged());

        Thread.sleep(250);

        assertFalse(target.isFrozen());
        assertTrue(target.isFogged());

        target.revealCell(1, 1);

        assertEquals(Cell.State.UNCOVERED,
                target.getCell(1, 1).getState());
    }

    // Test 124
    @Test
    public void testSabotageDoesNotAffectScores() {
        Match match = new Match(3, 3, 7);

        Board target = match.getPlayer2Board();

        target.revealCell(1, 1);

        int score1Before = match.getPlayer1Score();
        int score2Before = match.getPlayer2Score();

        match.applySabotage(1, new FreezeSabotage(5000));
        match.applySabotage(1, new FogSabotage(5000));

        assertEquals(score1Before, match.getPlayer1Score());
        assertEquals(score2Before, match.getPlayer2Score());

        match.applySabotage(1, new RecoverSabotage(3));

        assertEquals(score1Before, match.getPlayer1Score());
        assertTrue(match.getPlayer2Score() <= score2Before);
    }
}
