package dev.arctic.admiral.alliance.moderation;

import dev.arctic.admiral.alliance.AdmiralLogger;
import dev.arctic.admiral.alliance.AllianceGuild;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dev.arctic.admiral.alliance.AdmiralLogger.LogLevel.WARNING;

public class ModerationButtons extends ListenerAdapter {

    private static final Pattern USER_ID_PATTERN = Pattern.compile("-# userId: (\\d+)");
    private static final Pattern MESSAGE_ID_PATTERN = Pattern.compile("-# messageId: (\\d+)");
    private static final Pattern CHANNEL_ID_PATTERN = Pattern.compile("-# channelId: (\\d+)");

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String id = event.getComponentId();
        if (!id.equals("mod_delete") && !id.equals("mod_both") && !id.equals("mod_ignore")) return;

        event.deferReply().setEphemeral(true).queue();

        var modMessage = event.getMessage();
        var components = modMessage.getComponents();

        Long userId = extractLongFromDisplays(components, USER_ID_PATTERN);
        Long msgId = extractLongFromDisplays(components, MESSAGE_ID_PATTERN);
        Long cnlId = extractLongFromDisplays(components, CHANNEL_ID_PATTERN);

        if (userId == null || msgId == null || cnlId == null) {
            event.getHook().sendMessage("Failed to parse moderation data.").queue();
            return;
        }

        TextChannel channel = AllianceGuild.guild.getTextChannelById(cnlId);
        if (channel == null) {
            event.getHook().sendMessage("Channel not found.").queue();
            return;
        }

        if (id.equals("mod_ignore")) {
            modMessage.delete().queue();
            event.getHook().sendMessage("Flag ignored.").setEphemeral(true).queue();
            return;
        }

        // Delete flagged message, then log
        channel.retrieveMessageById(msgId).queue(message -> {
            channel.deleteMessageById(msgId).queue();

            Member target = AllianceGuild.guild.getMemberById(userId);
            String username = target != null ? target.getUser().getName() : "unknown user";

            if (id.equals("mod_both")) {
                Role shadow = AllianceGuild.guild.getRoleById(1400915085467058207L);
                if (target != null && shadow != null) {
                    AllianceGuild.guild.addRoleToMember(target, shadow).queue();
                }
                AdmiralLogger.log("Message `" + msgId + "` from `" + username + "` was deleted and shadowed by `" + event.getUser().getName() + "`", WARNING);
            } else {
                AdmiralLogger.log("Message `" + msgId + "` from `" + username + "` was deleted by `" + event.getUser().getName() + "`", WARNING);
            }

            modMessage.delete().queue();
            event.getHook().sendMessage("Moderation complete.").queue();

        }, err -> {
            event.getHook().sendMessage("Flagged message not found.").queue();
        });
    }

    private Long extractLongFromDisplays(List<MessageTopLevelComponentUnion> components, Pattern pattern) {
        for (var union : components) {
            var container = union.asContainer();
            for (var child : container.getComponents()) {
                if (child instanceof TextDisplay display) {
                    Matcher matcher = pattern.matcher(display.getContent());
                    if (matcher.find()) {
                        try {
                            return Long.parseLong(matcher.group(1));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }
        }
        return null;
    }
}
