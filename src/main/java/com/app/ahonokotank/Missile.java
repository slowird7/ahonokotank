package com.app.ahonokotank;

import java.util.ArrayList;
import java.util.List;

public class Missile extends MovingBody {


    public static List<Missile> missiles = new ArrayList<>();
    private static Battlefield theBattlefield = Battlefield.getInstance();

    public enum MISSILESTATE {
        FLY,
        EXPLODE,
        EXPLODED

    }

    private Tank owner;
    private final int SPEED = 1;
    public MISSILESTATE state;

    public Missile(Tank owner) {
        super(0, 1);
        this.owner = owner;
        this.towardDir = owner.towardDir;
        this.color = owner.color;
        this.type = 'M';
        this.state = MISSILESTATE.FLY;
        this.tx = owner.tx;
        this.ty = owner.ty;
//        switch (towardDir) {
//            case NORTH -> { tx = owner.tx; ty = owner.ty - 1; }
//            case EAST  -> { tx = owner.tx + 1; ty = owner.ty; }
//            case SOUTH -> { tx = owner.tx; ty = owner.ty + 1; }
//            case WEST  -> { tx = owner.tx - 1; ty = owner.ty; }
//            default    -> { throw new IllegalArgumentException("Unknown direction"); }
//        }
        missiles.add(this);
        theBattlefield.locate(ty, tx,  getType());
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
//        theBattlefield.locate(ty, tx, getType());
    }

}
