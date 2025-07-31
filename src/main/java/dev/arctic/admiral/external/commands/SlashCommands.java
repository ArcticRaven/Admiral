package dev.arctic.admiral.external.commands;

import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class SlashCommands extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        switch (event.getName()) {
            case "source" -> sendSource(event);
            default -> {
            } // do not respond to default
        }
    }

    protected void sendSource(SlashCommandInteractionEvent event) {
        File githubImage = new File("resources/github.png");

        if (!githubImage.exists()) {
            System.out.println("Github image does not exist!");
            event.getHook().sendMessage("Critical error while sending source. Please contact the developer.").setEphemeral(true).queue();
            return;
        }

        Container container = Container.of(
                TextDisplay.of("## Source Code"),
                Separator.createDivider(Separator.Spacing.SMALL),
                TextDisplay.of("[Admiral Chan's Source Code on Github](https://github.com/ArcticRaven/Admiral)")
        );

        event.replyComponents(container).useComponentsV2().queue();

    }
}
