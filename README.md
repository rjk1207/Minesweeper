Minesweeper Project

A  Java Swing Minesweeper application supporting single player and local two
player multiplayer gameplay.
Players navigate a grid, reveal safe cells, and identify mines using numerical
clues.
The game also includes abilities such as Hint, Shield and Scanner, alongside 
multiplayer sabotage features like Freeze, Fog and Recover.



1. System Requirements

a. Java Development Kit - JDK25
b. Build tool - Apache Maven
c. OS - Windows, MacOS, or Linux
d. UI - Graphical desktop environment
e. IDE (OPTIONAL) - Apache NetBeans

An internet connection may be required during the first build so that Maven can
download the necessary dependencies.

No external game engine or database required.

To verify Java and Maven installation-
1. Open PowerShell or Terminal
2. Run "java -version" and then "javac -version". Both commands should output 
Java version 25.
3. Run "mvn -version". This will display the installed Maven version. Make sure
Maven is using JDK25.
4. If Java is not installed, install JDK25 and ensure the JDK is available 
through the system's PATH.
5. If Maven is not recognized, install Apache Maven and configure the PATH 
environment variable.



2. Extracting the Project

a. Download the ZIP file.
b. Extract. 
c. Open extracted project folder names Minesweeper.
d. Locate the file pom.xml.

pom.xml file marks the root directory of the Maven Project.

The src/main/java directory contains the application source code. The
src/test/java directory contains automated tests.



3. Building the Application

a. Launch Apache NetBeans
b. Select File then Open Project.
c. Select the project folder containing pom.xml.
d. Click Open Project
e. Right click the project name in the panel on the left.
f. Select Clean and Build.
g. Wait until output window displays BUILD SUCCESS.



4. Running the Application

a. Open the Minesweeper project in NetBeans.
b. Expand Source Packages.
c. Open the package nl.tue.minesweeper.minesweeper.
d. Locate MinesweeperGUI.java.
e. Right click the file and click Run File.



5. Game Features and Controls

The application supports **single-player and two-player multiplayer** modes, 
with three difficulty levels: Easy, Medium, and Hard.

Players must reveal safe cells while avoiding mines. The game includes special 
abilities (Hint, Shield, Scanner) and multiplayer sabotages (Freeze, Fog, Re-cover).

Player 1 Controls:
- Movement: W, A, S, D
- Reveal: Space
- Flag/Unflag: F
- Hint, Shield, Scanner: H, G, C
- Freeze, Fog, Re-cover: Q, E, R

Player 2 Controls:
- Movement: Arrow Keys
- Reveal: Enter
- Flag/Unflag: L
- Hint, Shield, Scanner: O, P, K
- Freeze, Fog, Re-cover: U, I, J

In multiplayer, players compete on separate boards and earn points by revealing 
safe cells. Each player can use up to two sabotages per match.



6. Testing

The application uses JUnit 5 for automated testing. To execute the tests, right
click the project and click Test. 



7. Troubleshooting

- **Build fails:** Verify that Maven is using JDK 25.
- **Application does not launch:** Ensure the terminal is opened in the project 
directory containing `pom.xml`.
- **Keyboard controls do not respond:** Make sure the game window is active.
- **Dependencies cannot be downloaded:** Check your internet connection.
