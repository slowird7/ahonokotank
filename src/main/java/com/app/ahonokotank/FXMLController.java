package com.app.ahonokotank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.TimerTask;
import java.util.Timer;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import java.awt.event.KeyEvent;

public class FXMLController implements Initializable {

    public static FXMLController INSTANCE;
    private static int NO_OF_TANKS = 2;

    public enum DIRECTION {
        NORTH(0),
        EAST(1),
        SOUTH(2),
        WEST(3);

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

    public int columns = 0, rows = 0;

    @FXML
    private Button btnRun;
    @FXML
    private GridPane theMap;

    private Label cell[][];
    private char map[][];

    private Tank[] tank;
    private BooleanProperty isRunning;

    private Timer timer;

    @FXML
    private void onBtnStart(ActionEvent event) {
        if (!isRunning.get()) {
            btnRun.setText("stop");
            for (int nn = 0; nn < NO_OF_TANKS; nn++) {
                tank[nn] = new Tank(nn, Tank.TANKSTATE.AUTORUN);
                tank[nn].initLocation();
                plotTank(tank[nn]);
            }
            tank[0].state = Tank.TANKSTATE.OPERETED;

            timer = new Timer(false);
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    for (int nn = 0; nn < NO_OF_TANKS; nn++) {
                        if (tank[nn].state == Tank.TANKSTATE.AUTORUN) {
                            tank[nn].autoRun();
                        } else if (tank[nn].state == Tank.TANKSTATE.OPERETED) {
                            tank[nn].operate();
                        }
                    }
                }
            };

            timer.scheduleAtFixedRate(task, 0, 500);
        } else {
            timer.cancel();
            btnRun.setText("start");
        }
        isRunning.set(!isRunning.get());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadMap("map.txt");
        tank = new Tank[NO_OF_TANKS];
        INSTANCE = this;
        isRunning = new SimpleBooleanProperty(false);
        Platform.runLater(() -> {
            theMap.getScene().getWindow().showingProperty().addListener((observable, oldValue, newValue) -> {
                if (oldValue == true && newValue == false) {
                    timer.cancel();
                }
            });
        });
    }

    private BooleanProperty isRunningProperty() {
        return isRunning;
    }

    private void loadMap(String mapName) {
        theMap.getRowConstraints().clear();
        theMap.getColumnConstraints().clear();
        File file = new File(mapName);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                rows++;
                theMap.getRowConstraints().add(new RowConstraints(30));
                if (columns < line.length()) {
                    columns = line.length();
                }
            }
            cell = new Label[rows][columns];
            map = new char[rows][columns];
            for (int xx = 0; xx < columns; xx++) {
                theMap.getColumnConstraints().add(new ColumnConstraints(30));
            }
        } catch (IOException ex) {
            new Alert(Alert.AlertType.WARNING, "map file not found. [" + mapName + "]", ButtonType.OK).showAndWait();
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int yy = -1;
            while ((line = br.readLine()) != null) {
                yy++;
                for (int xx = 0; xx < line.length(); xx++) {
                    map[yy][xx] = line.charAt(xx);
                    cell[yy][xx] = new Label(String.valueOf(line.charAt(xx)));
                    cell[yy][xx].setVisible(true);
                    cell[yy][xx].setFont(new Font("system", 22));
                    cell[yy][xx].setEllipsisString("");
                    theMap.getChildren().add(cell[yy][xx]);
                    theMap.setRowIndex(cell[yy][xx], yy);
                    theMap.setColumnIndex(cell[yy][xx], xx);
                }
            }

        } catch (IOException ex) {
            new Alert(Alert.AlertType.WARNING, ex.getMessage() + " [" + mapName + "]", ButtonType.OK).showAndWait();
        }
    }

    public boolean isLocateOK(Tank tank) throws IllegalArgumentException {
        if (map[tank.ty][tank.tx] != '　') {
            return false;
        }
        switch (tank.td) {
            case NORTH:
                return map[tank.ty - 1][tank.tx] == '　';
            case EAST:
                return map[tank.ty][tank.tx + 1] == '　';
            case SOUTH:
                return map[tank.ty + 1][tank.tx] == '　';
            case WEST:
                return map[tank.ty][tank.tx - 1] == '　';
            default:
                throw new IllegalArgumentException("unknown direction:" + tank.td);
        }
    }

    public void clearTank(Tank tank) throws IllegalArgumentException {
        cell[tank.ty][tank.tx].setText("　");
        switch (tank.td) {
            case NORTH:
                cell[tank.ty - 1][tank.tx].setText("　");
                break;
            case EAST:
                cell[tank.ty][tank.tx + 1].setText("　");
                break;
            case SOUTH:
                cell[tank.ty + 1][tank.tx].setText("　");
                break;
            case WEST:
                cell[tank.ty][tank.tx - 1].setText("　");
                break;
            default:
                throw new IllegalArgumentException("unknown direction:" + tank.td);
        }
    }

    public void plotTank(Tank tank) throws IllegalArgumentException {
        cell[tank.ty][tank.tx].setText("〇");
        switch (tank.td) {
            case NORTH:
                cell[tank.ty - 1][tank.tx].setText("｜");
                break;
            case EAST:
                cell[tank.ty][tank.tx + 1].setText("―");
                break;
            case SOUTH:
                cell[tank.ty + 1][tank.tx].setText("｜");
                break;
            case WEST:
                cell[tank.ty][tank.tx - 1].setText("―");
                break;
            default:
                throw new IllegalArgumentException("unknown direction:" + tank.td);
        }
    }

//======================================================================
    public boolean isForwardOK(Tank tank) {
        Tank newPos = new Tank(tank);
        switch (tank.td) {
            case NORTH:
                newPos.ty--;
                break;
            case EAST:
                newPos.tx++;
                break;
            case SOUTH:
                newPos.ty++;
                break;
            case WEST:
                newPos.tx--;
                break;
        }
        return isLocateOK(newPos);
    }

    public boolean isBackwardOK(Tank tank) {
        Tank newPos = new Tank(tank);
        switch (tank.td) {
            case NORTH:
                newPos.ty++;
                break;
            case EAST:
                newPos.tx--;
                break;
            case SOUTH:
                newPos.ty--;
                break;
            case WEST:
                newPos.tx++;
                break;
        }
        return isLocateOK(newPos);
    }

    public boolean isTurnLeftOK(Tank tank) {
        Tank newPos = new Tank(tank);
        switch (tank.td) {
            case NORTH:
                newPos.td = DIRECTION.WEST;
                break;
            case EAST:
                newPos.td = DIRECTION.NORTH;
                break;
            case SOUTH:
                newPos.td = DIRECTION.EAST;
                break;
            case WEST:
                newPos.td = DIRECTION.SOUTH;
                break;
        }
        return isLocateOK(newPos);
    }

    public boolean isTurnRightOK(Tank tank) {
        Tank newPos = new Tank(tank);
        switch (tank.td) {
            case NORTH:
                newPos.td = DIRECTION.EAST;
                break;
            case EAST:
                newPos.td = DIRECTION.SOUTH;
                break;
            case SOUTH:
                newPos.td = DIRECTION.WEST;
                break;
            case WEST:
                newPos.td = DIRECTION.NORTH;
                break;
        }
        return isLocateOK(newPos);
    }

    public boolean moveForward(Tank tank) {
        if (!isForwardOK(tank)) {
            return false;
        }
        Tank current = new Tank(tank);
        Platform.runLater(() -> {
            FXMLController.INSTANCE.clearTank(current);
        });
        switch (tank.td) {
            case NORTH:
                tank.ty--;
                break;
            case EAST:
                tank.tx++;
                break;
            case SOUTH:
                tank.ty++;
                break;
            case WEST:
                tank.tx--;
                break;
        }
        Platform.runLater(() -> {
            FXMLController.INSTANCE.plotTank(tank);
        });
        return true;
    }

    public boolean moveBackward(Tank tank) {
        if (!isBackwardOK(tank)) {
            return false;
        }
        Tank current = new Tank(tank);
        Platform.runLater(() -> {
            FXMLController.INSTANCE.clearTank(current);
        });
        switch (tank.td) {
            case NORTH:
                tank.ty++;
                break;
            case EAST:
                tank.tx--;
                break;
            case SOUTH:
                tank.ty--;
                break;
            case WEST:
                tank.tx++;
                break;
        }
        Platform.runLater(() -> {
            FXMLController.INSTANCE.plotTank(tank);
        });
        return true;
    }

    public boolean turnLeft(Tank tank) {
        if (!isTurnLeftOK(tank)) {
            return false;
        }
        Tank current = new Tank(tank);
        Platform.runLater(() -> {
            FXMLController.INSTANCE.clearTank(current);
        });
        switch (tank.td) {
            case NORTH:
                tank.td = DIRECTION.WEST;
                break;
            case EAST:
                tank.td = DIRECTION.NORTH;
                break;
            case SOUTH:
                tank.td = DIRECTION.EAST;
                break;
            case WEST:
                tank.td = DIRECTION.SOUTH;
                break;
        }
        Platform.runLater(() -> {
            FXMLController.INSTANCE.plotTank(tank);
        });
        return true;
    }

    public boolean turnRight(Tank tank) {
        if (!isTurnRightOK(tank)) {
            return false;
        }
        Tank current = new Tank(tank);
        Platform.runLater(() -> {
            FXMLController.INSTANCE.clearTank(current);
        });
        switch (tank.td) {
            case NORTH:
                tank.td = DIRECTION.EAST;
                break;
            case EAST:
                tank.td = DIRECTION.SOUTH;
                break;
            case SOUTH:
                tank.td = DIRECTION.WEST;
                break;
            case WEST:
                tank.td = DIRECTION.NORTH;
                break;
        }
        Platform.runLater(() -> {
            FXMLController.INSTANCE.plotTank(tank);
        });
        return true;
    }

}
