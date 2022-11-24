package socket;

import enumeration.MsgIndicator;
import utils.IOUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static enumeration.MsgIndicator.*;
import static utils.PanelUtils.addMessageToQueue;
import static utils.PanelUtils.getMessageDisplay;

public class Client extends JFrame {
    private InputStream is;
    private OutputStream os;
    private JTextArea jTextArea; // GUI window

    private final Font font = new Font(null,Font.BOLD,16); // GUI font
    private final Map<Integer, String> msgDisplayQueue = new HashMap<>(); // GUI message display queue
    private boolean in_auction = false; // true: user in the auction screen; false: user in other screen
    private boolean in_item = false; // true: user in the item list screen; false: user in other screen

    public void run(){
        initialWindow();
        initialSession();
        connectServer();
        sessionHandler();
    }

    /**
     * Basic settings for GUI
     */
    private void initialWindow() {
        // Basic settings
        setSize(600,300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setTitle("undefined");
        setLayout(null);
        setBackground(Color.WHITE);

        // display area and input area settings
        defineDisplayArea();
        defineInputArea();
    }

    /**
     * GUI display message part definition
     */
    private void defineDisplayArea() {
        jTextArea = new JTextArea();
        jTextArea.setBounds(0,0,600,220);
        jTextArea.setFont(font);
        add(jTextArea);
    }

    /**
     * GUI input message part definition
     */
    private void defineInputArea() {
        JTextField jTextField = new JTextField();
        jTextField.setFont(font);
        jTextField.setBounds(0,220,600,50);
        add(jTextField);
        jTextField.addActionListener(e -> {
            String text = jTextField.getText();
            if (text.equals(String.valueOf(AUCTION))) { // if user input "AUCTION"
                in_auction = true;
                in_item = false;
            } else if (text.equals(String.valueOf(MENU))) { // if user input "MENU"
                in_auction = false;
                in_item = false;
            } else if (text.equals(String.valueOf(ALLITEM))) { // if user input "ALLITEM"
                in_auction = false;
                in_item = true;
            }
            jTextField.setText(""); // Reset the input box empty
            IOUtils.writeString(os, text); // Send message to server
        });
    }

    /**
     * Definite the start screen of client window
     */
    private void initialSession(){
        addMessageToQueue(msgDisplayQueue, "Hello, please input your username.");
        jTextArea.setText(getMessageDisplay(msgDisplayQueue));
        setVisible(true);
    }

    /**
     * Connect to server
     */
    private void connectServer() {
        try {
            Socket socket = new Socket("localhost", 58729);
            is = socket.getInputStream();
            os = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main method of client, handling all the message display
     */
    private void sessionHandler(){
        while (true){
            String response = IOUtils.readString(is);
            if(checkIndicator(response, REGISTER_SUCCESS)) {
                msgDisplayQueue.clear();
                setTitle(response.split("\\$")[1]);
                addMessageToQueue(msgDisplayQueue, "Welcome " + response.split("\\$")[1] + "!!!");
                handleNewLineFromServer(response.split("\\$")[2]);

            } else if (checkIndicator(response, REGISTER_FAIL)) {
                addMessageToQueue(msgDisplayQueue, "Sorry, the name " + response.split("\\$")[1] + " has been used.");
                addMessageToQueue(msgDisplayQueue, "Please input another username.");

            } else if (checkIndicator(response, SHOW_MENU)) {
                msgDisplayQueue.clear();
                handleNewLineFromServer(response.split("\\$")[1]);

            } else if (checkIndicator(response, QUIT_SUCCESS)) {
                dispose();
                break;

            } else if (checkIndicator(response, SHOW_AUCTION)) {
                msgDisplayQueue.clear();
                handleNewLineFromServer(response.split("\\$")[1]);

            } else if (checkIndicator(response, SHOW_ITEM)) {
                msgDisplayQueue.clear();
                addMessageToItemTable(response.split("\\$")[1]);

            } else if (checkIndicator(response, NEW_BID)) {
                if(in_auction) {
                    addMessageToAuctionTable(response.split("\\$")[1]);
                }

            } else if (checkIndicator(response, PROGRESS_BAR)) {
                if(in_auction) {
                    changeProgressBar(response.split("\\$")[1]);
                }

            } else if (checkIndicator(response, NEW_ITEM)) {
                if(in_item) {
                    msgDisplayQueue.clear();
                    addMessageToItemTable(response.split("\\$")[1]);
                }
            } else if (checkIndicator(response, AUCTION_FINISH)) {
                if(in_auction) {
                    addMessageToAuctionTable(response.split("\\$")[1]);
                }
            } else if (checkIndicator(response, NEW_AUCTION_START)) {
                if(in_auction) {
                    msgDisplayQueue.clear();
                    handleNewLineFromServer(response.split("\\$")[1]);
                }
            } else if (checkIndicator(response, NO_MORE_AUCTION)) {
                msgDisplayQueue.clear();
                addMessageToQueue(msgDisplayQueue, response.split("\\$")[1]);
                jTextArea.setText(getMessageDisplay(msgDisplayQueue));
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dispose(); // turn off window
                break; // finish thread
            } else if (checkIndicator(response, FORCE_QUIT)) {
                dispose(); // turn off window
                break; // finish thread
            }
            jTextArea.setText(getMessageDisplay(msgDisplayQueue)); // display the message to window
        }
    }

    /**
     * Compare the message and the indicator (simplify the code in sessionHandler().)
     * @param s message from server
     * @param i message indicator
     * @return bool result
     */
    private boolean checkIndicator(String s, MsgIndicator i) {
        return s.split("\\$")[0].equals(String.valueOf(i));
    }

    /**
     * When new bid coming, add it to auction table
     * @param s message from server
     */
    private void addMessageToAuctionTable(String s) {
        if(msgDisplayQueue.size() == 10) {
            for(int i = 3; i < msgDisplayQueue.size() - 1; i++) { // keep the auction table head
                msgDisplayQueue.put(i, msgDisplayQueue.get(i + 1)); // scroll only the record section
            }
            msgDisplayQueue.put(9, s);
        } else {
            msgDisplayQueue.put(msgDisplayQueue.size(), s);
        }
    }

    /**
     * Update the fake progress bar
     * @param s message from server
     */
    private void changeProgressBar(String s) {
        msgDisplayQueue.put(1, s);
    }

    /**
     * When new item coming, add it to item list table
     * @param s message from server
     */
    private void addMessageToItemTable(String s) {
        if(msgDisplayQueue.size() == 10) {
            for(int i = 2; i < msgDisplayQueue.size() - 1; i++) { // keep the item list table head
                msgDisplayQueue.put(i, msgDisplayQueue.get(i + 1)); // scroll only the list section
            }
            msgDisplayQueue.put(9, s);
        } else {
            msgDisplayQueue.put(msgDisplayQueue.size(), s);
        }
    }

    /**
     * Split the message by new_line and add each to message queue
     * @param s message from server
     */
    private void handleNewLineFromServer(String s) {
        while(s.contains("\n")) {
            String temp = s.substring(0, s.indexOf("\n"));
            addMessageToQueue(msgDisplayQueue, temp);
            s = s.substring(s.indexOf("\n") + "\n".length());
        }
        addMessageToQueue(msgDisplayQueue, s);
    }
}
