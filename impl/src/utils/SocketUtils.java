package utils;

import entity.Record;
import enumeration.MsgIndicator;
import socket.ServerThread;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

import static enumeration.MsgIndicator.*;
import static enumeration.MsgIndicator.NEW_AUCTION_START;
import static socket.Server.*;
import static utils.TableUtils.auctionInfo;

public class SocketUtils {

    /**
     * Send message to other users except me
     * @param threads All the threads of the ServerThread (client thread)
     * @param socketInfo Socket information of the user who call this method
     * @param response The message send to client side
     * @param newItem Message indicator to let client side identify the action
     * Difference between sendToOthers() and sendToAll():
     *      They do same thing. But in the server side logic, sometimes the message is for the user only, not for all (E. invalid input warning).
     */
    public static void sendToOthers(HashMap<String, ServerThread> threads, String socketInfo, String response, MsgIndicator newItem) {
        for (String user : threads.keySet()) { // Loop all user socket information
            if (!user.equals(socketInfo)) { // Don't send this to me
                Socket userSocket = threads.get(user).getSocket(); // Get the socket
                try {
                    OutputStream userOs = userSocket.getOutputStream(); // Get OutputStream
                    IOUtils.writeString(userOs, newItem + "$" + response); // Send to client
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Send message to everyone
     * @param msg Message indicator to let client side identify the action
     * @param response The message send to client side
     * @param threads All the threads of the ServerThread (client thread)
     */
    public static void sendToAll(MsgIndicator msg, String response, HashMap<String, ServerThread> threads) {
        for (String user : threads.keySet()) {
            Socket userSocket = threads.get(user).getSocket();
            try {
                OutputStream userOs = userSocket.getOutputStream();
                IOUtils.writeString(userOs, msg + "$" + response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handle the server side countdown, and send the message to client
     * @param t Countdown time
     * @param threads All the threads of the ServerThread (client thread)
     */
    public static void countDownHandler(int t, HashMap<String, ServerThread> threads) throws InterruptedException {
        String progressBar = "Please input your bid.\t" + generateProgressBar(t); // progress bar message (fake progress bar)
        sendToAll(PROGRESS_BAR, progressBar, threads); // send to everyone
        if (t == 0) { // when countdown finish
            String response; // other response message

            if(get_records().size() > 1) { // somebody bid the item
                response = "Auction finish, the buyer is " + get_records().get(get_records().size() - 1).getUser(); // display the buyer's name
                sendToAll(AUCTION_FINISH, response, threads);
                getAll_items().remove(0); // remove the first item from ArrayList, this allowed the second one become first one, and then automatically start auction

            } else { // nobody bid the item
                response = "No one get the item, auction continue.";
                sendToAll(AUCTION_FINISH, response, threads);
            }

            Thread.sleep(5000); // Wait 5 seconds, client can read the message send in the previous step
            if(getAll_items().isEmpty()) { // no more item to be auction!! client and server will close automatically.
                sendToAll(NO_MORE_AUCTION, "All auction finish!", threads); // let client know the good news
                allAuctionFinish(); // tell server all auction finish, and close server window
            } else { // start a new auction for next item
                get_records().clear(); // empty auction records
                get_records().add(new Record(getAll_items().get(0).getPrice(), "-", "-")); // initial the auction records
                resetTimer(); // reset countdown
                response = auctionInfo(get_records(), getAll_items(), true);
                sendToAll(NEW_AUCTION_START, response, threads);
            }
        }
    }

    /**
     * Generate the fake-text-progress-bar
     * @param t Countdown time
     */
    private static String generateProgressBar(int t) {
        StringBuilder s = new StringBuilder("|");
        for(int i = 45; i > 0; i--) {
            if(i > t) {
                s.append(":");
            } else {
                s.append(".");
            }
        }
        s.append("|");
        return s.toString();
    }

}
