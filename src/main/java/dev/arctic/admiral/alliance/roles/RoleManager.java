package dev.arctic.admiral.alliance.roles;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static dev.arctic.admiral.alliance.AllianceGuild.ROLE_IDS;

public class RoleManager extends ListenerAdapter {

    private boolean hasAnyRole(Member member, String... roleKeys) {
        Set<String> memberRoleIds = member.getRoles()
                .stream()
                .map(Role::getId)
                .collect(Collectors.toSet());

        for (String key : roleKeys) {
            Long roleId = ROLE_IDS.get(key);
            if (roleId != null && memberRoleIds.contains(roleId.toString())) {
                return true;
            }
        }
        return false;
    }

    private Role getMatchingRole(Guild guild, String roleKey) {
        return guild.getRoleById(ROLE_IDS.get(roleKey));
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply(true).queue(hook -> {
            Member sender = event.getMember();
            String cmd = event.getName();

            System.out.println("[DEBUG] Received command: " + cmd + " from user: " + sender.getIdLong());

            boolean isGuildLeader = hasAnyRole(sender, "GUILD_LEADER");
            boolean isR4 = hasAnyRole(sender, "R4");

            if (Set.of("addmember", "kickmember", "addstaff", "kickstaff", "addofficer", "kickofficer").contains(cmd)) {
                if (!isGuildLeader && !isR4) {
                    System.out.println("[DEBUG] Permission denied: not Guild Leader or R4.");
                    respondEvent(event, false, "You must be a Guild Leader or R4 to use this command.");
                    return;
                }
                if ((cmd.equals("addofficer") || cmd.equals("kickofficer")) && !isGuildLeader) {
                    System.out.println("[DEBUG] Permission denied: not Guild Leader for officer command.");
                    respondEvent(event, false, "Only Guild Leaders can manage officers.");
                    return;
                }
            }

            switch (cmd) {
                case "addmember", "kickmember", "addstaff", "kickstaff", "addofficer", "kickofficer" -> {
                    System.out.println("[DEBUG] Handling member-based command: " + cmd);
                    Member target = Optional.ofNullable(event.getOption("member"))
                            .map(OptionMapping::getAsMember)
                            .orElse(null);
                    if (target == null) {
                        System.out.println("[DEBUG] No member option provided.");
                        event.getHook().sendMessage("You must specify a member.").setEphemeral(true).queue();
                        return;
                    }

                    switch (cmd) {
                        case "addmember" -> manageMember(event, sender, target, true);
                        case "kickmember" -> manageMember(event, sender, target, false);
                        case "addstaff" -> manageStaff(event, sender, target, true);
                        case "kickstaff" -> manageStaff(event, sender, target, false);
                        case "addofficer" -> manageOfficer(event, sender, target, true, true);
                        case "kickofficer" -> {
                            boolean retain = event.getOption("staff") != null && event.getOption("staff").getAsBoolean();
                            manageOfficer(event, sender, target, false, retain);
                        }
                    }
                }

                default -> System.out.println("[DEBUG] Unknown command: " + cmd);
            }
        });
    }

    public void manageMember(SlashCommandInteractionEvent event, Member sender, Member member, boolean status) {
        System.out.println("[DEBUG] Running manageMember with status=" + status);
        for (String tag : List.of("SNTL", "DERPZ", "BEGR", "MPZ")) {
            if (hasAnyRole(sender, tag)) {
                Role role = getMatchingRole(event.getGuild(), tag);
                if (status) {
                    event.getGuild().addRoleToMember(member, role).queue();
                } else {
                    event.getGuild().removeRoleFromMember(member, role).queue();

                    Role staffRole = getMatchingRole(event.getGuild(), tag + "_L");
                    if (hasAnyRole(member, tag + "_L")) {
                        event.getGuild().removeRoleFromMember(member, staffRole).queue();
                    }

                    if (hasAnyRole(member, "R4")) {
                        Role r4Role = getMatchingRole(event.getGuild(), "R4");
                        event.getGuild().removeRoleFromMember(member, r4Role).queue();
                    }
                }
                respondEvent(event, true, null);
                return;
            }
        }
        respondEvent(event, false, "You do not have a guild tag role.");
    }

    public void manageStaff(SlashCommandInteractionEvent event, Member sender, Member member, boolean status) {
        System.out.println("[DEBUG] Running manageStaff with status=" + status);
        for (String tag : List.of("SNTL_L", "DERPZ_L", "BEGR_L", "MPZ_L")) {
            if (hasAnyRole(sender, tag)) {
                Role staffRole = getMatchingRole(event.getGuild(), tag);
                if (status) {
                    event.getGuild().addRoleToMember(member, staffRole).queue();
                } else {
                    event.getGuild().removeRoleFromMember(member, staffRole).queue();
                }
                respondEvent(event, true, null);
                return;
            }
        }
        respondEvent(event, false, "You do not have a staff role to assign this.");
    }

    public void manageOfficer(SlashCommandInteractionEvent event, Member sender, Member member, boolean status, boolean retainStaff) {
        System.out.println("[DEBUG] Running manageOfficer with status=" + status + ", retainStaff=" + retainStaff);
        for (String tag : List.of("SNTL", "DERPZ", "BEGR", "MPZ")) {
            if (hasAnyRole(sender, tag) && hasAnyRole(member, tag)) {
                Role r4 = getMatchingRole(event.getGuild(), "R4");
                Role staffRole = getMatchingRole(event.getGuild(), tag + "_L");

                if (status) {
                    event.getGuild().addRoleToMember(member, r4).queue();
                    event.getGuild().addRoleToMember(member, staffRole).queue();
                } else {
                    event.getGuild().removeRoleFromMember(member, r4).queue();
                    if (!retainStaff) {
                        event.getGuild().removeRoleFromMember(member, staffRole).queue();
                    }
                }
                respondEvent(event, true, null);
                return;
            }
        }
        respondEvent(event, false, "Sender/member must share a guild tag to promote.");
    }

    public void respondEvent(SlashCommandInteractionEvent event, boolean success, String reason) {
        System.out.println("[DEBUG] Sending respondEvent: success=" + success + ", reason=" + reason);
        if (success) {
            event.getHook().sendMessage("Roles updated successfully!").setEphemeral(true).queue();
        } else {
            event.getHook().sendMessage("Roles failed to update! Reason: " + reason + ".").setEphemeral(true).queue();
        }
    }
}
