/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package async;

import game.ClientCanvas;
import game.GameFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author juanangel
 */
public class AsyncClientPanel extends JPanel implements IAsyncLoaderObserver, ActionListener {
     
    // Identificador del panel y etiqueta de identificación.
     int id = 0;
     JLabel lbId;
    
    // Superficie de dibujo.
    ClientCanvas canvas = new ClientCanvas(320, 16);
    // ClientCanvasScheduled canvas; // = new ClientCanvasScheduled(320, 16);
    
    // Mensajes y trazas.
    JTextArea txMessages;
    
    // Selección de ficheros a cargar.
    JComboBox cbFilesToUpload;
    JLabel lbFilesToUpload;
    
    // Selección de juego a eproducir.
    JComboBox cbReadyToPlay;
    JLabel lbReadyToPlay;
   
    // Paneles auxiliares.
    JPanel pnControls;
    JPanel pnBoard;
    
    // Ejecutor para cargas de ficheros.
    ExecutorService fileLoader = null;
    
    // Tabla de juegos cargados en memoria.
    HashMap<String, ArrayList<GameFrame>> downloadedGamesTable = new HashMap<String, ArrayList<GameFrame>>();
    
    // Tabla de cargas solicitadas y aún no terminadas.
    HashMap<String, Future<ArrayList<GameFrame>>> pendingRequests = new HashMap<String, Future<ArrayList<GameFrame>>>();    
    
    // Directorio de trabajo asumido en la práctica.
    public static final String WORKING_PATH = "src/main/resources/games";
    File file;
        
    // Testigo para que el manejador de eventos de cbReadyToPlay sepa si tiene
    // los datos ya disponibles o debe recogerlos de un "futuro".
    int nFiles = 0;
        
    public AsyncClientPanel(int id, int width, int squareEdge){
        
        this.id = id;
        lbId = new JLabel("Cliente " + id);

        file = new File(WORKING_PATH);
        String filesList [] = file.list();
        cbFilesToUpload = new JComboBox(filesList);
        cbReadyToPlay = new JComboBox();
        lbFilesToUpload = new JLabel("Load File");
        lbReadyToPlay = new JLabel("See Game");   
        
        canvas = new ClientCanvas(width, squareEdge);
        canvas.setBorder(BorderFactory.createLineBorder(Color.darkGray));
        
        setLayout(new BorderLayout());

        pnControls = new JPanel();
        txMessages = new JTextArea(10,5);
        JScrollPane scMessages = new JScrollPane(txMessages);
        scMessages.setPreferredSize(new Dimension(width,2*width/3));
        
        pnControls.setLayout(new GridLayout(2,2));
        pnControls.add(lbFilesToUpload); 
        pnControls.add(cbFilesToUpload); 
        pnControls.add(lbReadyToPlay);
        pnControls.add(cbReadyToPlay);    
        pnBoard = new JPanel();
        pnBoard.setLayout(new BorderLayout());
        pnBoard.add(pnControls, BorderLayout.NORTH); 
        pnBoard.add(scMessages);
        
        JScrollPane scCanvas = new JScrollPane(canvas);
        scCanvas.setPreferredSize(new Dimension(width + 8, width + 8));
        add(scCanvas);
        add(pnBoard, BorderLayout.SOUTH);
        add(lbId, BorderLayout.NORTH);
        
        setBorder(BorderFactory.createEtchedBorder());
        
        cbFilesToUpload.addActionListener(this);
        cbReadyToPlay.addActionListener(this);         
    }
    
    
    public void loadComplete(String key) {
        
        // Trazas en consola y área de texto
        System.out.println("Client " + id + "setFileContents received ---> " + key);
        txMessages.append("Client " + id + "setFileContents received ---> " + key + "\n");
            
        // Coordinación entre este método y el manejador de eventos de
        // cbReadyToPlay.
        // La acción de obtención del resultado se delega al manejador de eventos
        // de cbReadyToPlay para no bloquear a la tarea que invoca loadToComplete.

        // Si al seleccionar un item, el manejador de eventoa de cbReadyToPlay
        // detecta que nFiles no coincide con el número de items sabe que debe 
        // completar la recogida de datos descargados de forma asíncrona.
        nFiles = cbReadyToPlay.getItemCount();
            
        // Actualizamos la lista de reproducibles añadiendo el nuevo item.
        // Añadimos el nuevo item en 0 para saber donde recuperar la clave 
        // para acceder al futuro. 
        // Ahora la lista de cbReadytoPlay tendrá un elemento más.
        cbReadyToPlay.insertItemAt(key, 0);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == cbFilesToUpload){
            String sFile = (String) cbFilesToUpload.getSelectedItem();
            if (!downloadedGamesTable.containsKey(sFile)){   
                System.out.println("Loading " + sFile + "  ");                       
                txMessages.append("Loading ... " +  sFile + "\n");   
                
                // 1.- Modifique el código para solicitar al ejecutor que ejecute 
                //     tarea para cargar el fichero escogido.
                Future<ArrayList<GameFrame>> future = null; 
                    
                // 2.- Guarde el futuro en pendingRequest asociado al nombre del fichero.
            }
            else{
                System.out.println(sFile + " is already loaded");   
                txMessages.append(sFile + " is already loaded\n");
            }
            requestFocusInWindow();            
        }
        else if (ae.getSource() == cbReadyToPlay){
            
            // Completar recogida de datos descargados en manejador de
            // cbFilesToUpload
            if (nFiles != cbReadyToPlay.getItemCount()){
                
                nFiles = cbReadyToPlay.getItemCount();
                System.out.println("cbReadyToPlay: getting movie");
                
                // 1.- Obtenemos clave para obtener el futuro.
                String key = (String) cbReadyToPlay.getItemAt(0);
                
                // 2.- Obtenemos el futuro asociado al fichero y lo eliminamos de la tabla.
                Future<ArrayList<GameFrame>> f = pendingRequests.remove(key);
            
                // 3.- Obtenemos el resultado asociado al futuro.
                ArrayList<GameFrame> movie = null;
                
                // 4.- Insertamos el resultado en la tabla de movies listos para reproducir.
            
                // Trazas en consola y área de texto 
                System.out.println(key + " has is ready");
                txMessages.append (key + " has is ready\n");
            }
               
            String sFile = (String) cbFilesToUpload.getSelectedItem();
            
            // Obtención del juego seleccionado.
            ArrayList<GameFrame> movie = downloadedGamesTable.get(sFile);  
            
            // Envío al canvas para su reproducción.
            canvas.playMovie(movie);
            System.out.println(sFile + " is being reproduced");
            txMessages.append(sFile + " is being reproduced\n");
            requestFocusInWindow();     
        }
    }
    
    public void setLoader(ExecutorService fileLoader) throws InterruptedException{
        System.out.println("SetLoader ---> " + fileLoader.getClass().getSimpleName());
        if (fileLoader != null){
            if (this.fileLoader != null){
                this.fileLoader.shutdown();
                this.fileLoader.awaitTermination(5, TimeUnit.SECONDS);   
            }
            this.fileLoader = fileLoader;
        }
    }
    
}