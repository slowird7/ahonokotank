package com.app.ahonokotank;

import javafx.scene.input.KeyEvent;

public class TankController {

    //キーフラグ
    public boolean isLeft;
    public boolean isRight;
    public boolean isUp;
    public boolean isDown;

    //キーを押した時のイベント
    private void keyPressed(KeyEvent e) {
        //上下左右キーを押した時フラグをONにする。
        switch(e.getCode()) {
            case LEFT:
                isLeft = true;
                break;
            case RIGHT:
                isRight = true;
                break;
            case UP:
                isUp = true;
                break;
            case DOWN:
                isDown = true;
                break;
            default:
                break;
        }
    }

    //キーを離した時のイベント
    private void keyReleased(KeyEvent e) {
        //上下左右キーを離した時フラグをOFFにする。
        switch(e.getCode()) {
            case LEFT:
                isLeft = false;
                break;
            case RIGHT:
                isRight = false;
                break;
            case UP:
                isUp = false;
                break;
            case DOWN:
                isDown = false;
                break;
            default:
                break;
        }
    }
}
