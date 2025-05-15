package org.bot;
import listeners.GuildLogs;
import listeners.EventListener;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class Bot {
    public static void main(String[] args) throws InterruptedException {
        Actions actions = new Actions();
        actions.permission()
                .status()
                .start();
        actions.getJda().addEventListener(new EventListener());
        actions.getJda().addEventListener(new GuildLogs());

        var deleteCommand = Commands.slash("delete-channel","delete channel")
                .addOption(OptionType.CHANNEL,"channel","delete Channel");
        var banUser = Commands.slash("ban-user","ban User")
                .addOption(OptionType.USER,"user","ban User");
        var kickUser = Commands.slash("kick-user","kick User")
                .addOption(OptionType.USER,"user","Kick User");
        var startGame = Commands.slash("start-game","start game");

        var AnonymousMessage = Commands.slash("anonymous-message","anonymous message");

        var adminSub = Commands.slash("admin-submission","admin sub")
                        .addOption(OptionType.CHANNEL,"channel","Admin Submission");
        actions.getJda().updateCommands().addCommands(deleteCommand, banUser,kickUser,startGame,AnonymousMessage,adminSub).queue(e -> {
            System.out.println("done:");
            e.forEach(see -> System.out.println(see.getAsMention()));
        }, error -> {
            System.out.println("error " + error.getMessage());
        });
    }
}