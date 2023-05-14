package com.app.ahonokotank;

public class Space extends MovingBody {

    public static Space theSpace = new Space();
    private Space() {
        super(0, 1, 'S');
    }

}
