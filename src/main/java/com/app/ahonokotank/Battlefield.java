package com.app.ahonokotank;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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

    private int columns = 0, rows = 0;
    private char map[][];

    private static Battlefield INSTANCE = new Battlefield();

    public static Battlefield getInstance() { return INSTANCE; }
    public int getColumns() { return columns; }
    public int getRows() { return rows; }

    public char getCell(int row, int column) { return map[row][column]; }
    public void loadMap(String mapName) {
        File file = new File(mapName);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                rows++;
                if (columns < line.length()) {
                    columns = line.length();
                }
            }
            map = new char[rows][columns];
        } catch (IOException ex) {
            new Alert(Alert.AlertType.WARNING, "map file not found. [" + mapName + "]", ButtonType.OK).showAndWait();
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int yy = 0;
            while ((line = br.readLine()) != null) {
                for (int xx = 0; xx < line.length(); xx++) {
                    map[yy][xx] = line.charAt(xx);
                }
                yy++;
            }

        } catch (IOException ex) {
            new Alert(Alert.AlertType.WARNING, ex.getMessage() + " [" + mapName + "]", ButtonType.OK).showAndWait();
        }
    }

    public boolean isEmpty(int row, int column) throws IllegalArgumentException {
        if (row < 0 || row >= rows || column < 0 || column >= columns) return false;
        return (getCell(row, column) == '　');
    }

    public void clear(int row, int column) throws IllegalArgumentException {
        if (row < 0 || row >= rows || column < 0 || column >= columns || map[row][column] == '　') {
            throw new IllegalArgumentException("");
        };
        map[row][column] = '　';
    }

    public void locate(int row, int column, char type) throws IllegalArgumentException {
        if (row < 0 || row >= rows || column < 0 || column >= columns || map[row][column] != '　') {
            throw new IllegalArgumentException("");
        };
        map[row][column] = type;
    }

}
