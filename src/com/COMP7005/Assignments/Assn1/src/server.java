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
    private static  String FILE_TO_SEND = "";

    //Creates a Server Socket
    public server(int port) throws IOException
    {
        serverSocket = new ServerSocket(port);
        //serverSocket.setSoTimeout(10000);

    }



    private static  void send(Socket soc)
    {
        File file = new File(FILE_TO_SEND);
        System.out.println("Hello " + soc.getRemoteSocketAddress() + " Sending file: " + file.getPath());
        try
        {
            //Sending the file
            if (file.exists() && !file.isDirectory())
            {
                byte[] fileData = new byte[(int) file.length()];
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                //bis.read(fileData, 0, fileData.length);
                System.out.println("Sending " + FILE_TO_SEND + "(" + fileData.length + " bytes)");

                BufferedOutputStream bos = new BufferedOutputStream(soc.getOutputStream(), (int) file.length());
                bos.write(fileData, 0, fileData.length);
                bis.close();
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
        int choice;
        while (true)
        {
            try
            {
                System.out.println("Waiting to connect to the client.....Will timeout in 10 SECS");
                Socket soc = serverSocket.accept();// Will wait for incoming connections or will timeout if the serversocket timesout
                System.out.println("Connected to client: " + soc.getRemoteSocketAddress());
                DataInputStream in = new DataInputStream(soc.getInputStream());
                choice = in.readInt();
                System.out.println("Here is the choice sent by client " +choice );

                FILE_TO_SEND = in.readUTF();
                System.out.println("Here is the file name " + FILE_TO_SEND);


                send(soc);
                DataOutputStream out = new DataOutputStream(soc.getOutputStream());
                out.writeUTF("Thanks for connecting to " + soc.getLocalSocketAddress() + " Good Bye!");
                out.flush();

                soc.close();
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
