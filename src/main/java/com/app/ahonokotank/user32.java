/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.ahonokotank;

import com.sun.jna.Library;
import com.sun.jna.Native;

interface User32 extends Library {

    // インスタンス生成
    User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class);
    int GetKeyState(short key);

}
