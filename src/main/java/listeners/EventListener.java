package listeners;

import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.bot.Actions;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EventListener extends ListenerAdapter {
    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        super.onMessageReactionAdd(event);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        super.onMessageReceived(event);
        if (event.getAuthor().isBot()) return;
        String message = event.getMessage().getContentRaw();
        String mention = "<@1298384923035439115>";
        if (message.contains(mention)) {
            event.getChannel().sendMessage("محدا بحبك").queue();
        }
    }


    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        super.onGuildMemberJoin(event);
        String avatar = event.getUser().getEffectiveAvatarUrl();
        System.out.println(avatar);
    }

    @Override
    public void onUserUpdateOnlineStatus(@NotNull UserUpdateOnlineStatusEvent event) {
        System.out.println("hello");
    }

    @Override
    public void onChannelDelete(ChannelDeleteEvent event) {

    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        //event.deferReply(true).setContent("Send").queue();
        if (event.getButton().getId().equalsIgnoreCase("Send")) {
            event.replyModal(Modal.create("title", "Anonymous message")
                    .addActionRow(TextInput.create("container", "User ID:", TextInputStyle.SHORT).build())
                    .addActionRow(TextInput.create("messageContent", "Message:", TextInputStyle.PARAGRAPH).build())
                    .build()).queue();
        }
        /*
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("ControlP");
        embedBuilder.setColor(Color.blue);
        embedBuilder.setDescription("Send a DM message for Friends");
        embedBuilder.setImage("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTfVGi6KfC7gsHmLvbFH_DMGWnfruAzHOg_jQ&s");
        Button SendMessage = Button.primary("Send", Emoji.fromUnicode("🟣"));
        event.getChannel().sendMessageEmbeds(embedBuilder.build()).setActionRow(SendMessage).queue(e -> {
            System.out.println("send");
        },e-> {
            System.out.println(e.getMessage());
        });
         */
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        System.out.println(event.getModalId());
        if (event.getModalId().equals("title")) {
            event.deferReply(true).setContent("Sending").queue();
            String inputID = Objects.requireNonNull(event.getValue("container")).getAsString();
            String message = Objects.requireNonNull(event.getValue("messageContent")).getAsString();
            Actions actions = new Actions();
            actions.sendDmMessage(inputID, message);
        }
    }
}
