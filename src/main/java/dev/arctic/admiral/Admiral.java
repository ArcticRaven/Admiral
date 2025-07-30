package dev.arctic.admiral;

import dev.arctic.admiral.alliance.roles.RoleManager;
import dev.arctic.admiral.alliance.AdmiralLogger;
import dev.arctic.admiral.utilities.ConsoleListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.util.*;

public class Admiral {

    public static JDA api;
    public static boolean isOperational;

    public static void main(String[] args) throws InterruptedException {

        // initialize the gateway intents
        EnumSet<GatewayIntent> intents = EnumSet.of(
                GatewayIntent.AUTO_MODERATION_CONFIGURATION,
                GatewayIntent.AUTO_MODERATION_EXECUTION,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.DIRECT_MESSAGE_POLLS,
                GatewayIntent.DIRECT_MESSAGE_TYPING,
                GatewayIntent.GUILD_INVITES,
                GatewayIntent.GUILD_EXPRESSIONS,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGE_POLLS,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_MESSAGE_TYPING,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MODERATION,
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.GUILD_WEBHOOKS,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.SCHEDULED_EVENTS
        );

        api = JDABuilder.createDefault(args[0])
                .enableIntents(intents)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .addEventListeners(new RoleManager())
                .build();

        api.awaitReady();
        ConsoleListener.startConsoleListener();
    }

    public static void shutdown() throws InterruptedException {
        AdmiralLogger.log("Admiral-chan is now shutting down!", AdmiralLogger.LogLevel.FINE);
        ConsoleListener.stopConsoleListener();
        api.shutdown();
        System.exit(0);
    }
}
