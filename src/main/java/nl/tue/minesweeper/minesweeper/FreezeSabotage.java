package nl.tue.minesweeper.minesweeper;

public class FreezeSabotage implements Sabotage {
    private final long durationMillis;

    public FreezeSabotage(long durationMillis) {
        if (durationMillis <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        this.durationMillis = durationMillis;
    }

    @Override
    public void apply(Board target) {
        target.freeze(durationMillis);
    }
}