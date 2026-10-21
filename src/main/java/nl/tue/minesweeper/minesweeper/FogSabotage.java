package nl.tue.minesweeper.minesweeper;

public class FogSabotage implements Sabotage {
    private final long durationMillis;

    public FogSabotage(long durationMillis) {
        if (durationMillis <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        this.durationMillis = durationMillis;
    }

    @Override
    public void apply(Board target) {
        target.fog(durationMillis);
    }
}