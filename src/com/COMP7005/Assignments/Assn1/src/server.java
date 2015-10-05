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
    private Socket dataSock;
    private static String FILE_TO_SEND = "";
    private static int CHOICE = 0;
    private static final int FILE_SIZE = 16 *1024;


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

            System.out.println("Choice " + CHOICE);
            System.out.println("File :" + FILE_TO_SEND);
        } catch (IOException e)
        {
            System.out.println("Error occured while receiving header on server");
            e.printStackTrace();
        }
    }

    private static void send(Socket workerSoc) throws FileNotFoundException, IOException
    {
        File file = new File(FILE_TO_SEND);

        //Sending the file
        if (file.exists() && !file.isDirectory())
        {
            if (file.length() > 0 && file.length() <= FILE_SIZE)
            {
                byte[] fileData = new byte[(int) file.length()];
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                bis.read(fileData, 0, fileData.length);
                System.out.println("Sending " + FILE_TO_SEND + "(" + fileData.length + " bytes)");
                BufferedOutputStream bos = new BufferedOutputStream(workerSoc.getOutputStream(), (int) file.length());
                bos.write(fileData, 0, fileData.length);
                bos.close();
                bis.close();
                workerSoc.close();
                System.out.println("Done!");
            } else
            {

                System.out.println("File size too big!!!" +" File Size: " + file.length());
            }

        } else
        {
            System.out.println("File does not exist!!! "  + file.getName());
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
                dataSock = serverSocket.accept();// Will wait for incoming connections or will timeout if the serversocket timesout
                System.out.println("Connected to client: " + dataSock.getRemoteSocketAddress());
                recHeader(dataSock);
                switch (CHOICE)
                {
                    case 1:
                        System.out.println("Call get method");
                        break;
                    case 2:
                         //dataSock = serverSocket.accept();

                        send(dataSock);
                        break;
                    default:
                        break;
                }

            } catch (SocketTimeoutException se)
            {
                se.printStackTrace();

            } catch (IOException e)
            {
                e.printStackTrace();
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
