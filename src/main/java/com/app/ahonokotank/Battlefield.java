package com.app.ahonokotank;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static com.app.ahonokotank.Space.theSpace;
import static com.app.ahonokotank.Wall.theWall;

public class Battlefield {
    public enum DIRECTION {
        UNDEF(0),
        NORTH(1),
        EAST(2),
        SOUTH(3),
        WEST(4);

        private final int code;

        private DIRECTION(int code) {
            this.code = code;
        }

        int getCode() {
            return code;
        }

        public DIRECTION toLeft(DIRECTION dir) {
            switch (dir) {
                case NORTH:
                    return WEST;
                case EAST:
                    return NORTH;
                case SOUTH:
                    return EAST;
                case WEST:
                    return SOUTH;
            }
            return null;
        }

        public DIRECTION toRight(DIRECTION dir) {
            switch (dir) {
                case NORTH:
                    return EAST;
                case EAST:
                    return SOUTH;
                case SOUTH:
                    return WEST;
                case WEST:
                    return NORTH;
            }
            return null;
        }

    }

    private int noOfColumns = 0, noOfRows = 0;
    private MovingBody cell[][];

    private static Battlefield INSTANCE = new Battlefield();

    public static Battlefield getInstance() { return INSTANCE; }
    public int getNoOfColumns() { return noOfColumns; }
    public int getNoOfRows() { return noOfRows; }

    public MovingBody getCell(int row, int column) { return cell[row][column]; }
    public void loadMap(String mapName) {
        File file = new File(mapName);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                noOfRows++;
                if (noOfColumns < line.length()) {
                    noOfColumns = line.length();
                }
            }
            cell = new MovingBody[noOfRows][noOfColumns];
        } catch (IOException ex) {
            new Alert(Alert.AlertType.WARNING, "map file not found. [" + mapName + "]", ButtonType.OK).showAndWait();
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int yy = 0;
            while ((line = br.readLine()) != null) {
                for (int xx = 0; xx < line.length(); xx++) {
                    switch (line.charAt(xx)) {
                        case '■' -> cell[yy][xx] = theWall;
                        case '　' -> cell[yy][xx] = theSpace;
                        default -> throw new IllegalArgumentException("Unknown map element");
                    }
                }
                yy++;
            }

        } catch (IOException ex) {
            new Alert(Alert.AlertType.WARNING, ex.getMessage() + " [" + mapName + "]", ButtonType.OK).showAndWait();
        }
    }

    public boolean isOccupied(int row, int column) throws IllegalArgumentException {
        if (row < 0 || row >= noOfRows || column < 0 || column >= noOfColumns) return false;
        return (getCell(row, column) != theSpace);
    }

    public void clear(int row, int column) throws IllegalArgumentException {
        if (row < 0 || row >= noOfRows || column < 0 || column >= noOfColumns || cell[row][column] == theSpace) {
            throw new IllegalArgumentException("");
        };
        cell[row][column] = theSpace;
    }

    public void clearAll() {
        for (int row = 0; row < noOfRows; row ++) {
            for (int column = 0; column < noOfColumns; column++) {
                if (cell[row][column].getType() != 'W') {
                    cell[row][column] = theSpace;
                }
            }
        }
    }

    public void locate(int row, int column, MovingBody mb) throws IllegalArgumentException {
        if (row < 0 || row >= noOfRows || column < 0 || column >= noOfColumns) {
            throw new IllegalArgumentException("");
        };
        if (cell[row][column] == mb) return;
        if (!isOccupied(row, column) || mb.getType() == 'M') {
            cell[row][column] = mb;
        } else {
            throw new IllegalArgumentException("is occupied");
        }
    }

}
