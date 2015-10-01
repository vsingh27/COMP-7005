package com.COMP7005.Assignments.Assn1.src;

import java.io.*;
import java.net.*;

/**
 * Created by vishavpreetsingh on 2015-09-22.
 */
public class server extends Thread
{

    //Server information
    //port

    private static final int SERVER_TCP_PORT = 7005;
    private ServerSocket serverSocket;
    private static String FILE_TO_SEND = "";
    private static int CHOICE = 0;


    //Creates a Server Socket
    public server(int port) throws IOException
    {
        serverSocket = new ServerSocket(port);
        //serverSocket.setSoTimeout(10000);

    }

    private static void recHeader(Socket soc)
    {
        try
        {
            DataInputStream in = new DataInputStream(soc.getInputStream());
            CHOICE = in.readInt();
            FILE_TO_SEND = in.readUTF();

            System.out.println("Echoing Client's choice: " + CHOICE);
            System.out.println("Echoing  path of the file to send: " + FILE_TO_SEND);
        } catch (IOException e)
        {
            System.out.println("Error occured while receiving header on server");
            e.printStackTrace();
        }
    }

    private static void send(Socket soc)
    {
        File file = new File(FILE_TO_SEND);
        try
        {
            //Sending the file
            if (file.exists() && !file.isDirectory())
            {
                byte[] fileData = new byte[(int) file.length()];
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                bis.read(fileData, 0, fileData.length);
                System.out.println("Sending " + FILE_TO_SEND + "(" + fileData.length + " bytes)");
                BufferedOutputStream bos = new BufferedOutputStream(soc.getOutputStream(), (int) file.length());
                bos.write(fileData, 0, fileData.length);
                bos.close();
                bis.close();
                soc.close();
                System.out.println("Done!");

            } else
            {
                System.out.println("File requested does not exist or is not a file");

            }

        } catch (IOException e)
        {
            System.out.println("Messed up on the server side while sending file. ");
            e.printStackTrace();
        }

    }

    //Bind the socket
    public void run()
    {
        while (true)
        {
            try
            {
                System.out.println("Waiting to connect to the client.....Will timeout in 10 SECS");
                Socket controlSocket = serverSocket.accept();// Will wait for incoming connections or will timeout if the serversocket timesout
                System.out.println("Connected to client: " + controlSocket.getRemoteSocketAddress());

                recHeader(controlSocket);

                switch (CHOICE)
                {
                    case 1:
                        break;
                    case 2:
                        Socket dataSock = serverSocket.accept();
                        send(dataSock);
                        break;
                    default:
                        System.out.println("HAHAHAHAHAHAH good try, but you fail!!!!!!");
                        break;
                }

            } catch (SocketTimeoutException se)
            {
                se.printStackTrace();
                break;
            } catch (IOException e)
            {
                e.printStackTrace();
                break;
            }

        }
    }


    public static void main(String[] args)
    {
        int port = 0; //sets the port where the server listen

        if (args.length == 0)
        {
            port = 7005;
        } else if (args.length == 1)
        {
            port = Integer.parseInt(args[0]);
        }

        try
        {
            Thread t = new server(port);
            t.start();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }


}
