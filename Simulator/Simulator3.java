package Simulator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import Algorithm.PathPlanner;
import Arena.Arena;
import Constants.Direction;
import Entities.Obstacle;
import Entities.Robot;

import java.awt.GridLayout;
import java.awt.Image;

public class Simulator3 {

    int arenaWidth = 20;
    int arenaHeight = 20;
    int iconScaling = 20;
    int timeLeft = 0;
    Arena arena;
    Object[][] path;

    private JFrame mainFrame;
    private JTextArea textArea;
    private String configFile = "";
    private JButton[][] buttonGrid = new JButton[arenaWidth][arenaHeight];
    private JPanel gridPanel;
    private JPanel controlPanel;
    private JLabel timeLabel;

    int stepCounter = 0;
    String headiconIdPath = "ImageSet/41-HEAD.png";

    public void guiSim() {
        prepareGUI(); // sets up the the main frame that holds everything
        prepareTextArea(); // sets up the config input area
        prepareMenu(); // sets up the load, save, create arena functionality
        prepareGrid(); // sets up an empty grid
        prepareControlPanel(); // sets up button for trying algo
        // path = PathPlanner.devPath(); // get the path of robot movement

        mainFrame.setVisible(true);
    }

    private void prepareGUI() {
        mainFrame = new JFrame("MDP Path Finding Algorithm Simulator");
        mainFrame.setSize(800, 800);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void prepareMenu() {
        JMenuBar menuBar = new JMenuBar();

        JButton loadConfig = new JButton(
                new AbstractAction("Load Configuration File") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("'Load Configuration File' clicked");
                        JFileChooser fileMgmt = new JFileChooser();
                        int option = fileMgmt.showOpenDialog(mainFrame);
                        if (option == JFileChooser.APPROVE_OPTION) {
                            File loadFile = fileMgmt.getSelectedFile();
                            try {
                                configFile = Files.readString(Paths.get(loadFile.getAbsolutePath()));
                                textArea.setText(configFile);
                                System.out.println(configFile);
                                System.out.println("Configuration file loaded");

                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });

        JButton saveConfig = new JButton(
                new AbstractAction("Save Configuration File") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("'Save Configuration File' clicked");
                        JFileChooser fileMgmt = new JFileChooser();
                        int option = fileMgmt.showSaveDialog(mainFrame);
                        if (option == JFileChooser.APPROVE_OPTION) {
                            File saveFile = fileMgmt.getSelectedFile();
                            try {
                                Files.writeString(saveFile.toPath(), textArea.getText(), StandardCharsets.UTF_8);
                                System.out.println("Config file saved");
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });

        // when Generate arena button is clicked...
        JButton setArena = new JButton(
                new AbstractAction("Generate Arena from Configuration") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("'Generate Arena' clicked");
                        // ... remove the old grid panel
                        mainFrame.remove(gridPanel);
                        mainFrame.remove(controlPanel);
                        stepCounter = 0;
                        timeLeft = 0;
                        // create a new grid panel
                        prepareGrid();
                        // set up the arena according to the config
                        setupArena();
                        // display the arena items onto the GUI
                        addEntityToArena();
                        addRobotToArena();
                        prepareControlPanel();
                        setHead((int) path[stepCounter][0], (int) path[stepCounter][1], (int) path[stepCounter][2], 1);
                    }
                });

        menuBar.add(loadConfig);
        menuBar.add(saveConfig);
        menuBar.add(setArena);
        mainFrame.getContentPane().add(BorderLayout.NORTH, menuBar);
    }

    private void prepareTextArea() {
        textArea = new JTextArea(0, 10);
        textArea.setLineWrap(true);
        mainFrame.getContentPane().add(BorderLayout.EAST, textArea);
    }

    private void prepareGrid() {
        gridPanel = new JPanel();
        for (int y = arenaHeight - 1; y >= 0; y--) {
            for (int x = 0; x < arenaWidth; x++) {
                buttonGrid[x][y] = new JButton();
                // gridArray[x][y] = new JLabel();
                gridPanel.add(buttonGrid[x][y]);
                // gridPanel.add(gridArray[x][y]);
            }
        }

        gridPanel.setPreferredSize(new Dimension(500, 500));
        gridPanel.setLayout(new GridLayout(20, 20));
        mainFrame.getContentPane().add(BorderLayout.WEST, gridPanel);
    }

    // create the arena object
    private void setupArena() {
        arena = SetupArena.setupArena(textArea.getText());
        path = PathPlanner.gridPath(textArea.getText(), true);
    }

    // translates the obstacles in arena into GUI
    private void addEntityToArena() {
        for (Obstacle obstacle : arena.getObstacleArray()) {
            int tempX = obstacle.getCoords()[0];
            int tempY = obstacle.getCoords()[1];
            String iconIdPath = String.format("ImageSet/%s-%s.png", obstacle.getPictureId(),
                    obstacle.getDirection().getCompass());
            ImageIcon icon = new ImageIcon(iconIdPath);
            Image image = icon.getImage();
            Image newImg = image.getScaledInstance(iconScaling, iconScaling, java.awt.Image.SCALE_SMOOTH);
            icon = new ImageIcon(newImg);
            buttonGrid[tempX][tempY].setIcon(icon);
            // gridArray[tempX][tempY].setIcon(icon);

        }
    }

    private void addRobotToArena() {
        int curX = ((Number) path[0][0]).intValue();
        int curY = ((Number) path[0][1]).intValue();
        int newX = curX;
        int newY = curY;
        Direction newDir = new Direction(path[0][2]);
        updateRobotDisplay(newX, newY, curX, curY, ((Number) path[0][2]).intValue());
    }

    private int getTime() {

        return this.timeLeft;
    }

    private void setTime(int x) {

        this.timeLeft = x;
    }

    private void prepareControlPanel() {
        controlPanel = new JPanel();
        JLabel stepCounterLabel = new JLabel(String.format("Step %s", stepCounter));
        JButton stepBack = new JButton(" << ");
        JButton stepForward = new JButton(" >> ");
        timeLabel = new JLabel(String.format("Time: %s seconds", getTime()));

        // when the back button is clicked...
        stepBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (path[stepCounter][3] == "SCAN") {
                    setTime(getTime() - 10);
                    timeLabel.setText(String.format("Time: %s seconds", getTime()));
                }
                // ... check if the robot can indeed move to its previous location
                if (stepCounter > 0) {
                    int curX = ((Number) path[stepCounter][0]).intValue();
                    int curY = ((Number) path[stepCounter][1]).intValue();
                    int newX = ((Number) path[stepCounter - 1][0]).intValue();
                    int newY = ((Number) path[stepCounter - 1][1]).intValue();
                    updateRobotDisplay(newX, newY, curX, curY, ((Number) path[stepCounter - 1][2]).intValue());
                    setHead(newX, newY, ((Number) path[stepCounter - 1][2]).intValue(), -1);

                    stepCounter--;
                    stepCounterLabel.setText(String.format("Step %s", stepCounter));

                    // while (stepCounter >= 0) {
                    if (path[stepCounter][2] == path[stepCounter + 1][2]) {
                        setTime(getTime() - 2);
                        timeLabel.setText(String.format("Time: %s seconds", getTime()));
                        // break;
                    }
                    if (Math.abs((int) path[stepCounter][2] - (int) path[stepCounter + 1][2]) > 40) {
                        setTime(getTime() - 3);
                        timeLabel.setText(String.format("Time: %s seconds", getTime()));
                        // break;
                    }
                    if (Math.abs((int) path[stepCounter][2] - (int) path[stepCounter + 1][2]) > 90) {
                        setTime(getTime() - 4);
                        timeLabel.setText(String.format("Time: %s seconds", getTime()));
                        // break;
                    }
                    // }

                }
            }
        });

        stepForward.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (stepCounter < path.length - 1) {
                    int curX = ((Number) path[stepCounter][0]).intValue();
                    int curY = ((Number) path[stepCounter][1]).intValue();
                    int newX = ((Number) path[stepCounter + 1][0]).intValue();
                    int newY = ((Number) path[stepCounter + 1][1]).intValue();
                    updateRobotDisplay(newX, newY, curX, curY, ((Number) path[stepCounter + 1][2]).intValue());
                    setHead(newX, newY, ((Number) path[stepCounter + 1][2]).intValue(), 1);

                    stepCounter++;

                    if (path[stepCounter][3] == "SCAN") {
                        setTime(getTime() + 10);
                        timeLabel.setText(String.format("Time: %s seconds", getTime()));
                    }

                    stepCounterLabel.setText(String.format("Step %s", stepCounter));
                    // while (stepCounter > 0) {
                    if (path[stepCounter][2] == path[stepCounter - 1][2]) {
                        setTime(getTime() + 2);
                        timeLabel.setText(String.format("Time: %s seconds", getTime()));
                        // break;
                    }
                    if (Math.abs((int) path[stepCounter][2] - (int) path[stepCounter - 1][2]) > 40) {
                        setTime(getTime() + 3);
                        timeLabel.setText(String.format("Time: %s seconds", getTime()));
                        // break;
                    }
                    if (Math.abs((int) path[stepCounter][2] - (int) path[stepCounter - 1][2]) > 90) {
                        setTime(getTime() + 4);
                        timeLabel.setText(String.format("Time: %s seconds", getTime()));
                        // break;
                    }
                    // }
                }
            }
        });

        controlPanel.add(stepBack);
        controlPanel.add(stepCounterLabel);
        controlPanel.add(stepForward);
        controlPanel.add(timeLabel);
        mainFrame.getContentPane().add(BorderLayout.SOUTH, controlPanel);
    }

    // calculates the coordinates to place the robot in grid
    private int[][] paddingCoords(int x, int y) {
        int robotWidth = arena.getRobot().getSize()[0];
        int robotHeight = arena.getRobot().getSize()[1];
        int startX = (int) (x - Math.floor(robotWidth / 2));
        int startY = (int) (y - Math.floor(robotHeight / 2));
        int index = 0;
        int[][] padding;
        padding = new int[robotWidth * robotHeight][2];
        for (int i = startX; i < startX + robotWidth; i++) {
            for (int j = startY; j < startY + robotHeight; j++) {
                int[] paddingCoord = { i, j };
                padding[index] = paddingCoord;
                index++;
            }
        }
        return padding;
    }

    // calculates the coordinates of the "face" of the robot
    private int[] robotHead(int x, int y, int direction) {
        int robotPaddingSize = arena.getRobot().getPaddingSize();
        int resultX = x;
        int resultY = y;
        switch (direction) {
            case 0:
                resultX = resultX + robotPaddingSize;
                break;
            case 45:
                resultX = resultX + robotPaddingSize;
                resultY = resultY + robotPaddingSize;
                break;
            case 90:
                resultY = resultY + robotPaddingSize;
                break;
            case 135:
                resultX = resultX - robotPaddingSize;
                resultY = resultY + robotPaddingSize;
                break;
            case 180:
                resultX = resultX - robotPaddingSize;
                break;
            case 225:
                resultX = resultX - robotPaddingSize;
                resultY = resultY - robotPaddingSize;
                break;
            case 270:
                resultY = resultY - robotPaddingSize;
                break;
            case 315:
                resultY = resultY - robotPaddingSize;
                resultX = resultX + robotPaddingSize;
                break;
            default:
                resultX = resultX + robotPaddingSize;
        }
        return new int[] { resultX, resultY };
    }

    private void updateRobotDisplay(int newX, int newY, int curX, int curY, int newDir) {
        // clear the robot from arena
        for (int[] coords : paddingCoords(curX, curY)) {
            int tempX = coords[0];
            int tempY = coords[1];
            String iconIdPath = "";
            ImageIcon icon = new ImageIcon(iconIdPath);
            Image image = icon.getImage();
            Image newImg = image.getScaledInstance(iconScaling, iconScaling, java.awt.Image.SCALE_SMOOTH);
            icon = new ImageIcon(newImg);
            buttonGrid[tempX][tempY].setIcon(icon);
        }

        // load new robot location into arena
        for (int[] coords : paddingCoords(newX, newY)) {
            int tempX = coords[0];
            int tempY = coords[1];
            String iconIdPath = "ImageSet/41-BODY.png";
            ImageIcon icon = new ImageIcon(iconIdPath);
            Image image = icon.getImage();
            Image newImg = image.getScaledInstance(iconScaling, iconScaling, java.awt.Image.SCALE_SMOOTH);
            icon = new ImageIcon(newImg);
            buttonGrid[tempX][tempY].setIcon(icon);
        }

        // set the face of the robot
        // String iconIdPath = "ImageSet/41-HEAD.png";
        // String headiconIdPath = path[stepCounter + 1][3] == "SCAN" ?
        // "ImageSet/42-SCAN.png" : "ImageSet/41-HEAD.png";

    }

    private void setHead(int newX, int newY, int newDir, int offset) {
        headiconIdPath = path[stepCounter + offset][3] == "SCAN" ? "ImageSet/42-SCAN.png"
                : "ImageSet/41-HEAD.png";

        ImageIcon icon = new ImageIcon(headiconIdPath);
        Image image = icon.getImage();
        Image newImg = image.getScaledInstance(iconScaling, iconScaling, java.awt.Image.SCALE_SMOOTH);
        icon = new ImageIcon(newImg);
        buttonGrid[robotHead(newX, newY, newDir)[0]][robotHead(newX, newY, newDir)[1]].setIcon(icon);
    }
}
