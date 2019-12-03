/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import common.FileUtilities;
import common.IToJsonObject;

import static common.IToJsonObject.TypeLabel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author juanangel
 */
public class Game_2 extends JFrame implements KeyListener, ActionListener {

    // KeyBoard
    public static final int UP_KEY    = 38;
    public static final int DOWN_KEY  = 40;
    public static final int RIGTH_KEY = 39;
    public static final int LEFT_KEY  = 37;
    public static final int SPACE_KEY = 32;
    int lastKey = DOWN_KEY;
    
    // Game Panel and 
    public static int CANVAS_WIDTH = 480;    
    int boxSize = 40;
    int row, col;
    GameCanvas canvas;
    JPanel canvasFrame;
    JLabel dataLabel;
    
    // Timer
    Timer timer;
    int tick = 200;
    
    // Game Variables
    ConcurrentLinkedQueue<IGameObject> gObjs = new ConcurrentLinkedQueue<IGameObject>();
    RidingHood_2 ridingHood = new RidingHood_2(new Position(0,0), 1, 1);
    int screenCounter = 0;
    
    // Creamos el menú
    JMenuBar menuBar;
    JMenu menuVistas,menuCarga;
    JMenuItem itColores, itFiguras, itCarga, itGuarda;

    
    public Game_2(int CANVAS_WIDTH) throws Exception{
    	
       super("Game_2");
       this.CANVAS_WIDTH=CANVAS_WIDTH;
       
       // Game Initializations.
       gObjs.add(ridingHood);
       loadNewBoard(0);
  
       // Window initializations.
       dataLabel = new JLabel(ridingHood.toString());
       dataLabel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)); 
       dataLabel.setPreferredSize(new Dimension(120,40));
       dataLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
       canvas = new GameCanvas(CANVAS_WIDTH, boxSize);
       canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_WIDTH));
       canvas.setBorder(BorderFactory.createLineBorder(Color.blue));
       
       canvasFrame = new JPanel();
       canvasFrame.setPreferredSize(new Dimension(CANVAS_WIDTH + 40, CANVAS_WIDTH + 40));
       canvasFrame.add(canvas);
       getContentPane().add(canvasFrame);
       getContentPane().add(dataLabel, BorderLayout.SOUTH);
       
       menuBar = new JMenuBar();
       menuVistas = new JMenu("Vistas");
       itColores = new JMenuItem("Colores");
       itFiguras = new JMenuItem("Figuras");
       menuCarga = new JMenu("Archivo");
       itGuarda = new JMenuItem("Guardar");
       itCarga = new JMenuItem("Cargar");
       
       itGuarda.addActionListener(
               new ActionListener(){
                   
                   public void actionPerformed(ActionEvent ae){
                	   String path = "src/main/resources/games/guardado.txt";
                       System.out.println("Saving objects");
                       if (gObjs != null){
                           JSONObject jObjs [] = new JSONObject[gObjs.size()];
                           for(int i = 0; i < jObjs.length; i++){
                               jObjs[i] = ((IToJsonObject)gObjs.poll()).toJSONObject();
                           }
                           FileUtilities.writeJsonsToFile(jObjs, path);
                       }
                       requestFocusInWindow();
                   }
               }
           );
       
       itCarga.addActionListener(
               new ActionListener(){  
                   public void actionPerformed(ActionEvent ae){
                	   String path = "src/main/resources/games/guardado.txt";
                       System.out.println("Loading objects");
                       JSONArray jArray = FileUtilities.readJsonsFromFile(path);
                       if (jArray != null){
                    	   gObjs = new ConcurrentLinkedQueue<IGameObject>();
                           for (int i = 0; i < jArray.length(); i++){
                               JSONObject jObj = jArray.getJSONObject(i);
                               String typeLabel = jObj.getString(TypeLabel);
                               gObjs.add(GameObjectsJSONFactory.getGameObject(jObj)); //PREGUNTAR
                           }
                           printGameItems(); 
                           canvas.drawObjects(gObjs);
                       }
                       requestFocusInWindow();          
                   }
               }
           );
       
       menuVistas.add(itColores);
       menuVistas.add(itFiguras);
       menuCarga.add(itCarga);
       menuCarga.add(itGuarda);
       menuBar.add(menuVistas);
       menuBar.add(menuCarga);
       menuBar.setBorder(BorderFactory.createLineBorder(Color.blue));
       setJMenuBar(menuBar); 
       
       setSize (CANVAS_WIDTH + 40, CANVAS_WIDTH + 110);
       setResizable(false);
       setVisible(true);         
       setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    
       
       addKeyListener(this);
       this.setFocusable(true);
       timer = new Timer(tick, this);
    }

    
    public void keyTyped(KeyEvent ke) {
    }

    // Version 1
    
    public void keyPressed(KeyEvent ke) {
        lastKey = ke.getKeyCode(); 
        if (lastKey == SPACE_KEY){
            if (timer.isRunning()){
                    timer.stop();
                }
                else{
                    timer.start();
                }
        }
    }

    
    public void keyReleased(KeyEvent ke) {
    }

    /**
     * Se invoca en cada tick de reloj
     * @param ae 
     */  
    
    public void actionPerformed(ActionEvent ae) {
       
        // Actions on Caperucita
        setDirection(lastKey);
        
        // Moving Caperucita
        ridingHood.moveToNextPosition();
        
        // Check if Caperucita is in board limits
        setInLimits();
        
        // Logic to change to a new screen.
        if (processCell() == 1){
            screenCounter++;
            ridingHood.incLifes(1);
            loadNewBoard(screenCounter);
        }
        
        // Updating graphics and labels
        dataLabel.setText(ridingHood.toString());
        canvas.drawObjects(gObjs);
    }

    /*
    Procesa la celda en la que se encuentra caperucita.
    Si Caperucita está sobre un blossom añade su valor al de Caperucita
    y lo elimina del tablero.
    Devuelve el número de blossoms que hay en el tablero.
    */    
    private int processCell(){
        Position rhPos = ridingHood.getPosition();
        for (IGameObject gObj: gObjs){
            if(gObj != ridingHood && rhPos.isEqual(gObj.getPosition())){
                int v = ridingHood.getValue() + gObj.getValue();
                ridingHood.setValue(v);
                gObjs.remove(gObj);
            }
        }
        return gObjs.size();
    }
    
    /*
    Fija la dirección de caperucita.
    Caperucita se moverá en esa dirección cuando se invoque
    su método moveToNextPosition.
    */    
    private void setDirection(int lastKey){
        switch (lastKey) {
            case UP_KEY:  
                ridingHood.moveUp();
                break;
            case DOWN_KEY:
                ridingHood.moveDown();                    
                break;
            case RIGTH_KEY:
                ridingHood.moveRigth();
                break;
            case LEFT_KEY:
                ridingHood.moveLeft();
                break; 
        }
    }
    
    /*
    Comprueba que Caperucita no se sale del tablero.
    En caso contrario corrige su posición
    */
    private void setInLimits(){
        
        int lastBox = (CANVAS_WIDTH/boxSize) - 1;
        
        if (ridingHood.getPosition().getX() < 0){
            ridingHood.position.x = 0;
        }
        else if ( ridingHood.getPosition().getX() > lastBox ){
            ridingHood.position.x = lastBox;
        }
        
        if (ridingHood.getPosition().getY() < 0){
            ridingHood.position.y = 0;
        }
        else if (ridingHood.getPosition().getY() > lastBox){
            ridingHood.position.y = lastBox;
        } 
    }
    
    /*
    Carga un nuevo tablero
    */
    private void loadNewBoard(int counter){
        switch(counter){
            case 0: 
              gObjs.add(new Blossom(new Position(2,2), 10, 10));
              gObjs.add(new Blossom(new Position(2,8), 4, 10));
              gObjs.add(new Blossom(new Position(8,8), 10, 10));
              gObjs.add(new Blossom(new Position(8,2), 4, 10));
              break;
            case 1:
                String path = "src/main/resources/games/nivel1.txt";
                System.out.println("Loading objects");
                JSONArray jArray = FileUtilities.readJsonsFromFile(path);
                if (jArray != null){
                    for (int i = 0; i < jArray.length(); i++){
                        JSONObject jObj = jArray.getJSONObject(i);
                        String typeLabel = jObj.getString(TypeLabel);
                        gObjs.add(GameObjectsJSONFactory.getGameObject(jObj));
                    }                       
                }
                break;
                
            case 2:
                String path1 = "src/main/resources/games/nivel2.txt";
                System.out.println("Loading objects");
                JSONArray jArray1 = FileUtilities.readJsonsFromFile(path1);
                if (jArray1 != null){
                    for (int i = 0; i < jArray1.length(); i++){
                        JSONObject jObj = jArray1.getJSONObject(i);
                        String typeLabel = jObj.getString(TypeLabel);
                        gObjs.add(GameObjectsJSONFactory.getGameObject(jObj));
                    }                       
                }
                break;
            default:
            	screenCounter=0;
              gObjs.add(new Blossom(new Position(2,2), 10, 10));
              gObjs.add(new Blossom(new Position(2,8), 4, 10));
              gObjs.add(new Blossom(new Position(8,8), 10, 10));
              gObjs.add(new Blossom(new Position(8,2), 4, 10));  
        }        
    }
    
    private void printGameItems(){
        System.out.println("Objects Added to Game are: ");
        for (IGameObject obj: gObjs){
            System.out.println( ( (IToJsonObject) obj).toJSONObject());
        }
    }
    
    
    
    public static void main(String [] args) throws Exception{
       Game_2 gui = new Game_2(480);
    }
}
