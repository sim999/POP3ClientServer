package server;

import java.io.Serializable;

public class POP3Message implements Serializable
{
    private String header;
    private String body;
    
    public static final String HEADEROK = "+Ok";
    public static final String HEADERERR = "-Err";
    
    public POP3Message(String header, String body)
    {
	this.header = header;
	this.body = body;
    }
    
    public String toString()
    {
	return this.header +" "+ this.body;
    }

    public String getHeader()
    {
        return header;
    }

    public void setHeader(String header)
    {
        this.header = header;
    }

    public String getBody()
    {
        return body;
    }

    public void setBody(String body)
    {
        this.body = body;
    }
    
}

