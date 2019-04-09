package datacommfinalproject;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class ClientHandler extends Thread {
    private String username = null;
    private DataInputStream din = null;
    private PrintStream dout = null;
    private Socket s = null;
    private final ClientHandler[] threads;
    private int clientCount;

    public ClientHandler(Socket s, ClientHandler[] threads) {
        this.s = s;
        this.threads = threads;
        clientCount = threads.length;
        // TODO remove
        System.out.println("ClientHandler - ClientHandler constructed");
    }

    public void run() {
        int clientCount = this.clientCount;
        ClientHandler[] threads = this.threads;

        try {
            din = new DataInputStream(s.getInputStream());
            dout = new PrintStream(s.getOutputStream());
            // TODO remove
            System.out.println("ClientHandler - input & output streams created");
            String name;
            while (true) {
                name = din.readLine().trim();
                // TODO remove
                System.out.println("ClientHandler - Name received");
                break;
            }

            dout.println("Connected as: " + name
                    + " type /exit to leave");
            synchronized (this) {
                for (int i = 0; i < clientCount; i++) {
                        username = "@" + name;
                        break;
                }
                for (int i = 0; i < clientCount; i++) {
                    if (threads[i] != null && threads[i] != this) {
                        threads[i].dout.println("--- "+name+" has joined the chat ---");
                        // TODO remove
                        System.out.println("ClientHandler - join message broadcasted to client "+i);
                    }
                }
            }

            // send messages
            while (true) {
                String message = din.readLine();
                if (message.startsWith("/exit")) {
                    break;
                }
                synchronized (this) {
                    for (int i = 0; i < clientCount; i++) {
                        if (threads[i] != null && threads[i].username != null) {
                            threads[i].dout.println("<" + name + ">: " + message);
                            // TODO remove
                            System.out.println("ClientHandler - message broadcasted to client "+i);
                        }
                    }
                }

            }

            // handle user leaving
            synchronized (this) {
                for (int i = 0; i < clientCount; i++) {
                    if (threads[i] != null && threads[i] != this
                            && threads[i].username != null) {
                        threads[i].dout.println("--- "+name+" has left the chat ---");
                        // TODO remove
                        System.out.println("ClientHandler - leave message broadcasted to client "+i);
                    }
                }
            }

            // allow new connections
            synchronized (this) {
                for (int i = 0; i < clientCount; i++) {
                    if (threads[i] == this) {
                        threads[i] = null;
                        // TODO remove
                        System.out.println("ClientHandler - client "+i+" thread removed");
                    }
                }
            }

            // close streams and socket
            din.close();
            dout.close();
            s.close();
            // TODO remove
            System.out.println("ClientHandler - Input & Output streams closed");
            System.out.println("ClientHandler - Client socket closed");
        } catch (IOException e) {
        }
    }

}
