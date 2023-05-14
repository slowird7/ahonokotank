package com.app.ahonokotank;

import java.net.URL;
import java.util.*;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import static com.app.ahonokotank.Missile.MISSILESTATE.EXPLODE;
import static com.app.ahonokotank.Missile.MISSILESTATE.FLY;
import static com.app.ahonokotank.Missile.missiles;
import static com.app.ahonokotank.MovingBody.BODYSTATE.EXPLODED;
import static com.app.ahonokotank.Tank.TANKSTATE.*;
import static com.app.ahonokotank.Tank.tanks;
import static java.lang.Thread.sleep;

public class FXMLController implements Initializable {

    public static FXMLController INSTANCE;
    private static int NO_OF_TANKS = 2;
    private static int GRID_SIZE = 20;

    int turn = 0;
    final int CYCLE = 2;


    @FXML
    private Button btnRun;
    @FXML
    private GridPane theMap;

    private Label cell[][];

    private BooleanProperty isRunning;

    private Timer timer;

    private Battlefield theBattlefield;

    public void clearAll() {
        for (int row = 0; row < theMap.getRowCount(); row ++) {
            for (int column = 0; column < theMap.getColumnCount(); column++) {
                if (!cell[row][column].getText().equals("■")) {
                    cell[row][column].setText("　");
                }
            }
        }
    }

    @FXML
    private void onBtnStart(ActionEvent event) {
        if (!isRunning.get()) {
            turn = 0;
            btnRun.setText("stop");
            theMap.requestFocus();
            theBattlefield.clearAll();
            clearAll();
            for (Tank tank : tanks) {
                tank.state = AUTORUN;
                tank.initLocation();
                FXMLPlotTank(tank);
            }
            tanks.get(0).state = OPERATED;

            timer = new Timer(false);
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if (turn == 0) {
                        Platform.runLater(() -> {
                            for (Tank tank : tanks) {
                                if (tank.state == Tank.TANKSTATE.AUTORUN || tank.state == Tank.TANKSTATE.OPERATED) {
                                    FXMLhideMovingBody(tank);
                                }
                            }

                            for (Tank tank : tanks) {
                                tank.play();
                                FXMLController.INSTANCE.FXMLPlotTank(tank);
                            }
                        });
                    }

                    Platform.runLater(() -> {
                        for (Missile missile : missiles) {
                        }
                        for (int i = 0; i < missiles.size(); i++) {
                            Missile missile = missiles.get(i);
                            if (missile.state == FLY) {
                                FXMLhideMovingBody(missile);
                                missile.fly();
                                if (theBattlefield.getCell(missile.ty, missile.tx).getType() == 'W' || theBattlefield.getCell(missile.ty, missile.tx).getType() == 'T') {
                                    missile.state = EXPLODE;
                                    if (theBattlefield.getCell(missile.ty, missile.tx).getType() == 'T') {
                                        for (Tank tank : tanks) {
                                            if (tank.state != DESTROYED && tank.ty == missile.ty && tank.tx == missile.tx) {
                                                tank.state = DESTROYED;
                                                if (tank.getId() == 0) {
                                                    new Alert(Alert.AlertType.INFORMATION, "You are destryed.").show();
                                                }
                                            }
                                         }
                                    }
                                }
                            } else if (missile.state == EXPLODE) {
                                missile.state = Missile.MISSILESTATE.EXPLODED;
                            } else if (missile.state == Missile.MISSILESTATE.EXPLODED) {
                                missiles.remove(missile);
                                missile.getOwner().missile = null;
                                missile.getOwner().inFight = false;
                            }
                        }
                        for (Missile missile : missiles) {
                            FXMLPlotMissile(missile);
                        }
                    });

                    turn = (turn + 1) % CYCLE;
                }
            };

            timer.scheduleAtFixedRate(task, 3000, 100);
        } else {
            timer.cancel();
            btnRun.setText("start");
        }
        isRunning.set(!isRunning.get());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        theBattlefield = Battlefield.getInstance();
        loadMap("map.txt");
        // 自機生成
        tanks.add(new Tank(0, Tank.TANKSTATE.OPERATED));
        // NPC生成
        for (int id = 1; id < NO_OF_TANKS; id++) {
            tanks.add(new Tank(id, Tank.TANKSTATE.AUTORUN));
        }
        INSTANCE = this;
        isRunning = new SimpleBooleanProperty(false);
        Platform.runLater(() -> {
            theMap.getScene().getWindow().showingProperty().addListener((observable, oldValue, newValue) -> {
                if (oldValue == true && newValue == false) {
                    timer.cancel();
                }
            });
        });
        btnRun.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.SPACE) {
                    event.consume(); // Prevents the button from receiving the SPACE key
                }
            }
        });
    }

    private BooleanProperty isRunningProperty() {
        return isRunning;
    }

    private void loadMap(String mapName) {
        theBattlefield.loadMap(mapName);
        theMap.getRowConstraints().clear();
        theMap.getColumnConstraints().clear();
        for (int ii = 0; ii < theBattlefield.getNoOfRows(); ii++) {
            theMap.getRowConstraints().add(new RowConstraints(GRID_SIZE));
        }
        for (int jj = 0; jj < theBattlefield.getNoOfColumns(); jj++) {
            theMap.getColumnConstraints().add(new ColumnConstraints(GRID_SIZE));
        }
            cell = new Label[theBattlefield.getNoOfRows()][theBattlefield.getNoOfColumns()];
        for (int yy = 0; yy < theBattlefield.getNoOfRows(); yy++) {
            for (int xx = 0; xx < theBattlefield.getNoOfColumns(); xx++) {
                switch (theBattlefield.getCell(yy ,xx).getType()) {
                    case 'W' -> cell[yy][xx] = new Label("■");
                    case 'S' -> cell[yy][xx] = new Label("　");
                }
                cell[yy][xx].setVisible(true);
                cell[yy][xx].setFont(new Font("system", GRID_SIZE));
                cell[yy][xx].setEllipsisString("");
                theMap.getChildren().add(cell[yy][xx]);
                theMap.setRowIndex(cell[yy][xx], yy);
                theMap.setColumnIndex(cell[yy][xx], xx);
            }
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

    public void FXMLPlotTank(Tank tank) throws IllegalArgumentException {
//        cell[tank.ty][tank.tx].setText("〇");
        switch (tank.towardDir) {
            case NORTH:
                cell[tank.ty][tank.tx].setText("∩");
                break;
            case EAST:
                cell[tank.ty][tank.tx].setText("⊃");
                break;
            case SOUTH:
                cell[tank.ty][tank.tx].setText("∪");
                break;
            case WEST:
                cell[tank.ty][tank.tx].setText("⊂");
                break;
            default:
                throw new IllegalArgumentException("unknown direction:" + tank.towardDir);
        }
        if (tank.state == DESTROYED) {
            cell[tank.ty][tank.tx].setTextFill(Color.GRAY);
        } else {
            cell[tank.ty][tank.tx].setTextFill(tank.color);
        }
    }

    public void FXMLPlotMissile(Missile missile) throws IllegalArgumentException {
//        cell[tank.ty][tank.tx].setText("〇");
        if (missile.state == EXPLODE) {
            cell[missile.ty][missile.tx].setText("※");
            cell[missile.ty][missile.tx].setTextFill(missile.color);
        } else if (missile.state == Missile.MISSILESTATE.EXPLODED) {
            cell[missile.ty][missile.tx].setText(theBattlefield.getCell(missile.ty, missile.tx).getType() == 'W' ? "■" : "　");
            cell[missile.ty][missile.tx].setTextFill(Color.BLACK);
        } else if (missile.state == FLY) {
            switch (missile.getOwner().towardDir) {
                case NORTH:
                    cell[missile.ty][missile.tx].setText("↑");
                    break;
                case EAST:
                    cell[missile.ty][missile.tx].setText("→");
                    break;
                case SOUTH:
                    cell[missile.ty][missile.tx].setText("↓");
                    break;
                case WEST:
                    cell[missile.ty][missile.tx].setText("←");
                    break;
                default:
                    throw new IllegalArgumentException("unknown direction:" + missile.towardDir);
            }
            cell[missile.ty][missile.tx].setTextFill(missile.color);
        }
    }


}
