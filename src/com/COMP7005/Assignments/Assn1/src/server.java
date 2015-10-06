package com.COMP7005.Assignments.Assn1.src;


import java.io.*;
import java.net.*;

/**
 * Created by vishavpreetsingh on 2015-09-22.
 */
public class server extends Thread
{

    private static final int SERVER_TCP_PORT = 7005;
    private ServerSocket serverSocket;
    private Socket dataSock;
    private static String FILE_TO_SEND = "";
    private static int CHOICE = 0;
    private static final int FILE_SIZE = 16 * 1024;


    /**
     * @param port
     * @throws IOException
     */
    //Creates a Server Socket
    public server(int port) throws IOException
    {
        serverSocket = new ServerSocket(port);
    }

    /**
     * @param soc
     */
    private static void recHeader(Socket soc)
    {
        try
        {
            DataInputStream in = new DataInputStream(soc.getInputStream());

            CHOICE = in.readInt();
            FILE_TO_SEND = in.readUTF();
        } catch (IOException e)
        {
            System.out.println("Error occured while receiving header on server");
            e.printStackTrace();
        }
    }


    /**
     * @param workerSoc
     * @throws IOException
     */
    private static void get(Socket workerSoc) throws IOException
    {
        System.out.println("Getting file from " + workerSoc.getRemoteSocketAddress());
        int bytesRead;
        int current = 0;

        byte[] serverData = new byte[FILE_SIZE];


        InputStream is = workerSoc.getInputStream();
        String workingDirectory = System.getProperty("user.dir");
        String filePath = workingDirectory + File.separator + "download" + File.separator + FILE_TO_SEND;
        DataInputStream dis = new DataInputStream(workerSoc.getInputStream());
        int i = dis.readInt();

        if (i == 1)
        {
            System.out.println("File size too big");
            return;
        } else if (i == 2)
        {
            System.out.println("File not found on the server");
            return;
        } else
        {
            while ((bytesRead = is.read(serverData, current, (serverData.length - current))) != -1)
            {
                if (bytesRead >= 0)
                {
                    current += bytesRead;
                }
            }

        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        bos.write(serverData, 0, current);
        bos.flush();
        dis.close();
        System.out.println("File " + filePath + " downloaded (" + current + " bytes read)");
        workerSoc.close();
    }

    /**
     * @param workerSoc
     * @throws IOException
     */
    private static void send(Socket workerSoc) throws IOException
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
                DataOutputStream dos = new DataOutputStream(workerSoc.getOutputStream());
                dos.writeInt(1);
                dos.flush();
                System.out.println("File size too big!!!" + " File Size: " + file.length());
            }

        } else
        {
            DataOutputStream dos = new DataOutputStream(workerSoc.getOutputStream());
            dos.writeInt(2);
            dos.flush();
            System.out.println("File does not exist!!! " + file.getName());
        }

    }


    /**
     *
     */
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
                        get(dataSock);
                        dataSock.close();
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


    /**
     * @param args
     */
    public static void main(String[] args)
    {
        int port = 0; //sets the port where the server listen

        if (args.length == 0)
        {
            port = SERVER_TCP_PORT;
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
