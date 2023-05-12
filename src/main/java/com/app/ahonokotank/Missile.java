package com.app.ahonokotank;

import java.util.ArrayList;
import java.util.List;

public class Missile extends MovingBody {

    public static List<Missile> missiles = new ArrayList<>();

    private Tank owner;
    private final int SPEED = 1;

    public Missile(Tank owner) {
        super(0, BODYSTATE.OPERATED, 1);
        this.owner = owner;
        towardDir = owner.towardDir;
        color = owner.color;
        tx = owner.tx;
        ty = owner.ty;
//        switch (towardDir) {
//            case NORTH -> { tx = owner.tx; ty = owner.ty - 2; }
//            case EAST  -> { tx = owner.tx + 2; ty = owner.ty; }
//            case SOUTH -> { tx = owner.tx; ty = owner.ty + 2; }
//            case WEST  -> { tx = owner.tx - 2; ty = owner.ty; }
//            default    -> { throw new IllegalArgumentException("Unknown direction"); }
//        }
        missiles.add(this);
    }

    public Tank getOwner() {
        return owner;
    }

    public void fly() {
        switch (owner.towardDir) {
            case NORTH -> { ty = ty - SPEED; }
            case EAST  -> { tx = tx + SPEED; }
            case SOUTH -> { ty = ty + SPEED; }
            case WEST  -> { tx = tx - SPEED; }
            default    -> { throw new IllegalArgumentException("Unknown direction"); }
        }
    }

}
