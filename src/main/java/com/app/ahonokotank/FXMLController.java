package com.app.ahonokotank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

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

public class FXMLController implements Initializable {

    public static FXMLController INSTANCE;
    private static int NO_OF_TANKS = 8;


    public int columns = 0, rows = 0;

    private List<Tank> tanks;
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
            for (Tank tank : tanks) {
                tank.initLocation();
                FXMLplotTank(tank);
            }

            timer = new Timer(false);
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    for (Tank tank : tanks) {
                        Platform.runLater(()-> {
                            FXMLhideMovingBody(tank);
                            if (tank.bodystate == MovingBody.BODYSTATE.AUTORUN) {
                                tank.autoRun();
                            } else if (tank.bodystate == MovingBody.BODYSTATE.OPERETED) {
                                tank.maneuver();
                            }
                            FXMLController.INSTANCE.FXMLplotTank(tank);
                        });
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
        tanks = new ArrayList<>();
        for (int id = 1; id < NO_OF_TANKS; id++) {
            tanks.add(new Tank(id, MovingBody.BODYSTATE.AUTORUN));
        }
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

    public boolean isLocateOK(MovingBody mb) throws IllegalArgumentException {
        if (map[mb.ty][mb.tx] != '　') {
            return false;
        }
        switch (mb.towardDir) {
            case NORTH:
                return map[mb.ty - 1][mb.tx] == '　';
            case EAST:
                return map[mb.ty][mb.tx + 1] == '　';
            case SOUTH:
                return map[mb.ty + 1][mb.tx] == '　';
            case WEST:
                return map[mb.ty][mb.tx - 1] == '　';
            default:
                throw new IllegalArgumentException("unknown direction:" + mb.towardDir);
        }
    }

    public void FXMLhideMovingBody(MovingBody mb) throws IllegalArgumentException {
        for (int partNo = 0; partNo < mb.size; partNo++) {
            switch (mb.towardDir) {
                case NORTH:
                    cell[mb.ty - partNo][mb.tx].setText("　");
                    break;
                case EAST:
                    cell[mb.ty][mb.tx + partNo].setText("　");
                    break;
                case SOUTH:
                    cell[mb.ty + partNo][mb.tx].setText("　");
                    break;
                case WEST:
                    cell[mb.ty][mb.tx - partNo].setText("　");
                    break;
                default:
                    throw new IllegalArgumentException("unknown direction:" + mb.towardDir);
            }
        }
    }

    public void FXMLplotTank(Tank tank) throws IllegalArgumentException {
        cell[tank.ty][tank.tx].setText("〇");
        switch (tank.towardDir) {
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
                throw new IllegalArgumentException("unknown direction:" + tank.towardDir);
        }
    }


}
