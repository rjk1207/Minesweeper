
package nl.tue.minesweeper.minesweeper;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MinesweeperGUI {

    private JFrame frame;
    private JPanel mainPanel;
    private JPanel gameArea;
    private JPanel headerPanel;

    private JLabel statusLabel;
    private JLabel timerLabel;
    private JLabel messageLabel;
    private JLabel controlsLabel;

    private JComboBox<String> modeBox;
    private JComboBox<String> difficultyBox;

    private Board[] boards;
    private GameController[] controllers;
    private JButton[][][] buttons;

    private JLabel[] playerStatusLabels;
    private JLabel[] playerScoreLabels;
    private JLabel[] playerMineLabels;
    private JLabel[] playerAbilityLabels;

    private JPanel[] playerPanels;
    private JPanel[] boardPanels;

    private Match match;

    private Timer gameTimer;
    private Timer refreshTimer;

    private int elapsedSeconds;
    private boolean timerStarted;
    private boolean rebuilding;

    private final Color coveredColor = new Color(100, 116, 139);
    private final Color uncoveredColor = new Color(226, 232, 240);
    private final Color flagColor = new Color(245, 158, 11);
    private final Color mineColor = new Color(220, 38, 38);
    private final Color backgroundColor = new Color(241, 245, 249);
    private final Color textColor = new Color(30, 41, 59);

    private final Border normalBorder =
            BorderFactory.createLineBorder(
                    new Color(203, 213, 225), 1);

    private final Border selectedBorder =
            BorderFactory.createLineBorder(
                    new Color(37, 99, 235), 3);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MinesweeperGUI gui = new MinesweeperGUI();
            gui.createWindow();
        });
    }

    private boolean isMultiplayer() {
        return "Multiplayer".equals(modeBox.getSelectedItem());
    }

    private int playerCount() {
        return isMultiplayer() ? 2 : 1;
    }

    private void createWindow() {
        frame = new JFrame("Minesweeper");
        frame.setSize(1350, 850);
        frame.setMinimumSize(new Dimension(850, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout(0, 10));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel titleLabel = new JLabel("MINESWEEPER");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(textColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        statusLabel = new JLabel("Status: Playing");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 17));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        timerLabel = new JLabel("Time: 00:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 15));
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        messageLabel = new JLabel("Game ready");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        messageLabel.setForeground(new Color(71, 85, 105));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        controlsLabel = new JLabel(
                "P1: WASD, Space, F, H, G, C | P2: Arrows, Enter, L, O, P, K");
        controlsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        controlsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        modeBox = new JComboBox<>(
                new String[]{"Single Player", "Multiplayer"});
        modeBox.setFocusable(false);

        difficultyBox = new JComboBox<>(
                new String[]{"Easy", "Medium", "Hard"});
        difficultyBox.setFocusable(false);

        JButton restartButton = new JButton("Restart Game");
        restartButton.setFocusable(false);

        JPanel settingsPanel = new JPanel(new FlowLayout(
                FlowLayout.CENTER, 10, 4));
        settingsPanel.setOpaque(false);
        settingsPanel.add(new JLabel("Mode:"));
        settingsPanel.add(modeBox);
        settingsPanel.add(new JLabel("Difficulty:"));
        settingsPanel.add(difficultyBox);
        settingsPanel.add(restartButton);

        headerPanel = new JPanel();
        headerPanel.setLayout(
                new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(statusLabel);
        headerPanel.add(timerLabel);
        headerPanel.add(settingsPanel);
        headerPanel.add(messageLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(controlsLabel);

        for (Component component : headerPanel.getComponents()) {
            if (component instanceof JComponent) {
                ((JComponent) component).setAlignmentX(
                        Component.CENTER_ALIGNMENT);
            }
        }

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        gameArea = new JPanel();
        gameArea.setBackground(backgroundColor);
        mainPanel.add(gameArea, BorderLayout.CENTER);

        gameTimer = new Timer(1000, e -> {
            if (!isGameFinished()) {
                elapsedSeconds++;
                updateTimerLabel();
            } else {
                gameTimer.stop();
            }
        });

        refreshTimer = new Timer(200, e -> {
            if (!rebuilding && boards != null) {
                updateDisplay();
            }
        });

        modeBox.addActionListener(e -> restartCurrentDifficulty());
        difficultyBox.addActionListener(e -> restartCurrentDifficulty());
        restartButton.addActionListener(e -> restartCurrentDifficulty());

        frame.setContentPane(mainPanel);

        restartCurrentDifficulty();
        refreshTimer.start();

        frame.setVisible(true);
    }

    private void restartCurrentDifficulty() {
        String difficulty = (String) difficultyBox.getSelectedItem();

        if ("Easy".equals(difficulty)) {
            startGame(9, 9, 10);
        } else if ("Medium".equals(difficulty)) {
            startGame(16, 16, 40);
        } else {
            startGame(16, 30, 99);
        }
    }

    private void startGame(int rows, int cols, int mines) {
        rebuilding = true;

        gameTimer.stop();
        elapsedSeconds = 0;
        timerStarted = false;
        updateTimerLabel();

        int count = playerCount();

        boards = new Board[count];
        controllers = new GameController[count];
        buttons = new JButton[count][][];
        playerStatusLabels = new JLabel[count];
        playerScoreLabels = new JLabel[count];
        playerMineLabels = new JLabel[count];
        playerAbilityLabels = new JLabel[count];
        playerPanels = new JPanel[count];
        boardPanels = new JPanel[count];

        if (isMultiplayer()) {
            match = new Match(rows, cols, mines);

            boards[0] = match.getPlayer1Board();
            boards[1] = match.getPlayer2Board();

            controllers[0] = match.getPlayer1Controller();
            controllers[1] = match.getPlayer2Controller();
        } else {
            match = null;
            boards[0] = new Board(rows, cols, mines);
            controllers[0] = new GameController(boards[0]);
        }

        gameArea.removeAll();
        gameArea.setLayout(
                new GridLayout(1, count, 12, 0));

        for (int player = 0; player < count; player++) {
            createPlayerPanel(player);
            gameArea.add(playerPanels[player]);
        }

        messageLabel.setText("Game ready");

        setupKeyboardControls();

        gameArea.revalidate();
        gameArea.repaint();

        rebuilding = false;
        updateDisplay();
    }

    private void createPlayerPanel(int player) {
        Board currentBoard = boards[player];

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(203, 213, 225), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel nameLabel = new JLabel(
                isMultiplayer()
                ? "PLAYER " + (player + 1)
                : "SINGLE PLAYER");

        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 19));
        nameLabel.setForeground(textColor);

        playerStatusLabels[player] = new JLabel("Playing");
        playerStatusLabels[player].setHorizontalAlignment(
                SwingConstants.CENTER);

        playerScoreLabels[player] = new JLabel("Score: 0");
        playerScoreLabels[player].setHorizontalAlignment(
                SwingConstants.CENTER);

        playerMineLabels[player] = new JLabel(
                "Mines Remaining: " + currentBoard.getNumberOfMines());
        playerMineLabels[player].setHorizontalAlignment(
                SwingConstants.CENTER);

        playerAbilityLabels[player] = new JLabel("Abilities ready");
        playerAbilityLabels[player].setHorizontalAlignment(
                SwingConstants.CENTER);
        playerAbilityLabels[player].setFont(
                new Font("Arial", Font.PLAIN, 12));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(
                new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        infoPanel.add(nameLabel);
        infoPanel.add(playerStatusLabels[player]);
        infoPanel.add(playerScoreLabels[player]);
        infoPanel.add(playerMineLabels[player]);
        infoPanel.add(playerAbilityLabels[player]);

        for (Component component : infoPanel.getComponents()) {
            if (component instanceof JComponent) {
                ((JComponent) component).setAlignmentX(
                        Component.CENTER_ALIGNMENT);
            }
        }

        panel.add(infoPanel, BorderLayout.NORTH);

        int rows = currentBoard.getRows();
        int cols = currentBoard.getCols();

        JPanel grid = new JPanel(new GridLayout(
                rows, cols, 2, 2));
        grid.setBackground(new Color(148, 163, 184));

        boardPanels[player] = grid;
        buttons[player] = new JButton[rows][cols];

        int cellSize = cols > 16 ? 24 : 32;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                JButton button = new JButton();

                button.setFocusable(false);
                button.setFont(new Font(
                        "Arial", Font.BOLD,
                        cols > 16 ? 11 : 16));

                button.setOpaque(true);
                button.setContentAreaFilled(true);
                button.setFocusPainted(false);
                button.setMargin(new Insets(0, 0, 0, 0));

                button.setPreferredSize(
                        new Dimension(cellSize, cellSize));

                buttons[player][row][col] = button;
                grid.add(button);
            }
        }

        grid.setPreferredSize(new Dimension(
                cols * cellSize + (cols - 1) * 2,
                rows * cellSize + (rows - 1) * 2));

        JScrollPane scrollPane = new JScrollPane(grid);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(
                new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        actionPanel.setOpaque(false);

        JPanel abilityButtons = new JPanel(new FlowLayout(
                FlowLayout.CENTER, 5, 3));
        abilityButtons.setOpaque(false);

        JButton hintButton = new JButton("Hint");
        JButton shieldButton = new JButton("Shield");
        JButton scanButton = new JButton("Scanner");

        hintButton.setFocusable(false);
        shieldButton.setFocusable(false);
        scanButton.setFocusable(false);

        final int playerIndex = player;

        hintButton.addActionListener(e -> useHint(playerIndex));
        shieldButton.addActionListener(e -> useShield(playerIndex));
        scanButton.addActionListener(e -> useScanner(playerIndex));

        abilityButtons.add(hintButton);
        abilityButtons.add(shieldButton);
        abilityButtons.add(scanButton);

        actionPanel.add(abilityButtons);

        if (isMultiplayer()) {
            JPanel sabotageButtons = new JPanel(new FlowLayout(
                    FlowLayout.CENTER, 5, 3));
            sabotageButtons.setOpaque(false);

            JButton freezeButton = new JButton("Freeze");
            JButton fogButton = new JButton("Fog");
            JButton recoverButton = new JButton("Recover");

            freezeButton.setFocusable(false);
            fogButton.setFocusable(false);
            recoverButton.setFocusable(false);

            freezeButton.addActionListener(e -> useSabotage(
                    playerIndex,
                    new FreezeSabotage(5000),
                    "Freeze"));

            fogButton.addActionListener(e -> useSabotage(
                    playerIndex,
                    new FogSabotage(5000),
                    "Fog"));

            recoverButton.addActionListener(e -> useSabotage(
                    playerIndex,
                    new RecoverSabotage(3),
                    "Recover"));

            sabotageButtons.add(freezeButton);
            sabotageButtons.add(fogButton);
            sabotageButtons.add(recoverButton);

            actionPanel.add(sabotageButtons);
        }

        panel.add(actionPanel, BorderLayout.SOUTH);
        playerPanels[player] = panel;
    }

    private void setupKeyboardControls() {
        InputMap inputMap = mainPanel.getInputMap(
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        ActionMap actionMap = mainPanel.getActionMap();

        inputMap.clear();
        actionMap.clear();

        bindKey(inputMap, actionMap, "W", "p1-up",
                () -> movePlayer(0, "UP"));
        bindKey(inputMap, actionMap, "S", "p1-down",
                () -> movePlayer(0, "DOWN"));
        bindKey(inputMap, actionMap, "A", "p1-left",
                () -> movePlayer(0, "LEFT"));
        bindKey(inputMap, actionMap, "D", "p1-right",
                () -> movePlayer(0, "RIGHT"));

        bindKey(inputMap, actionMap, "SPACE", "p1-reveal",
                () -> revealSelectedCell(0));
        bindKey(inputMap, actionMap, "F", "p1-flag",
                () -> toggleFlag(0));
        bindKey(inputMap, actionMap, "H", "p1-hint",
                () -> useHint(0));
        bindKey(inputMap, actionMap, "G", "p1-shield",
                () -> useShield(0));
        bindKey(inputMap, actionMap, "C", "p1-scan",
                () -> useScanner(0));

        if (isMultiplayer()) {
            bindKey(inputMap, actionMap, "UP", "p2-up",
                    () -> movePlayer(1, "UP"));
            bindKey(inputMap, actionMap, "DOWN", "p2-down",
                    () -> movePlayer(1, "DOWN"));
            bindKey(inputMap, actionMap, "LEFT", "p2-left",
                    () -> movePlayer(1, "LEFT"));
            bindKey(inputMap, actionMap, "RIGHT", "p2-right",
                    () -> movePlayer(1, "RIGHT"));

            bindKey(inputMap, actionMap, "ENTER", "p2-reveal",
                    () -> revealSelectedCell(1));
            bindKey(inputMap, actionMap, "L", "p2-flag",
                    () -> toggleFlag(1));
            bindKey(inputMap, actionMap, "O", "p2-hint",
                    () -> useHint(1));
            bindKey(inputMap, actionMap, "P", "p2-shield",
                    () -> useShield(1));
            bindKey(inputMap, actionMap, "K", "p2-scan",
                    () -> useScanner(1));
        } else {
            bindKey(inputMap, actionMap, "UP", "single-up",
                    () -> movePlayer(0, "UP"));
            bindKey(inputMap, actionMap, "DOWN", "single-down",
                    () -> movePlayer(0, "DOWN"));
            bindKey(inputMap, actionMap, "LEFT", "single-left",
                    () -> movePlayer(0, "LEFT"));
            bindKey(inputMap, actionMap, "RIGHT", "single-right",
                    () -> movePlayer(0, "RIGHT"));
            bindKey(inputMap, actionMap, "ENTER", "single-reveal",
                    () -> revealSelectedCell(0));
        }
    }

    private void bindKey(
            InputMap inputMap,
            ActionMap actionMap,
            String key,
            String actionName,
            Runnable action) {

        inputMap.put(
                KeyStroke.getKeyStroke(key), actionName);

        actionMap.put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
    }

    private void movePlayer(int player, String direction) {
        if (controllers == null
                || player < 0
                || player >= controllers.length) {
            return;
        }

        GameController current = controllers[player];

        switch (direction) {
            case "UP":
                current.moveUp();
                break;
            case "DOWN":
                current.moveDown();
                break;
            case "LEFT":
                current.moveLeft();
                break;
            case "RIGHT":
                current.moveRight();
                break;
        }

        updateDisplay();
    }

    private boolean canPlay(int player) {
        return boards != null
                && player >= 0
                && player < boards.length
                && boards[player].getStatus() == GameStatus.PLAYING;
    }

    private boolean isGameFinished() {
        if (isMultiplayer()) {
            return match != null && match.isFinished();
        }

        return boards[0].getStatus() != GameStatus.PLAYING;
    }

    private void startTimerIfNeeded() {
        if (!timerStarted) {
            timerStarted = true;
            gameTimer.start();
        }
    }

    private void revealSelectedCell(int player) {
        if (!canPlay(player)) {
            return;
        }

        Board currentBoard = boards[player];
        GameController currentController = controllers[player];

        int row = currentController.getSelectedRow();
        int col = currentController.getSelectedCol();

        Cell cell = currentBoard.getCell(row, col);

        if (cell.getState() != Cell.State.COVERED) {
            return;
        }

        startTimerIfNeeded();
        currentController.revealSelectedCell();

        updateDisplay();
    }

    private void toggleFlag(int player) {
        if (!canPlay(player)) {
            return;
        }

        Board currentBoard = boards[player];
        GameController currentController = controllers[player];

        Cell cell = currentBoard.getCell(
                currentController.getSelectedRow(),
                currentController.getSelectedCol());

        if (cell.getState() == Cell.State.COVERED) {
            cell.setState(Cell.State.FLAGGED);
        } else if (cell.getState() == Cell.State.FLAGGED) {
            cell.setState(Cell.State.COVERED);
        }

        updateDisplay();
    }

    private int countUncoveredCells(int player) {
        Board currentBoard = boards[player];
        int count = 0;

        for (int row = 0; row < currentBoard.getRows(); row++) {
            for (int col = 0; col < currentBoard.getCols(); col++) {
                if (currentBoard.getCell(row, col).getState()
                        == Cell.State.UNCOVERED) {
                    count++;
                }
            }
        }

        return count;
    }

    private void useHint(int player) {
        if (!canPlay(player)) {
            return;
        }

        int before = countUncoveredCells(player);

        boards[player].useHint();

        int after = countUncoveredCells(player);

        if (after > before) {
            playerAbilityLabels[player].setText(
                    "Hint revealed a safe cell");
            startTimerIfNeeded();
        } else {
            playerAbilityLabels[player].setText(
                    "No safe hint available");
        }

        updateDisplay();
    }

    private void useShield(int player) {
        if (!canPlay(player)) {
            return;
        }

        Board currentBoard = boards[player];

        currentBoard.activateShield();

        if (currentBoard.isShieldActive()) {
            playerAbilityLabels[player].setText(
                    "Shield active for next turn");
        } else {
            playerAbilityLabels[player].setText(
                    "Shield unavailable");
        }

        updateDisplay();
    }

    private void useScanner(int player) {
        if (!canPlay(player)) {
            return;
        }

        int row = controllers[player].getSelectedRow();
        int col = controllers[player].getSelectedCol();

        int result = boards[player].scanArea(row, col);

        if (result == -1) {
            playerAbilityLabels[player].setText(
                    "Reveal a cell before scanning");
        } else {
            playerAbilityLabels[player].setText(
                    "Scanner: " + result + " mine(s) nearby");
        }

        updateDisplay();
    }

    private void useSabotage(
            int player,
            Sabotage sabotage,
            String name) {

        if (!isMultiplayer() || match == null) {
            return;
        }

        if (!canPlay(player)) {
            return;
        }

        boolean applied = match.applySabotage(
                player + 1, sabotage);

        if (applied) {
            int opponent = player == 0 ? 2 : 1;

            playerAbilityLabels[player].setText(
                    name + " sent to Player " + opponent);

            messageLabel.setText(
                    "Player " + (player + 1)
                    + " used " + name);
        } else {
            playerAbilityLabels[player].setText(
                    "Sabotage unavailable");
        }

        updateDisplay();
    }

    private void updateTimerLabel() {
        int minutes = elapsedSeconds / 60;
        int seconds = elapsedSeconds % 60;

        timerLabel.setText(String.format(
                "Time: %02d:%02d", minutes, seconds));
    }

    private void updateDisplay() {
        if (boards == null || rebuilding) {
            return;
        }

        for (int player = 0; player < boards.length; player++) {
            updatePlayerDisplay(player);
        }

        if (isMultiplayer()) {
            if (match.isFinished()) {
                MatchResult result = match.getResult();

                if (result == MatchResult.PLAYER1_WINS) {
                    statusLabel.setText("Player 1 Wins!");
                } else if (result == MatchResult.PLAYER2_WINS) {
                    statusLabel.setText("Player 2 Wins!");
                } else {
                    statusLabel.setText("Match Draw!");
                }

                gameTimer.stop();
            } else {
                statusLabel.setText("Multiplayer Match In Progress");
            }
        } else {
            GameStatus status = boards[0].getStatus();

            if (status == GameStatus.WON) {
                statusLabel.setText("You Won!");
                gameTimer.stop();
            } else if (status == GameStatus.LOST) {
                statusLabel.setText("Game Over!");
                gameTimer.stop();
            } else {
                statusLabel.setText("Status: Playing");
            }
        }
    }

    private void updatePlayerDisplay(int player) {
        Board currentBoard = boards[player];
        GameController currentController = controllers[player];

        int flaggedCells = 0;

        for (int row = 0; row < currentBoard.getRows(); row++) {
            for (int col = 0; col < currentBoard.getCols(); col++) {
                JButton button = buttons[player][row][col];
                Cell cell = currentBoard.getCell(row, col);

                button.setText("");
                button.setBackground(coveredColor);
                button.setForeground(Color.WHITE);
                button.setBorder(normalBorder);

                if (cell.getState() == Cell.State.FLAGGED) {
                    flaggedCells++;
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
                                    button.setForeground(textColor);
                                    break;
                            }
                        }
                    }
                }

                if (row == currentController.getSelectedRow()
                        && col == currentController.getSelectedCol()) {
                    button.setBorder(selectedBorder);
                }
            }
        }

        playerMineLabels[player].setText(
                "Mines Remaining: "
                + (currentBoard.getNumberOfMines() - flaggedCells));

        if (isMultiplayer()) {
            int score = player == 0
                    ? match.getPlayer1Score()
                    : match.getPlayer2Score();

            playerScoreLabels[player].setText(
                    "Score: " + score);
        } else {
            playerScoreLabels[player].setText(
                    "Safe Cells: " + countUncoveredCells(player));
        }

        if (currentBoard.getStatus() == GameStatus.WON) {
            playerStatusLabels[player].setText("Won");
            playerStatusLabels[player].setForeground(
                    new Color(21, 128, 61));
        } else if (currentBoard.getStatus() == GameStatus.LOST) {
            playerStatusLabels[player].setText("Lost");
            playerStatusLabels[player].setForeground(
                    new Color(220, 38, 38));
        } else {
            playerStatusLabels[player].setText("Playing");
            playerStatusLabels[player].setForeground(textColor);
        }
    }
}
