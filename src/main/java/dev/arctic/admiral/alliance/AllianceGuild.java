package dev.arctic.admiral.alliance;

import dev.arctic.admiral.Admiral;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.Map;

import static dev.arctic.admiral.Admiral.api;

public class AllianceGuild {

    public static Guild guild;

    public static final Map<String, Long> ROLE_IDS = Map.of(
            "GUILD_LEADER", 1393788767189602304L,
            "R4", 1365543891163418637L,
            "SNTL", 1393792477881958410L,
            "SNTL_L", 1394143033414520842L,
            "DERPZ", 1393792612770775052L,
            "DERPZ_L", 1394143144056197140L,
            "BEGR", 1393792711064031364L,
            "BEGR_L", 1394143223924133888L,
            "MPZ", 1393792795575189554L,
            "MPZ_L", 1394143280949760131L
    );

    public static void init() throws InterruptedException {
        guild = api.getGuildById(1397596087207989359L);

        if(guild == null){
            Admiral.shutdown();
        } else {
            System.out.println("Admiral Connected! Ready for operations!");
            AdmiralLogger.log("Admiral-chan is now operational!", AdmiralLogger.LogLevel.FINE);
        }

        guild.updateCommands().addCommands(
                Commands.slash("addmember", "Adds a member to your guild.")
                        .addOption(OptionType.USER, "member", "the member to add to your guild"),
                Commands.slash("kickmember", "removes a member from your guild.")
                        .addOption(OptionType.USER, "member", "the member to remove from your guild"),
                Commands.slash("addstaff", "adds the staff role to a guild member")
                        .addOption(OptionType.USER, "member", "the member to promote add to staff"),
                Commands.slash("kickstaff", "removes staff role from a member")
                        .addOption(OptionType.USER, "member", "the member being removed from staff"),
                Commands.slash("addofficer", "promotes a member to R4 in the discord")
                        .addOption(OptionType.USER, "member", "the member to promote add to R4 in the discord"),
                Commands.slash("kickofficer", "the R4 to demote")
                        .addOption(OptionType.USER, "member", "the member to demote from R4 in the discord")
                        .addOption(OptionType.BOOLEAN, "staff", "should this member remain staff in the guild"),
                Commands.slash("ares", "changes the color of Ares's role")
                        .addOption(OptionType.STRING, "hex", "Hex color code to set. ex. #ffffff")
        ).queue();
    }

    public static long getGuildID(){
        return guild.getIdLong();
    }

    public static Map<String, Long> getRoleIDs(){
        return ROLE_IDS;
    }
}
