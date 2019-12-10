/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import common.FileUtilities;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author juanangel
 */
public class TestRidingHood {
    
    // Práctica 3. Ejercicio 2.1
    /**
     * Crea dos objetos RidingHoog.
     * Comprueba paso a JSON y constructor con Jobjeto JSON
     */
    public static void testConstructorAndToJson(){
        
        System.out.println("testConstructorAndToJson");
        
        RidingHood b1 = new RidingHood(new Position(1,1));
        JSONObject jObj = b1.toJSONObject();
        RidingHood b2 = new RidingHood(jObj);
        System.out.println(b1);
        System.out.println(b2);
        System.out.println(b1.toJSONObject());
        System.out.println(b2.toJSONObject());
        System.out.println("-----------------------------------------------");
    }
    
    /**
     * Se ejecuta el método moveToNextPosition hasta que se alcanzan todos los targets o
     * se realizan n llamadas.
     * @param gObj  objetos en el que se realizan las invocaciones
     * @param targets objetos a alcanzar
     * @param  n número de llamadas al método
     * @return posiciones por las que ha pasado el objeto
     */
    public static ArrayList<Position> testMoveToNextPosition(IGameObject gObj, ArrayList<Blossom> targets, int calls){
        
        System.out.println("testMoveToNextPosition");
        
        // Array para guardar posiciones trayectoria objeto RidingHood
        ArrayList<Position> trace = new  ArrayList<Position>();
        
        for (int i = 0; i < calls; i++){
            gObj.moveToNextPosition();
            trace.add(new Position(gObj.getPosition()));
        }
        System.out.println("-----------------------------------------------");

        return trace;
    }
    
    /**
     * Guarda un array de posiciones en un fichero, las lee del fichero y las muestra 
     * por consola.
     * @param fileName
     * @param trace 
     */
    public static void testSaveAndLoad(String fileName, ArrayList<Position> trace) {
        
        System.out.println("testSaveAndLoad");
        
        if (trace == null){
            System.out.println("testSaveAndLoad: second argument is null");
        }
        
        // Guardamos las posiciones por las que ha pasado el objeto RidingHood 
        // en formato JSON en un fichero.
        JSONObject jPositions [] = new JSONObject[trace.size()];
        int counter = 0;
        for (Position p : trace){
            if (p != null){
                jPositions[counter] = p.toJSONObject();
            }
            counter++;
        }
        FileUtilities.writeJsonsToFile(jPositions, fileName);
         
        // Leer posiciones guardadas en el fichero anterior y mostrarlas
        // en consola.
        JSONArray jArray = FileUtilities.readJsonsFromFile(fileName);
        
        for(int i = 0; i < jArray.length(); i++){
            System.out.println(jArray.get(i).toString());
        }
        System.out.println("-----------------------------------------------");
    }
    
    
    public static void main(String [] args){
        
        // Ejercicio 2.1 ....................................................................
        testConstructorAndToJson();

        // Ejercicio 2.2 Invocación constructor con array de blossoms .......................
        // Crear array de Blossoms
        ArrayList<Blossom> blossoms = new ArrayList<Blossom>();
        blossoms.add(new Blossom(new Position(1,1)));       
        blossoms.add(new Blossom(new Position(6,1)));
        blossoms.add(new Blossom(new Position(3,3)));
        blossoms.add(new Blossom(new Position(6,6)));
        blossoms.add(new Blossom(new Position(1,6)));
        blossoms.add(new Blossom(new Position(1,1)));
        blossoms.add(new Blossom(new Position(1,4)));
                             
        // Crear nuevo objeto RidingHood pasándole el array de blossoms.
        IGameObject b = new RidingHood(new Position(1,1), 0, 0);
        
        // Ejercicio 2.5 .....................................................................
        ArrayList<Position> trace = testMoveToNextPosition(b, blossoms, 40);
        
        // Ejercicio 2.6 .....................................................................
        // Create directory for saveing test results if it does not exist.
        FileUtilities.crearDirectorio("src/main/resources/tests");
        testSaveAndLoad("src/main/resources/tests/traces.txt", trace);       
    }       
}
