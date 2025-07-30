package dev.arctic.admiral.alliance;

import dev.arctic.admiral.Admiral;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;

public class AdmiralLogger {

    public enum LogLevel{
        FINE,
        WARNING,
        ERROR,
        CRITICAL
    }

    public static void log(String message, LogLevel level){

        if(Admiral.isOperational){
            TextChannel channel = AllianceGuild.guild.getTextChannelById(1397596087207989359L);
            if (channel == null) return;

            EmbedBuilder embed = new EmbedBuilder();
            switch (level){
                case FINE -> embed.setColor(Color.GREEN);
                case WARNING -> embed.setColor(Color.YELLOW);
                case ERROR -> embed.setColor(Color.ORANGE);
                case CRITICAL -> embed.setColor(Color.RED);
            }

            embed.setTitle(message);

            channel.sendMessageEmbeds(embed.build()).queue();
        }
    }
}
