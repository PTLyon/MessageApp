import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame {

    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message = "";
    private String serverIP;
    private Socket connection;

    //constructor
    public Client(String host) {
        super("Client for Lyon's Server");
        serverIP = host;
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        sendMessage(event.getActionCommand());
                        userText.setText("");
                    }
                }
        );
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow), BorderLayout.CENTER);
        setSize(300, 150);
        setVisible(true);
    }

    //connect to server
    public void startRunning() {
        try {
            connectToServer();
            setupStreams();
            whileChatting();
        } catch (EOFException eofException) {
            showMessage("\n client terminated the connection");
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        } finally {
            closeCrap();
        }
    }

    /**
     *
     * @throws IOException
     */
    private void connectToServer() throws IOException {
        showMessage("\n Attempting connection...");
        connection = new Socket(InetAddress.getByName(serverIP), 6789);
        showMessage("Connected to:" + connection.getInetAddress().getHostName());
    }

    /**
     * Sets up I/O streams
     * @throws IOException
     */
    private void setupStreams () throws IOException {
        output =  new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage("\n Streams are now connected, go!");
    }

    /**
     * While chatting
     */
    private void whileChatting() throws IOException{
        ableToType(true);
        do{
            try{
                message = (String) input.readObject();
                showMessage("\n " + message);
            }catch(ClassNotFoundException classNotFoundException){
                showMessage("\n I don't know that object type");
            }
        }while(!message.equals("SERVER - END"));
    }

    //close streams and sockets
    private void closeCrap(){
        showMessage("\n Closing crap down...");
        ableToType(false);
        try{
            output.close();
            input.close();
            connection.close();
        }catch(IOException ioException){
            ioException.printStackTrace();
        }
    }


    /**
     * send messages to the server
     * @param message
     */
    private void sendMessage(String message) {
        try{
            output.writeObject("CLIENT - " + message);
            output.flush();
            showMessage("\nCLIENT - " + message);
        }catch(IOException ioException){
            chatWindow.append("\n something messed up sending message");
        }
    }

    //change or update chat window
    private void showMessage(final String m){
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        chatWindow.append(m);
                    }
                }
        );
    }
    //gives user permission to type
    private void ableToType(final boolean tof){
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        userText.setEditable(tof);
                    }
                }
        );

    }
}
