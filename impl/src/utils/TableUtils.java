package utils;

import entity.Item;
import entity.Record;

import java.util.List;

import static enumeration.MsgIndicator.*;
import static enumeration.MsgIndicator.QUIT;

public class TableUtils {

    /**
     * Generate manu message
     * @return Menu table in String format
     */
    public static String clientMenuInfo() {
        String s = "Menu: (case sensitive)\n\n";
        s += AUCTION + ".\tJoin the current auction\n";
        s += ALLITEM + ".\tCheck all the auction items\n";
        s += MENU + ".\tOpen Menu\n";
        s += QUIT + ".\tExit the session.";
        return s;
    }

    /**
     * Generate item list message
     * @param all_items All the items of the auction
     * @return Item list table in String format
     */
    public static String itemInfo(List<Item> all_items) {
        StringBuilder s = new StringBuilder("You can add your item input with format \"ADD ITEM PRICE\".");
        s.append("\nNo.\tName\tPrice");

        // Item list should start on the current auction item, and no longer than 8, excess part won't display
        int display_item_max = 8; // Perfect on my Mac screen, but not very good on my Windows
        int item_size = all_items.size();
        int item_start = item_size < display_item_max ? 0 : item_size - display_item_max;
        for(int i = item_start; i < item_size; i++) {
            s.append("\n").append(i + 1) // Item No.
                    .append("\t").append(all_items.get(i).getName()); // Item name
            if(i == 0) {
                s.append("\t").append(all_items.get(i).getPrice()) // Item price
                        .append("\t").append("current");
            } else {
                s.append("\t").append(all_items.get(i).getPrice());
            }
        }
        return s.toString();
    }

    /**
     * Generate auction records list message
     * @param records All the auction records of the item
     * @param all_items All the items of the auction
     * @param with_head Display auction head
     * @return Auction records list table in String format
     */
    public static String auctionInfo(List<Record> records, List<Item> all_items, boolean with_head) {
        String s;
        int last_bid = records.size() - 1;
        if(records.size() == 0) {
            last_bid = 0; // prevent crash
        }
        if(with_head) { // Case with head, such as user input "AUCTION" then move to auction screen
            s = "Current auction item: " + all_items.get(0).getName();
            s += "\nPlease input your bid.";
            s += "\nTimes\tBid\tClient\tTimestamp";
            s += "\n" + (last_bid) + "\t" // Records No. (How many times of the bid is?)
                    + records.get(last_bid).getPrice() // Bid price
                    + "\t" + records.get(last_bid).getUser() // Bid user (Who bid this?)
                    + "\t" + records.get(last_bid).getTimestamp(); // Bid timestamp
        } else { // Case 2: Case without head, such as user already in auction screen, just need to update the display
            s = (last_bid) + "\t"
                    + records.get(last_bid).getPrice()
                    + "\t" + records.get(last_bid).getUser()
                    + "\t" + records.get(last_bid).getTimestamp();
        }
        return s;
    }

}
