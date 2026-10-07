package nl.tue.minesweeper.minesweeper;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class GameControllerTest {

    // ---------------------------------------------------------
    // Test 1: Controller starts at top-left (0,0)
    // ---------------------------------------------------------
    @Test
    public void startsAtTopLeft() {
        Board board = new Board(3, 3, 2);
        GameController controller = new GameController(board);

        assertEquals(0, controller.getSelectedRow());
        assertEquals(0, controller.getSelectedCol());

        // Controller should contain the exact same Board object
        assertSame(board, controller.getBoard());
    }


    // ---------------------------------------------------------
    // Test 2: Move down once
    // ---------------------------------------------------------
    @Test
    public void moveDown() {
        Board board = new Board(3, 3, 2);
        GameController controller = new GameController(board);

        controller.moveDown();

        assertEquals(1, controller.getSelectedRow());
        assertEquals(0, controller.getSelectedCol());
    }


    // ---------------------------------------------------------
    // Test 3: Move right once
    // ---------------------------------------------------------
    @Test
    public void moveRight() {
        Board board = new Board(3, 3, 2);
        GameController controller = new GameController(board);

        controller.moveRight();

        assertEquals(0, controller.getSelectedRow());
        assertEquals(1, controller.getSelectedCol());
    }


    // ---------------------------------------------------------
    // Test 4: Move down and then back up
    // ---------------------------------------------------------
    @Test
    public void moveUp() {
        Board board = new Board(3, 3, 2);
        GameController controller = new GameController(board);

        controller.moveDown();
        controller.moveUp();

        assertEquals(0, controller.getSelectedRow());
        assertEquals(0, controller.getSelectedCol());
    }


    // ---------------------------------------------------------
    // Test 5: Move right and then back left
    // ---------------------------------------------------------
    @Test
    public void moveLeft() {
        Board board = new Board(3, 3, 2);
        GameController controller = new GameController(board);

        controller.moveRight();
        controller.moveLeft();

        assertEquals(0, controller.getSelectedRow());
        assertEquals(0, controller.getSelectedCol());
    }


    // ---------------------------------------------------------
    // Test 6: Cannot move above the board
    // ---------------------------------------------------------
    @Test
    public void cannotMoveAboveBoard() {
        Board board = new Board(3, 3, 2);
        GameController controller = new GameController(board);

        controller.moveUp();

        assertEquals(0, controller.getSelectedRow());
        assertEquals(0, controller.getSelectedCol());
    }


    // ---------------------------------------------------------
    // Test 7: Cannot move left of the board
    // ---------------------------------------------------------
    @Test
    public void cannotMoveLeftOfBoard() {
        Board board = new Board(3, 3, 2);
        GameController controller = new GameController(board);

        controller.moveLeft();

        assertEquals(0, controller.getSelectedRow());
        assertEquals(0, controller.getSelectedCol());
    }


    // ---------------------------------------------------------
    // Test 8: Cannot move below the board
    // ---------------------------------------------------------
    @Test
    public void cannotMoveBelowBoard() {
        Board board = new Board(3, 3, 2);
        GameController controller = new GameController(board);

        controller.moveDown();
        controller.moveDown();
        controller.moveDown();
        controller.moveDown();
        controller.moveDown();

        // Last valid row of a 3x3 board is row 2
        assertEquals(2, controller.getSelectedRow());
        assertEquals(0, controller.getSelectedCol());
    }


    // ---------------------------------------------------------
    // Test 9: Cannot move right of the board
    // ---------------------------------------------------------
    @Test
    public void cannotMoveRightOfBoard() {
        Board board = new Board(3, 3, 2);
        GameController controller = new GameController(board);

        controller.moveRight();
        controller.moveRight();
        controller.moveRight();
        controller.moveRight();
        controller.moveRight();

        // Last valid column of a 3x3 board is column 2
        assertEquals(0, controller.getSelectedRow());
        assertEquals(2, controller.getSelectedCol());
    }


    // ---------------------------------------------------------
    // Test 10: Reveal currently selected cell
    // ---------------------------------------------------------
    @Test
    public void revealSelectedCell() {
        Board board = new Board(3, 3, 2);
        GameController controller = new GameController(board);

        // Start: (0,0)
        controller.moveDown();   // (1,0)
        controller.moveRight();  // (1,1)

        controller.revealSelectedCell();

        assertEquals(
                Cell.State.UNCOVERED,
                board.getCell(1, 1).getState()
        );

        // Task 8 guarantees first revealed cell is safe
        assertFalse(board.getCell(1, 1).isMine());
    }
}
