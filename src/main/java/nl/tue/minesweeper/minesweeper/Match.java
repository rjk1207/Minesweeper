package nl.tue.minesweeper.minesweeper;

public class Match {
    private static final int POINTS_PER_SAFE_CELL = 10;

    private final Board player1Board;
    private final Board player2Board;
    private final GameController player1Controller;
    private final GameController player2Controller;

    public Match(int rows, int cols, int numberOfMines) {
        this.player1Board = new Board(rows, cols, numberOfMines);
        this.player2Board = new Board(rows, cols, numberOfMines);
        this.player1Controller = new GameController(player1Board);
        this.player2Controller = new GameController(player2Board);
    }

    public Board getPlayer1Board() {
        return player1Board;
    }

    public Board getPlayer2Board() {
        return player2Board;
    }

    public GameController getPlayer1Controller() {
        return player1Controller;
    }

    public GameController getPlayer2Controller() {
        return player2Controller;
    }

    public GameStatus getPlayer1Status() {
        return player1Board.getStatus();
    }

    public GameStatus getPlayer2Status() {
        return player2Board.getStatus();
    }

    public boolean isFinished() {
        return player1Board.getStatus() != GameStatus.PLAYING
                && player2Board.getStatus() != GameStatus.PLAYING;
    }

    public int getPlayer1Score() {
        return calculateScore(player1Board);
    }

    public int getPlayer2Score() {
        return calculateScore(player2Board);
    }

    private int calculateScore(Board board) {
        int safeUncovered = 0;
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                Cell cell = board.getCell(r, c);
                if (cell.getState() == Cell.State.UNCOVERED && !cell.isMine()) {
                    safeUncovered++;
                }
            }
        }
        return safeUncovered * POINTS_PER_SAFE_CELL;
    }

    public MatchResult getResult() {
        if (!isFinished()) {
            return MatchResult.IN_PROGRESS;
        }
        int score1 = getPlayer1Score();
        int score2 = getPlayer2Score();
        if (score1 > score2) {
            return MatchResult.PLAYER1_WINS;
        }
        if (score2 > score1) {
            return MatchResult.PLAYER2_WINS;
        }
        return MatchResult.DRAW;
    }
}