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

    public void forward() {
        Tank newPos = new Tank(this);
        
    }
    
    public void backward() {
    }
    
    public void turnLeft() {
    }
    
    public void turnRight() {
    }
    
    void startController() {
        timer = new Timer(false);
        TimerTask task = new TimerTask() {
            long beforeTime = System.currentTimeMillis();
            int cnt = 0;

            @Override
            public void run() {
                int x = random.nextInt(100);
                if(x < 40) {
                    forward();
                } else if (x < 70) {
                    backward();
                } else if (x < 85) {
                    turnLeft();
                } else {
                    turnRight();
                }
            }
        };

        timer.scheduleAtFixedRate(task, 0, 500);
    }

}
