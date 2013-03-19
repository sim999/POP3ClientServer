package client;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

import server.POP3Message;

public class POP3Client
{
    // Client socket
    private Socket socket;
    private final int DEFAULT_SERVER_PORT = 1110;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String secret = "test";

    public POP3Client()
    {
	try
	{
	    this.socket = new Socket("localhost", DEFAULT_SERVER_PORT);
	    output = new ObjectOutputStream(this.socket.getOutputStream());
	    input = new ObjectInputStream(this.socket.getInputStream());
	} catch (UnknownHostException e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public POP3Client(String host, int port)
    {
	try
	{
	    this.socket = new Socket(host, port);
	} catch (UnknownHostException e)
	{
	    e.printStackTrace();
	} catch (IOException e)
	{
	    e.printStackTrace();
	}
    }

    public void readMessageFromServer()
    {
	try
	{
	    POP3Message response = (POP3Message) input.readObject();
	    // Server is ready
	    if (response.getHeader().equals(POP3Message.HEADEROK))
	    {
		// Sending the APOP Command
		try
		{
		    MessageDigest md5diggest = MessageDigest.getInstance("MD5");
		    String diggest = response.getBody().concat(secret);
		    byte[] md5Bytes = md5diggest.digest(diggest.getBytes());
		    // Convert to hex string
		    StringBuffer sb = new StringBuffer();
		    for (int i = 0; i < md5Bytes.length; i++)
		    {
			sb.append(Integer.toHexString(0xff & md5Bytes[i]));
		    }
		    String md5 = sb.toString();

		    output.writeObject("APOP" + " sim " + md5);
		    
		    response = (POP3Message) input.readObject();
		    if (response.getHeader().equals(POP3Message.HEADEROK))
		    {
			output.writeObject("STAT");
		    }	    
		    
		} catch (NoSuchAlgorithmException e)
		{

		}

	    }

	} 
	catch (IOException e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (ClassNotFoundException e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} 
    }

    public static void main(String[] args)
    {
	POP3Client client = new POP3Client();
	client.readMessageFromServer();
    }
}
