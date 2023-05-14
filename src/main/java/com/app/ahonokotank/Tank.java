/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.ahonokotank;

import javafx.scene.control.skin.TextAreaSkin;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.app.ahonokotank.User32.INSTANCE;

/**
 *
 * @author otsuka
 */
public class Tank extends MovingBody {

    public static final List<Tank> tanks = new ArrayList<>();

    public enum TANKSTATE {
        OPERATED,
        AUTORUN,
        DESTROYED,
    }

    static final int TANK_SIZE = 1;
    public TANKSTATE state;
    public boolean inFight = false;
    private Random random;

    public Missile missile;

    private static Battlefield theBattlefield = Battlefield.getInstance();


    static final Color colors[] = {Color.RED, Color.GREEN, Color.BLUE, Color.VIOLET, Color.YELLOW, Color.INDIGO, Color.ORANGE, Color.BLUEVIOLET};

    public Tank(int id, TANKSTATE state) {
        super(id, TANK_SIZE);
//        this.missile = new Missile();
        if (state == TANKSTATE.OPERATED) {
            this.color = colors[0];
        } else {
            this.color = colors[id % 7 + 1];
        }
        this.type = 'T';
        this.state = state;
    }

    void initLocation() {
        random = new Random();
        towardDir = Battlefield.DIRECTION.NORTH;
        while (!theBattlefield.isEmpty(ty, tx)) {
            tx = random.nextInt(theBattlefield.getNoOfColumns());
            ty = random.nextInt(theBattlefield.getNoOfRows());
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
        theBattlefield.locate(ty, tx, getType());
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
        if (!inFight && random.nextInt(100) > 80) {
            launchMissile();
        }
    }

    public void maneuver() {
        if (((User32.INSTANCE.GetKeyState((short) java.awt.event.KeyEvent.VK_UP)) & 0x8000) == 0x8000) {
            switch (towardDir) {
                case NORTH -> moveForward();
                case EAST  -> turnLeft();
                case SOUTH -> moveBackward();
                case WEST  -> turnRight();
            }
        } else if (((User32.INSTANCE.GetKeyState((short) java.awt.event.KeyEvent.VK_DOWN)) & 0x8000) == 0x8000) {
            switch (towardDir) {
                case NORTH -> moveBackward();
                case EAST  -> turnRight();
                case SOUTH -> moveForward();
                case WEST  -> turnLeft();
            }
        } else if (((User32.INSTANCE.GetKeyState((short) java.awt.event.KeyEvent.VK_LEFT)) & 0x8000) == 0x8000) {
            switch (towardDir) {
                case NORTH -> turnLeft();
                case EAST  -> moveBackward();
                case SOUTH -> turnRight();
                case WEST  -> moveForward();
            }
        } else if (((User32.INSTANCE.GetKeyState((short) java.awt.event.KeyEvent.VK_RIGHT)) & 0x8000) == 0x8000) {
            switch (towardDir) {
                case NORTH -> turnRight();
                case EAST  -> moveForward();
                case SOUTH -> turnLeft();
                case WEST  -> moveBackward();
            }
        }
    }

    private void launchMissile() {
        inFight = true;
        missile = new Missile(this);
    }
}
