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

    //Creates a Server Socket
    public server(int port) throws IOException
    {
        serverSocket = new ServerSocket(port);
        //serverSocket.setSoTimeout(10000);

    }



    private static int send(Socket soc, String fileName)
    {
        File file = new File(fileName);
        System.out.println("Hello " + soc.getRemoteSocketAddress() + " Sending file: " + file.getName());
        try
        {
            if (file.exists() && !file.isDirectory())
            {
                byte[] fileData = new byte[(int) file.length()];
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                bis.read(fileData, 0, fileData.length);

                BufferedOutputStream bos = new BufferedOutputStream(soc.getOutputStream(), (int) file.length());
                bos.write(fileData, 0, fileData.length);

                bos.flush();
                bis.close();
                return 0;
            } else return -1;

        } catch (IOException e)
        {
            System.out.println("Messed up on the server side while sending file. ");
            e.printStackTrace();
        }

        return 0;
    }

    //Bind the socket
    public void run()
    {
        while (true)
        {
            try
            {
                System.out.println("Waiting to connect to the client.....Will timeout in 10 SECS");
                Socket soc = serverSocket.accept();// Will wait for incoming connections or will timeout if the serversocket timesout
                System.out.println("Connected to client: " + soc.getRemoteSocketAddress());
                DataInputStream in = new DataInputStream(soc.getInputStream());
                System.out.println(in.readUTF());
                System.out.println("Here is the choice sent by client " + in.readInt());

                String fileName = in.readUTF();
                System.out.println("Here is the file name " + fileName);

                DataOutputStream out = new DataOutputStream(soc.getOutputStream());
                System.out.println("i am sending the file " + fileName);
                send(soc, fileName);
                out.writeUTF("Thanks for connecting to " + soc.getLocalSocketAddress() + " Good Bye!");

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
