package dev.arctic.admiral.external.commands;

import dev.arctic.admiral.Admiral;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class CommandRegistry {

    public static void init(){
        System.out.println("Registering commands...");

        for (Guild guild : Admiral.api.getGuilds()) {
            updateCommands(guild);
        }
    }

    public static void updateCommands(Guild guild){

        guild.updateCommands().addCommands(
                Commands.slash("source", "View the source code.")
        ).queue();
    }
}
