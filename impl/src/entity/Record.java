package entity;

public class Record {

    private final int price;
    private final String user;
    private final String timestamp;

    public Record(int price, String user, String timestamp) {
        this.price = price;
        this.user = user;
        this.timestamp = timestamp;
    }

    public int getPrice() {
        return price;
    }

    public String getUser() {
        return user;
    }

    public String getTimestamp() {
        return timestamp;
    }

}
