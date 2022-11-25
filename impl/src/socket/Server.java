package socket;

import entity.Item;
import entity.Record;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Timer;
import java.util.*;

import static enumeration.MsgIndicator.*;
import static java.lang.System.exit;
import static utils.PanelUtils.addMessageToQueue;
import static utils.PanelUtils.getMessageDisplay;
import static utils.SocketUtils.countDownHandler;
import static utils.SocketUtils.sendToAll;
import static utils.TableUtils.itemInfo;

public class Server extends JFrame {

    private static JTextArea jTextArea; // GUI window
    private final Font font = new Font(null, Font.BOLD,16); // GUI font

    private static final HashMap<String, ServerThread> threads = new HashMap<>(); // thread hashmap, stores all user's thread
    private static final HashSet<String> all_username = new HashSet<>(); // username hashset, use to check username exists
    private static final List<Item> all_items = new ArrayList<>(); // item list
    private static final List<Record> records = new ArrayList<>(); // record list
    private static final Map<Integer, String> msgDisplayQueue = new HashMap<>(); // GUI message display queue
    private static boolean server_running = true; // true: start count down; false: finish count down
    static Integer t = 45; // count down from 45

    public static HashSet<String> getAll_username() {
        return all_username;
    }

    public static List<Item> getAll_items() {
        return all_items;
    }

    public static List<Record> get_records() {
        return records;
    }

    public void run() throws IOException {
        initialItem();
        windowHandler();
        timeCount();

        ServerSocket serverSocket = new ServerSocket(58729);
        while (true){
            if(records.isEmpty()) { // initial records
                records.add(new Record(getAll_items().get(0).getPrice(), "-", "-"));
            }
            Socket accept = serverSocket.accept();
            String address = accept.getInetAddress() + ":" + accept.getPort();
            addMessageToQueue(msgDisplayQueue, "Connect success: " + address);
            jTextArea.setText(getMessageDisplay(msgDisplayQueue));

            ServerThread serverThread = new ServerThread(accept, threads); // create thread for user's server side
            threads.put(address, serverThread);
            serverThread.start();
        }
    }

    /**
     * Initial item list
     */
    private void initialItem() {
        all_items.add(new Item("Violin", 1000));
        all_items.add(new Item("Piano", 5000));
        all_items.add(new Item("Drum", 500));
        all_items.add(new Item("Guitar", 800));
        all_items.add(new Item("Flute", 200));
    }

    /**
     * Basic settings for GUI
     */
    private void initialWindow() {

        // Basic settings
        setSize(600,300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setTitle("SERVER");
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
        jTextArea.setBackground(Color.GREEN);
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
            String text = jTextField.getText().trim();
            if(text.equals(String.valueOf(QUIT))) { // if user input "QUIT"
                server_running = false; // Finish count down, close the countdown thread
                sendToAll(FORCE_QUIT, "", threads);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                exit(0);
            } else if (text.equals(String.valueOf(ALLITEM))) { // if user input "ALLITEM"
                msgDisplayQueue.clear();
                addMessageToQueue(msgDisplayQueue, itemInfo(all_items));
            } else if (text.split(" ")[0].equals(String.valueOf(ADD))) { // if user input "ADD"
                addItem(text);
            } else {
                addMessageToQueue(msgDisplayQueue, text);
            }
            jTextArea.setText(getMessageDisplay(msgDisplayQueue)); // display the message to window
            jTextField.setText(""); // Reset the input box empty
        });
    }

    /**
     * Initial GUI when server running
     */
    private void windowHandler() {
        initialWindow();
        addMessageToQueue(msgDisplayQueue, "Server is running...");
        jTextArea.setText(getMessageDisplay(msgDisplayQueue));
        setVisible(true);
    }

    /**
     * count down method
     */
    private static void timeCount() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() { // create a new thread to count down
                while(server_running) {
                    t--;
                    try {
                        Thread.sleep(1000); // wait 1 second
                        if(!all_username.isEmpty()) { // prevent crash
                            countDownHandler(t, threads); // send count down message to everyone (update progress bar)
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(t == 0) { // reset countdown index to 45, prevent the countdown close when nobody in the auction
                        t = 45;
                    }
                }
            }
        }, 0);
    }

    /**
     * reset count down
     */
    public static void resetTimer() {
        t = 45;
    }

    /**
     * display the user left message on server window
     */
    public static void userLeftMsg(String s) {
        addMessageToQueue(msgDisplayQueue, s);
        jTextArea.setText(getMessageDisplay(msgDisplayQueue));
    }

    /**
     * display auction finish message on server window
     */
    public static void allAuctionFinish() {
        addMessageToQueue(msgDisplayQueue, "All auction finish!");
        jTextArea.setText(getMessageDisplay(msgDisplayQueue));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        exit(0);
    }

    /**
     * Add a new item
     * @param text message input
     */
    private void addItem(String text) {
        String item_name = text.split(" ")[1];
        String item_price = text.split(" ")[2];
        if(item_price.chars().allMatch(Character::isDigit)) { // check if the bid price is integer
            int price = Integer.parseInt(item_price);
            Item i = new Item(item_name, price);
            all_items.add(i);
            msgDisplayQueue.clear();
            addMessageToQueue(msgDisplayQueue, itemInfo(all_items));
        } else {
            addMessageToQueue(msgDisplayQueue, "WARNING: Invalid price.");
        }
    }
}
