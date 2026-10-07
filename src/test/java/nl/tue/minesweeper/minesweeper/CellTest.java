package nl.tue.minesweeper.minesweeper;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class CellTest {

    // Test 1: New cell should be COVERED
    @Test
    public void newCellIsCovered() {
        Cell cell = new Cell();

        assertEquals(Cell.State.COVERED, cell.getState());
    }

    // Test 2: New cell should not be a mine
    @Test
    public void newCellIsNotMine() {
        Cell cell = new Cell();

        assertFalse(cell.isMine());
    }

    // Test 3: Setting mine to true
    @Test
    public void setMineToTrue() {
        Cell cell = new Cell();

        cell.setMine(true);

        assertTrue(cell.isMine());
    }

    // Test 4: Set neighbour mines to 3
    @Test
    public void setNeighbourMinesToThree() {
        Cell cell = new Cell();

        cell.setNeighbourMines(3);

        assertEquals(3, cell.getNeighbourMines());
    }

    // Test 5: COVERED -> FLAGGED
    @Test
    public void coveredToFlagged() {
        Cell cell = new Cell();

        cell.setState(Cell.State.FLAGGED);

        assertEquals(Cell.State.FLAGGED, cell.getState());
    }

    // Test 6: FLAGGED -> COVERED
    @Test
    public void flaggedToCovered() {
        Cell cell = new Cell();

        cell.setState(Cell.State.FLAGGED);
        cell.setState(Cell.State.COVERED);

        assertEquals(Cell.State.COVERED, cell.getState());
    }

    // Test 7: COVERED -> UNCOVERED
    @Test
    public void coveredToUncovered() {
        Cell cell = new Cell();

        cell.setState(Cell.State.UNCOVERED);

        assertEquals(Cell.State.UNCOVERED, cell.getState());
    }

    // Test 8: UNCOVERED -> FLAGGED should NOT work
    @Test
    public void uncoveredCannotBeFlagged() {
        Cell cell = new Cell();

        cell.setState(Cell.State.UNCOVERED);
        cell.setState(Cell.State.FLAGGED);

        assertEquals(Cell.State.UNCOVERED, cell.getState());
    }

    // Test 9: FLAGGED -> UNCOVERED should NOT work
    @Test
    public void flaggedCannotBeUncovered() {
        Cell cell = new Cell();

        cell.setState(Cell.State.FLAGGED);
        cell.setState(Cell.State.UNCOVERED);

        assertEquals(Cell.State.FLAGGED, cell.getState());
    }
}