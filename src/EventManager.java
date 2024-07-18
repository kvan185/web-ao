package src;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.databind.type.TypeFactory;

public class EventManager {
    private List<Event> events;

    public EventManager() {
        events = new ArrayList<>();
    }

    public void addEvent(Event event) {
        events.add(event);
        saveEvents();
    }

    public List<Event> getEvents() {
        return events;
    }

    public void loadEvents() {
        Type eventType = new TypeToken<List<Event>>(){}.getType();
        events = JsonUtil.readFromFile("data/events.json", eventType);
        if (events == null) {
            events = new ArrayList<>();
        }
    }

    public void updateEvent(Event oldEvent, Event newEvent) {
        int index = events.indexOf(oldEvent);
        if (index != -1) {
            events.set(index, newEvent);
            saveEvents();
        }
    }

    public void saveEvents() {
        JsonUtil.writeToFile("data/events.json", events);
    }
}
