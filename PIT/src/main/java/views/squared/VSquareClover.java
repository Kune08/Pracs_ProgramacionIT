/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.squared;

import game.IGameObject;
import game.Position;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import views.AbstractGameView;

/**
 *
 * @author juanangel
 */
public class VSquareClover extends AbstractGameView {

    Color myColor = Color.green;
        
    public VSquareClover(IGameObject mObject, int l) throws Exception{
        super(mObject, l);
    }
        
    
    public void draw(Graphics g) {
        
        Graphics2D g2 = (Graphics2D) g;
        
        Position coord = gObj.getPosition();
        
        
        Color c = g2.getColor();
        g2.setColor(myColor);    
        g2.setStroke(new BasicStroke(1));
                
        g2.fillRect(
                length * coord.getX() + (int)((1/4.0) * length),
                length * coord.getY() + (int)((1/4.0) * length),      
                (int) ((1/2.0) * length),
                (int) ((1/2.0) * length)
        );
        g2.drawRect(
                length * coord.getX()+1, length * coord.getY()+1,      
                (int) ((1/2.0) * length) - 2,
                (int) ((1/2.0) * length) - 2
        );
        g2.drawRect(
                length * coord.getX() + (int) ((1/2.0) * length) + 1, length * coord.getY() + 1,      
                (int) ((1/2.0) * length)-2,
                (int) ((1/2.0) * length)-2);
        g2.drawRect(
                length * coord.getX()+1, length * coord.getY() + (int) ((1/2.0) * length)+1,      
                (int) ((1/2.0) * length)-2,
                (int) ((1/2.0) * length)-2
        );
        g2.drawRect(
                length * coord.getX() + (int) ((1/2.0) * length) + 1, 
                length * coord.getY() + (int) ((1/2.0) * length) + 1,      
                (int) ((1/2.0) * length) - 2,
                (int) ((1/2.0) * length) - 2
        );
                
        g2.setColor(c);
    }
    
}
