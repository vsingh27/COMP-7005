package com.COMP7005.Assignments.Assn1.src;

import java.net.*;
import java.io.*;
import java.util.Scanner;

/**
 * Created by vishavpreetsingh on 2015-09-22.
 */
public class client
{

    /*Private method to send file
    will take a file name to send
    Output Stream*/

    private static String SERVER_NAME = "localhost";
    private static int PORT = 7005;
    private static final int FILE_SIZE = 16 * 1024;

    /**
     * @param file the file to be sent to the server
     * @param client The socket used to connect to the server
     * @throws IOException
     */
    private static void send(File file, Socket client) throws IOException
    {
        if (file.exists() && !file.isDirectory())
        {

            if (file.length() > 0 && file.length() <= FILE_SIZE)
            {
                byte[] fileData = new byte[(int) file.length()];
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                bis.read(fileData, 0, fileData.length);
                System.out.println("Sending " + file.getName() + "(" + fileData.length + " bytes)");
                BufferedOutputStream bos = new BufferedOutputStream(client.getOutputStream(), (int) file.length());

                bos.write(fileData, 0, fileData.length);
                bos.close();
                bis.close();
                //client.close();
                System.out.println("Done!");
            } else
            {
                DataOutputStream dos = new DataOutputStream(client.getOutputStream());
                dos.writeInt(1);
                dos.flush();
                System.out.println("File size too big!!!" + " File Size: " + file.length());
            }

        } else
        {
            DataOutputStream dos = new DataOutputStream(client.getOutputStream());
            dos.writeInt(2);
            dos.flush();
            System.out.println("File does not exist!!! " + file.getName());

        }

    }


    /**
     * @param file the file to receive
     * @param client the socket to cnnect to the client
     * @return
     * @throws IOException
     */
    private static int get(File file, Socket client) throws IOException
    {
        System.out.println("Getting file from " + client.getRemoteSocketAddress());
        int bytesRead;
        int current = 0;

        byte[] serverData = new byte[FILE_SIZE];
        InputStream is = client.getInputStream();
        String workingDirectory = System.getProperty("user.dir");
        String filePath = workingDirectory + File.separator + "download" + File.separator + file.getName();
        DataInputStream dis = new DataInputStream(client.getInputStream());
        int i = dis.readInt();

        if (i == 1)
        {
            System.out.println("File size too big");
        } else if (i == 2)
        {
            System.out.println("File not found on the server");
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
        client.close();
        return 0;
    }

    /**
     * @return the socket binded to the server
     * @throws IOException
     */
    // Creates a socket
    private static Socket connect() throws IOException
    {
        Socket client = new Socket(SERVER_NAME, PORT);
        System.out.println("Socket created at " + client.getLocalSocketAddress() + "...");

        return client;
    }

    /**
     * @param soc the socket to be disconnected
     * @throws IOException
     */
    //disconnects the socket
    private static void disconnet(Socket soc) throws IOException
    {
        System.out.println("Disconneting " + soc.getLocalSocketAddress());
        soc.close();
    }


    /**
     * @param filePath path of the file name
     * @param choice choice 1->Send, 2-> Get, 0-> Quit
     * @param soc Socket that is binded with the server
     */
    private static void sendHeader(String filePath, int choice, Socket soc)
    {
        try
        {
            OutputStream outToServer = soc.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            out.writeInt(choice);
            out.writeUTF(filePath);
            out.flush();

        } catch (IOException e)
        {
            System.out.println("Error while sending header from client");
            e.printStackTrace();
        }
    }


    /**
     * @param args
     */
    public static void main(String[] args)
    {
        if (args.length != 2)
        {
            System.out.println("Program not started with valid arguments ");
            System.exit(-1);
        }

        SERVER_NAME = args[0];
        PORT = Integer.parseInt(args[1]);

        Scanner scan = new Scanner(System.in);
        int choice = 1;
        try
        {
            while (choice != 0)
            {

                System.out.println("Please choose one of the following");
                System.out.println("1- To Send");
                System.out.println("2- To Receive");
                System.out.println("0- To Exit");

                if (scan.hasNextInt())
                {
                    choice = scan.nextInt();

                    System.out.println("Connecting to " + SERVER_NAME + " on port " + PORT);
                    switch (choice)
                    {
                        case 0:
                            System.out.println("Thanks for using this. Exiting the program now");
                            break;
                        case 1:
                            System.out.println("Please specify the file to send");
                            String sendFileName = scan.next();
                            File file = new File(sendFileName);
                            Socket workerSocket = connect();
                            sendHeader(file.getPath(), choice, workerSocket);
                            send(file, workerSocket);
                            disconnet(workerSocket);

                            break;
                        case 2:
                            System.out.println("Please specify the file to get");
                            String recFileName = scan.next();
                            File file1 = new File(recFileName);
                            Socket workerSocket1 = connect();
                            sendHeader(file1.getPath(), choice, workerSocket1);
                            get(file1, workerSocket1);
                            disconnet(workerSocket1);
                            break;
                    }
                } else
                {
                    System.out.println("Please enter a valid choice");
                    choice = 0;
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
