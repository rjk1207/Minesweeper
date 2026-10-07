package nl.tue.minesweeper.minesweeper;

public class GameController {
    private final Board board;
    private int selectedRow = 0;
    private int selectedCol = 0;

    public GameController(Board board) {
        this.board = board;
    }

    public void moveUp() {
        if (selectedRow > 0) {
            selectedRow--;
        }
    }

    public void moveDown() {
        if (selectedRow < board.getRows() - 1) {
            selectedRow++;
        }
    }

    public void moveLeft() {
        if (selectedCol > 0) {
            selectedCol--;
        }
    }

    public void moveRight() {
        if (selectedCol < board.getCols() - 1) {
            selectedCol++;
        }
    }

    public void revealSelectedCell() {
        board.revealCell(selectedRow, selectedCol);
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    public int getSelectedCol() {
        return selectedCol;
    }

    public Board getBoard() {
        return board;
    }
}
