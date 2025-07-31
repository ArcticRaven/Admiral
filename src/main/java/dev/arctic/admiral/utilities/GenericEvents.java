package dev.arctic.admiral.utilities;

import dev.arctic.admiral.Admiral;
import dev.arctic.admiral.alliance.AdmiralLogger;
import dev.arctic.admiral.alliance.AllianceGuild;
import dev.arctic.admiral.external.commands.CommandRegistry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class GenericEvents extends ListenerAdapter {

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        Guild guild = event.getGuild();
        CommandRegistry.updateCommands(guild);
        System.out.println("Joined guild: " + guild.getName());
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.println("Admiral heard ready, initializing classes...");

        // Delay AllianceGuild init to avoid command overwrite
        Admiral.scheduler.schedule(() -> {
            try {
                AllianceGuild.init();
                System.out.println("Admiral initialized Alliance Guild!");
            } catch (Exception e) {
                System.err.println("[CRITICAL] Alliance init failed: " + e.getMessage());
                e.printStackTrace();
            }
        }, 2, TimeUnit.SECONDS);

        // Initialize other commands immediately
        CommandRegistry.init();
        System.out.println("Admiral initialized Commands!");

        AdmiralLogger.log("Admiral is fully initialized!", AdmiralLogger.LogLevel.FINE);
    }
}
