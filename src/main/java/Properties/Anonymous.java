package Properties;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.bot.Actions;

import java.awt.*;


public class Anonymous extends ListenerAdapter {

    public static void sendAnonymousMessage(SlashCommandInteractionEvent event) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Anonymous Message")
                .setColor(Color.GRAY)
                .setImage("https://cdn.discordapp.com/avatars/916775039263715349/57371b6e76579835ceb57159077749c4.webp?size=256")
                .setDescription("Send Anonymous Message")
                .setFooter("Made by dre&wee");
        event.getChannel().sendMessageEmbeds(embed.build()).setActionRow(Button.primary("anonymous-sender", Emoji.fromUnicode("📩"))).queue();
    }

    public static void openModal(ButtonInteractionEvent event) {
        Modal modal = Modal.create("anonymous_message", "Send Anonymous Message")
                .addActionRow(TextInput
                        .create("personId", "User ID:", TextInputStyle.SHORT).setPlaceholder("Example: 29418839824132")
                        .setRequired(true).build())
                .addActionRow(TextInput.create("message_content", "Message:", TextInputStyle.PARAGRAPH).
                        setRequired(true).build()).build();
        event.replyModal(modal).queue();
        System.out.println(modal);
    }

    public static void sendDmMessage(String userId, String message) {
        User user = Actions.getUserByID(userId);
        EmbedBuilder embed = new EmbedBuilder();

        assert user != null;
        embed.setTitle("Anonymous Message 📩")
                .setDescription(message)
                .setColor(Color.magenta)
                .setFooter("Made by dre&wee");

        user.openPrivateChannel().queue(channel -> {
            channel.sendMessageEmbeds(embed.build()).queue();
        }, error -> {
            System.out.println("something went wrong" + error);
        });
    }
}



