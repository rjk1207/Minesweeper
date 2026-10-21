package nl.tue.minesweeper.minesweeper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RecoverSabotage implements Sabotage {
    private final int count;
    private final Random random = new Random();

    public RecoverSabotage(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Count must be at least 1");
        }
        this.count = count;
    }

    @Override
    public void apply(Board target) {
        if (target.getStatus() != GameStatus.PLAYING) {
            return;
        }

        List<int[]> eligible = new ArrayList<>();
        for (int r = 0; r < target.getRows(); r++) {
            for (int c = 0; c < target.getCols(); c++) {
                Cell cell = target.getCell(r, c);
                if (cell.getState() == Cell.State.UNCOVERED && !cell.isMine()) {
                    eligible.add(new int[] {r, c});
                }
            }
        }

        Collections.shuffle(eligible, random);

        int toRecover = Math.min(count, eligible.size());
        for (int i = 0; i < toRecover; i++) {
            int[] position = eligible.get(i);
            target.getCell(position[0], position[1]).setState(Cell.State.COVERED);
        }
    }
}