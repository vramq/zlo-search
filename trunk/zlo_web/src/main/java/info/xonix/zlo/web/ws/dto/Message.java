package info.xonix.zlo.web.ws.dto;

import java.util.Date;

/**
 * User: Vovan
 * Date: 12.12.10
 * Time: 19:09
 */
public class Message {
    private String nick;
    private String host;

    private String topic;

    private String title;

    private String body;

    private Date date;

    private boolean reg = false;

    private int num = -1; // default

    private boolean hasUrl;
    private boolean hasImg;

    public Message() {
    }

    public Message(int num, String nick, String host, boolean reg, String topic, String title, String body, Date date, boolean hasUrl, boolean hasImg) {
        this.nick = nick;
        this.host = host;
        this.topic = topic;
        this.title = title;
        this.body = body;
        this.date = date;
        this.reg = reg;
        this.num = num;
        this.hasUrl = hasUrl;
        this.hasImg = hasImg;
    }

    public String getNick() {
        return nick;
    }

    public String getHost() {
        return host;
    }

    public String getTopic() {
        return topic;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public Date getDate() {
        return date;
    }

    public boolean isReg() {
        return reg;
    }

    public int getNum() {
        return num;
    }

    public boolean isHasUrl() {
        return hasUrl;
    }

    public boolean isHasImg() {
        return hasImg;
    }
}
