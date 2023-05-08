/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.ahonokotank;

import javafx.application.Platform;

import static com.app.ahonokotank.Battlefield.DIRECTION.*;
import static com.app.ahonokotank.User32.INSTANCE;

/**
 *
 * @author otsuka
 */
public class MovingBody {

    public enum BODYSTATE {
        OPERETED,
        AUTORUN,
        DESTRUCTED,
    }

    private int id;
    public BODYSTATE bodystate;
    public int tx, ty;
    public Battlefield.DIRECTION towardDir, md;
    public int size;


    public MovingBody(int id, BODYSTATE bodystate, int size) {
        this.id = id;
        this.bodystate = bodystate;
        this.size = size;
        this.towardDir = UNDEF;
    }

    public MovingBody(MovingBody movingBody) {
        this.tx = movingBody.tx;
        this.ty = movingBody.ty;
        this.size = movingBody.size;
        this.towardDir = movingBody.towardDir;
    }


    boolean isKeyPressed(short key) {
        return (INSTANCE.GetKeyState(key) & 0x8000) == 0x8000;
    }

    ;

    public void clear() {

    }

    //======================================================================
    public boolean isForwardOK() {
        MovingBody newPos = new MovingBody(this);
        switch (towardDir) {
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
        return FXMLController.INSTANCE.isLocateOK(newPos);
    }

    public boolean isBackwardOK() {
        MovingBody newPos = new MovingBody(this);
        switch (towardDir) {
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
        return FXMLController.INSTANCE.isLocateOK(newPos);
    }

    public boolean isTurnLeftOK() {
        MovingBody newPos = new MovingBody(this);
        switch (towardDir) {
            case NORTH:
                newPos.towardDir = Battlefield.DIRECTION.WEST;
                break;
            case EAST:
                newPos.towardDir = Battlefield.DIRECTION.NORTH;
                break;
            case SOUTH:
                newPos.towardDir = Battlefield.DIRECTION.EAST;
                break;
            case WEST:
                newPos.towardDir = Battlefield.DIRECTION.SOUTH;
                break;
        }
        return FXMLController.INSTANCE.isLocateOK(newPos);
    }

    public boolean isTurnRightOK() {
        MovingBody newPos = new MovingBody(this);
        switch (towardDir) {
            case NORTH:
                newPos.towardDir = Battlefield.DIRECTION.EAST;
                break;
            case EAST:
                newPos.towardDir = Battlefield.DIRECTION.SOUTH;
                break;
            case SOUTH:
                newPos.towardDir = Battlefield.DIRECTION.WEST;
                break;
            case WEST:
                newPos.towardDir = Battlefield.DIRECTION.NORTH;
                break;
        }
        return FXMLController.INSTANCE.isLocateOK(newPos);
    }

    public boolean moveForward() {
        if (!isForwardOK()) {
            return false;
        }
        switch (towardDir) {
            case NORTH:
                ty--;
                break;
            case EAST:
                tx++;
                break;
            case SOUTH:
                ty++;
                break;
            case WEST:
                tx--;
                break;
        }
        return true;
    }

    public boolean moveBackward() {
        if (!isBackwardOK()) {
            return false;
        }
        switch (towardDir) {
            case NORTH:
                ty++;
                break;
            case EAST:
                tx--;
                break;
            case SOUTH:
                ty--;
                break;
            case WEST:
                tx++;
                break;
        }
        return true;
    }

    public boolean turnLeft() {
        if (!isTurnLeftOK()) {
            return false;
        }
        switch (towardDir) {
            case NORTH:
                towardDir = Battlefield.DIRECTION.WEST;
                break;
            case EAST:
                towardDir = Battlefield.DIRECTION.NORTH;
                break;
            case SOUTH:
                towardDir = Battlefield.DIRECTION.EAST;
                break;
            case WEST:
                towardDir = Battlefield.DIRECTION.SOUTH;
                break;
        }
        return true;
    }

    public boolean turnRight() {
        if (!isTurnRightOK()) {
            return false;
        }
        switch (towardDir) {
            case NORTH:
                towardDir = Battlefield.DIRECTION.EAST;
                break;
            case EAST:
                towardDir = Battlefield.DIRECTION.SOUTH;
                break;
            case SOUTH:
                towardDir = Battlefield.DIRECTION.WEST;
                break;
            case WEST:
                towardDir = Battlefield.DIRECTION.NORTH;
                break;
        }
        return true;
    }

    public void maneuver() {
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