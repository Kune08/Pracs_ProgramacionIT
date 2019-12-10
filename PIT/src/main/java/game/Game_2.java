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
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
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
    
    //Contador pantallas
    int screenCounter = 0;
    int pantallaGuardada;
    int nivelesPasados = 1;

    // Timer
    Timer timer;
    int tick = 200;
    int auto = 0;
    
    // Game Variables
    
    ConcurrentLinkedQueue<IGameObject> gObjs = new ConcurrentLinkedQueue<IGameObject>();
    RidingHood_2 ridingHood = new RidingHood_2(new Position(0,0), 1, 1,gObjs);
    
    
	Bee bees = new Bee(new Position(-1,-1), 1, 1, gObjs); 
	//Bee bees = null; 
	Fly fly = new Fly(new Position(-2,-2), 1, 1, gObjs);
	//Fly fly = null;
	Spider spider = new Spider(new Position(-3,-3), 1, 1, gObjs);
	//Spider spider = null;
	Stone stone = new Stone(getRandomPosition(CANVAS_WIDTH/boxSize,CANVAS_WIDTH/boxSize));
    
    // Creamos el menú
    JMenuBar menuBar;
    JMenu menuVistas,menuCarga;
    JMenuItem itColores, itFiguras,itGeo, itCarga, itGuarda;

    
    public Game_2(int boxSize, int auto) throws Exception{
    	
       super("Game_2");
       this.boxSize=boxSize;
       this.auto=auto;
       
       // Game Initializations.
       gObjs.add(ridingHood);
       //gObjs.add(bees);
       //gObjs.add(fly);
       //gObjs.add(spider);
       gObjs.add(stone);
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
       itGeo = new JMenuItem("Geometria");
       menuCarga = new JMenu("Archivo");
       itGuarda = new JMenuItem("Guardar");
       itCarga = new JMenuItem("Cargar");
       
       itColores.addActionListener(
               new ActionListener(){  
                   public void actionPerformed(ActionEvent ae){
                	   GameCanvas.setVistas(1);
                       requestFocusInWindow();          
                   }
               }
           );
       
       itFiguras.addActionListener(
               new ActionListener(){  
                   public void actionPerformed(ActionEvent ae){
                	   GameCanvas.setVistas(2);
                       requestFocusInWindow();          
                   }
               }
           );
       itGeo.addActionListener(
               new ActionListener(){  
                   public void actionPerformed(ActionEvent ae){
                	   GameCanvas.setVistas(3);
                       requestFocusInWindow();          
                   }
               }
           );
       
       itGuarda.addActionListener(
               new ActionListener(){
                   
                   public void actionPerformed(ActionEvent ae){
                	   timer.stop();
                	   String path = "src/main/resources/games/guardado.txt";
                       System.out.println("Saving objects");
                       if (gObjs != null){
                           JSONObject jObjs [] = new JSONObject[gObjs.size()];
                           int i=0;
                           for (IGameObject igo : gObjs)
                           //for(int i = 0; i < jObjs.length; i++)
                           {
                               jObjs[i++] = ((IToJsonObject)igo).toJSONObject();
                           }
                           FileUtilities.writeJsonsToFile(jObjs, path);
                       }
                       requestFocusInWindow();
                       //System.exit(0);
                   }
               }
           );
       
       itCarga.addActionListener(
               new ActionListener(){  
                   public void actionPerformed(ActionEvent ae){
                	   System.out.println("Nivel CARGADO: " + screenCounter);
                	   String path = "src/main/resources/games/guardado.txt";
                       System.out.println("Loading objects");
                       JSONArray jArray = FileUtilities.readJsonsFromFile(path);
                       if (jArray != null){
                    	   gObjs = new ConcurrentLinkedQueue<IGameObject>();
                           for (int i = 0; i < jArray.length(); i++){
                               JSONObject jObj = jArray.getJSONObject(i);
                               String typeLabel = jObj.getString(TypeLabel);
                               if(GameObjectsJSONFactory.getGameObject(jObj) instanceof Bee) {
                            	  screenCounter=2;
                            	  bees = new Bee(new Position(0,0), GameObjectsJSONFactory.getGameObject(jObj).getValue(), GameObjectsJSONFactory.getGameObject(jObj).getLifes(), gObjs);
                              	  bees.setPosition(GameObjectsJSONFactory.getGameObject(jObj).getPosition());
                              	  gObjs.add(bees);
                                 }
                               else if(GameObjectsJSONFactory.getGameObject(jObj) instanceof Fly) {
                               	  screenCounter=1;
                            	  fly = new Fly(new Position(0,0), GameObjectsJSONFactory.getGameObject(jObj).getValue(), GameObjectsJSONFactory.getGameObject(jObj).getLifes(), gObjs);
                               	  fly.setPosition(GameObjectsJSONFactory.getGameObject(jObj).getPosition());
                               	  gObjs.add(fly);
                                }
                               else if(GameObjectsJSONFactory.getGameObject(jObj) instanceof Spider) {
                            	   screenCounter=3;	  
                            	   spider = new Spider(new Position(0,0), GameObjectsJSONFactory.getGameObject(jObj).getValue(), GameObjectsJSONFactory.getGameObject(jObj).getLifes(), gObjs);
                                   spider.setPosition(GameObjectsJSONFactory.getGameObject(jObj).getPosition());
                                   gObjs.add(spider);
                                }
                               
                               else if(GameObjectsJSONFactory.getGameObject(jObj) instanceof Stone) {
                            	  stone = (Stone) GameObjectsJSONFactory.getGameObject(jObj);
                             	  gObjs.add(stone);
                               }
                               
                               else if(GameObjectsJSONFactory.getGameObject(jObj) instanceof RidingHood_2) {
                            	  ridingHood = new RidingHood_2(new Position(0,0), GameObjectsJSONFactory.getGameObject(jObj).getValue(), GameObjectsJSONFactory.getGameObject(jObj).getLifes(), gObjs);
                            	  ridingHood.setPosition(GameObjectsJSONFactory.getGameObject(jObj).getPosition());
                            	  gObjs.add(ridingHood);
                               }
                               else {
                               	gObjs.add(GameObjectsJSONFactory.getGameObject(jObj));
                               }
                              
                           }
                           printGameItems(); 
                           canvas.drawObjects(gObjs);
                           //timer.start();
                       }
                       
                       requestFocusInWindow();          
                   }
               }
           );
       
       menuVistas.add(itColores);
       menuVistas.add(itFiguras);
       menuVistas.add(itGeo);
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
        if(auto==0) {
        	ridingHood.moveToNextPosition();
        }
        else if(auto==1) {
        	ridingHood.moveToNextPositionAuto();
        }
        else {ridingHood.moveToNextPosition();}
        
        //Si están en la pantalla que toca se habilitará el movimiento del "bicho"
        if(screenCounter==1) {
        	fly.moveToNextPosition();
        }
        if(screenCounter==2) {
        	bees.moveToNextPosition();
        }
        if(screenCounter==3) {
        	spider.moveToNextPosition();
        }
        
        
        // Check if Caperucita is in board limits
        setInLimits();
        setInLimitsFly();
        noPuedesPasar();
       //setInLimitsBees();
        
        // Logic to change to a new screen.
        if (processCell() <= 3){
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
        Position bePos = bees.getPosition();
        Position spiPos = spider.getPosition();
        Position flyPos = fly.getPosition();
        for (IGameObject gObj: gObjs){
            if(gObj != ridingHood && !(gObj instanceof Bee) && !(gObj instanceof Spider) && !(gObj instanceof Fly) && !(gObj instanceof Stone)  && rhPos.isEqual(gObj.getPosition())){
                int v = ridingHood.getValue() + gObj.getValue();
                ridingHood.setValue(v);
                gObjs.remove(gObj);
            }
            else if(!(gObj instanceof Bee) && !(gObj instanceof Spider) && !(gObj instanceof Fly) && !(gObj instanceof Stone) && bePos.isEqual(gObj.getPosition())){
            	if(bePos.isEqual(rhPos)) {
            		ridingHood.setValue(ridingHood.getValue()-5);
            		System.out.println("Has chocado contra una abeja. -5 puntos");
            	}
            	else {
                gObjs.remove(gObj);
                }
            }
            else if((gObj instanceof Spider) && !(gObj instanceof Fly) && !(gObj instanceof Stone) && spiPos.isEqual(gObj.getPosition())){
            	if(spiPos.isEqual(rhPos)) {
            		ridingHood.setLifes(ridingHood.getLifes()-1);
            		System.out.println("Una araña te ha pillado. -1 vida");
            		gObjs.remove(spider);
            	}
            }
            else if((gObj instanceof Fly) && flyPos.isEqual(gObj.getPosition())){
            	if(flyPos.isEqual(rhPos)) {
            		ridingHood.setValue(ridingHood.getValue()-10);
            		System.out.println("Una mosca te ha pillado. -10 puntos");
            		gObjs.remove(fly);
            	}
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
    

    //JAJAJAJAJA, has encontrado una piedra y no te dejo pasar.
    private void noPuedesPasar() {
    	if(ridingHood.getPosition().getX() == stone.getPosition().getX() && ridingHood.getPosition().getY() == stone.getPosition().getY() ) {
            if(lastKey==DOWN_KEY) {
                ridingHood.position.y=stone.getPosition().getY()-1;
            }
            else if(lastKey==UP_KEY) {
                ridingHood.position.y=stone.getPosition().getY()+1; 
            }
            if(lastKey==RIGTH_KEY) {
                ridingHood.position.x=stone.getPosition().getX()-1;
            }
            else if(lastKey==LEFT_KEY) {
                ridingHood.position.x=stone.getPosition().getX()+1;
            }
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
    Comprueba que la mosca no se sale del tablero.
    En caso contrario corrige su posición
    */
	private void setInLimitsFly(){
	        
	        int lastBox = (CANVAS_WIDTH/boxSize) - 1;
	        
	        if (fly.getPosition().getX() < 0){
	        	fly.position.x = 0;
	        }
	        else if (fly.getPosition().getX() > lastBox ){
	        	fly.position.x = lastBox;
	        }
	        
	        if (fly.getPosition().getY() < 0){
	        	fly.position.y = 0;
	        }
	        else if (fly.getPosition().getY() > lastBox){
	        	fly.position.y = lastBox;
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
              nivelesPasados++;
              break;
            case 1:

                String path = "src/main/resources/games/nivel1.txt";
                gObjs.remove(stone);
            	gObjs.remove(bees);
            	bees.setPosition(new Position(-1,-1));
            	gObjs.remove(spider);
            	spider.setPosition(new Position(-3,-3));
            	System.out.println("------- NUEVO NIVEL 1 ------- ");
            	System.out.println("Contador de pantallas: " + screenCounter);
            	System.out.println("Abeja:" + bees.getPosition());
            	System.out.println("Araña:" + spider.getPosition());
            	System.out.println("Loading objects...");
                JSONArray jArray = FileUtilities.readJsonsFromFile(path);
                if (jArray != null){
                	for (int i = 0; i < jArray.length(); i++){
                        JSONObject jObj = jArray.getJSONObject(i);
                        String typeLabel = jObj.getString(TypeLabel);
                        if(GameObjectsJSONFactory.getGameObject(jObj) instanceof Fly) {
                        	fly = new Fly(new Position(0,0), GameObjectsJSONFactory.getGameObject(jObj).getValue(), GameObjectsJSONFactory.getGameObject(jObj).getLifes(), gObjs);
                        	fly.setPosition(getRandomPosition(CANVAS_WIDTH/boxSize,CANVAS_WIDTH/boxSize));
                        	System.out.println("Mosca generada:" + fly.getPosition());
                        	gObjs.add(fly);
                         }
                        else if(GameObjectsJSONFactory.getGameObject(jObj) instanceof Stone) {
                        	stone = new Stone(new Position(0,0), GameObjectsJSONFactory.getGameObject(jObj).getValue(), GameObjectsJSONFactory.getGameObject(jObj).getLifes());
                        	stone.setPosition(getRandomPosition(CANVAS_WIDTH/boxSize,CANVAS_WIDTH/boxSize));
                        	gObjs.add(stone);
                         }
                        else {
                        	gObjs.add(GameObjectsJSONFactory.getGameObject(jObj));
                        }
                    }                   
                }
                for(int i = 0 ; i<nivelesPasados ; i++) {
                	gObjs.add(new Blossom(getRandomPosition(CANVAS_WIDTH/boxSize,CANVAS_WIDTH/boxSize), 4, (int)(Math.random()*20+1)));
                }
                System.out.println("------- NIVEL 1 CARGADO | EVENTOS ------- ");
                nivelesPasados++;
                break;
                
            case 2:
                String path1 = "src/main/resources/games/nivel2.txt";
                gObjs.remove(stone);
            	gObjs.remove(fly);
            	fly.setPosition(new Position(-2,-2));
            	gObjs.remove(spider);
            	spider.setPosition(new Position(-3,-3));
            	System.out.println("------- NUEVO NIVEL 2 ------- ");
            	System.out.println("Contador de pantallas: " + screenCounter);
            	System.out.println("Mosca:" + fly.getPosition());
            	System.out.println("Araña:" + spider.getPosition());
                System.out.println("Loading objects...");
                JSONArray jArray1 = FileUtilities.readJsonsFromFile(path1);
                if (jArray1 != null){
                	for (int i = 0; i < jArray1.length(); i++){
                        JSONObject jObj = jArray1.getJSONObject(i);
                        String typeLabel = jObj.getString(TypeLabel);
                        if(GameObjectsJSONFactory.getGameObject(jObj) instanceof Bee) {
                        	bees = new Bee(new Position(0,0), GameObjectsJSONFactory.getGameObject(jObj).getValue(), GameObjectsJSONFactory.getGameObject(jObj).getLifes(), gObjs);
                        	bees.setPosition(getRandomPosition(CANVAS_WIDTH/boxSize,CANVAS_WIDTH/boxSize));
                        	System.out.println("Bee generada:" + bees.getPosition());
                        	gObjs.add(bees);
                         }
                        else if(GameObjectsJSONFactory.getGameObject(jObj) instanceof Stone) {
                        	stone = new Stone(new Position(0,0), GameObjectsJSONFactory.getGameObject(jObj).getValue(), GameObjectsJSONFactory.getGameObject(jObj).getLifes());
                        	stone.setPosition(getRandomPosition(CANVAS_WIDTH/boxSize,CANVAS_WIDTH/boxSize));
                        	gObjs.add(stone);
                         }
                        else {
                        	gObjs.add(GameObjectsJSONFactory.getGameObject(jObj));
                        }
                    }                            
                }
                for(int i = 0 ; i<nivelesPasados ; i++) {
                	gObjs.add(new Blossom(getRandomPosition(CANVAS_WIDTH/boxSize,CANVAS_WIDTH/boxSize), 4, (int)(Math.random()*20+1)));
                }
                System.out.println("------- NIVEL 2 CARGADO | EVENTOS ------- ");
                nivelesPasados++;
                break;
            case 3:
                String path2 = "src/main/resources/games/nivel3.txt";
                gObjs.remove(stone);
            	gObjs.remove(bees);
            	bees.setPosition(new Position(-1,-1));
            	gObjs.remove(fly);
            	fly.setPosition(new Position(-2,-2));
            	System.out.println("------- NUEVO NIVEL 3 ------- ");
            	System.out.println("Contador de pantallas: " + screenCounter);
            	System.out.println("Abeja:" + bees.getPosition());
            	System.out.println("Mosca:" + fly.getPosition());
                System.out.println("Loading objects...");
                JSONArray jArray2 = FileUtilities.readJsonsFromFile(path2);
                if (jArray2 != null){
                	for (int i = 0; i < jArray2.length(); i++){
                        JSONObject jObj = jArray2.getJSONObject(i);
                        String typeLabel = jObj.getString(TypeLabel);
                        if(GameObjectsJSONFactory.getGameObject(jObj) instanceof Spider) {
                        	spider = new Spider(new Position(0,0), GameObjectsJSONFactory.getGameObject(jObj).getValue(), GameObjectsJSONFactory.getGameObject(jObj).getLifes(), gObjs);
                        	spider.setPosition(getRandomPosition(CANVAS_WIDTH/boxSize,CANVAS_WIDTH/boxSize));
                        	System.out.println("Araña generada:" + spider.getPosition());
                        	gObjs.add(spider);
                         }
                        else if(GameObjectsJSONFactory.getGameObject(jObj) instanceof Stone) {
                        	stone = new Stone(new Position(0,0), GameObjectsJSONFactory.getGameObject(jObj).getValue(), GameObjectsJSONFactory.getGameObject(jObj).getLifes());
                        	stone.setPosition(getRandomPosition(CANVAS_WIDTH/boxSize,CANVAS_WIDTH/boxSize));
                        	gObjs.add(stone);
                         }
                        else {
                        	gObjs.add(GameObjectsJSONFactory.getGameObject(jObj));
                        } 
                    }                            
                }
                for(int i = 0 ; i<nivelesPasados ; i++) {
                	gObjs.add(new Blossom(getRandomPosition(CANVAS_WIDTH/boxSize,CANVAS_WIDTH/boxSize), 4, (int)(Math.random()*20+1)));
                }
                System.out.println("------- NIVEL 3 CARGADO | EVENTOS ------- ");
                nivelesPasados++;
                break;
            default:
              screenCounter=0;
        }        
    }
    
    private void printGameItems(){
        System.out.println("Objects Added to Game are: ");
        for (IGameObject obj: gObjs){
            System.out.println(((IToJsonObject)obj).toJSONObject());
        }
    }
    
    public Position getRandomPosition(int mX, int mY){
        int x = (int)(mX * Math.random());
        int y = (int)(mY * Math.random());
        return new Position(x, y);
    }
    
    public static void main(String [] args) throws Exception{
       Game_2 gui = new Game_2(40,0);
    }
}
