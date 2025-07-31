package dev.arctic.admiral.users;

import java.util.HashSet;
import java.util.Set;

public class PlayerManager {

    public static final Set<Player> players = new HashSet<>();

    public static void addPlayer(Player player){
        players.add(player);
    }

    public static void removePlayer(Player player){
        players.remove(player);
    }

    public static Player getPlayerByID(long discordID){
        for(Player player : players){
            if(player.discordID == discordID){
                return player;
            }
        }
        return null;
    }

    public static Player getPlayerByName(String name){
        for(Player player : players){
            if(player.name.equalsIgnoreCase(name)){
                return player;
            }
        }
        return null;
    }
}
