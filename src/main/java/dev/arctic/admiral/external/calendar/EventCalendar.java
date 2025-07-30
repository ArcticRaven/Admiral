package dev.arctic.admiral.external.calendar;

import java.util.HashSet;
import java.util.Set;

public class EventCalendar {

    public static Set<Event> events = new HashSet<>();

    public static void addEvent(Event event){
        events.add(event);
    }

    public static void removeEvent(Event event){
        events.remove(event);
    }

    public static Set<Event> getEvents(){
        return events;
    }

    public static void updateCalendars(){

    }
}
