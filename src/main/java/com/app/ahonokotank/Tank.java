/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.ahonokotank;

import com.app.ahonokotank.FXMLController.DIRECTION;
import static com.app.ahonokotank.User32.INSTANCE;
import java.util.Random;
import java.util.TimerTask;
import com.sun.jna.Library;
import com.sun.jna.Native;
import java.util.Timer;
import javafx.application.Platform;
/**
 *
 * @author otsuka
 */
public class Tank {

    public int tx, ty;
    public int mx, my;
    public DIRECTION td, md;
    public boolean inFight = false;
    private Random random;
    Timer timer;
    
    public Tank() {
        
    }
    
    private Tank(Tank tank) {
        this.tx = tank.tx;
        this.ty = tank.ty;
        this.td = tank.td;
    }
    
    void init() {
        random = new Random();
        td = DIRECTION.NORTH;
        while (!FXMLController.INSTANCE.isLocateOK(this)) {
            tx = random.nextInt(FXMLController.INSTANCE.columns);
            ty = random.nextInt(FXMLController.INSTANCE.rows);
            switch (random.nextInt(3)) {
                case 0: td = DIRECTION.NORTH; break;
                case 1: td = DIRECTION.EAST; break;
                case 2: td = DIRECTION.SOUTH; break;
                case 3: td = DIRECTION.WEST; break;
            }
        }
    }

    boolean isKeyPressed(short key) {
        return (INSTANCE.GetKeyState(key) & 0x8000) == 0x8000;
    };

    public boolean isForwardOK() {
        Tank newPos = new Tank(this);
        switch(td) {
            case NORTH: newPos.ty--;break;
            case EAST : newPos.tx++;break;
            case SOUTH: newPos.ty++;break;
            case WEST : newPos.tx--;break;
        }
        return FXMLController.INSTANCE.isLocateOK(newPos);
    }
    
    public boolean isBackwardOK() {
        Tank newPos = new Tank(this);
        switch(td) {
            case NORTH: newPos.ty++; break;
            case EAST : newPos.tx--; break;
            case SOUTH: newPos.ty--; break;
            case WEST : newPos.tx++; break;
        }
        return FXMLController.INSTANCE.isLocateOK(newPos);
    }
    
    public boolean isTurnLeftOK() {
        Tank newPos = new Tank(this);
        switch(td) {
            case NORTH: newPos.td = DIRECTION.WEST;  break;
            case EAST : newPos.td = DIRECTION.NORTH; break;
            case SOUTH: newPos.td = DIRECTION.EAST;  break;
            case WEST : newPos.td = DIRECTION.SOUTH; break;
        }
        return FXMLController.INSTANCE.isLocateOK(newPos);
    }
    
    public boolean isTurnRightOK() {
        Tank newPos = new Tank(this);
        switch(td) {
            case NORTH: newPos.td = DIRECTION.EAST;  break;
            case EAST : newPos.td = DIRECTION.SOUTH; break;
            case SOUTH: newPos.td = DIRECTION.WEST;  break;
            case WEST : newPos.td = DIRECTION.NORTH; break;
        }
        return FXMLController.INSTANCE.isLocateOK(newPos);
    }
    
    public void clear() {
        
    }

    public void moveForward() {
        Tank current = new Tank(this);
        Platform.runLater(()->{
            FXMLController.INSTANCE.clearTank(current);
        });
        switch(td) {
            case NORTH: this.ty--;break;
            case EAST : this.tx++;break;
            case SOUTH: this.ty++;break;
            case WEST : this.tx--;break;
        }
        Platform.runLater(()->{
            FXMLController.INSTANCE.plotTank(this);
        });
    }
    
    public void moveBackward() {
        Tank current = new Tank(this);
        Platform.runLater(()->{
            FXMLController.INSTANCE.clearTank(current);
        });
        switch(td) {
            case NORTH: this.ty++;break;
            case EAST : this.tx--;break;
            case SOUTH: this.ty--;break;
            case WEST : this.tx++;break;
        }
        Platform.runLater(()->{
            FXMLController.INSTANCE.plotTank(this);
        });
    }
    
    public void turnLeft() {
        Tank current = new Tank(this);
        Platform.runLater(()->{
            FXMLController.INSTANCE.clearTank(current);
        });
        switch(td) {
            case NORTH: this.td = DIRECTION.WEST;  break;
            case EAST : this.td = DIRECTION.NORTH; break;
            case SOUTH: this.td = DIRECTION.EAST;  break;
            case WEST : this.td = DIRECTION.SOUTH; break;
        }
        Platform.runLater(()->{
            FXMLController.INSTANCE.plotTank(this);
        });
    }
    
    public void turnRight() {
        Tank current = new Tank(this);
        Platform.runLater(()->{
            FXMLController.INSTANCE.clearTank(current);
        });
        switch(td) {
            case NORTH: this.td = DIRECTION.EAST;  break;
            case EAST : this.td = DIRECTION.SOUTH; break;
            case SOUTH: this.td = DIRECTION.WEST;  break;
            case WEST : this.td = DIRECTION.NORTH; break;
        }
        Platform.runLater(()->{
            FXMLController.INSTANCE.plotTank(this);
        });
    }
    
    void startController() {
        timer = new Timer(false);
        TimerTask task = new TimerTask() {
            long beforeTime = System.currentTimeMillis();
            int cnt = 0;

            @Override
            public void run() {
                clear();
                int x = random.nextInt(100);
                if(x < 50) {
                    if (isForwardOK()) moveForward();
                } else if (x < 60) {
                    if (isBackwardOK()) moveBackward();
                } else if (x < 80) {
                    if(isTurnLeftOK()) turnLeft();
                } else {
                    if (isTurnRightOK()) turnRight();
                }
            }
        };
        
        timer.scheduleAtFixedRate(task, 0, 500);
    }
    
    public void terminate() {
        timer.cancel();
    }

}
