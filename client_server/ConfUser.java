package com.multiclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ConfUser {
public static int i=1; 
public static void main(String args[]) throws IOException{


    InetAddress address=InetAddress.getLocalHost();
    Socket s1=null;
    String line=null;
    int port=0;
    BufferedReader br=null;
    BufferedReader is=null;
    PrintWriter os=null;
    try {
    	br = new BufferedReader(new InputStreamReader(System.in));
    	        
    	System.out.println("Enter Server port number :");
        try {
    		port = Integer.valueOf(br.readLine());
    	} catch (Exception e1) {
    		System.out.println("Invalid port number.");
    	}
        
        // connect to server
        s1=new Socket(address, port);
        
        // Establish input and output stream readers with server 
        br= new BufferedReader(new InputStreamReader(System.in));
        is=new BufferedReader(new InputStreamReader(s1.getInputStream()));
        os= new PrintWriter(s1.getOutputStream());
    }
    catch (IOException e){
        e.printStackTrace();
        System.err.print("IO Exception");
    }
    /*if(ConfServer.clientList.size()==0) {
    System.out.println("Client Name : Sai");
    }else if(ConfServer.clientList.size()==1) {
    	System.out.println("Client Name : Harish");
    }else if(ConfServer.clientList.size()==2) {
    	System.out.println("Client Name : Harsha");
    }
    i++;*/
    String response=null;
    
    // Logic to send and receive messages to  and from  server
    try{
    	System.out.println("Client Name :" + is.readLine());
        while(true) {
        	
            System.out.println("Enter Data to echo Server ( Enter quit to end, R to recieve messages and S to send message):");
            line=br.readLine();
   	
        	if(line.compareTo("quit")!=0) {
        	if("r".equalsIgnoreCase(line)) {
        		System.out.println("Waiting to recieve messages...");
        		 response=is.readLine();
                 System.out.println(response);
        	}else if("s".equalsIgnoreCase(line)) {
        		System.out.println("Enter Message : ");
        		line=br.readLine();
                os.println(line);
                os.flush();

        	}else {
        		System.out.println("Invalid Input");
        	}
        	}else {

                break;
        		
        	}
            }



    }
    catch(IOException e){
        e.printStackTrace();
    System.out.println("Socket read Error");
    }
    finally{
// closing connection with server
    	is.close();os.close();br.close();s1.close();
    	System.out.println("Connection Closed");

    }

}
}