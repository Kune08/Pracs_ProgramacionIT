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
public class Game extends JFrame implements KeyListener, ActionListener {

    // KeyBoard
    public static final int UP_KEY    = 38;
    public static final int DOWN_KEY  = 40;
    public static final int RIGTH_KEY = 39;
    public static final int LEFT_KEY  = 37;
    public static final int SPACE_KEY = 32;
    int lastKey = DOWN_KEY;
    
    // Game Panel
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
    
    // Para que CAPERUCITA funcione de forma automatica.
    int auto = 0;
    
    // Game Variables
    
    ConcurrentLinkedQueue<IGameObject> gObjs = new ConcurrentLinkedQueue<IGameObject>();
    
    // A los objetos que se le pasan gObjs es debido a que tienen que detectar 
    // al resto de objetos para realizar una acción
    RidingHood ridingHood = new RidingHood(new Position(0,0), 1, 1, gObjs);
	Bee bees = new Bee(new Position(-1,-1), 1, 1, gObjs); 
	Fly fly = new Fly(new Position(-2,-2), 1, 1, gObjs);
	Spider spider = new Spider(new Position(-3,-3), 1, 1, gObjs);

	// Generamos una piedra de forma aleatoria
	Stone stone = new Stone(getRandomPosition(CANVAS_WIDTH/boxSize,CANVAS_WIDTH/boxSize));
    
    // Creamos el menú
    JMenuBar menuBar;
    JMenu menuVistas,menuCarga;
    JMenuItem itColores, itFiguras,itGeo, itCarga, itGuarda;

    // Pasamos por parametro el "boxSize" (con el fin de cambiar el tamaño de la cuadricula).
    // "auto" es un entero que varia entre 0 y 1 e indica si queremos que caperucita
    // funcione de forma automática.
    public Game(int boxSize, int auto) throws Exception{
    	super("Caperucita Roja | Andrés Ruz Nieto");
    	this.boxSize=boxSize;
    	this.auto=auto;
       
       // Game Initializations.
    	gObjs.add(ridingHood);
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
					System.out.println("Guardando objetos...");
					if (gObjs != null){
						JSONObject jObjs [] = new JSONObject[gObjs.size()];
						int i=0;
						for (IGameObject igo : gObjs){
							jObjs[i++] = ((IToJsonObject)igo).toJSONObject();
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
				   System.out.println("Nivel CARGADO: " + screenCounter);
				   String path = "src/main/resources/games/guardado.txt";
				   System.out.println("Cargando objetos...");
				   JSONArray jArray = FileUtilities.readJsonsFromFile(path);
				   if (jArray != null){
					   gObjs = new ConcurrentLinkedQueue<IGameObject>();
					   for (int i = 0; i < jArray.length(); i++){
						   JSONObject jObj = jArray.getJSONObject(i);
						   String typeLabel = jObj.getString(TypeLabel);
						   // Si el objeto es una instancia de Bee
						   if(GameObjectsJSONFactory.getGameObject(jObj) instanceof Bee) {
							   // Colocamos screenCounter a 2 para que la siguiente pantalla sea la 3 (ya que 
							   // tenemos que seguir el orden de Fly, Bee, Spider)
							   screenCounter=2;
							   // Generamos una Bee en 0,0 con los valores que obtenemos del JSON
							   bees = new Bee(new Position(0,0), GameObjectsJSONFactory.getGameObject(jObj).getValue(), GameObjectsJSONFactory.getGameObject(jObj).getLifes(), gObjs);
							   // Modificamos su posición moviéndola a la posición que se nos da el JSON
							   bees.setPosition(GameObjectsJSONFactory.getGameObject(jObj).getPosition());
							   // Añadimos la abeja a la lista de objetos.
							   gObjs.add(bees);
						   }
						   // Si el objeto es una instancia de Fly, ver explicación en Bee
						   else if(GameObjectsJSONFactory.getGameObject(jObj) instanceof Fly) {
							   screenCounter=1;
							   fly = new Fly(new Position(0,0), GameObjectsJSONFactory.getGameObject(jObj).getValue(), GameObjectsJSONFactory.getGameObject(jObj).getLifes(), gObjs);
							   fly.setPosition(GameObjectsJSONFactory.getGameObject(jObj).getPosition());
							   gObjs.add(fly);
						   }
						   // Si el objeto es una instancia de Spider, ver explicación en Bee
						   else if(GameObjectsJSONFactory.getGameObject(jObj) instanceof Spider) {
							   screenCounter=3;	  
							   spider = new Spider(new Position(0,0), GameObjectsJSONFactory.getGameObject(jObj).getValue(), GameObjectsJSONFactory.getGameObject(jObj).getLifes(), gObjs);
							   spider.setPosition(GameObjectsJSONFactory.getGameObject(jObj).getPosition());
							   gObjs.add(spider);
						   }
						   // Si el objeto es una instancia de Stone, ver explicación en Bee
						   else if(GameObjectsJSONFactory.getGameObject(jObj) instanceof Stone) {
							   stone = (Stone) GameObjectsJSONFactory.getGameObject(jObj);
							   gObjs.add(stone);
						   }    
						   // Si el objeto es una instancia de RidingHood_2, ver explicación en Bee
						   else if(GameObjectsJSONFactory.getGameObject(jObj) instanceof RidingHood) {
							   ridingHood = new RidingHood(new Position(0,0), GameObjectsJSONFactory.getGameObject(jObj).getValue(), GameObjectsJSONFactory.getGameObject(jObj).getLifes(), gObjs);
							   ridingHood.setPosition(GameObjectsJSONFactory.getGameObject(jObj).getPosition());
							   gObjs.add(ridingHood);
                           	}
						   // Si no es ninguno de los anteriores, será un BLOSSOM
						   else {
							   gObjs.add(GameObjectsJSONFactory.getGameObject(jObj));
                           }      
                       }
					   // Saca por pantalla los objetos que se han creado
                       printGameItems(); 
                       // Pintamos en el canvas los objetos
                       canvas.drawObjects(gObjs);
                   }  
                       requestFocusInWindow();          
              	}
       		}
   		);
       
       	// Añadimos al menú todas las opciones.
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

    
    public void keyTyped(KeyEvent ke) {}
    
    // Si pulsamos la barra espaciadora el timer se parará y se iniciará
    public void keyPressed(KeyEvent ke) {
    	lastKey = ke.getKeyCode(); 
    	if (lastKey == SPACE_KEY){
            if (timer.isRunning()){
        		timer.stop();
            }
            else {
            	timer.start();
            }
        }
    }

    
    public void keyReleased(KeyEvent ke) {}

    /**
     * Se invoca en cada tick de reloj
     * @param ae 
     */  
    
    public void actionPerformed(ActionEvent ae) {
       
        // Actions on Caperucita
        setDirection(lastKey);
        
        // Moving Caperucita. Si auto es 1 se moverá de forma automática
        if(auto==0) {ridingHood.moveToNextPosition();}
        else if(auto==1) {ridingHood.moveToNextPositionAuto();}
        else {ridingHood.moveToNextPosition();}
        
        // Si están en la pantalla que toca se habilitará el movimiento del "bicho"
        if(screenCounter==1) {fly.moveToNextPosition();}
        if(screenCounter==2) {bees.moveToNextPosition();}
        if(screenCounter==3) {spider.moveToNextPosition();}
        
        // Check if Caperucita is in board limits
        setInLimits();
        setInLimitsFly();
        noPuedesPasar();
        
        // Logic to change to a new screen.
        // Minimo encontraremos 3 objetos, RidingHood, Stone y Fly, Bee o Spider
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
        	// Si el objeto no es ni ridinhood ni una instancia de Bee, ni una de Spider, ni de Fly, ni de Stone, y la posicion de ridinghood es igual a la del objeto
            if(gObj != ridingHood && !(gObj instanceof Bee) && !(gObj instanceof Spider) && !(gObj instanceof Fly) && !(gObj instanceof Stone)  && rhPos.isEqual(gObj.getPosition())){
                // Sumamos el valor del objeto al del objeto.
            	int v = ridingHood.getValue() + gObj.getValue();
                ridingHood.setValue(v);
                gObjs.remove(gObj);
            }
            // Mismo procedimiento que linea 337
            else if(!(gObj instanceof Bee) && !(gObj instanceof Spider) && !(gObj instanceof Fly) && !(gObj instanceof Stone) && bePos.isEqual(gObj.getPosition())){
            	if(bePos.isEqual(rhPos)) {
            		ridingHood.setValue(ridingHood.getValue()-5);
            		System.out.println("Has chocado contra una abeja. -5 puntos");
            	}
            	else {
                gObjs.remove(gObj);
                }
            }
            // Mismo procedimiento que linea 337
            else if((gObj instanceof Spider) && !(gObj instanceof Fly) && !(gObj instanceof Stone) && spiPos.isEqual(gObj.getPosition())){
            	if(spiPos.isEqual(rhPos)) {
            		ridingHood.setLifes(ridingHood.getLifes()-1);
            		System.out.println("Una araña te ha pillado. -1 vida");
            		gObjs.remove(spider);
            	}
            }
            // Mismo procedimiento que linea 337
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
              //gObjs.add(new Blossom(new Position(2,8), 4, 10));
              //gObjs.add(new Blossom(new Position(8,8), 10, 10));
              //gObjs.add(new Blossom(new Position(8,2), 4, 10));
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
	        	System.out.println("Cargando objetos...");
	            JSONArray jArray = FileUtilities.readJsonsFromFile(path);
	            if (jArray != null){
	            	for (int i = 0; i < jArray.length(); i++){
	                    JSONObject jObj = jArray.getJSONObject(i);
	                    String typeLabel = jObj.getString(TypeLabel);
						// Si el objeto es una instancia de Fly
	                    if(GameObjectsJSONFactory.getGameObject(jObj) instanceof Fly) {
	                    	fly = new Fly(new Position(0,0), GameObjectsJSONFactory.getGameObject(jObj).getValue(), GameObjectsJSONFactory.getGameObject(jObj).getLifes(), gObjs);
	                    	// Movemos la mosca a una posición aleatoria.
	                    	fly.setPosition(getRandomPosition(CANVAS_WIDTH/boxSize,CANVAS_WIDTH/boxSize));
	                    	System.out.println("Mosca generada:" + fly.getPosition());
	                    	gObjs.add(fly);
	                     }
	                    // Mismo procedimiento que para Fly
	                    else if(GameObjectsJSONFactory.getGameObject(jObj) instanceof Stone) {
	                    	stone = new Stone(new Position(0,0), GameObjectsJSONFactory.getGameObject(jObj).getValue(), GameObjectsJSONFactory.getGameObject(jObj).getLifes());
	                    	stone.setPosition(getRandomPosition(CANVAS_WIDTH/boxSize,CANVAS_WIDTH/boxSize));
	                    	gObjs.add(stone);
	                     }
	                    else {
	                    	if(GameObjectsJSONFactory.getGameObject(jObj).getPosition().getX()<CANVAS_WIDTH/boxSize && GameObjectsJSONFactory.getGameObject(jObj).getPosition().getY()<CANVAS_WIDTH/boxSize) {
	                    		gObjs.add(GameObjectsJSONFactory.getGameObject(jObj));
	                    	}
	                    }
	                }                   
	            }
	            // Generamos BLOSSOMS de forma aleatoria. X BLOSSOMS como pantallas nos hayamos pasado
	            for(int i = 0 ; i<nivelesPasados ; i++) {
	            	gObjs.add(new Blossom(getRandomPosition(CANVAS_WIDTH/boxSize,CANVAS_WIDTH/boxSize), 4, (int)(Math.random()*20+1)));
	            }
	            System.out.println("------- NIVEL 1 CARGADO | EVENTOS ------- ");
                System.out.println(nivelesPasados + " objetos generados aleatoriamente");
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
                System.out.println("Cargando objetos...");
                JSONArray jArray1 = FileUtilities.readJsonsFromFile(path1);
                // Mismo procedimiento que al cargar el nivel 1
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
	                    	if(GameObjectsJSONFactory.getGameObject(jObj).getPosition().getX()<CANVAS_WIDTH/boxSize && GameObjectsJSONFactory.getGameObject(jObj).getPosition().getY()<CANVAS_WIDTH/boxSize) {
	                    		gObjs.add(GameObjectsJSONFactory.getGameObject(jObj));
	                    	}
                        }
                    }                            
                }
                for(int i = 0 ; i<nivelesPasados ; i++) {
                	gObjs.add(new Blossom(getRandomPosition(CANVAS_WIDTH/boxSize,CANVAS_WIDTH/boxSize), 4, (int)(Math.random()*20+1)));
                }
                System.out.println("------- NIVEL 2 CARGADO | EVENTOS ------- ");
                System.out.println(nivelesPasados + " objetos generados aleatoriamente");
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
                System.out.println("Cargando objetos...");
                JSONArray jArray2 = FileUtilities.readJsonsFromFile(path2);
                // Mismo procedimiento que al cargar el nivel 1
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
	                    	if(GameObjectsJSONFactory.getGameObject(jObj).getPosition().getX()<CANVAS_WIDTH/boxSize && GameObjectsJSONFactory.getGameObject(jObj).getPosition().getY()<CANVAS_WIDTH/boxSize) {
	                    		gObjs.add(GameObjectsJSONFactory.getGameObject(jObj));
	                    	}
                        } 
                    }                            
                }
                for(int i = 0 ; i<nivelesPasados ; i++) {
                	gObjs.add(new Blossom(getRandomPosition(CANVAS_WIDTH/boxSize,CANVAS_WIDTH/boxSize), 4, (int)(Math.random()*20+1)));
                }
                System.out.println("------- NIVEL 3 CARGADO | EVENTOS ------- ");
                System.out.println(nivelesPasados + " objetos generados aleatoriamente");
                nivelesPasados++;
                break;
            default:
            	screenCounter=0;
        }     	   
    }
    
    // Función para imprimir por pantalla los objetos que se han creado
    private void printGameItems(){
        System.out.println("Los objetos añadidos al juego son: ");
        for (IGameObject obj: gObjs){
            System.out.println(((IToJsonObject)obj).toJSONObject());
        }
    }
    
    // Generar posiciones aleatorias
    public Position getRandomPosition(int mX, int mY){
        int x = (int)(mX * Math.random());
        int y = (int)(mY * Math.random());
        return new Position(x, y);
    }
    
    public static void main(String [] args) throws Exception{
       Game gui = new Game(40,0);
    }
}
