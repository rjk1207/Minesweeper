
package nl.tue.minesweeper.minesweeper;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MinesweeperGUI {

    private JFrame frame;
    private JPanel mainPanel;
    private JPanel boardPanel;
    private JLabel statusLabel;
    private JLabel controlsLabel;
    private JLabel timerLabel;
    private JLabel minesLabel;
    private JComboBox<String> difficultyBox;

    private Board board;
    private GameController controller;
    private JButton[][] buttons;

    private Timer gameTimer;
    private int elapsedSeconds;
    private boolean timerStarted;

    private final Color coveredColor = new Color(100, 116, 139);
    private final Color uncoveredColor = new Color(226, 232, 240);
    private final Color flagColor = new Color(245, 158, 11);
    private final Color mineColor = new Color(220, 38, 38);

    private final Border normalBorder = BorderFactory.createLineBorder(
            new Color(203, 213, 225), 1);

    private final Border selectedBorder = BorderFactory.createLineBorder(
            new Color(30, 64, 175), 3);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MinesweeperGUI gui = new MinesweeperGUI();
            gui.createWindow();
        });
    }

    private void createWindow() {
        frame = new JFrame("Minesweeper");
        frame.setSize(900, 800);
        frame.setMinimumSize(new Dimension(650, 550));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout(0, 12));
        mainPanel.setBackground(new Color(241, 245, 249));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(
                20, 20, 20, 20));

        JLabel titleLabel = new JLabel("MINESWEEPER");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(30, 41, 59));

        statusLabel = new JLabel("Status: Playing");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));

        timerLabel = new JLabel("Time: 00:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timerLabel.setForeground(new Color(30, 41, 59));

        minesLabel = new JLabel("Mines Remaining: 10");
        minesLabel.setFont(new Font("Arial", Font.BOLD, 16));
        minesLabel.setForeground(new Color(30, 41, 59));

        controlsLabel = new JLabel(
                "Arrow Keys: Move | Enter/Space: Reveal | F: Flag");
        controlsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        controlsLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        controlsLabel.setForeground(new Color(71, 85, 105));

        difficultyBox = new JComboBox<>(
                new String[]{"Easy", "Medium", "Hard"});
        difficultyBox.setFocusable(false);

        JButton restartButton = new JButton("Restart Game");
        restartButton.setFocusable(false);
        restartButton.setFont(new Font("Arial", Font.BOLD, 14));
        restartButton.setBackground(new Color(59, 130, 246));
        restartButton.setForeground(Color.WHITE);

        JPanel difficultyPanel = new JPanel();
        difficultyPanel.setOpaque(false);
        difficultyPanel.add(new JLabel("Difficulty:"));
        difficultyPanel.add(difficultyBox);
        difficultyPanel.add(restartButton);

        JPanel countersPanel = new JPanel(new FlowLayout(
                FlowLayout.CENTER, 40, 0));
        countersPanel.setOpaque(false);
        countersPanel.add(timerLabel);
        countersPanel.add(minesLabel);

        JPanel headerPanel = new JPanel(new GridLayout(5, 1, 0, 5));
        headerPanel.setOpaque(false);
        headerPanel.add(titleLabel);
        headerPanel.add(statusLabel);
        headerPanel.add(countersPanel);
        headerPanel.add(difficultyPanel);
        headerPanel.add(controlsLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        gameTimer = new Timer(1000, e -> {
            if (board != null
                    && board.getStatus() == GameStatus.PLAYING) {
                elapsedSeconds++;
                updateTimerLabel();
            } else {
                gameTimer.stop();
            }
        });

        difficultyBox.addActionListener(e -> {
            restartCurrentDifficulty();
        });

        restartButton.addActionListener(e -> {
            restartCurrentDifficulty();
        });

        frame.add(mainPanel);
        startGame(9, 9, 10);
        frame.setVisible(true);
    }

    private void restartCurrentDifficulty() {
        String difficulty = (String) difficultyBox.getSelectedItem();

        if ("Easy".equals(difficulty)) {
            startGame(9, 9, 10);
        } else if ("Medium".equals(difficulty)) {
            startGame(16, 16, 40);
        } else if ("Hard".equals(difficulty)) {
            startGame(16, 30, 99);
        }
    }

    private void startGame(int rows, int cols, int mines) {
        if (gameTimer != null) {
            gameTimer.stop();
        }

        elapsedSeconds = 0;
        timerStarted = false;
        updateTimerLabel();

        board = new Board(rows, cols, mines);
        controller = new GameController(board);

        if (boardPanel != null) {
            mainPanel.remove(boardPanel);
        }

        boardPanel = new JPanel(new GridLayout(rows, cols, 3, 3));
        boardPanel.setBackground(new Color(148, 163, 184));

        buttons = new JButton[rows][cols];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                JButton button = new JButton();
                button.setFocusable(false);
                button.setFont(new Font("Arial", Font.BOLD,
                        cols > 16 ? 14 : 20));
                button.setOpaque(true);
                button.setContentAreaFilled(true);
                button.setFocusPainted(false);
                button.setBorderPainted(true);
                button.setMargin(new Insets(0, 0, 0, 0));

                buttons[row][col] = button;
                boardPanel.add(button);
            }
        }

        setupKeyboardControls();

        mainPanel.add(boardPanel, BorderLayout.CENTER);
        updateDisplay();

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void setupKeyboardControls() {
        InputMap inputMap = boardPanel.getInputMap(
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = boardPanel.getActionMap();

        String[] keys = {"UP", "DOWN", "LEFT", "RIGHT"};

        Runnable[] movements = {
            controller::moveUp,
            controller::moveDown,
            controller::moveLeft,
            controller::moveRight
        };

        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            Runnable movement = movements[i];

            inputMap.put(KeyStroke.getKeyStroke(key), key);

            actionMap.put(key, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    movement.run();
                    updateDisplay();
                }
            });
        }

        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "reveal");
        inputMap.put(KeyStroke.getKeyStroke("SPACE"), "reveal");

        actionMap.put("reveal", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                revealSelectedCell();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("F"), "flag");

        actionMap.put("flag", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleFlag();
            }
        });
    }

    private void revealSelectedCell() {
        if (board.getStatus() != GameStatus.PLAYING) {
            return;
        }

        int row = controller.getSelectedRow();
        int col = controller.getSelectedCol();

        Cell cell = board.getCell(row, col);

        if (cell.getState() != Cell.State.COVERED) {
            return;
        }

        if (!timerStarted) {
            timerStarted = true;
            gameTimer.start();
        }

        controller.revealSelectedCell();
        updateDisplay();
    }

    private void toggleFlag() {
        if (board.getStatus() != GameStatus.PLAYING) {
            return;
        }

        int row = controller.getSelectedRow();
        int col = controller.getSelectedCol();

        Cell cell = board.getCell(row, col);

        if (cell.getState() == Cell.State.COVERED) {
            cell.setState(Cell.State.FLAGGED);
        } else if (cell.getState() == Cell.State.FLAGGED) {
            cell.setState(Cell.State.COVERED);
        }

        updateDisplay();
    }

    private void updateTimerLabel() {
        int minutes = elapsedSeconds / 60;
        int seconds = elapsedSeconds % 60;

        timerLabel.setText(String.format(
                "Time: %02d:%02d", minutes, seconds));
    }

    private void updateMinesLabel() {
        int flaggedCells = 0;

        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                if (board.getCell(row, col).getState()
                        == Cell.State.FLAGGED) {
                    flaggedCells++;
                }
            }
        }

        int remainingMines =
                board.getNumberOfMines() - flaggedCells;

        minesLabel.setText(
                "Mines Remaining: " + remainingMines);
    }

    private void updateDisplay() {
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                JButton button = buttons[row][col];
                Cell cell = board.getCell(row, col);

                button.setText("");
                button.setEnabled(true);
                button.setBackground(coveredColor);
                button.setForeground(Color.WHITE);
                button.setBorder(normalBorder);

                if (cell.getState() == Cell.State.FLAGGED) {
                    button.setText("F");
                    button.setBackground(flagColor);
                    button.setForeground(Color.BLACK);
                } else if (cell.getState() == Cell.State.UNCOVERED) {
                    button.setBackground(uncoveredColor);

                    if (cell.isMine()) {
                        button.setText("*");
                        button.setBackground(mineColor);
                        button.setForeground(Color.WHITE);
                    } else {
                        int mines = cell.getNeighbourMines();

                        if (mines > 0) {
                            button.setText(String.valueOf(mines));

                            switch (mines) {
                                case 1:
                                    button.setForeground(
                                            new Color(37, 99, 235));
                                    break;
                                case 2:
                                    button.setForeground(
                                            new Color(21, 128, 61));
                                    break;
                                case 3:
                                    button.setForeground(
                                            new Color(220, 38, 38));
                                    break;
                                case 4:
                                    button.setForeground(
                                            new Color(88, 28, 135));
                                    break;
                                default:
                                    button.setForeground(
                                            new Color(15, 23, 42));
                                    break;
                            }
                        }
                    }
                }

                if (row == controller.getSelectedRow()
                        && col == controller.getSelectedCol()) {
                    button.setBorder(selectedBorder);
                }
            }
        }

        updateMinesLabel();

        if (board.getStatus() == GameStatus.WON) {
            statusLabel.setText("Status: You Won!");
            statusLabel.setForeground(new Color(21, 128, 61));
            gameTimer.stop();
        } else if (board.getStatus() == GameStatus.LOST) {
            statusLabel.setText("Status: Game Over!");
            statusLabel.setForeground(new Color(220, 38, 38));
            gameTimer.stop();
        } else {
            statusLabel.setText("Status: Playing");
            statusLabel.setForeground(new Color(30, 41, 59));
        }
    }
}
