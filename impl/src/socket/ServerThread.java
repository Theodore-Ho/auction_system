package socket;

import entity.Item;
import entity.Record;
import utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static enumeration.MsgIndicator.*;
import static socket.Server.*;
import static utils.SocketUtils.sendToOthers;
import static utils.TableUtils.*;

public class ServerThread extends Thread {
    private final Socket socket;
    private final HashMap<String, ServerThread> threads;
    private final InputStream is;
    private final OutputStream os;
    private String username;
    private boolean in_auction; // true: user in the auction screen; false: user in other screen
    private boolean in_item; // true: user in the item list screen; false: user in other screen

    private boolean valid_username = false; // true: user register success; false: user hasn't been register success

    public ServerThread(Socket socket, HashMap<String, ServerThread> threads) throws IOException {
        this.socket = socket;
        this.threads = threads;
        this.is = socket.getInputStream();
        this.os = socket.getOutputStream();
        this.username = "";
        this.in_auction = false;
        this.in_item = false;
    }

    private String getUsername() {
        return username;
    }

    private void setUsername(String s) {
        this.username = s;
    }

    private boolean getIn_auction() {
        return in_auction;
    }

    private void setIn_auction(boolean b) {
        this.in_auction = b;
    }

    private boolean getIn_item() {
        return in_item;
    }

    private void setIn_item(boolean b) {
        this.in_item = b;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        String socketInfo = socket.getInetAddress() + ":" + socket.getPort();
        String msg = "";
        while (!msg.equals(String.valueOf(QUIT))){ // Thread finish when input "QUIT"
            msg = IOUtils.readString(is).trim();
            if(msg.equals(String.valueOf(AUCTION))) { // if user input "AUCTION"
                this.setIn_auction(true);
                this.setIn_item(false);
            } else if (msg.equals(String.valueOf(MENU))) { // if user input "MENU"
                this.setIn_auction(false);
                this.setIn_item(false);
            } else if (msg.equals(String.valueOf(ALLITEM))) { // if user input "ALLITEM"
                this.setIn_auction(false);
                this.setIn_item(true);
            }

            // User register
            if(!msg.equals(String.valueOf(QUIT)) && !msg.equals("") && !valid_username) {
                if(!getAll_username().contains(msg.toLowerCase())) { // valid username
                    this.setUsername(msg.toLowerCase());
                    getAll_username().add(this.getUsername());
                    IOUtils.writeString(os, REGISTER_SUCCESS + "$" + this.getUsername() + "$\n" + clientMenuInfo());
                    valid_username = true; // Set register success
                } else { // username exists
                    IOUtils.writeString(os, REGISTER_FAIL + "$" + msg.toLowerCase());
                }

            // Open menu
            } else if (msg.equals(String.valueOf(MENU))) {
                IOUtils.writeString(os, SHOW_MENU + "$" + clientMenuInfo());

            // Open auction table
            } else if (msg.equals(String.valueOf(AUCTION))) {
                IOUtils.writeString(os, SHOW_AUCTION + "$" + auctionInfo(get_records(), getAll_items(), true));

            // Open item table
            } else if (msg.equals(String.valueOf(ALLITEM))) {
                IOUtils.writeString(os, SHOW_ITEM + "$" + itemInfo(getAll_items()));

            // User quit
            } else if (msg.equals(String.valueOf(QUIT))) {
                threads.remove(socketInfo); // remove the thread, prevent crash on server console
                if(this.getUsername().equals("")) {
                    IOUtils.writeString(os, String.valueOf(QUIT_SUCCESS));
                    userLeftMsg("User " + socketInfo + " left the session.");
                } else {
                    IOUtils.writeString(os, String.valueOf(QUIT_SUCCESS));
                    getAll_username().remove(this.getUsername());
                    userLeftMsg("User " + this.getUsername() +  " (" + socketInfo + ") left the session.");
                }
                try {
                    is.close();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            // User bid
            } else if (!msg.equals("") && this.getIn_auction()) {
                String response;
                if(msg.chars().allMatch(Character::isDigit)) { // check if the bid price is integer
                    int bid = Integer.parseInt(msg);
                    Record i = get_records().get(get_records().size() - 1);
                    if(bid > i.getPrice()) { // check if the bid is greater than the last bid
                        resetTimer(); // reset main thread countdown
                        Long timeStamp = System.currentTimeMillis();
                        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
                        String sd = sdf.format(new Date(Long.parseLong(String.valueOf(timeStamp))));
                        Record new_record = new Record(bid, this.getUsername(), sd);
                        getAll_items().get(0).setPrice(bid);
                        get_records().add(new_record);
                        response = auctionInfo(get_records(), getAll_items(), false);
                        sendToOthers(threads, socketInfo, response, NEW_BID);
                    } else {
                        response = "WARNING: Your bid must greater than current bid.";
                    }
                } else {
                    response = "WARNING: Invalid bid, please enter an integer number.";
                }
                IOUtils.writeString(os, NEW_BID + "$" + response);

            // User add item
            } else if (this.getIn_item() && msg.split(" ")[0].equals(String.valueOf(ADD))) {
                String response;
                String item_name = msg.split(" ")[1];
                String item_price = msg.split(" ")[2];
                if(item_price.chars().allMatch(Character::isDigit)) { // check if the item price is integer
                    int price = Integer.parseInt(item_price);
                    Item i = new Item(item_name, price);
                    getAll_items().add(i);
                    response = itemInfo(getAll_items());
                    sendToOthers(threads, socketInfo, response, NEW_ITEM);
                } else {
                    response = "WARNING: Invalid price.";
                }
                IOUtils.writeString(os, NEW_ITEM + "$" + response);
            }
        }
    }
}
