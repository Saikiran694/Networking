package com.net;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class WebProxyserver 
{
	/** Port for the proxy */
	private static int port=17212;
	/** Socket for client connections */
	private static ServerSocket socket;
	/** Create the ProxyCache object and the socket */
	public static void init(int p) {
		port = p;
		try {
		socket =new ServerSocket(port); // creating server socket
		System.out.println("Web Proxy server is running and listening for client requests on port "+ port + " ...!");
		} catch (IOException e) {
			System.out.println("Error creating socket: " + e);
			System.exit(-1);
		}
	}

	public static void handle(Socket client) {
		Socket server = null;
		HttpRequest request = null;
		HttpResponse response = null;

		/* Process request. If there are any exceptions, then simply
		 * return and end this request. This unfortunately means the
		 * client will hang for a while, until it timeouts. */

		/* Read request by using BufferedReader*/
		try {
			BufferedReader clientInputStreamReader=new BufferedReader(new InputStreamReader(client.getInputStream()));
			request=new HttpRequest(clientInputStreamReader);
		} catch (IOException e) {
			System.out.println("Error reading request from client: " + e);
			return;
		}
		/* Send request to server by using DataOutputStream and writeBytes */
		try {
			/* Open socket and write request to socket */
			server=new Socket(request.getHost(),request.getPort());
			DataOutputStream serverOutputWriter=new DataOutputStream(server.getOutputStream());
			serverOutputWriter.writeBytes(request.toString());
		} catch (UnknownHostException e) {
			System.out.println("Unknown host: " + request.getHost());
			System.out.println(e);
			return;
		} catch (IOException e) {
			System.out.println("Error writing request to server: " + e);
			return;
		}
		/* Read response and forward it to client by using DataInputStream and DataOutputStream */
		try {
			DataInputStream serverInputStreamReader=new DataInputStream(server.getInputStream());
			response=new HttpResponse(serverInputStreamReader);
			/* Write response to client. First headers by using writeBytes, then body by using write */
			DataOutputStream clientOutputWriter=new DataOutputStream(client.getOutputStream());
			clientOutputWriter.writeBytes(response.toString());
			clientOutputWriter.write(response.body);
			clientOutputWriter.flush();
			// closing input stream of server and output stream of client
			clientOutputWriter.close();
			serverInputStreamReader.close();
			client.close();
			server.close();
		}catch (IOException e) {
	//		System.out.println("Error writing response to client: " + e);
		}
	}


	/** Read command line arguments and start proxy */
	public static void main(String args[]) {
		int myPort = 0;

		try {
			InputStreamReader inputStream = new InputStreamReader(System.in);
			BufferedReader bufferedReader = new BufferedReader(inputStream);
				System.out.println("Please enter Port number of Web Proxy server - ");
				String line = bufferedReader.readLine();
				myPort = Integer.parseInt(line);
		}catch (NumberFormatException e) {
			System.out.println("Please input valid port number!");
			System.exit(-1);
		}catch(IOException e) {
			System.out.println(e);
		}

		init(myPort);

		/** Main loop. Listen for incoming connections and spawn a new
		 * thread for handling them */
		Socket client = null;

		while (true) {
			try {
				client = socket.accept();
				handle(client);
			} catch (IOException e) {
				System.out.println("Error reading request from client: " + e);
				/* Definitely cannot continue processing this request,
				 * so skip to next iteration of while loop. */
				continue;
			}
		}

	}
}


