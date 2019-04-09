package datacommfinalproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

public class Client {

    static class ClientObservable extends Observable {
        private Socket s;
        private OutputStream dout;

        @Override
        public void notifyObservers(Object arg) {
            super.setChanged();
            super.notifyObservers(arg);
        }

        // create socket for observers
        public void InitSocket(String server, int port) throws IOException {
            s = new Socket(server, port);
            // TODO remove
            System.out.println("Client - ClientObservable - InitSocket(): Client Socket Created");
            dout = s.getOutputStream();
            // TODO remove
            System.out.println("Client - ClientObservable - InitSocket(): Output Stream Created");
            Thread receivingThread = new Thread() {
                @Override
                public void run() {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null)
                            notifyObservers(line);
                        // TODO remove
                        System.out.println("Client - ClientObservable - InitSocket - Thread - run(): Observers Notified");
                    } catch (IOException ex) {
                        notifyObservers(ex);
                    }
                }
            };
            receivingThread.start();
            // TODO remove
            System.out.println("Client - ClientObservable - InitSocket (): thread started");
        }

        // carriage return
        private static final String CRLF = "\r\n";

        // send message
        public void send(String text) {
            try {
                // TODO remove
                dout.write((text + CRLF).getBytes());
                System.out.println("Client - send(): text written to output stream");
                dout.flush();
                System.out.println("Client - send(): output stream flushed");
            } catch (IOException ex) {
                notifyObservers(ex);
            }
        }

        // close socket
        public void close() {
            try {
                s.close();
                // TODO remove
                System.out.println("Client - close():");
            } catch (IOException ex) {
                notifyObservers(ex);
            }
        }
    }

    static class ClientGui extends JFrame implements Observer {
        private JTextArea chat_display;
        private JTextField message_text;
        private JButton message_send;
        private static ClientObservable clientObservable;

        public ClientGui(ClientObservable clientObservable) {
            this.clientObservable = clientObservable;
            clientObservable.addObserver(this);
            // TODO remove
            System.out.println("Client - ClientGui(): Observer added");
            build();
            // TODO remove
            System.out.println("Client - ClientGui(): GUI Built");
        }

        // build the gui
        private void build() {
            chat_display = new JTextArea(20, 50);
            chat_display.setEditable(false);
            chat_display.setLineWrap(true);
            add(new JScrollPane(chat_display), BorderLayout.CENTER);

            Box box = Box.createHorizontalBox();
            add(box, BorderLayout.SOUTH);
            message_text = new JTextField();
            message_text.setPreferredSize( new Dimension( 200, 100 ) );
            message_send = new JButton("Send");
            box.add(message_text);
            box.add(message_send);

            // action for textfield and button
            ActionListener sendListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String message_out = message_text.getText();
                    if (message_out != null && message_out.trim().length() > 0)
                        clientObservable.send(message_out);
                    message_text.selectAll();
                    message_text.requestFocus();
                    message_text.setText("");
                }
            };
            message_text.addActionListener(sendListener);
            message_send.addActionListener(sendListener);

            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });

        }

        // update observers
        public void update(Observable o, Object arg) {
            final Object finalArg = arg;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    chat_display.append(finalArg.toString());
                    chat_display.append("\n");
                    // TODO remove
                    System.out.println(" Client - ClientGui - update(): chat_display updated");
                }
            });
        }
    }

    public static void main(String[] args) {
        int port = 1201;
        ClientObservable observable = new ClientObservable();

        JFrame frame = new ClientGui(observable);
        frame.setTitle("Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        // get server address from dialog
        JFrame addressdialog = new JFrame("Server Information");
        String server_address = JOptionPane.showInputDialog(addressdialog, "Enter the Server IP:");

        // connect to server
        try {
            observable.InitSocket(server_address, port);
            // TODO remove
            System.out.println("Client - connecting to server");
        } catch (Exception e) {
            System.out.println("Cannot connect to " + server_address + ":" + port);
            ((ClientGui) frame).chat_display.append("Cannot connect to " + server_address + ":" + port);
            e.printStackTrace();
        }

        // get username from dialog
        JFrame userdialog = new JFrame("User Information");
        String uname = JOptionPane.showInputDialog(userdialog, "Enter your username:");
        ClientGui.clientObservable.send(uname);
        // TODO remove
        System.out.println("Client - username sent to server");


    }
}
