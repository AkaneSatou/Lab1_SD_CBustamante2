/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpserver;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author clau
 **/
public class TCPServer {
    
	private static String consulta; // la que ingresa el usuario
	private static ArrayList<String> busqueda; //la que está en caché
		/*
		 * @param args the command line arguments
		 */
    public static void compareCacheConsulta(){
		try {
			File fXmlFile = new File("C:\\Users\\Clau\\Documents\\NetBeansProjects\\socket-java-example-master\\cache.xml");
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
					//System.out.println("Staff id : " + eElement.getAttribute("id"));
					System.out.println("String consulta : " + eElement.getElementsByTagName("stringconsulta").item(0).getTextContent());
					System.out.println("veces consultado : " + eElement.getElementsByTagName("cantidadvecesconsultado").item(0).getTextContent());
					String prueba = eElement.getElementsByTagName("stringconsulta").item(0).getTextContent();
					if(prueba.equals(consulta)){							
						NodeList nlist2 = eElement.getElementsByTagName("respuesta");
						//return resultado;
						for (int temp2 = 0; temp2 < nlist2.getLength(); temp2++){
							Node nNode2 = nlist2.item(temp2);
							Element eElement2 = (Element) nNode2;
							System.out.println("Resultado " + temp + ": "+eElement2.getElementsByTagName("stringrespuesta").item(0).getTextContent());
							busqueda.add(eElement2.getElementsByTagName("stringrespuesta").item(0).getTextContent());
						}
						//return busqueda;
					}else{
						//return "miss :C";
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}	
	public static int leerxml(String parts){
		try {
			File fXmlFile = new File("C:\\Users\\Clau\\Documents\\NetBeansProjects\\socket-java-example-master\\preposiciones.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
					
			doc.getDocumentElement().normalize();

			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());	
			NodeList nList = doc.getElementsByTagName("lista");
			System.out.println("----------------------------");

			for (int temp = 0; temp < nList.getLength(); temp++){
				Node nNode = nList.item(temp);
				//System.out.println("\nCurrent Element :" + nNode.getNodeName());
				if (nNode.getNodeType() == Node.ELEMENT_NODE){
					Element eElement = (Element) nNode;
					//System.out.println("Staff id : " + eElement.getAttribute("id"));
					//System.out.println("String preposicion : " + eElement.getElementsByTagName("preposicion").item(0).getTextContent());
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
	public static String procesarQuery(String queryUsuario){
		String string = queryUsuario;
		String[] parts = string.split(" ");
		int i = parts.length;
		String consulta2 = "";
		for (int j = 0; j < i; j++){
			//System.out.println("partes: "+ parts[j]);
			int resultado = leerxml(parts[j]);
			if (resultado == 0){
				consulta2 = consulta2 + parts[j] + " ";
				System.out.println(consulta2);
			}
		}
		consulta = consulta2;
		return consulta2;
	}
    
    public static void main(String[] args) throws Exception{
        //Variables
        String fromClient;
        String processedData;    
        
        //Socket para el servidor en el puerto 5000
        ServerSocket acceptSocket = new ServerSocket(5000);
        System.out.println("Server is running...\n");
        
        while(true){
            //Socket listo para recibir 
            Socket connectionSocket = acceptSocket.accept();
            //Buffer para recibir desde el cliente
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            //Buffer para enviar al cliente
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            
            //Recibimos el dato del cliente y lo mostramos en el server
            fromClient =inFromClient.readLine();
            System.out.println("Received: " + fromClient);
            
            //Se procesa el dato recibido
            //processedData = fromClient.toUpperCase() + '\n';
            String resultado = procesarQuery(fromClient);
            String reverse = new StringBuffer(resultado).toString() + '\n';
            
            //Se le envia al cliente
            outToClient.writeBytes(reverse);

        }
    }
}
