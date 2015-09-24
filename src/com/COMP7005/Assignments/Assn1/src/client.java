package com.COMP7005.Assignments.Assn1.src;

import java.net.*;
import java.io.*;

/**
 * Created by vishavpreetsingh on 2015-09-22.
 */
public class client
{

    /*Private method to receive file
    will take a file name to receive
    Input Stream*/


    private void send()
    {

    }

    /*Private method to send files
    will take a file name to send
    Output stream*/
    private void receive()
    {

    }


    public static void main(String[] args)
    {
        String serverName = args[0];
        int port = Integer.parseInt(args[1]);

        try
        {
            System.out.println("Connecting to " + serverName + " on port " + port);
            Socket client = new Socket(serverName, port);
            System.out.println("Just connected to " + client.getRemoteSocketAddress());
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            out.writeUTF("Hello from " + client.getLocalSocketAddress() + "\n" + "I will SEND or GET files");
            InputStream inFromServer = client.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);
            System.out.println("Server says " + in.readUTF());
            client.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        //test


    }

}
