package proyecto;

import java.awt.Button;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Timer;

public class Minesweeper {

    private static int width = 350;
    private static int height = 450;
    private static int mines = 10;
    private static int gameTime = 0;
    private static int numberOfSquaresX = 10;
    private static int numberOfSquaresY = 9;
    private static int unflaggedminesMatrixRemaining = mines;
    // mine / empty / number...
    private static int[][] squareState = new int[numberOfSquaresX][numberOfSquaresY];
    // still covered by top layer? t/f
    private static boolean[][] coveredState = new boolean[numberOfSquaresX][numberOfSquaresY];
    private static boolean[][] flaggedState = new boolean[numberOfSquaresX][numberOfSquaresY];
    private static Image[] images = new Image[13];
    static MyFrame F = new MyFrame();
    private static ActionListener taskPerformer;
    static Timer timer = new Timer(1000, null);
    private static boolean isTimerRunning = false;
    private static Button restartButton;
    private static Font font = new Font("Arial", Font.PLAIN, 14);

    private static Image imageCovered = null;
    private static Image image1 = null;
    private static Image image2 = null;
    private static Image image3 = null;
    private static Image image4 = null;
    private static Image image5 = null;
    private static Image image6 = null;
    private static Image image7 = null;
    private static Image image8 = null;
    private static Image imageFlag = null;
    private static Image imageMine = null;
    private static Image imageMineClicked = null;
    private static Image imageEmptySquare = null;
    private static Image imageClock = null;
    private static Image imageCheck = null;

    private static int[][] myMatrix = new int[numberOfSquaresX][numberOfSquaresY];
    private static int[][] minesMatrix = new int[numberOfSquaresX][numberOfSquaresY];
    private static final int BLANK_SPACE = 12;
    private static final int COVER_SPACE = 0;
    private static int checkX;
    private static int checkY;

    public static boolean isValid(int x, int y) {
        if ((y >= 0 && y < numberOfSquaresY) && (x >= 0 && x < numberOfSquaresX)) {
            return true;
        } else {
            return false;
        }
    }

    public static void probabilities(int x, int y) {
        if (!isValid(x, y)) {
            return;
        }

        if (myMatrix[x][y] == COVER_SPACE) {
            return;
        }
        if (isValid(x + 1, y + 1) && myMatrix[x + 1][y + 1] == COVER_SPACE) {//Diagonal inferior derecha
            minesMatrix[x + 1][y + 1] += myMatrix[x][y];

        }
        if (isValid(x, y + 1) && myMatrix[x][y + 1] == COVER_SPACE) {//Baja una row
            minesMatrix[x][y + 1] += myMatrix[x][y];

        }
        if (isValid(x - 1, y - 1) && myMatrix[x - 1][y - 1] == COVER_SPACE) {//Diagonal superior izquierda
            minesMatrix[x - 1][y - 1] += myMatrix[x][y];

        }
        if (isValid(x - 1, y) && myMatrix[x - 1][y] == COVER_SPACE) {//Se mueve una column a la izquierda
            minesMatrix[x - 1][y] += myMatrix[x][y];

        }

        if (isValid(x + 1, y) && myMatrix[x + 1][y] == COVER_SPACE) {//Se mueve una column a la derecha 
            minesMatrix[x + 1][y] += myMatrix[x][y];

        }

        if (isValid(x - 1, y + 1) && myMatrix[x - 1][y + 1] == COVER_SPACE) {//Diagonal inferior izquierda
            minesMatrix[x - 1][y + 1] += myMatrix[x][y];

        }

        if (isValid(x, y - 1) && myMatrix[x][y - 1] == COVER_SPACE) {//Sube una row
            minesMatrix[x][y - 1] += myMatrix[x][y];
        }

        if (isValid(x + 1, y - 1) && myMatrix[x + 1][y - 1] == COVER_SPACE) {//Diagonal superior derecha
            minesMatrix[x + 1][y - 1] += myMatrix[x][y];

        }

    }

    public static void fillMineMatrix() {
        minesMatrix = new int[numberOfSquaresX][numberOfSquaresY];
        for (int i = 0; i < numberOfSquaresX; i++) {
            for (int j = 0; j < numberOfSquaresY; j++) {
                if (!coveredState[i][j]) {
                    myMatrix[i][j] = squareState[i][j];
                }
            }
        }
    }

    public static void solveMineSweeper(int x, int y) {
        fillMineMatrix();
        for (int i = 0; i < numberOfSquaresX; i++) {
            for (int j = 0; j < numberOfSquaresY; j++) {
                probabilities(i, j);
            }
        }
        System.out.println("Matriz analizada");
        for (int i = 0; i < numberOfSquaresY; i++) {
            for (int j = 0; j < numberOfSquaresX; j++) {
                System.out.printf("\t" + coveredState[j][i]);
            }
            System.out.println();
        }

        System.out.println("\n\n\n");

        for (int i = 0; i < numberOfSquaresY; i++) {
            for (int j = 0; j < numberOfSquaresX; j++) {
                System.out.printf("\t%d", minesMatrix[j][i]);
            }
            System.out.println();
        }
        System.out.println("\n\n\n");
    }

    public static void main(String args[]) {
        MyFrame meinFrame;
        meinFrame = new MyFrame();
        meinFrame.loadImages();
        drawCanvas();
        CanvasEvents();
        resetWholeGrid();
        placeminesMatrix();
        placeAllSquares();
        addButton();
        MouseEvents();
        startTimer();
    }

    private static void MouseEvents() {
        F.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                // LMB was clicked
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (getIsTimerRunning() == false) {
                        startTimer();
                        setIsTimerRunning(true);
                    }
                    squareClicked(e.getX(), e.getY(), "LMB");
                } // RMB was clicked
                else if (e.getButton() == MouseEvent.BUTTON3) {
                    squareClicked(e.getX(), e.getY(), "RMB");
                } // MMB was clicked
                else if (e.getButton() == MouseEvent.BUTTON2) {
                    squareClicked(e.getX(), e.getY(), "MMB");
                }
            }

        });
    }

    private static void repaintTimerEverySecond() {
        F.repaint(width - 50, 20, 40, 30);
    }

    private static void startTimer() {
        timer.start();
        timer.addActionListener(taskPerformer);
        taskPerformer = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                // System.out.println(gameTime);
                gameTime++;
                repaintTimerEverySecond();
            }
        };
    }

    private static void setIsTimerRunning(boolean isRunning) {
        isTimerRunning = isRunning;
    }

    private static boolean getIsTimerRunning() {
        return isTimerRunning;
    }

    private static void squareClicked(int mouseX, int mouseY, String mouseButton) {
        // if clicked on grid
        if (getGridOffsetX() < mouseX
                && mouseX < (getGridOffsetX() + getSquareSize()
                * numberOfSquaresX)
                && getGridOffsetY() < mouseY
                && mouseY < (getGridOffsetY() + getSquareSize()
                * numberOfSquaresY)) {
            int x = (mouseX - getGridOffsetX()) / getSquareSize();
            int y = (mouseY - getGridOffsetY()) / getSquareSize();
            uncover(x, y, mouseButton);
            solveMineSweeper(x, y);

        }
    }

    private static void uncover(int squareX, int squareY, String mouseButton) {
        // LMB on a square
        if (mouseButton == "LMB" && coveredState[squareX][squareY]
                && !flaggedState[squareX][squareY]) {

            // clicked on a mine
            if (squareState[squareX][squareY] == 10) {
                gameOver();
                squareState[squareX][squareY] = 11;
                F.repaint();
            } // empty square
            else if (squareState[squareX][squareY] == 12) {
                uncoverEmptySquaresAround(squareX, squareY);
                F.repaint();
            }
            coveredState[squareX][squareY] = false;
            F.repaint(getGridOffsetX() + squareX * getSquareSize(),
                    getGridOffsetY() + squareY * getSquareSize(),
                    getSquareSize(), getSquareSize());
            checkIfGameWon();
        } // RMB on a square
        else if (mouseButton == "RMB" && coveredState[squareX][squareY]) {
            flaggedState[squareX][squareY] = !flaggedState[squareX][squareY];
            mineUnflagged(squareX, squareY, flaggedState[squareX][squareY]);
            F.repaint(getGridOffsetX() + squareX * getSquareSize(),
                    getGridOffsetY() + squareY * getSquareSize(),
                    getSquareSize(), getSquareSize());
        } // MMB on a square
        else if (mouseButton == "MMB" & !coveredState[squareX][squareY]
                && squareState[squareX][squareY] >= 1
                && squareState[squareX][squareY] <= 8) {
            checkIfAbleToUncoverminesMatrixAround(squareX, squareY);
        }
    }

    // this method is doing the calculation for the string displaying: remaining
    // number of minesMatrix - number of flags
    private static void mineUnflagged(int x, int y, boolean isFlagged) {
        if (isFlagged) {
            unflaggedminesMatrixRemaining--;
        } else if (!isFlagged) {
            unflaggedminesMatrixRemaining++;
        }
        // this drawing-method is a workaround for a repaint()-bug. Can't
        // specify the correct area.. Also placed in paint().
        Graphics g = F.getGraphics().create();
        g.setFont(font);
        g.clearRect(width - 115, 25, 25, 20);
        g.drawString("" + unflaggedminesMatrixRemaining, width - 115, 45);
    }

    private static void checkIfGameWon() {
        for (int i = 0; i < numberOfSquaresX; i++) {
            for (int j = 0; j < numberOfSquaresY; j++) {
                if (coveredState[i][j] && squareState[i][j] != 10) {
                    return;
                }
            }
        }
        gameOver();
        F.repaint();
    }

    private static void gameOver() {
        timer.stop();
        // System.out.println(gameTime);
        setWholeGridCovered(false);
    }

    // Event after a middle-mouse-button click
    private static void checkIfAbleToUncoverminesMatrixAround(int x, int y) {
        int numberOfAdjacentFlags = 0;
        int j;
        for (int i = -1; i < 2; i++) {
            for (j = -1; j < 1; j++) {
                if (x + i < numberOfSquaresX && x + i >= 0
                        && y + j < numberOfSquaresY && y + j >= 0
                        && flaggedState[x + i][y + j] == true) {
                    numberOfAdjacentFlags++;
                }
            }
            if (x + i < numberOfSquaresX && x + i >= 0
                    && y + j < numberOfSquaresY && y + j >= 0
                    && flaggedState[x + i][y + j] == true) {
                numberOfAdjacentFlags++;
            }
        }
        uncoverAdjacentFieldsAfterMiddleclick(x, y, numberOfAdjacentFlags);
    }

    private static void uncoverAdjacentFieldsAfterMiddleclick(int x, int y,
            int numberOfAdjacentFlags) {
        // checking if there is the same number of adjacent flags as there are
        // minesMatrix --> if not: quit this method
        if (numberOfAdjacentFlags != squareState[x][y]) {
            return;
        }

        int j;
        for (int i = -1; i < 2; i++) {
            for (j = -1; j < 1; j++) {
                // Simulates a left mouseclick. The "if" is just for the first
                // and last squares (X and Y) so there are no errors.
                if (x + i < numberOfSquaresX && x + i >= 0
                        && y + j < numberOfSquaresY && y + j >= 0) {
                    uncover(x + i, y + j, "LMB");
                }
            }
            // Simulates a left mouseclick. The "if" is just for the first and
            // last squares (X and Y) so there are no errors.
            if (x + i < numberOfSquaresX && x + i >= 0
                    && y + j < numberOfSquaresY && y + j >= 0) {
                uncover(x + i, y + j, "LMB");
            }
        }

    }

    // This is the method that uncovers all the plain squares around a plain
    // square that has just been pressed
    private static void uncoverEmptySquaresAround(int x, int y) {
        // Has to check cross first: then has to uncover those plain squares
        // Then check all 8 adjacent squares for every single plain square and
        // uncover them fucking numbers
        int adjacentX;
        int adjacentY;
        boolean uncoverRight = false;
        boolean uncoverLeft = false;
        boolean uncoverAbove = false;
        boolean uncoverBelow = false;

        adjacentX = x + 1;
        adjacentY = y;
        if ((adjacentX) >= 0 && (adjacentX) < numberOfSquaresX
                && (adjacentY) >= 0 && (adjacentY) < numberOfSquaresY
                && coveredState[adjacentX][adjacentY]
                && !flaggedState[adjacentX][adjacentY]) {
            if (squareState[adjacentX][adjacentY] == 12) {
                coveredState[adjacentX][adjacentY] = false;
                uncoverRight = true;
            }
        }

        adjacentX = x - 1;
        adjacentY = y;
        if ((adjacentX) >= 0 && (adjacentX) < numberOfSquaresX
                && (adjacentY) >= 0 && (adjacentY) < numberOfSquaresY
                && coveredState[adjacentX][adjacentY]
                && !flaggedState[adjacentX][adjacentY]) {
            if (squareState[adjacentX][adjacentY] == 12) {
                coveredState[adjacentX][adjacentY] = false;
                uncoverLeft = true;
            }
        }

        adjacentX = x;
        adjacentY = y + 1;
        if ((adjacentY) >= 0 && (adjacentY) < numberOfSquaresY
                && coveredState[adjacentX][adjacentY]
                && !flaggedState[adjacentX][adjacentY]) {
            if (squareState[adjacentX][adjacentY] == 12) {
                coveredState[adjacentX][adjacentY] = false;
                uncoverAbove = true;
            }
        }

        adjacentX = x;
        adjacentY = y - 1;
        if ((adjacentY) >= 0 && (adjacentY) < numberOfSquaresY
                && coveredState[adjacentX][adjacentY]
                && !flaggedState[adjacentX][adjacentY]) {
            if (squareState[adjacentX][adjacentY] == 12) {
                coveredState[adjacentX][adjacentY] = false;
                uncoverBelow = true;
            }
        }

        if (uncoverBelow) {
            uncoverEmptySquaresAround(x, y - 1);
        }
        if (uncoverAbove) {
            uncoverEmptySquaresAround(x, y + 1);
        }
        if (uncoverLeft) {
            uncoverEmptySquaresAround(x - 1, y);
        }
        if (uncoverRight) {
            uncoverEmptySquaresAround(x + 1, y);
        }
        uncoverAbove = false;
        uncoverBelow = false;
        uncoverLeft = false;
        uncoverRight = false;

        int j;
        for (int i = -1; i < 2; i++) {
            for (j = -1; j < 1; j++) {
                if ((x + i) >= 0 && (x + i) < numberOfSquaresX && (y + j) >= 0
                        && (y + j) < numberOfSquaresY
                        && coveredState[x + i][y + j]
                        && !flaggedState[x + i][y + j]
                        && squareState[x + i][y + j] != 12) {
                    coveredState[x + i][y + j] = false;
                }
            }
            if ((x + i) >= 0 && (x + i) < numberOfSquaresX && (y + j) >= 0
                    && (y + j) < numberOfSquaresY && coveredState[x + i][y + j]
                    && !flaggedState[x + i][y + j]
                    && squareState[x + i][y + j] != 12) {
                coveredState[x + i][y + j] = false;
            }
        }

    }

    private static void drawCanvas() {
        F.setTitle("Dacm26 minesMatrixweeper");
        F.setSize(width, height);
        F.setLayout(null);
        F.setVisible(true);
    }

    // actions for closing and resizing
    private static void CanvasEvents() {

        // closing window
        F.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });

        // resizing window
        F.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {

                // changed height
                if (F.getSize().height != height) {
                    // System.out.println(F.getSize().height + " " + height);
                    F.setSize(F.getSize().width, height);
                }

                // changed width
                if (F.getSize().width != width) {
                    width = F.getSize().width;
                    height = F.getSize().height;
                    while (getGridOffsetY() < 55) {
                        height += 2;
                    }
                    while (getGridOffsetY() > 58) {
                        height -= 2;
                    }
                    F.setSize(F.getSize().width, height);
                }

                F.repaint();
                relocateButtonWithFrameSize(width, height);
            }
        });
    }

    private static void addButton() {
        restartButton = new Button("Restart");
        restartButton.setLocation(width / 2, 25);
        restartButton.setSize(60, 25);
        restartButton.setVisible(true);
        ActionListener buttonEvent = new ActionListener() {

            @Override
            // Restart-Button was pressed
            public void actionPerformed(ActionEvent e) {
                timer.stop();
                timer.removeActionListener(taskPerformer);
                gameTime = 0;
                unflaggedminesMatrixRemaining = mines;
                timer.restart();
                resetWholeGrid();
                placeminesMatrix();
                placeAllSquares();
                F.repaint();
                myMatrix = new int[numberOfSquaresX][numberOfSquaresY];
                minesMatrix = new int[numberOfSquaresX][numberOfSquaresY];
            }
        };
        restartButton.addActionListener(buttonEvent);

        F.add(restartButton);
    }

    private static void relocateButtonWithFrameSize(int width, int height) {
        restartButton.setLocation(width / 2, 26);
    }

    private static int getSquareSize() {
        int squareWidth = (width - 15) / numberOfSquaresX;
        return squareWidth;
    }

    private static int getGridOffsetX() {
        // the distance of the grid to the left side of the window so it appears
        // centered
        int gridOffset = (width - (getSquareSize() * numberOfSquaresX)) / 2;
        return gridOffset;
    }

    private static int getGridOffsetY() {
        // the distance of the grid to the top of the window so it is at the
        // bottom
        int gridOffset = height - getSquareSize() * numberOfSquaresY - 10;
        return gridOffset;
    }

    private static void resetWholeGrid() {
        int xIncr;
        for (int yIncr = 0; yIncr < numberOfSquaresY; yIncr++) {
            for (xIncr = 0; xIncr < numberOfSquaresX - 1; xIncr++) {
                coveredState[xIncr][yIncr] = true;
                squareState[xIncr][yIncr] = 0;
                flaggedState[xIncr][yIncr] = false;
            }
            coveredState[xIncr][yIncr] = true;
            squareState[xIncr][yIncr] = 0;
            flaggedState[xIncr][yIncr] = false;
        }
    }

    private static void setWholeGridCovered(boolean covered) {
        int xIncr;
        for (int yIncr = 0; yIncr < numberOfSquaresY; yIncr++) {
            for (xIncr = 0; xIncr < numberOfSquaresX - 1; xIncr++) {
                coveredState[xIncr][yIncr] = covered;
            }
            coveredState[xIncr][yIncr] = covered;
        }
    }

    private static void placeminesMatrix() {
        for (int i = 0; i < mines; i++) {
            double randomX = Math.random() * numberOfSquaresX;
            double randomY = Math.random() * numberOfSquaresY;
            if (squareState[(int) randomX][(int) randomY] == 10) {
                // if there is a mine on random square
                i--;
            } else {
                // if there is no mine on the random square
                squareState[(int) randomX][(int) randomY] = 10;
            }
        }
    }

    private static void placeAllSquares() {
        int numberOfAdjacentminesMatrix = 0;
        int xIncr;

        for (int yIncr = 0; yIncr < numberOfSquaresY; yIncr++) {
            for (xIncr = 0; xIncr < numberOfSquaresX; xIncr++) {
                for (int i = 0; i < 3; i++) {

                    for (int j = 0; j < 3; j++) {
                        // so the clicked outer squares don't search for
                        // non-existing arrays
                        if ((xIncr - 1 + i) >= 0
                                && (xIncr + i) <= numberOfSquaresX
                                && (yIncr - 1 + j) >= 0
                                && (yIncr + j) <= numberOfSquaresY) {
                            if (squareState[xIncr - 1 + i][yIncr - 1 + j] == 10) {
                                numberOfAdjacentminesMatrix++;
                            }
                        }
                    }

                }
                // for squares with adjascent minesMatrix, the number are placed
                if (squareState[xIncr][yIncr] < 9) {
                    squareState[xIncr][yIncr] = numberOfAdjacentminesMatrix;
                    if (numberOfAdjacentminesMatrix == 0) {
                        squareState[xIncr][yIncr] = 12;
                    }
                } else {
                }
                numberOfAdjacentminesMatrix = 0;

            }
        }

    }

    private static class MyFrame extends Frame {

        private static final long serialVersionUID = 1L;

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            g.setFont(font);
            g.drawString("" + gameTime, width - 50, 45);
            g.clearRect(width - 100, 25, 25, 20);
            g.drawString("" + unflaggedminesMatrixRemaining, width - 115, 45);
            g.drawImage(imageFlag, width - 130, 32, width - 115, 47, 0, 0, 64,
                    64, null);
            g.drawImage(imageClock, width - 70, 32, width - 55, 47, 0, 0, 64,
                    64, null);

            // all Squares covered, initial state --> state 0
            // numbers (adjacent to minesMatrix) --> state 1 - 8
            // flag --> state 9
            // mine --> state 10
            // mine clicked --> state 11
            // clicked and empty square --> state 12
            int xIncr;
            for (int yIncr = 0; yIncr < numberOfSquaresY; yIncr++) {
                for (xIncr = 0; xIncr < numberOfSquaresX - 1; xIncr++) {

                    if (coveredState[xIncr][yIncr]) {
                        if (flaggedState[xIncr][yIncr]) {
                            g.drawImage(images[9], xIncr * getSquareSize()
                                    + getGridOffsetX(), yIncr * getSquareSize()
                                    + getGridOffsetY(), xIncr * getSquareSize()
                                    + getSquareSize() + getGridOffsetX(), yIncr
                                    * getSquareSize() + getSquareSize()
                                    + getGridOffsetY(), 0, 0, 64, 64, null);
                        } else {
                            g.drawImage(images[0], xIncr * getSquareSize()
                                    + getGridOffsetX(), yIncr * getSquareSize()
                                    + getGridOffsetY(), xIncr * getSquareSize()
                                    + getSquareSize() + getGridOffsetX(), yIncr
                                    * getSquareSize() + getSquareSize()
                                    + getGridOffsetY(), 0, 0, 64, 64, null);
                        }
                    } else {
                        g.drawImage(images[squareState[xIncr][yIncr]], xIncr
                                * getSquareSize() + getGridOffsetX(), yIncr
                                * getSquareSize() + getGridOffsetY(), xIncr
                                * getSquareSize() + getSquareSize()
                                + getGridOffsetX(), yIncr * getSquareSize()
                                + getSquareSize() + getGridOffsetY(), 0, 0, 64,
                                64, null);
                    }

                }

                if (coveredState[xIncr][yIncr]) {
                    if (flaggedState[xIncr][yIncr]) {
                        g.drawImage(images[9], xIncr * getSquareSize()
                                + getGridOffsetX(), yIncr * getSquareSize()
                                + getGridOffsetY(), xIncr * getSquareSize()
                                + getSquareSize() + getGridOffsetX(), yIncr
                                * getSquareSize() + getSquareSize()
                                + getGridOffsetY(), 0, 0, 64, 64, null);
                    } else {
                        g.drawImage(images[0], xIncr * getSquareSize()
                                + getGridOffsetX(), yIncr * getSquareSize()
                                + getGridOffsetY(), xIncr * getSquareSize()
                                + getSquareSize() + getGridOffsetX(), yIncr
                                * getSquareSize() + getSquareSize()
                                + getGridOffsetY(), 0, 0, 64, 64, null);
                    }
                } else {
                    g.drawImage(images[squareState[xIncr][yIncr]], xIncr
                            * getSquareSize() + getGridOffsetX(), yIncr
                            * getSquareSize() + getGridOffsetY(), xIncr
                            * getSquareSize() + getSquareSize()
                            + getGridOffsetX(), yIncr * getSquareSize()
                            + getSquareSize() + getGridOffsetY(), 0, 0, 64, 64,
                            null);
                }

            }

            g.drawImage(imageCheck, checkX
                    * getSquareSize() + getGridOffsetX(), checkY
                    * getSquareSize() + getGridOffsetY(), checkX
                    * getSquareSize() + getSquareSize()
                    + getGridOffsetX(), checkY * getSquareSize()
                    + getSquareSize() + getGridOffsetY(), 0, 0, 64, 64,
                    null);

        }

        private void loadImages() {
            try {
                imageCovered = ImageIO.read(getClass().getClassLoader()
                        .getResource("proyecto/covered.png"));
                image1 = ImageIO.read(getClass().getClassLoader().getResource(
                        "proyecto/1.jpg"));
                image2 = ImageIO.read(getClass().getClassLoader().getResource(
                        "proyecto/2.jpg"));
                image3 = ImageIO.read(getClass().getClassLoader().getResource(
                        "proyecto/3.jpg"));
                image4 = ImageIO.read(getClass().getClassLoader().getResource(
                        "proyecto/4.jpg"));
                image5 = ImageIO.read(getClass().getClassLoader().getResource(
                        "proyecto/5.jpg"));
                image6 = ImageIO.read(getClass().getClassLoader().getResource(
                        "proyecto/6.jpg"));
                image7 = ImageIO.read(getClass().getClassLoader().getResource(
                        "proyecto/7.jpg"));
                image8 = ImageIO.read(getClass().getClassLoader().getResource(
                        "proyecto/8.jpg"));
                imageFlag = ImageIO.read(getClass().getClassLoader()
                        .getResource("proyecto/flag.jpg"));
                imageMine = ImageIO.read(getClass().getClassLoader()
                        .getResource("proyecto/mine.png"));
                imageMineClicked = ImageIO.read(getClass().getClassLoader()
                        .getResource("proyecto/mine_activated.png"));
                imageEmptySquare = ImageIO.read(getClass().getClassLoader()
                        .getResource("proyecto/empty_pressed.jpg"));
                imageClock = ImageIO.read(getClass().getClassLoader()
                        .getResource("proyecto/clock.jpg"));
                /*                imageCheck = ImageIO.read(getClass().getClassLoader()
                 .getResource("proyecto/check-icon.png"));*/
            } catch (IOException ex) {
                System.out.println("Image is null");
            }
            images[0] = imageCovered;
            images[1] = image1;
            images[2] = image2;
            images[3] = image3;
            images[4] = image4;
            images[5] = image5;
            images[6] = image6;
            images[7] = image7;
            images[8] = image8;
            images[9] = imageFlag;
            images[10] = imageMine;
            images[11] = imageMineClicked;
            images[12] = imageEmptySquare;
        }

    }
    
    

}
