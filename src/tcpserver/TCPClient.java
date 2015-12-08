/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpserver;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 *
 * @author clau
 */
public class TCPClient {
    
    public static void main(String args[]) throws Exception{
        //Variables
        String sentence;
        String fromServer;
        
        //Buffer para recibir desde el usuario
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        
        //Socket para el cliente (host, puerto)
        Socket clientSocket = new Socket("localhost", 5000);
        
        //Buffer para enviar el dato al server
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        
        //Buffer para recibir dato del servidor
        //  BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
        //Leemos del cliente y lo mandamos al servidor
        sentence = inFromUser.readLine();
        outToServer.writeBytes(sentence + '\n');
        
        //Recibimos del servidor
        ObjectInputStream objectInput = new ObjectInputStream(clientSocket.getInputStream());
        Object object = objectInput.readObject();
        if(object != null)
        {
            ArrayList<String> resultado = (ArrayList<String>) object;
            for (String resultado1 : resultado) {
                System.out.println(resultado1);
            }
        }
        else
        {
            System.out.println("Miss :C");
        }
//fromServer = inFromServer.readLine();
        //System.out.println("Server response: " + fromServer);
        
        //Cerramos el socket
        clientSocket.close();
    }
    
}
