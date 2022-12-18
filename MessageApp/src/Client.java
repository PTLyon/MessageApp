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
    public Client(String host){
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
    public void startRunning(){
        try{
            connectToServer();
            setupStreams();
            whileChatting();
        }catch(EOFException eofException){
            showMessage("\n client terminated the connection");
        }catch(IOException ioexception){
            ioexception.printStackTrace();
        }finally{
            closeCrap();
        }
    }

    private void connectToServer() throws IOException{
        showMessage("\n Attempting connection...");
        connection = new Socket(InetAddress.getByName(serverIP), 6789);
        showMessage("Connected to:" + connection.getInetAddress().getHostName())
    }

}
