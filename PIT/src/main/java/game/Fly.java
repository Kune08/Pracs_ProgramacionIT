/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.json.JSONObject;


/**
 *
 * @author juanangel
 */
public class Fly extends AbstractGameObject{
    
    ConcurrentLinkedQueue<IGameObject> gObjs = new ConcurrentLinkedQueue<IGameObject>();
    
    Fly(Position position) {
        super(position);    
    }
    
    Fly(Position position, int value, int life ) {
        super(position, value, life);    
    }
    
    Fly(Position position, int value, int life, ConcurrentLinkedQueue<IGameObject> gObjs) {
        super(position, value, life);    
        this.gObjs = gObjs;
    }
    
    Fly(JSONObject jObj) {
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
    
    public Position moveToNextPosition(){
        
        position = getRandomPosition(10,10);
       
        return position;       
    }  
    
    public Position getRandomPosition(int mX, int mY){
        int x = (int)(mX * Math.random());
        int y = (int)(mY * Math.random());
        return new Position(x, y);
    }
    
   /* private ArrayList<RidingHood_2> getBlossoms(){
        ArrayList<RidingHood_2> blossoms = new ArrayList<RidingHood_2>();
        for (IGameObject obj: gObjs){
            if (obj instanceof RidingHood_2){
                blossoms.add((RidingHood_2) obj);
            }
        }
        return blossoms;
    }
    
    private void approachTo(Position p){
        if (position.x != p.x){
            position.x = position.x > p.x? position.x-1:position.x+1;
        }
        if (position.y != p.y){
            position.y = position.y > p.y? position.y-1:position.y+1;
        }
    } */
    
    
    public void printFly(){
        System.out.println(this.toJSONObject());
    }
 
}
