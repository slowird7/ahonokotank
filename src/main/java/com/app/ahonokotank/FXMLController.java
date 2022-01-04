package com.app.ahonokotank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

public class FXMLController implements Initializable {

    public static final int mapSize = 10;

    public static FXMLController INSTANCE;
    
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
                case NORTH: return WEST;
                case EAST:  return NORTH;
                case SOUTH: return EAST;
                case WEST:  return SOUTH;
            }
            return null;
        }
        
        public DIRECTION toRight(DIRECTION dir) {
            switch (dir) {
                case NORTH: return EAST;
                case EAST:  return SOUTH;
                case SOUTH: return WEST;
                case WEST:  return NORTH;
            }
            return null;
        }
        
    }

    public int columns = 0, rows = 0;

    @FXML
    private Button btnStart;
    @FXML
    private GridPane theMap;

    private Label cell[][];

    private Tank tank1;

    @FXML
    private void onBtnStart(ActionEvent event) {
        tank1.init();
        plotTank(tank1);
        tank1.startController();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadMap("map.txt");
        tank1 = new Tank();
        INSTANCE = this;

    }

    private void loadMap(String mapName) {
        File file = new File(mapName);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                rows++;
                if (columns < line.length()) columns = line.length();
            }
            cell = new Label[rows][columns];
        } catch (IOException ex) {
            new Alert(Alert.AlertType.WARNING, "map file not found. [" + mapName+ "]", ButtonType.OK).showAndWait();
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int yy = -1;
            while ((line = br.readLine()) != null) {
                yy++;
                for (int xx = 0; xx < line.length(); xx++) {
                    cell[yy][xx] =  new Label(String.valueOf(line.charAt(xx)));
                    cell[yy][xx].setVisible(true);
                    cell[yy][xx].setFont(new Font("system", 22));
                    cell[yy][xx].setEllipsisString("");
                    theMap.setRowIndex(cell[yy][xx], yy);
                    theMap.setColumnIndex(cell[yy][xx], xx);
                    theMap.getChildren().add(cell[yy][xx]);
                }
            }
           
        } catch (IOException ex) {
            new Alert(Alert.AlertType.WARNING, ex.getMessage()+ " [" + mapName+ "]", ButtonType.OK).showAndWait();
        }
    }
    
    public boolean isLocateOK(Tank tank) throws IllegalArgumentException {
        if (!cell[tank.ty][tank.tx].getText().equals("　") && !cell[tank.ty][tank.tx].getText().equals("〇")  && !cell[tank.ty][tank.tx].getText().equals("｜")  && !cell[tank.ty][tank.tx].getText().equals("―")) {
            return false;
        }
        switch (tank.td) {
            case NORTH: return cell[tank.ty - 1][tank.tx].getText().equals("　");
            case EAST : return cell[tank.ty][tank.tx + 1].getText().equals("　");
            case SOUTH: return cell[tank.ty + 1][tank.tx].getText().equals("　");
            case WEST : return cell[tank.ty][tank.tx - 1].getText().equals("　");
            default:
                throw new IllegalArgumentException("unknown direction:" + tank.td);
        }
    }
    
    public void plotTank(Tank tank) throws IllegalArgumentException {
        cell[tank.ty][tank.tx].setText("〇");
        switch (tank.td) {
            case NORTH: cell[tank.ty - 1][tank.tx].setText("｜"); break;
            case EAST : cell[tank.ty][tank.tx + 1].setText("―"); break;
            case SOUTH: cell[tank.ty + 1][tank.tx].setText("｜"); break;
            case WEST : cell[tank.ty][tank.tx - 1].setText("―"); break;
            default:
                throw new IllegalArgumentException("unknown direction:" + tank.td);
        }
    }
}
