/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import common.FileUtilities;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author juanangel
 */
public class RidingHood_2 extends AbstractGameObject {
    
    int dX, dY;

    RidingHood_2(Position position) {
        super(position);    
    }
    
    RidingHood_2(Position position, int value, int life ) {
        super(position, value, life);    
    }
    
    RidingHood_2(JSONObject jObj) {
        super(jObj);    
    } 
    
    /**
     * Cada vez que se invoca se dirige hacia el siguiente blossom, 
     * moviéndose una posición en x y otra en y.
     * Cuando ha pasado por todos los blossoms avanza en diagonal 
     * hacia abajo a las derecha.
     * @return posición en la que se encuentra después de ejecutarse el
     * método.
     */
    @Override
    public Position moveToNextPosition(){
        this.position.x += dX;
        this.position.y += dY;       
        return position;       
    }  
    
    public void moveRigth(){
        dY = 0; dX = 1;
    }
    
    public void moveLeft(){
        dY = 0; dX = -1;
    }
    
    public void moveUp(){
        dY = -1; dX = 0;
    }
    
    public void moveDown(){
        dY = 1; dX = 0;
    }
}
