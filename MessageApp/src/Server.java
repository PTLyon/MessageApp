import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class Server extends JFrame{

    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket socket;
    private Socket connection;

    //Construtor
    public Server(){
        super("Lyon's Instant Messenger");
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(
                new ActionListener(){
                    public void actionPerformed(ActionEvent event){
                        sendMessage(event.getActionCommand());
                        userText.setText("");
                    }
                }
        );
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow));
        setSize(300,150);
        setVisible(true);
    }

    //set up and run the server
    public void startRunning(){
        try{
            server = new ServerSocket(6789, 100);
            while(true){
                try{
                    waitForConnection();
                    setupStreams();
                    whileChatting();
                }catch(EOFException eofException){
                    showMessage("\n Server ended connection");
                }finally{
                    closeCrap();
                }
            }
        }catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

    //wait for connection, then display connection information
    private void waitForConnection() throws IOException{
        showMessage("\nWaiting for someone to connect...\n");
        connection = server.accept();
        showMessage("Now connected to " + connection.getInetAddress().getHostName());
    }

    //get stream to send and receive data
    private void setupStreams() throws IOException{
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage("\n Streams are now setup \n");
    }



    /***
     * during the chat conversation
     * @throws IOException
     */
    private void whileChatting() throws IOException{
        String message = "You are now connected";
        sendMessage(message);
        ableToType(true);
        do{
            try{
                message = (String) input.readObject();
                showMessage("\n" + message);
            }catch(ClassNotFoundException classNotFoundException){
                showMessage("\n idk what the user sent");
            }
        }while(!message.equals("CLIENT - END"));
    }

    /**
     *
     */
    private void closeCrap(){
        showMessage("\n Closing connections \n");
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
     * Sends message to the client
     * @param message
     */
    private void sendMessage(String message){
        try{
            output.writeObject("SERVER - "+ message);
            output.flush();
            showMessage("\n SERVER - " + message);
        }catch(IOException ioException){
            chatWindow.append("\n ERROR: DUDE I CAN'T SEND THAT MESSAGE");
        }
    }

    /**
     * Updates chatWindow
     * @param text
     */
    private void showMessage(final String text){
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        chatWindow.append(text);
                    }
                }
        );
    }


}
