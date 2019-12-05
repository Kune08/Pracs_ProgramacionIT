/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.geo;

import game.Bee;
import game.Bee;
import game.Blossom;
import game.Fly;
import game.IGameObject;
import game.RidingHood_1;
import game.RidingHood_2;
import game.Spider;
import views.IAWTGameView;
import views.IViewFactory;

/**
 *
 * @author juanangel
 */
public class GeoFactory implements IViewFactory {
    
    public IAWTGameView getView(IGameObject gObj, int length) throws Exception {
        
                IAWTGameView view = null;
        
        
        if (gObj instanceof Fly){
           view = new VGeo(gObj, "src/main/resources/images/geo/fly.png", length); 
        }
        else if (gObj instanceof Bee){
           view = new VGeo(gObj, "src/main/resources/images/geo/bee.png", length); 
        }  
        else if (gObj instanceof RidingHood_2){
           view = new VGeo(gObj, "src/main/resources/images/geo/caperucita.png", length); 
        } 
        else if (gObj instanceof Spider){
           view = new VGeo(gObj, "src/main/resources/images/geo/spider.png", length); 
        } 
        else if (gObj instanceof Blossom){
           if (gObj.getValue() < 10){
                view = new VGeo(gObj, "src/main/resources/images/geo/dandelion2.png", length); 
           }
           else {
                view = new VGeo(gObj, "src/main/resources/images/geo/clover.png",  length); 
           }
        }
            
        return view;
    }
    
}
