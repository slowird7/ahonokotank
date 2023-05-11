package com.app.ahonokotank;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;

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

    public boolean isLocateOK(int row, int column) throws IllegalArgumentException {
        if (row < 0 || row >= rows || column < 0 || column >= columns) return false;
        if (getCell(row, column) != '　') {
            return false;
        }
//        switch (mb.towardDir) {
//            case NORTH:
//                return map[mb.ty - 1][mb.tx] == '　';
//            case EAST:
//                return map[mb.ty][mb.tx + 1] == '　';
//            case SOUTH:
//                return map[mb.ty + 1][mb.tx] == '　';
//            case WEST:
//                return map[mb.ty][mb.tx - 1] == '　';
//            default:
//                throw new IllegalArgumentException("unknown direction:" + mb.towardDir);
//        }
        return true;
    }

}
