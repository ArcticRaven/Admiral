package dev.arctic.admiral.external.calendar;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class Event {

    public String name;
    public UUID uuid = UUID.randomUUID();
    public String description;
    public Long channelID;
    public Set<Long> guildIDs;
    public Set<Long> roles;
    public Long startTime;
    public Long endTime;
    public Long duration;
    public Long creatorID;

    public boolean updateCalendars;

    public Event(String name,
                 String description,
                 Long channelID,
                 Long guildID,
                 Long role,
                 Long startTime,
                 Long endTime,
                 Long duration,
                 Long creatorID) {
        this.name = name;
        this.description = description;
        this.channelID = channelID;
        this.guildIDs = Collections.singleton(guildID);
        this.roles = Collections.singleton(role);
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.creatorID = creatorID;
    }

    public void addRole(Long roleID){
        roles.add(roleID);
    }

    public void addGuild(Long guildID){
        guildIDs.add(guildID);
    }

    public void updateStartTime(Long newStartTime){
        this.startTime = newStartTime;
    }

    public void updateEndTime(Long newEndTime){
        this.endTime = newEndTime;
    }
}
