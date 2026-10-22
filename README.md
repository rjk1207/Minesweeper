

\# Minesweeper — Java Multiplayer Game



A Java-based Minesweeper game developed using Apache NetBeans, Maven, and Java Swing.



The project supports both single-player and two-player gameplay, with power-ups and sabotage abilities that add strategic elements to the classic Minesweeper experience.



\## Features



\- Single-player and multiplayer modes

\- Three difficulty levels: Easy, Medium, and Hard

\- Keyboard-controlled gameplay

\- First-click protection

\- Automatic revealing of connected empty cells

\- Cell flagging and chording

\- Win and loss detection

\- Multiplayer scoring system

\- Restart functionality



\## Power-Ups



\### Hint

Automatically reveals an eligible safe cell.



\### Shield

Protects the player against a mine during the next valid action.



\### Scanner

Reports the number of mines in the selected cell's surrounding 3×3 area.



\## Multiplayer Sabotage



\### Freeze

Temporarily prevents the opponent from performing game actions.



\### Fog

Temporarily hides the opponent's board information.



\### Recover

Covers previously revealed safe cells on the opponent's board.



\## Controls



\### Player 1



| Key | Action |

| --- | --- |

| W | Move up |

| A | Move left |

| S | Move down |

| D | Move right |

| Space | Reveal cell |

| F | Flag cell |

| H | Hint |

| G | Shield |

| C | Scanner |



\### Player 2



| Key | Action |

| --- | --- |

| Up Arrow | Move up |

| Left Arrow | Move left |

| Down Arrow | Move down |

| Right Arrow | Move right |

| Enter | Reveal cell |

| L | Flag cell |

| O | Hint |

| P | Shield |

| K | Scanner |



Sabotage abilities can be activated using the buttons in the multiplayer interface.



\## Technologies



\- Java

\- Java Swing

\- Apache NetBeans

\- Maven

\- JUnit 5

\- Git and GitHub



\## Running the Project



1\. Clone the repository.

2\. Open Apache NetBeans.

3\. Select File > Open Project.

4\. Choose the Minesweeper project folder.

5\. Allow Maven dependencies to load.

6\. Run `MinesweeperGUI.java`.

7\. Select the game mode and difficulty.



\## Testing



The project includes automated JUnit tests covering:



\- Board initialization

\- Mine placement

\- Neighbour mine calculations

\- Revealing and flagging cells

\- Flood-fill functionality

\- Game status

\- Chording

\- Hint, Shield, and Scanner

\- Multiplayer matches

\- Sabotage abilities



Final verified test results:



\- Tests executed: 143

\- Tests passed: 143

\- Failures: 0

\- Errors: 0



\## Team Development



The project was developed collaboratively by two developers.



\### Developer 1

Focused on the game logic, board functionality, multiplayer systems, and backend implementation.



\### Developer 2

Focused on the Java Swing graphical interface, keyboard controls, multiplayer interaction, and integration testing.



\## Repository



https://github.com/rjk1207/Minesweeper



