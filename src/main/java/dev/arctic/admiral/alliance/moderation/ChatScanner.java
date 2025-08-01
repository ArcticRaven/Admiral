package dev.arctic.admiral.alliance.moderation;

import dev.arctic.admiral.alliance.AllianceGuild;
import dev.arctic.admiral.utilities.AIUtil;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.*;

public class ChatScanner extends ListenerAdapter {

    private static final BlockingQueue<ModerationTask> moderationQueue = new LinkedBlockingQueue<>(1000);
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    static {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                ModerationTask task = moderationQueue.poll();
                if (task != null) {
                    AIUtil.reviewMessage(task.userId(), task.channelId(), task.messageId(), task.content());
                }
            } catch (Exception e) {
                System.err.println("[ChatScanner] Error during moderation task: " + e.getMessage());
            }
        }, 0, 250, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (!event.getGuild().equals(AllianceGuild.guild)) return;

        String content = event.getMessage().getContentStripped();
        if (content.isBlank()) return;

        boolean added = moderationQueue.offer(new ModerationTask(
                event.getAuthor().getIdLong(),
                event.getChannel().getIdLong(),
                event.getMessageIdLong(),
                content
        ));

        if (!added) {
            System.err.println("[ChatScanner] Moderation queue full. Message skipped.");
        }
    }

    private record ModerationTask(Long userId, Long channelId, Long messageId, String content) {}

    public static void shutdown() {
        scheduler.shutdownNow();
    }
}
