package dev.arctic.admiral.utilities;

import com.openai.client.OpenAIClientAsync;
import com.openai.client.okhttp.OpenAIOkHttpClientAsync;
import com.openai.models.moderations.Moderation;
import com.openai.models.moderations.ModerationCreateParams;
import com.openai.models.moderations.ModerationModel;
import dev.arctic.admiral.Admiral;
import dev.arctic.admiral.alliance.AllianceGuild;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;

import java.util.*;

public class AIUtil {
    private static OpenAIClientAsync client;

    public static void init() {
        client = OpenAIOkHttpClientAsync.builder()
                .apiKey(BotConfig.aiToken)
                .build();
    }

    public static void reviewMessage(Long userID, Long channelID, Long messageID, String message) {
        var createParams = ModerationCreateParams.builder()
                .input(message)
                .model(ModerationModel.OMNI_MODERATION_LATEST)
                .build();

        client.moderations().create(createParams).thenAccept(response -> {
            List<Moderation> mods = response.results();

            Map<String, Boolean> flagged = new HashMap<>();
            Map<String, Boolean> violations = new HashMap<>();

            for (Moderation mod : mods) {
                var cats = mod.categories();
                flagged.put("sexual", cats.sexual());
                flagged.put("hate", cats.hate());
                flagged.put("hate/threatening", cats.hateThreatening());
                flagged.put("harassment", cats.harassment());
                flagged.put("harassment/threatening", cats.harassmentThreatening());
                flagged.put("self-harm", cats.selfHarm());
                flagged.put("self-harm/intent", cats.selfHarmIntent());
                flagged.put("self-harm/instructions", cats.selfHarmInstructions());
                flagged.put("sexual/minors", cats.sexualMinors());
                flagged.put("violence", cats.violence());
                flagged.put("violence/graphic", cats.violenceGraphic());

                var scores = mod.categoryScores();

                if (BotConfig.thresholds.getOrDefault("sexual", 1.0) <= scores.sexual()) violations.put("sexual", true);
                if (BotConfig.thresholds.getOrDefault("hate", 1.0) <= scores.hate()) violations.put("hate", true);
                if (BotConfig.thresholds.getOrDefault("hate_threatening", 1.0) <= scores.hateThreatening())
                    violations.put("hate/threatening", true);
                if (BotConfig.thresholds.getOrDefault("harassment", 1.0) <= scores.harassment())
                    violations.put("harassment", true);
                if (BotConfig.thresholds.getOrDefault("harassment_threatening", 1.0) <= scores.harassmentThreatening())
                    violations.put("harassment/threatening", true);
                if (BotConfig.thresholds.getOrDefault("self_harm", 1.0) <= scores.selfHarm())
                    violations.put("self-harm", true);
                if (BotConfig.thresholds.getOrDefault("self_harm_intent", 1.0) <= scores.selfHarmIntent())
                    violations.put("self-harm/intent", true);
                if (BotConfig.thresholds.getOrDefault("self_harm_instructions", 1.0) <= scores.selfHarmInstructions())
                    violations.put("self-harm/instructions", true);
                if (BotConfig.thresholds.getOrDefault("sexual_minors", 1.0) <= scores.sexualMinors())
                    violations.put("sexual/minors", true);
                if (BotConfig.thresholds.getOrDefault("violence", 1.0) <= scores.violence())
                    violations.put("violence", true);
                if (BotConfig.thresholds.getOrDefault("violence_graphic", 1.0) <= scores.violenceGraphic())
                    violations.put("violence/graphic", true);
            }

            if (!violations.isEmpty()) {
                notifyFlaggedContent(userID, channelID, messageID, message, violations);
            }
        }).exceptionally(err -> {
            err.printStackTrace();
            return null;
        });
    }

    public static void notifyFlaggedContent(Long userID, Long channelID, Long messageID, String message, Map<String, Boolean> violations) {
        List<ContainerChildComponent> children = new ArrayList<>();

        children.add(TextDisplay.of("## Flagged Content from " +
                                    Admiral.api.getUserById(userID).getName()));
        children.add(Separator.createDivider(Separator.Spacing.SMALL));
        String safeMessage = message.length() > 1024 ? message.substring(0, 1021) + "..." : message;
        children.add(TextDisplay.of("**Message:**\n" + safeMessage));
        children.add(Separator.createDivider(Separator.Spacing.SMALL));
        children.add(TextDisplay.of("-# userId: " + userID));
        children.add(TextDisplay.of("-# channelId: " + channelID));
        children.add(TextDisplay.of("-# messageId: " + messageID));
        children.add(Separator.createDivider(Separator.Spacing.SMALL));

        for (Map.Entry<String, Boolean> entry : violations.entrySet()) {
            if (entry.getValue()) {
                children.add(TextDisplay.of(entry.getKey() + ": flagged"));
            }
        }

        children.add(
                ActionRow.of(
                        Button.primary("mod_delete", "Delete Message"),
                        Button.danger("mod_both", "Shadow and Delete"),
                        Button.secondary("mod_ignore", "Ignore")
                )
        );



        Container container = Container.of(children);

        Objects.requireNonNull(AllianceGuild.guild.getTextChannelById(1397596087207989359L)).sendMessageComponents(container).useComponentsV2().queue();
    }
}

