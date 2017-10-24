package com.multiclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


public class ConfServer {
// map to store list of client connections
static Map<Integer,Socket> clientList = new HashMap<Integer,Socket>();
static Map<Integer,String> userList = new HashMap<Integer,String>();
static Map<String,Integer> portList = new HashMap<String,Integer>();

public static void main(String args[]){

	BufferedReader br= new BufferedReader(new InputStreamReader(System.in));
	int portNo = 0;
    Socket s=null;
    ServerSocket ss2=null;

    System.out.println("Enter Server port number :");
    try {
		portNo = Integer.valueOf(br.readLine());
	} catch (Exception e1) {
		System.out.println("Invalid port number.");
	}
    try{
    	// create server socket
        ss2 = new ServerSocket(portNo); 
        
        System.out.println("Server is listening for clients...");

    }
    catch(IOException e){
    e.printStackTrace();
    System.out.println("Server error");

    }

    // logic to accept connections from clients
    while(true){
        try{
            s= ss2.accept();
            clientList.put(s.getPort(), s);
            if(clientList.size()==1) {
            	userList.put(s.getPort(),"sai");
            	portList.put("sai", s.getPort());
            }else if(clientList.size()==2) {
            	userList.put(s.getPort(),"harish");
            	portList.put("harish", s.getPort());
            }else if(clientList.size()==3) {
            	userList.put(s.getPort(),"harsha");
            	portList.put("harsha", s.getPort());
            }
            System.out.println("connection established with client "+ s.getPort());
            ConfServerThread st=new ConfServerThread(s);
            st.start();
        } catch(Exception e){
        e.printStackTrace();
        System.out.println("Connection Error");

    }
    }

}

}

// server thread to handle input and output messages from clients
class ConfServerThread extends Thread{  

    String line=null;
    BufferedReader  is = null;
    PrintWriter os=null;
    Socket s=null;

    public ConfServerThread(Socket s){
        this.s=s;
    }

    public void run() {
    try{
        is= new BufferedReader(new InputStreamReader(s.getInputStream()));
        os=new PrintWriter(s.getOutputStream());
        os.println(ConfServer.userList.get(s.getPort()));
        os.flush();
    }catch(IOException e){
        System.out.println("IO error in server thread");
    }

    try {
        line=is.readLine();
         
        while(line.compareTo("quit")!=0){
        	
// logic for sending message to designated user using whisper command        	
        	if(line.contains("whisper")) {
        		String message[]  = line.split(" ");
        		int portNo = ConfServer.portList.get(message[1].toLowerCase());
        		Socket s1 =  ConfServer.clientList.get(portNo);
        		String line3 = message[2];
        		for(int i=3;i<message.length;i++) {
        			line3+=" "+message[i];
        		}
        		 try{
         	        PrintWriter os1=new PrintWriter(s1.getOutputStream());
         	       String line2 =  String.valueOf("Client " + ConfServer.userList.get(s.getPort())) + " : "+ line3;
         	        os1.println(line2);
                     os1.flush();
         	    }catch(IOException e){
         	        System.out.println("IO error in server thread");
         	    }
        	}else { // logic for sending messages to all the clients
        	Map<Integer,Socket> clientMap = ConfServer.clientList;
        	for (Map.Entry<Integer, Socket> entry : clientMap.entrySet()) {
        	    if(s.getPort()!=entry.getKey()) {
        	    Socket s1 =  entry.getValue();
        	    try{
        	        PrintWriter os1=new PrintWriter(s1.getOutputStream());
        	       String line2 =  String.valueOf("Client " + ConfServer.userList.get(s.getPort())) + " : "+ line;
        	        os1.println(line2);
                    os1.flush();
        	    }catch(IOException e){
        	        System.out.println("IO error in server thread");
        	    }
        	   
        	    }
        	}
        	}
            System.out.println("Response to Client  :  "+line);
            line=is.readLine();
        }   
    } catch (IOException e) {

        line=this.getName(); 
        System.out.println("IO Error/ Client "+line+" terminated abruptly");
    }
    catch(NullPointerException e){
        line=this.getName(); 
        System.out.println("Client "+line+" Closed");
    }
// logic to disconnect from client
    finally{    
    try{
        System.out.println("Connection Closing..");
        if (is!=null){
            is.close(); 
            System.out.println(" Socket Input Stream Closed");
        }

        if(os!=null){
            os.close();
            System.out.println("Socket Out Closed");
        }
        if (s!=null){
        ConfServer.clientList.remove(s.getPort()); // removing client from list
        s.close();
        System.out.println("Socket Closed");
        }

        }
    catch(IOException ie){
        System.out.println("Socket Close Error");
    }
    }
    }
}