package game;

import static common.IToJsonObject.TypeLabel;

import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.*;

import org.json.JSONArray;
import org.json.JSONObject;

import common.FileUtilities;

public class Inicio extends JFrame {
	
	private JButton peque, medi, grande;
	
	public Inicio(){
		super("Selecciona el tamaño del tablero");
		Container container = getContentPane();
		container.setLayout(new FlowLayout());
		peque = new JButton("11x11");
		container.add(peque);
		medi = new JButton("14x14");
		container.add( medi );
		grande = new JButton();
		grande.setText("18x18");
		container.add(grande);
		setSize(500,75);
		setVisible(true);
		
		peque.addActionListener(
	            new ActionListener(){  
					public void actionPerformed(ActionEvent ae) {
						try {
							Game_2 gui = new Game_2(480,0);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
	            }
	     );
		medi.addActionListener(
	            new ActionListener(){  
					public void actionPerformed(ActionEvent ae) {
						try {
							Game_2 gui = new Game_2(600,0);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
	            }
	     );
		grande.addActionListener(
	            new ActionListener(){  
					public void actionPerformed(ActionEvent ae) {
						try {
							Game_2 gui = new Game_2(480/12,0);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
	            }
	     );
	}

	
public static void main( String args[] ){
	Inicio inicio = new Inicio();
	inicio.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
	}
}
