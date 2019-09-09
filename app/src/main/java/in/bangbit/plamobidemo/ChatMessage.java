package in.bangbit.plamobidemo;

public class ChatMessage {

    private String username;
    private String urlPhoto;
    private String text;
    private long time; //in millis

    public ChatMessage(String username, String urlPhoto, String text, long time) {
        this.username = username;
        this.urlPhoto = urlPhoto;
        this.text = text;
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public String getText() {
        return text;
    }

    public long getTime() {
        return time;
    }
}
