package nl.tue.minesweeper.minesweeper;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class BoardTest {

    // Test 1: Board should have 3 rows
    @Test
    public void boardHasThreeRows() {
        Board board = new Board(3, 4);

        assertEquals(3, board.getRows());
    }

    // Test 2: Board should have 4 columns
    @Test
    public void boardHasFourColumns() {
        Board board = new Board(3, 4);

        assertEquals(4, board.getCols());
    }

    // Test 3: Cell at (0,0) should not be null
    @Test
    public void firstCellIsNotNull() {
        Board board = new Board(3, 4);

        assertNotNull(board.getCell(0, 0));
    }

    // Test 4: Cell at (2,3) should not be null
    @Test
    public void lastCellIsNotNull() {
        Board board = new Board(3, 4);

        assertNotNull(board.getCell(2, 3));
    }

    // Test 5: Every cell in a 3x4 board should not be null
    @Test
    public void allCellsAreNotNull() {
        Board board = new Board(3, 4);

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 4; c++) {
                assertNotNull(board.getCell(r, c));
            }
        }
    }
}