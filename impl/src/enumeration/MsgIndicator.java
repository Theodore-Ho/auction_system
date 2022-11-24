package enumeration;

public enum MsgIndicator {

    // Following are the indicator of message server send to client
    REGISTER_SUCCESS,
    REGISTER_FAIL,
    SHOW_MENU,
    SHOW_AUCTION,
    SHOW_ITEM,
    NEW_BID,
    NEW_ITEM,
    QUIT_SUCCESS,
    PROGRESS_BAR,
    AUCTION_FINISH,
    NEW_AUCTION_START,
    NO_MORE_AUCTION,
    FORCE_QUIT,

    // Following are the indicator of message client send to server
    AUCTION,
    ALLITEM,
    MENU,
    QUIT,
    ADD

}
