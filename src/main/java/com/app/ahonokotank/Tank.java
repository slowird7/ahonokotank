/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.ahonokotank;

import com.app.ahonokotank.FXMLController.DIRECTION;

import java.util.Random;

import static com.app.ahonokotank.User32.INSTANCE;

/**
 *
 * @author otsuka
 */
public class Tank {

    public enum TANKSTATE {
        OPERETED,
        AUTORUN,
        DESTRUCTED,
    }
    
    private int id;
    public TANKSTATE state; 
    public int tx, ty;
    public int mx, my;
    public DIRECTION td, md;
    public boolean inFight = false;
    private Random random;

    public Tank(int id, TANKSTATE state) {
        this.id = id;
        this.state = state;
    }

    public Tank(Tank tank) {
        this.tx = tank.tx;
        this.ty = tank.ty;
        this.td = tank.td;
    }

    void initLocation() {
        random = new Random();
        td = DIRECTION.NORTH;
        while (!FXMLController.INSTANCE.isLocateOK(this)) {
            tx = random.nextInt(FXMLController.INSTANCE.columns);
            ty = random.nextInt(FXMLController.INSTANCE.rows);
            switch (random.nextInt(3)) {
                case 0:
                    td = DIRECTION.NORTH;
                    break;
                case 1:
                    td = DIRECTION.EAST;
                    break;
                case 2:
                    td = DIRECTION.SOUTH;
                    break;
                case 3:
                    td = DIRECTION.WEST;
                    break;
            }
        }
    }

    boolean isKeyPressed(short key) {
        return (INSTANCE.GetKeyState(key) & 0x8000) == 0x8000;
    }

    ;

    public boolean isForwardOK() {
        return FXMLController.INSTANCE.isForwardOK(this);
    }

    public boolean isBackwardOK() {
        return FXMLController.INSTANCE.isBackwardOK(this);
    }

    public boolean isTurnLeftOK() {
        return FXMLController.INSTANCE.isTurnLeftOK(this);
    }

    public boolean isTurnRightOK() {
        return FXMLController.INSTANCE.isTurnRightOK(this);
    }

    public void clear() {

    }

    public boolean moveForward() {
        return FXMLController.INSTANCE.moveForward(this);
    }

    public boolean moveBackward() {
        return FXMLController.INSTANCE.moveBackward(this);
    }

    public boolean turnLeft() {
        return FXMLController.INSTANCE.turnLeft(this);
    }

    public boolean turnRight() {
        return FXMLController.INSTANCE.turnRight(this);
    }

    void autoRun() {
        while (true) {
            int x = random.nextInt(100);
            if (x < 70 && isForwardOK()) {
                moveForward();
                break;
            } else if (x < 80 && isTurnLeftOK()) {
                turnLeft();
                break;

            } else if (x < 90 && isTurnRightOK()) {
                turnRight();
                break;

            } else if (isBackwardOK()) {
                moveBackward();
                break;
            }
        }
    }

    public void operate() {
        if (((User32.INSTANCE.GetKeyState((short) java.awt.event.KeyEvent.VK_UP)) & 0x8000) == 0x8000) {
            moveForward();
        } else if (((User32.INSTANCE.GetKeyState((short) java.awt.event.KeyEvent.VK_DOWN)) & 0x8000) == 0x8000) {
            moveBackward();
        } else if (((User32.INSTANCE.GetKeyState((short) java.awt.event.KeyEvent.VK_LEFT)) & 0x8000) == 0x8000) {
            turnLeft();
        } else if (((User32.INSTANCE.GetKeyState((short) java.awt.event.KeyEvent.VK_RIGHT)) & 0x8000) == 0x8000) {
            turnRight();
        }
    }
}
