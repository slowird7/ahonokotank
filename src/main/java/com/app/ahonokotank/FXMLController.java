package com.app.ahonokotank;

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
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import static com.app.ahonokotank.Missile.MISSILESTATE.EXPLODE;
import static com.app.ahonokotank.Missile.MISSILESTATE.FLY;
import static com.app.ahonokotank.Missile.missiles;
import static com.app.ahonokotank.MovingBody.BODYSTATE.EXPLODED;
import static com.app.ahonokotank.Tank.TANKSTATE.DESTROYED;
import static com.app.ahonokotank.Tank.tanks;

public class FXMLController implements Initializable {

    public static FXMLController INSTANCE;
    private static int NO_OF_TANKS = 20;
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

    @FXML
    private void onBtnStart(ActionEvent event) {
        if (!isRunning.get()) {
            turn = 0;
            btnRun.setText("stop");
            for (Tank tank : tanks) {
                tank.initLocation();
                FXMLPlotTank(tank);
            }

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
                                if (tank.state == Tank.TANKSTATE.AUTORUN) {
                                    tank.autoRun();
                                } else if (tank.state == Tank.TANKSTATE.OPERATED) {
                                    tank.maneuver();
                                }
                                FXMLController.INSTANCE.FXMLPlotTank(tank);
                            }

                            for (Tank tank : tanks) {
                                FXMLPlotTank(tank);
                            }
                        });
                    }

                    Platform.runLater(() -> {
                        for (Missile missile : missiles) {
                            if (missile.state == FLY) {
                                FXMLhideMovingBody(missile);
                            }
                        }
                        for (int i = 0; i < missiles.size(); i++) {
                            Missile missile = missiles.get(i);
                            if (missile.state == FLY) {
                                missile.fly();
                                if (theBattlefield.getCell(missile.ty, missile.tx) == '■' || theBattlefield.getCell(missile.ty, missile.tx) == 'T') {
                                    missile.state = EXPLODE;
                                    if (theBattlefield.getCell(missile.ty, missile.tx) == 'T') {
                                        for (Tank tank : tanks) {
                                            if (tank.ty == missile.ty && tank.tx == missile.tx) {
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

            timer.scheduleAtFixedRate(task, 0, 100);
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
                cell[yy][xx] = new Label(String.valueOf(theBattlefield.getCell(yy ,xx)));
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
            cell[missile.ty][missile.tx].setText(String.valueOf(theBattlefield.getCell(missile.ty, missile.tx)));
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
