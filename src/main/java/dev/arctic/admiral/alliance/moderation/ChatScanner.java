package dev.arctic.admiral.alliance.moderation;

import dev.arctic.admiral.alliance.AllianceGuild;
import dev.arctic.admiral.utilities.AIUtil;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.*;

public class ChatScanner extends ListenerAdapter {

    private static final BlockingQueue<ModerationTask> moderationQueue = new LinkedBlockingQueue<>();
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    static {
        scheduler.scheduleAtFixedRate(() -> {
            ModerationTask task = moderationQueue.poll();
            if (task != null) {
                AIUtil.reviewMessage(task.userId, task.messageId, task.content);
            }
        }, 0, 250, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (!event.getGuild().equals(AllianceGuild.guild)) return;

        String message = event.getMessage().getContentStripped();
        moderationQueue.offer(new ModerationTask(
                event.getAuthor().getIdLong(),
                event.getMessageIdLong(),
                message
        ));
    }

    private record ModerationTask(Long userId, Long messageId, String content) {}

    public static void shutdown() {
        scheduler.shutdown();
    }
}
