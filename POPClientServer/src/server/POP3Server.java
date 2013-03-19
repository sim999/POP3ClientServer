package server;

import java.io.IOException;
import java.net.ServerSocket;

import client.POP3Client;

public class POP3Server
{
    private ServerSocket socket;
    private final int PORT = 1110;


    public POP3Server()
    {
	try
	{
	    this.socket = new ServerSocket(PORT);
	} catch (IOException e)
	{
	    e.printStackTrace();
	}
    }

    public void launch()
    {
	System.out.println("SERVER LISTENNING ON " + PORT);

	while (true)
	{
	    try
	    {
		POP3SubServer sub = new POP3SubServer(this.socket.accept());
		Thread t = new Thread(sub);
		System.out.println("A client has connected on " + PORT);
		t.start();
	    } catch (IOException e)
	    {
		e.printStackTrace();
	    }
	}
    }

    public static void main(String[] args)
    {
	POP3Server server = new POP3Server();
	server.launch();
    }

}
