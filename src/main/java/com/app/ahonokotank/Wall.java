package com.app.ahonokotank;

public class Wall extends MovingBody {
    public static Wall theWall = new Wall();

    private Wall() {
        super(0, 1, 'W');
    }
}
