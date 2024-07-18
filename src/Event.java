package src;

import java.util.Date;

public class Event {
    private String description;
    private Date date;
    private boolean sound;
    private boolean status;

    public Event(String description, Date date, boolean sound, boolean status) {
        this.description = description;
        this.date = date;
        this.sound = sound;
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public boolean getNotifycationBySound() {
        return sound;
    }

    public boolean getStatus() {
        return status;
    }
}
