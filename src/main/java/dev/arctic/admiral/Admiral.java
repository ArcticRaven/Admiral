package dev.arctic.admiral;

import dev.arctic.admiral.alliance.moderation.ChatScanner;
import dev.arctic.admiral.alliance.moderation.ModerationButtons;
import dev.arctic.admiral.alliance.roles.RoleManager;
import dev.arctic.admiral.external.commands.SlashCommands;
import dev.arctic.admiral.utilities.AIUtil;
import dev.arctic.admiral.utilities.BotConfig;
import dev.arctic.admiral.utilities.ConsoleListener;
import dev.arctic.admiral.utilities.GenericEvents;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Admiral {

    public static JDA api;
    public static boolean isOperational;
    public static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) throws InterruptedException, IOException {

        if (!BotConfig.loadConfig()) {
            System.out.println("[CRITICAL] Config failed to load. Fix it and restart.");
            System.exit(1);
        }

        AIUtil.init();

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
                .addEventListeners(new RoleManager(),
                        new SlashCommands(),
                        new GenericEvents(),
                        new ChatScanner(),
                        new ModerationButtons())
                .build();

        System.out.println("Admiral-chan is now initializing...waiting ready");

        api.awaitReady();
        System.out.println("Starting console listener...");
        ConsoleListener.startConsoleListener();
        System.out.println("Listener is active, Admiral-chan is now operational!");
    }

    public static void shutdown() throws InterruptedException {
        ConsoleListener.stopConsoleListener();
        ChatScanner.shutdown();
        scheduler.shutdownNow();
        if (api != null) api.shutdownNow();
        System.exit(0);
    }
}
