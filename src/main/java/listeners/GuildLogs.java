package listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildDeafenEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildMuteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class GuildLogs extends ListenerAdapter {
    public static final String LOG_ID_CHANNEL = "1340791397036457984";



    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String messageContent = event.getMessage().getContentRaw();
        if (messageContent.equalsIgnoreCase("!reaction")) {
            event.getMessage().addReaction(Emoji.fromUnicode("\uD83D\uDC80")).queue();
        }
    }

    @Override
    public void onChannelUpdateName(ChannelUpdateNameEvent event) {
        String oldName = event.getOldValue();
        String newName = event.getNewValue();
        String ChannelID = event.getChannel().getId();
        final String[] updatedBy = {""};

        event.getGuild().retrieveAuditLogs().type(ActionType.CHANNEL_UPDATE).queue(logs ->{
            for (var entry : logs) {
                if(entry.getTargetId().equals(ChannelID)) {
                    updatedBy[0] = entry.getUser().getName();
                }
            }
        });
        EmbedBuilder emb = new EmbedBuilder();
        emb.setTitle("Channel Update");
        emb.addField("Old Name", oldName, false);
        emb.addField("New Name", newName, false);
        emb.addField("Channel ID:", ChannelID, false);
        emb.addField("Updated By", updatedBy[0], false);

        Objects.requireNonNull(event.getGuild().getTextChannelById(LOG_ID_CHANNEL)).sendMessageEmbeds(emb.build()).queue();
        System.out.println(oldName + " " + newName + " " + LOG_ID_CHANNEL);
    }

    @Override
    public void onChannelDelete(@NotNull ChannelDeleteEvent event) {
        var channelName = event.getChannel().getName();
        String ChannelId = event.getChannel().getId();
        final String[] Moderator= {""};
        event.getGuild().retrieveAuditLogs().type(ActionType.CHANNEL_DELETE).queue(logs ->{
            for(var entry : logs){
                if(entry.getTargetId().equals(ChannelId)){
                    Moderator[0] = entry.getUser().getName();
                }
            }
        });

        EmbedBuilder emb = new EmbedBuilder();
        emb.setTitle("Channel Delete");
        emb.addField("Channel deleted", event.getChannel().getName(), false);
        emb.addField("Channel ID:", ChannelId, false);
        emb.addField("Channel Name :",channelName, false);
        emb.addField("by:",Moderator[0],false);
        Objects.requireNonNull(event.getGuild().getTextChannelById(LOG_ID_CHANNEL)).sendMessageEmbeds(emb.build()).queue();
    }

    @Override
    public void onGuildBan(GuildBanEvent event) {
        var userBanned = event.getUser();
        event.getGuild().retrieveAuditLogs().type(ActionType.BAN).queue(logs ->{
            AuditLogEntry wanted = logs.stream().filter(auditLogEntry -> auditLogEntry.getTargetId().equals(userBanned.getId())).findFirst().orElse(null);
            if(wanted == null){
                return;
            }
            EmbedBuilder emb = new EmbedBuilder();
            emb.setTitle("Banned");
            emb.addField("Get Banned:",userBanned.getAsMention(),false);
            emb.addField("User ID:",userBanned.getId(),false);
            emb.addField("By:",wanted.getUser().getAsMention(),false);
            if (wanted.getReason() != null)
                emb.addField("Reason:", wanted.getReason(), false);

            event.getGuild().getTextChannelById(LOG_ID_CHANNEL).sendMessageEmbeds(emb.build()).queue();
        });
    }

    @Override
    public void onGuildVoiceGuildMute(GuildVoiceGuildMuteEvent event) {
        if (event.isGuildMuted()){
            var channel = event.getMember().getVoiceState().getChannel().getAsMention();
        var user = event.getMember().getUser().getAsMention();
        final String[] muter = {""};
        event.getGuild().retrieveAuditLogs().type(ActionType.MEMBER_UPDATE).queue(logs -> {
            for (var entry : logs) {
                if (entry.getTargetId().equalsIgnoreCase(event.getMember().getUser().getId())) {
                    muter[0] = entry.getUser().getName();
                    break;
                }
            }
            EmbedBuilder emd = new EmbedBuilder();
            emd.setTitle("Voice Mute");
            emd.addField("Voice Muted:", user, false);
            emd.addField("in:", channel, false);
            emd.addField("by:", muter[0], false);
            Objects.requireNonNull(event.getGuild().getTextChannelById(LOG_ID_CHANNEL)).sendMessageEmbeds(emd.build()).queue();
        });
    }
}


    @Override
    public void onChannelCreate(ChannelCreateEvent event) {
        final String[] creatorName = {""};
        var channelNameCreated = event.getChannel().getAsMention();
        var ChannelID = event.getChannel().getId();
        var ChannelType = event.getChannel().getType().toString();
        event.getGuild().retrieveAuditLogs().type(ActionType.CHANNEL_CREATE).queue(logs -> {
            for(var entry : logs) {
                if (entry.getTargetId().equals(event.getChannel().getId())) {
                    creatorName[0] = Objects.requireNonNull(entry.getUser()).getName();
                    break;
                }

            }
            EmbedBuilder emb = new EmbedBuilder();
            emb.setTitle("Channel Created");
            emb.addField("Channel ID:", ChannelID, false);
            emb.addField("Channel Type:",ChannelType,false);
            emb.addField("Channel Name:", channelNameCreated, false);
            emb.addField("Created By:", creatorName[0],false);
            Objects.requireNonNull(event.getGuild().getTextChannelById(LOG_ID_CHANNEL)).sendMessageEmbeds(emb.build()).queue();
        });
    }

    @Override
    public void onGuildVoiceGuildDeafen(GuildVoiceGuildDeafenEvent event) {
        if(event.isGuildDeafened()){
            final String[] deafener = {""};
            var channel =  event.getMember().getVoiceState().getChannel().getAsMention();
            var user = event.getMember().getUser().getAsMention();
            event.getGuild().retrieveAuditLogs().type(ActionType.MEMBER_UPDATE).queue(logs -> {
                for(var entry : logs) {
                    if (entry.getTargetId().equals(event.getMember().getUser().getId())) {
                        deafener[0] = entry.getUser().getName();
                        break;
                    }
                }
                EmbedBuilder emb = new EmbedBuilder();
                emb.setTitle("Voice Deafened");
                emb.addField("Voice Deafened:", user, false);
                emb.addField("in:", channel, false);
                emb.addField("by:", deafener[0], false);

               Objects.requireNonNull(event.getGuild().getTextChannelById(LOG_ID_CHANNEL)).sendMessageEmbeds(emb.build()).queue();
            });
        }
    }

}
