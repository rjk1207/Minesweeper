package nl.tue.minesweeper.minesweeper;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class MatchTest {

    // =========================================================
    // HELPER METHODS
    // =========================================================

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
                if (!cell.isMine() && cell.getState() == Cell.State.COVERED) {
                    return new int[]{r, c};
                }
            }
        }
        return null;
    }

    private int countUncoveredSafeCells(Board board) {
        int count = 0;
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                Cell cell = board.getCell(r, c);
                if (!cell.isMine() && cell.getState() == Cell.State.UNCOVERED) {
                    count++;
                }
            }
        }
        return count;
    }

    private void loseBoard(Board board) {
        board.revealCell(1, 1);
        assertEquals(GameStatus.PLAYING, board.getStatus());
        int[] mine = findMine(board);
        assertNotNull(mine);
        board.revealCell(mine[0], mine[1]);
        assertEquals(GameStatus.LOST, board.getStatus());
    }

    private static class DummySabotage implements Sabotage {
        private boolean applied;
        private Board targetBoard;

        @Override
        public void apply(Board target) {
            applied = true;
            targetBoard = target;
        }
    }

    private static class OtherDummySabotage implements Sabotage {
        private int applicationCount;
        private Board targetBoard;

        @Override
        public void apply(Board target) {
            applicationCount++;
            targetBoard = target;
        }
    }

    // =========================================================
    // TESTS 77-84: VERSUS MODE MATCH SYSTEM
    // =========================================================

    // Test 77
    @Test
    public void testTwoBoardsCreated() {
        Match match = new Match(3, 3, 7);
        assertNotNull(match.getPlayer1Board());
        assertNotNull(match.getPlayer2Board());
    }

    // Test 78
    @Test
    public void testBoardsAreIndependent() {
        Match match = new Match(3, 3, 7);
        Board board1 = match.getPlayer1Board();
        Board board2 = match.getPlayer2Board();
        assertNotSame(board1, board2);
        board1.revealCell(1, 1);
        assertEquals(Cell.State.UNCOVERED, board1.getCell(1, 1).getState());
        assertEquals(Cell.State.COVERED, board2.getCell(1, 1).getState());
        assertEquals(GameStatus.PLAYING, board2.getStatus());
    }

    // Test 79
    @Test
    public void testTwoControllersCreated() {
        Match match = new Match(3, 3, 7);
        assertNotNull(match.getPlayer1Controller());
        assertNotNull(match.getPlayer2Controller());
        assertNotSame(match.getPlayer1Controller(), match.getPlayer2Controller());
        assertSame(match.getPlayer1Board(), match.getPlayer1Controller().getBoard());
        assertSame(match.getPlayer2Board(), match.getPlayer2Controller().getBoard());
    }

    // Test 80
    @Test
    public void testControllersAreIndependent() {
        Match match = new Match(3, 3, 7);
        match.getPlayer1Controller().moveRight();
        match.getPlayer1Controller().moveDown();
        assertEquals(1, match.getPlayer1Controller().getSelectedRow());
        assertEquals(1, match.getPlayer1Controller().getSelectedCol());
        assertEquals(0, match.getPlayer2Controller().getSelectedRow());
        assertEquals(0, match.getPlayer2Controller().getSelectedCol());
    }

    // Test 81
    @Test
    public void testSameDifficulty() {
        Match match = new Match(5, 6, 8);
        Board board1 = match.getPlayer1Board();
        Board board2 = match.getPlayer2Board();
        assertEquals(5, board1.getRows());
        assertEquals(6, board1.getCols());
        assertEquals(8, board1.getNumberOfMines());
        assertEquals(board1.getRows(), board2.getRows());
        assertEquals(board1.getCols(), board2.getCols());
        assertEquals(board1.getNumberOfMines(), board2.getNumberOfMines());
    }

    // Test 82
    @Test
    public void testInitialGameStatus() {
        Match match = new Match(3, 3, 7);
        assertEquals(GameStatus.PLAYING, match.getPlayer1Status());
        assertEquals(GameStatus.PLAYING, match.getPlayer2Status());
        assertFalse(match.isFinished());
        assertEquals(MatchResult.IN_PROGRESS, match.getResult());
    }

    // Test 83
    @Test
    public void testOnePlayerLoses() {
        Match match = new Match(3, 3, 7);
        loseBoard(match.getPlayer1Board());
        assertEquals(GameStatus.LOST, match.getPlayer1Status());
        assertEquals(GameStatus.PLAYING, match.getPlayer2Status());
        assertFalse(match.isFinished());
        match.getPlayer2Board().revealCell(1, 1);
        assertEquals(Cell.State.UNCOVERED,
                match.getPlayer2Board().getCell(1, 1).getState());
    }

    // Test 84
    @Test
    public void testBothPlayersFinish() {
        Match match = new Match(3, 3, 7);
        loseBoard(match.getPlayer1Board());
        assertFalse(match.isFinished());
        loseBoard(match.getPlayer2Board());
        assertTrue(match.isFinished());
    }

    // =========================================================
    // TESTS 85-94: MULTIPLAYER SCORING SYSTEM
    // =========================================================

    // Test 85
    @Test
    public void testInitialScores() {
        Match match = new Match(3, 3, 7);
        assertEquals(0, match.getPlayer1Score());
        assertEquals(0, match.getPlayer2Score());
    }

    // Test 86
    @Test
    public void testSingleCellScore() {
        Match match = new Match(3, 3, 7);
        match.getPlayer1Board().revealCell(1, 1);
        assertEquals(1, countUncoveredSafeCells(match.getPlayer1Board()));
        assertEquals(10, match.getPlayer1Score());
    }

    // Test 87
    @Test
    public void testFloodFillScore() {
        Match match = new Match(3, 3, 0);
        match.getPlayer1Board().revealCell(1, 1);
        assertEquals(9, countUncoveredSafeCells(match.getPlayer1Board()));
        assertEquals(90, match.getPlayer1Score());
    }

    // Test 88
    @Test
    public void testScoresAreIndependent() {
        Match match = new Match(3, 3, 7);
        match.getPlayer1Board().revealCell(1, 1);
        assertEquals(10, match.getPlayer1Score());
        assertEquals(0, match.getPlayer2Score());
    }

    // Test 89
    @Test
    public void testNoDuplicatePoints() {
        Match match = new Match(3, 3, 7);
        Board board = match.getPlayer1Board();
        board.revealCell(1, 1);
        int scoreBefore = match.getPlayer1Score();
        board.revealCell(1, 1);
        assertEquals(scoreBefore, match.getPlayer1Score());
    }

    // Test 90
    @Test
    public void testChordScoring() {
        Match match = new Match(3, 3, 7);
        Board board = match.getPlayer1Board();
        board.revealCell(1, 1);
        int before = countUncoveredSafeCells(board);
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board.getCell(r, c).isMine()) {
                    board.getCell(r, c).setState(Cell.State.FLAGGED);
                }
            }
        }
        board.chordCell(1, 1);
        int newlyUncovered = countUncoveredSafeCells(board) - before;
        assertEquals(1, newlyUncovered);
        assertEquals((before + newlyUncovered) * 10, match.getPlayer1Score());
        assertEquals(GameStatus.WON, board.getStatus());
    }

    // Test 91
    @Test
    public void testHintScoring() {
        Match match = new Match(3, 3, 7);
        Board board = match.getPlayer1Board();
        board.revealCell(1, 1);
        int before = countUncoveredSafeCells(board);
        board.useHint();
        int after = countUncoveredSafeCells(board);
        assertTrue(after > before);
        assertEquals(after * 10, match.getPlayer1Score());
    }

    // Test 92
    @Test
    public void testNoPointsForScanner() {
        Match match = new Match(3, 3, 7);
        Board board = match.getPlayer1Board();
        board.revealCell(1, 1);
        int scoreBefore = match.getPlayer1Score();
        assertTrue(board.scanArea(1, 1) >= 0);
        assertEquals(scoreBefore, match.getPlayer1Score());
    }

    // Test 93
    @Test
    public void testWinnerByScore() {
        Match match = new Match(3, 3, 7);
        Board board1 = match.getPlayer1Board();
        board1.revealCell(1, 1);
        int[] safe = findCoveredSafeCell(board1);
        assertNotNull(safe);
        board1.revealCell(safe[0], safe[1]);
        assertEquals(GameStatus.WON, board1.getStatus());
        loseBoard(match.getPlayer2Board());
        assertTrue(match.isFinished());
        assertEquals(20, match.getPlayer1Score());
        assertEquals(10, match.getPlayer2Score());
        assertEquals(MatchResult.PLAYER1_WINS, match.getResult());
    }

    // Test 94
    @Test
    public void testDraw() {
        Match match = new Match(3, 3, 7);
        loseBoard(match.getPlayer1Board());
        loseBoard(match.getPlayer2Board());
        assertTrue(match.isFinished());
        assertEquals(10, match.getPlayer1Score());
        assertEquals(10, match.getPlayer2Score());
        assertEquals(MatchResult.DRAW, match.getResult());
    }

    // =========================================================
    // TESTS 95-100: SABOTAGE INTERFACE
    // =========================================================

    // Test 95
    @Test
    public void testSabotageTargetsPlayerTwo() {
        Match match = new Match(3, 3, 7);
        DummySabotage sabotage = new DummySabotage();
        assertTrue(match.applySabotage(1, sabotage));
        assertTrue(sabotage.applied);
        assertSame(match.getPlayer2Board(), sabotage.targetBoard);
    }

    // Test 96
    @Test
    public void testSabotageTargetsPlayerOne() {
        Match match = new Match(3, 3, 7);
        DummySabotage sabotage = new DummySabotage();
        assertTrue(match.applySabotage(2, sabotage));
        assertTrue(sabotage.applied);
        assertSame(match.getPlayer1Board(), sabotage.targetBoard);
    }

    // Test 97
    @Test
    public void testInvalidPlayer() {
        Match match = new Match(3, 3, 7);
        DummySabotage sabotage = new DummySabotage();
        assertThrows(IllegalArgumentException.class,
                () -> match.applySabotage(3, sabotage));
        assertThrows(IllegalArgumentException.class,
                () -> match.applySabotage(0, sabotage));
        assertFalse(sabotage.applied);
    }

    // Test 98
    @Test
    public void testNullSabotage() {
        Match match = new Match(3, 3, 7);
        assertThrows(IllegalArgumentException.class,
                () -> match.applySabotage(1, null));
    }

    // Test 99
    @Test
    public void testSabotageAfterMatchEnd() {
        Match match = new Match(3, 3, 7);
        loseBoard(match.getPlayer1Board());
        loseBoard(match.getPlayer2Board());
        assertTrue(match.isFinished());
        DummySabotage sabotage = new DummySabotage();
        assertFalse(match.applySabotage(1, sabotage));
        assertFalse(sabotage.applied);
        assertNull(sabotage.targetBoard);
    }

    // Test 100
    @Test
    public void testSabotageStrategyDelegation() {
        Match match = new Match(3, 3, 7);
        DummySabotage first = new DummySabotage();
        OtherDummySabotage second = new OtherDummySabotage();
        assertTrue(match.applySabotage(1, first));
        assertTrue(match.applySabotage(2, second));
        assertTrue(first.applied);
        assertEquals(1, second.applicationCount);
        assertSame(match.getPlayer2Board(), first.targetBoard);
        assertSame(match.getPlayer1Board(), second.targetBoard);
    }
}
