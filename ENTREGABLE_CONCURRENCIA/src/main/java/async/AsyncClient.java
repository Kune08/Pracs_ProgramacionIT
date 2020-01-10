/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package async;

import java.awt.GridLayout;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class AsyncClient extends JFrame {
    
    public static final int CANVAS_WIDTH = 240;
    public static final int BOX_SIZE = 20;
    
    static int fcounter = 0;    
    int row, col;
    
    AsyncClientPanel client[] = new AsyncClientPanel[4];
    
    ExecutorService fileLoader;
    
    JMenuBar barraMenu;
    JMenu mSeleccionEjecutor;
    JMenu mFixedPool;
    JMenuItem itThreeThreads, itFiveThreads;
    JMenuItem itFlexiblePool, itSingleThreaded;
    
    
    public AsyncClient() throws Exception{

        super("Cliente v3");
                
        getContentPane().setLayout(new GridLayout(1,4));
        
        for (int i = 0; i < client.length; i++){
            client[i] = new AsyncClientPanel(i, CANVAS_WIDTH, BOX_SIZE);       
            getContentPane().add(client[i]);
        }
        setSize (4*(CANVAS_WIDTH + 20), 2 * (CANVAS_WIDTH + 40));
        setVisible(true);         
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    
       
        System.out.println(this.getFocusableWindowState());
        this.setFocusable(true);
        
        // Set default loader for panels.
        ExecutorService fileLoader = Executors.newFixedThreadPool(3);
        for (AsyncClientPanel panel : client){
            try {
                panel.setLoader(fileLoader);
            } catch (InterruptedException ex) {
                Logger.getLogger(AsyncClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    

    
    public static void main(String [] args) throws Exception{
       AsyncClient gui = new AsyncClient();
    } 
}    
    
    
