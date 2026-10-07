package nl.tue.minesweeper.minesweeper;

public class Cell { 
    public enum State {FLAGGED, COVERED, UNCOVERED}
    private State state = State.COVERED;
    private boolean mine;
    private int neighbourMines;

    public State getState() {
        return state;
    }
    public void setState(State change) {
        if (change == State.FLAGGED && state == State.UNCOVERED) {
            return;
        }
        else if (change == State.UNCOVERED && state == State.FLAGGED) {
            return;
        }
        else {
            this.state = change;
        }
    }

    public boolean isMine() {
        return mine;
    }
    public void setMine(boolean ismine) {
        this.mine = ismine;
    }

    public int getNeighbourMines() {
        return neighbourMines;
    }
    public void setNeighbourMines(int mines) {
        this.neighbourMines = mines;
    }
}
