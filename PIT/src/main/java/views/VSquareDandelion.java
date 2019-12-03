/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views;

import game.IGameObject;
import game.Position;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 *
 * @author juanangel
 */
public class VSquareDandelion extends AbstractGameView {
    
    Color mC = Color.pink;

    public VSquareDandelion(IGameObject mObject, int l) throws Exception{
        super(mObject, l);
    }
    
    
    public void draw(Graphics g) {
        
        Graphics2D g2 = (Graphics2D) g;
        
        Position coord = gObj.getPosition();
        
        Color c = g2.getColor();
        g2.setStroke(new BasicStroke(4));
        
        g2.setColor(Color.yellow);    
        g.drawRect(length * coord.getX() + length/4, length * coord.getY() + length/4, 
                   length/2, length/2 );
        g2.setColor(mC);    
                
        g.drawLine(length * coord.getX(), length * coord.getY(), 
                   length * coord.getX() + length, length * coord.getY() + length );
        g.drawLine(length * coord.getX() + length, length * coord.getY(), 
                   length * coord.getX(), length * coord.getY() + length );
        g.drawLine(length * coord.getX() + length/2, length * coord.getY(), 
                   length * coord.getX()+length/2, length * coord.getY() + length );
        g.drawLine(length * coord.getX(), length * coord.getY()+length/2, 
                   length * coord.getX()+length, length * coord.getY()+length/2 );
        
        g2.setColor(c);
    }   
}