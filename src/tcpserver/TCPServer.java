/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpserver;

import java.io.*;
import java.net.*;
import java.text.Format;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;


/**
 *
 * @author clau
 **/
public class TCPServer {
    
private static String consulta; // la que ingresa el usuario
private static ArrayList<String> busqueda; //la que está en caché
private static File[] ficheros;
public static int id;

    /*
     * @param args the command line arguments
     */
    public static void compareCacheConsulta(String archivo, String consultar){
        try {
            File fXmlFile = new File("C:\\Users\\Clau\\Documents\\GitHub\\Lab1_SD_CBustamante\\cache\\" +archivo);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());	
            NodeList nList = doc.getElementsByTagName("consulta");
            System.out.println("----------------------------");

            for (int temp = 0; temp < nList.getLength(); temp++){
                Node nNode = nList.item(temp);
                System.out.println("\nCurrent Element :" + nNode.getNodeName());
                if (nNode.getNodeType() == Node.ELEMENT_NODE){
                    Element eElement = (Element) nNode;
                    System.out.println("String en cache : " + eElement.getElementsByTagName("stringconsulta").item(0).getTextContent());
                    //System.out.println("Veces consultado : " + eElement.getElementsByTagName("cantidadvecesconsultado").item(0).getTextContent());
                    String prueba = eElement.getElementsByTagName("stringconsulta").item(0).getTextContent();
                    System.out.println("la consulta es: " +consulta);
                    System.out.println("prueba: " + prueba);
            //aca no está entrando :c                             
                    if(consultar.equals(prueba)){
                        System.out.println("lo encontre!");
                        NodeList nlist2 = eElement.getElementsByTagName("respuesta");
                        //return resultado;
                        ArrayList<String> listaResultado = new ArrayList<>();
                        for (int temp2 = 0; temp2 < nlist2.getLength(); temp2++){
                            Node nNode2 = nlist2.item(temp2);
                            Element eElement2 = (Element) nNode2;
                            System.out.println("Resultado " + temp + ": "+eElement2.getElementsByTagName("stringrespuesta").item(0).getTextContent());
                            listaResultado.add(eElement2.getElementsByTagName("stringrespuesta").item(0).getTextContent());
                        }
                        busqueda = listaResultado;
                        //return busqueda;   
                    }else{
                        //return "miss :C";
                        System.out.println("no esta :C");
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }	
    
    public static int leerXML(String parts){ //lee el xml de preposiciones o palabras basura
        try {
            File fXmlFile = new File("C:\\Users\\Clau\\Documents\\GitHub\\Lab1_SD_CBustamante\\preposiciones.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());	
            NodeList nList = doc.getElementsByTagName("lista");
            System.out.println("----------------------------");

            for (int temp = 0; temp < nList.getLength(); temp++){ //si encuentra una preposicion la borra de la consulta
                Node nNode = nList.item(temp);
                //System.out.println("\nCurrent Element :" + nNode.getNodeName());
                if (nNode.getNodeType() == Node.ELEMENT_NODE){
                    Element eElement = (Element) nNode;
                    if(parts.equals(eElement.getElementsByTagName("preposicion").item(0).getTextContent())){
                        return 1;
                    }
                }
            }
            return 0;
        } catch (Exception e){
                e.printStackTrace();
        }
        return 2;
    }  
    
    public static String procesarQuery(String queryUsuario){ //retorna una lista de strings
        String string = queryUsuario;
        String[] parts = string.split(" "); //corta la query por espacios
        int i = parts.length;
        String consultaCorregida = "";
        for (int j = 0; j < i; j++){
            int resultado = leerXML(parts[j]);
            if (resultado == 0){
                    consultaCorregida = consultaCorregida + parts[j] + " ";
                    System.out.println(consultaCorregida);
            }
        }
        consulta = consultaCorregida;
        consulta = consulta.trim();
        return consultaCorregida;
    }
    
    public static int contadorFicheros(String direccion){
        File f = new File(direccion);
        int aux;
        if(f.exists()){ //si existe
            File[] ficheros2 = f.listFiles();
            aux = ficheros2.length; 
            
        }else{ //no existe
            aux= 0;
        }
        return aux;
    }

   
    
    public static void main(String[] args) throws Exception{
        //Variables
        String fromClient;
        String processedData;
        int nThreads;
        String direccion = "C:\\Users\\Clau\\Documents\\GitHub\\Lab1_SD_CBustamante\\cache";
        
        nThreads = contadorFicheros(direccion);
        System.out.println("numero de hebras a crear: " +nThreads);
        Thread[] threads = new Thread[nThreads];
        
        //Socket para el servidor en el puerto 5000
        ServerSocket acceptSocket = new ServerSocket(5000);
        System.out.println("Server is running...\n");
        
        while(true){
            //Socket listo para recibir 
            busqueda = null;
            Socket connectionSocket = acceptSocket.accept();
            //Buffer para recibir desde el cliente
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            //Buffer para enviar al cliente
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            
            //Recibimos el dato del cliente y lo mostramos en el server
            fromClient =inFromClient.readLine();
            System.out.println("Received: " + fromClient);
            
            //Se procesa el dato recibido
            processedData = fromClient.toLowerCase()+ '\n' ; //toma el mensaje del cliente y lo pasa a minusculas
            String resultado = procesarQuery(processedData); //crea la lista de palabras de la consulta y elimina preposiciones
            
            String ConsultaLista = new StringBuffer(resultado).toString() + '\n';
            System.out.println("esta es la consulta final: " +ConsultaLista); //lista para buscar el resultado en caché
            //consulta = ConsultaLista; //actualizo variable global
            String aux;
            
            File f = new File(direccion);
            if(f.exists()){ //si existe
                File[] ficheros2 = f.listFiles();
                for (int i = 0; i < ficheros2.length; i++) {
                    System.out.println(ficheros2[i].getName());
                }
                ficheros = ficheros2;
            }else{
                System.out.println("No hay ficheros :c");
            }
            
            for (int j = 0; j < threads.length; j++) {
                id = j;
                //System.out.println(j);
                final int i = j;
                threads[j] = new Thread(() -> {
                    //System.out.println("leer archivo:" +ficheros[id].getName());
                    //aca cada hebra debe buscar en un archivo
                    System.out.println("int: " + i);
                    compareCacheConsulta(ficheros[i].getName(), consulta);
                    
                    //System.out.println(ficheros[0]);
                });
                threads[j].start();
            }
            for(int i = 0; i < threads.length; i++)
            {
                threads[i].join();
            }
            for (int j = 0; j < threads.length; j++)
            {
                //System.out.println(threads[j].getId());
                threads[j].interrupt();
            }
            //Se le envia al cliente
            System.out.println("Busqueda: " + busqueda);
            ArrayList<String> resultado2 = busqueda;
            busqueda = null;
            ObjectOutputStream objectOutput = new ObjectOutputStream(connectionSocket.getOutputStream());
            objectOutput.writeObject(resultado2);
            //outToClient.writeBytes(ConsultaLista);

        }
    }
}
