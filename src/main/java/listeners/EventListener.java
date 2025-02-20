package listeners;

import Properties.Anonymous;
import Properties.TicTacToeGame;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.bot.Actions;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class EventListener extends ListenerAdapter {
    HashMap<String, TicTacToeGame> toeGameHashMap = new HashMap<>();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        String message = event.getMessage().getContentRaw();
        String mention = "<@1298384923035439115>";
        if (message.contains(mention)) {
            event.getChannel().sendMessage("محدا بحبك").queue();
        }

    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getFullCommandName().equalsIgnoreCase("delete-channel")) {
            if (event.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
                event.deferReply().setContent("deleting the room").queue();
                event.getInteraction().getOption("channel").getAsChannel().delete().queue(e -> {
                    event.getHook().editOriginal("Room been deleted").queue();
                }, error -> event.getHook().editOriginal("Error: " + error.getMessage()).queue());
            }
        }
        if (event.getFullCommandName().equalsIgnoreCase("ban-user")) {
            if (event.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
                event.deferReply().setContent("banning the user").queue();
                var user = event.getInteraction().getOption("user").getAsUser();
                event.getGuild().ban(user, 14, TimeUnit.DAYS).queue();
            }
        }
        if (event.getFullCommandName().equalsIgnoreCase("kick-user")) {
            if (event.getMember().hasPermission(Permission.KICK_MEMBERS)) {
                event.deferReply().setContent("kicking the user").queue();
                var user = event.getInteraction().getOption("user").getAsUser();
                event.getGuild().kick(user).queue();
            }
        }
        if (event.getFullCommandName().equalsIgnoreCase("start-game")) {
            event.deferReply().setContent("starting the game").queue();
            event.getChannel().sendMessage("Starting the game").queue(Message -> {
                TicTacToeGame ticTacToeGame = new TicTacToeGame(Message);
                toeGameHashMap.put(Message.getId(), ticTacToeGame);
            });
        }
        if (event.getFullCommandName().equalsIgnoreCase("anonymous-message")) {
            Anonymous.sendAnonymousMessage(event);
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        //event.deferReply(true).setContent("Send").queue();
        if (event.getButton().getId().equals("Send")) {
            event.replyModal(Modal.create("title", "Anonymous message")
                    .addActionRow(TextInput.create("container", "User    ID:", TextInputStyle.SHORT).build())
                    .addActionRow(TextInput.create("messageContent", "Message:", TextInputStyle.PARAGRAPH).build())
                    .build()).queue();
            event.getMessage().reply("im gay")
                    .delay(10, TimeUnit.SECONDS)
                    .flatMap(message -> {
                        return message.retrievePollVoters(13);
                    })
                    .flatMap(users -> users.getFirst().openPrivateChannel())
                    .delay(10, TimeUnit.SECONDS)
                    .flatMap(privateChannel -> privateChannel.sendMessage("congrats anta gay "))
                    .queue();

        }
        if (event.getButton().getId().equalsIgnoreCase("join")) {
            var player = event.getMember();
            var gameId = event.getMessage().getId();
            var game = toeGameHashMap.get(gameId);
            if (game.setPlayer(player)) {
                event.deferReply(true).setContent("You have joined the game").queue();
            } else {
                event.deferReply().setContent("game is full").queue();
            }

        }
        if (toeGameHashMap.containsKey(event.getMessage().getId()) && !event.getButton().getId().equalsIgnoreCase("join")) {
            TicTacToeGame game = toeGameHashMap.get(event.getMessage().getId());
            game.changeGameStatus(event.getComponentId());
            event.deferEdit().queue();
        }
        System.out.println(event.getButton().getId());
        if (event.getButton().getId().equalsIgnoreCase("anonymous-sender")) {
            Anonymous.openModal(event);
        }
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
        if(event.getModalId().equals("anonymous_message")){
            String memberId = event.getValue("personId").getAsString();
            String message = event.getValue("message_content").getAsString();
            Anonymous.sendDmMessage(memberId,message);
        }
    }

}
