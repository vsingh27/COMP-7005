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
    private static final int FILE_BUFFER = 16 * 1024;

    private static void send(String file)
    {

    }

    /*Private method to get files
    will take a file name to get
    will create a socket as well
    input stream*/
    private static int get(File file, Socket client) throws IOException
    {

        //Getting the requested file
        String Directory = "/Users/vishavpreetsingh/file2";
        // File file = new File();
        System.out.println("Getting file from " + client.getRemoteSocketAddress());
        if (file.isDirectory())
        {
            System.out.println("Not a valid File");
            return -1;
        }
        int bytesRead;
        int current = 0;

        byte[] serverData = new byte[FILE_BUFFER];
        InputStream is = client.getInputStream();
        String newFileName = "/Users/vishavpreetsingh/file2/" + file.getName();


        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(newFileName));

        do
        {
            bytesRead =
                    is.read(serverData, current, (serverData.length - current));
            if (bytesRead >= 0)
            {
                current += bytesRead;
                System.out.println("Cureent " + current);
            }
        } while (bytesRead > -1);

        bos.write(serverData, 0, current);
        bos.flush();
        System.out.println("File " + newFileName
                + " downloaded (" + current + " bytes read)");

        //client.close();
        return 0;
    }

    // Creates a socket
    private static Socket connect() throws IOException
    {
        System.out.println("Creating a socket...");
        Socket client = new Socket(SERVER_NAME, PORT);
        System.out.println("Socket created at " + client.getLocalSocketAddress() + "...");
        return client;
    }

    //disconnects the socket
    private static void disconnet(Socket soc) throws IOException
    {
        soc.close();
    }


    private static void sendHeader(String filePath, int choice, Socket soc)
    {
        System.out.println("Sending Header Info to the Server");
        System.out.println("File: " + filePath);
        System.out.println("Choice: " + choice);
        try
        {
            OutputStream outToServer = soc.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            out.writeInt(2);
            out.writeUTF(filePath);

        } catch (IOException e)
        {
            System.out.println("Error while sending header from client");
            e.printStackTrace();
        }
    }


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
                            //Error checking method for the file
                            String sendFileName = scan.next();
                            send(sendFileName);
                            break;
                        case 2:
                            System.out.println("Please specify the file to get");
                            String recFileName = scan.next();
                            File file = new File(recFileName);
                            Socket controlSocket = connect();
                            sendHeader(file.getPath(), choice, controlSocket);
                            Socket workerSocket = connect();
                            get(file, workerSocket);
                            disconnet(workerSocket);
                            break;
                    }
                } else
                {
                    System.out.println("Please enter a valid choice");
                    choice = 0;
                }
            }
            //disconnet(controlSocket);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
