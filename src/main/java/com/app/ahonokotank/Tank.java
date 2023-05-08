/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.ahonokotank;

import java.util.Random;

import static com.app.ahonokotank.User32.INSTANCE;

/**
 *
 * @author otsuka
 */
public class Tank extends MovingBody {

    public enum TANKSTATE {
        OPERETED,
        AUTORUN,
        DESTRUCTED,
    }

    private int id;
    public TANKSTATE state; 
    public boolean inFight = false;
    private Random random;

    private Missile missile;

    public Tank(int id, BODYSTATE state) {
        super(id, state, 2);
//        this.missile = new Missile();
    }

    void initLocation() {
        random = new Random();
        towardDir = Battlefield.DIRECTION.NORTH;
        while (!FXMLController.INSTANCE.isLocateOK(this)) {
            tx = random.nextInt(FXMLController.INSTANCE.columns);
            ty = random.nextInt(FXMLController.INSTANCE.rows);
            switch (random.nextInt(3)) {
                case 0:
                    towardDir = Battlefield.DIRECTION.NORTH;
                    break;
                case 1:
                    towardDir = Battlefield.DIRECTION.EAST;
                    break;
                case 2:
                    towardDir = Battlefield.DIRECTION.SOUTH;
                    break;
                case 3:
                    towardDir = Battlefield.DIRECTION.WEST;
                    break;
            }
        }
    }

    boolean isKeyPressed(short key) {
        return (INSTANCE.GetKeyState(key) & 0x8000) == 0x8000;
    }

    ;

    public void clear() {

    }

    void autoRun() {
        while (true) {
            int x = random.nextInt(100);
            if (x < 80 && isForwardOK()) {
                moveForward();
                break;
            } else if (x < 90 && isTurnLeftOK()) {
                turnLeft();
                break;

            } else if (x < 100 && isTurnRightOK()) {
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
