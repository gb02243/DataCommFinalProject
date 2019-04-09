package datacommfinalproject;

import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static ServerSocket ss = null;
    private static Socket s = null;

    private static final int maxClients = 10;
    private static final ClientHandler[] threads = new ClientHandler[maxClients];

    public static void main(String[] args) {
        try{
            ss = new ServerSocket(1201);
            // TODO remove
            System.out.println("Server - Server started");
        }catch(Exception e){
            e.printStackTrace();
        }

        //create a new socket & thread for each client
        while(true){
            try{
                s = ss.accept();
                // TODO remove
                System.out.println("Server - Client connected");
                int i = 0;
                for (i = 0; i < maxClients; i++) {
                    if(threads[i] == null){
                        (threads[i] = new ClientHandler(s, threads)).start();
                        // TODO remove
                        System.out.println("Server - thread created for client ");
                        break;
                    }
                }
                if(i == maxClients){
                    PrintStream os = new PrintStream(s.getOutputStream());
                    os.println(maxClients+" clients are already connected.");
                    os.close();
                    s.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

}
