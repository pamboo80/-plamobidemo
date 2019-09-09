package in.bangbit.plamobidemo;

import com.google.android.gms.maps.model.LatLng;

public class Message {
    private String username;
    private String imgUrl;
    private LatLng latLng;
    private String email;
    private String phone;
    private int chat;
    private String message;
    private int messageType;
    private long messageTimeStamp;

    //Chat values
    public static final int CHAT_UNDEFINED = -1;
    public static final int CHAT_NOT_ALLOWED = 0;
    public static final int CHAT_ALLOWED = 1;
    //Message type values
    public static final int MESSAGE_TYPE_GENERAL = 0;
    public static final int MESSAGE_TYPE_SELL = 1;
    public static final int MESSAGE_TYPE_SEEK = 2;

    public Message(String username, String imgUrl, double latitude, double longitude, String email, String phone, String chat,
                   String message, String messageType, long messageTimeStamp) { //From JSON
        this.username = username;
        this.imgUrl = imgUrl;
        this.latLng = new LatLng(latitude,longitude);
        this.email = email;
        this.phone = phone;
        if(!chat.equals("")) this.chat = Integer.parseInt(chat);
                else this.chat = CHAT_UNDEFINED;
        this.message = message;
        switch (messageType) {
            case "General":
                this.messageType = MESSAGE_TYPE_GENERAL;
                break;
            case "Sell":
                this.messageType = MESSAGE_TYPE_SELL;
                break;
            case "Seek":
                this.messageType = MESSAGE_TYPE_SEEK;
                break;
        }
        this.messageTimeStamp = messageTimeStamp;
    }

    public Message(String username, String imgUrl, double latitude, double longitude, String email, String phone, int chat,
                   String message, int messageType, long messageTimeStamp) { //From database
        this.username = username;
        this.imgUrl = imgUrl;
        this.latLng = new LatLng(latitude,longitude);
        this.email = email;
        this.phone = phone;
        this.chat = chat;
        this.message = message;
        this.messageType = messageType;
        this.messageTimeStamp = messageTimeStamp;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public int getMessageType() {
        return messageType;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getText() {
        return message;
    }

    public long getMessageTimeStamp() {
        return messageTimeStamp;
    }

    public int getChat() {
        return chat;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }
}
