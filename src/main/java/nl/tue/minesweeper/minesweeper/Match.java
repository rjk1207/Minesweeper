package nl.tue.minesweeper.minesweeper;

public class Match {
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
}