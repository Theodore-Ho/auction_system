package utils;

import java.util.Map;

public class PanelUtils {

    /**
     * Add message to the display queue
     * @param msgDisplayQueue Message queue hashmap
     * @param s Message need to be added in
     */
    public static void addMessageToQueue(Map<Integer, String> msgDisplayQueue, String s) {
        if(msgDisplayQueue.size() == 10) { // Limitation of the window height, 10 rows is the max (working on my Mac)
            for(int i = 0; i < msgDisplayQueue.size() - 1; i++) { // If the queue is full (10 messages)
                msgDisplayQueue.put(i, msgDisplayQueue.get(i + 1)); // Move every message forward one space
            }
            msgDisplayQueue.put(9, s); // add the new message at the end
        } else { // if the queue not full (less than 10 messages)
            msgDisplayQueue.put(msgDisplayQueue.size(), s); // add the message to end
        }
    }

    /**
     * Generate the window display message
     * @param msgDisplayQueue Message queue hashmap
     */
    public static String getMessageDisplay(Map<Integer, String> msgDisplayQueue) {
        StringBuilder message = new StringBuilder();
        for(int i = 0; i < msgDisplayQueue.size(); i++) {
            if(i == 0) {
                message.append(msgDisplayQueue.get(i));
            } else {
                message.append("\n").append(msgDisplayQueue.get(i)); // Message followed by a new line
            }
        }
        return String.valueOf(message);
    }

}
