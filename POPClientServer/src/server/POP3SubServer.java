package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class POP3SubServer implements Runnable
{
    private final int TRANSACTION = 3;
    private final int AUTHORIZATION = 2;
    private final int INITIALISATION = 1;
    private final int UPDATE = 4;

    private Socket clientSocket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private int state = INITIALISATION;
    private int portClient;
    private String secret = "test";
    private String messageId;

    public POP3SubServer(Socket socket)
    {
	this.clientSocket = socket;
	portClient = this.clientSocket.getPort();
	try
	{
	    input = new ObjectInputStream(clientSocket.getInputStream());
	    output = new ObjectOutputStream(this.clientSocket.getOutputStream());

	} catch (IOException e1)
	{
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}
    }

    @Override
    public void run()
    {
	while (true)
	{
	    switch (state)
	    {
	    case INITIALISATION:
		System.out.println("Initialisation");
		UUID id = UUID.randomUUID();
		messageId = "<" + id.toString() + "@localhost" + ">";
		POP3Message welcome = new POP3Message(POP3Message.HEADEROK,
			messageId);
		try
		{
		    System.out.println("write");

		    output.writeObject(welcome);
		} catch (IOException e)
		{
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		state = AUTHORIZATION;
		break;

	    case AUTHORIZATION:
		try
		{
		    String command = (String) input.readObject();
		    System.out.println("Authorization");

		    if (command != null & command.startsWith("APOP"))
		    {
			String[] commandUserMd5 = command.split(" ");

			if (commandUserMd5[1].equals("sim"))
			{
			    MessageDigest md5diggest;
			    try
			    {
				md5diggest = MessageDigest.getInstance("MD5");

				String diggest = messageId.concat(secret);
				byte[] md5Bytes = md5diggest.digest(diggest
					.getBytes());
				// Convert to hex string
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < md5Bytes.length; i++)
				{
				    sb.append(Integer
					    .toHexString(0xff & md5Bytes[i]));
				}
				String md5 = sb.toString();

				if (md5.equals(commandUserMd5[2]))
				{
				    POP3Message granted = new POP3Message(
					    POP3Message.HEADEROK,
					    "maildrop locked and ready");
				    output.writeObject(granted);
				}
			    } catch (NoSuchAlgorithmException e)
			    {
				// TODO Auto-generated catch block
				e.printStackTrace();
			    }
			}
		    }
		} catch (EOFException e)
		{
		    break;
		} catch (IOException e1)
		{
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		} catch (ClassNotFoundException e1)
		{
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}
		break;
	    case TRANSACTION:
		break;
	    case UPDATE:
		break;
	    default:
		System.out.print("default envoi APOP");
		break;
	    }
	}
    }
}
