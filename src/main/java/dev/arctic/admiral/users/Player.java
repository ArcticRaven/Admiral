package dev.arctic.admiral.users;

public class Player {

    Long discordID;
    String name;
    boolean isLinked;

    public Player(Long discordID, String name, boolean isLinked) {
        this.discordID = discordID;
        this.name = name;
        this.isLinked = isLinked;
    }

    public Player(Long discordID, String name) {
        this.discordID = discordID;
        this.name = name;
        this.isLinked = false;
    }

    public Player(String name) {
        this.discordID = 0L;
        this.name = name;
        this.isLinked = false;
    }

    public Long getDiscordID() {
        return discordID;
    }

}
