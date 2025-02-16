package org.bot;

import command.CommandManager;
import listeners.botEvents;
import listeners.EventListener;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class Bot {
    public static void main(String[] args) throws InterruptedException {
        CommandManager manager = new CommandManager();
        Actions actions = new Actions();
        actions.permission()
                .status()
                .start();
        actions.getJda().addEventListener(new EventListener());
        actions.getJda().addEventListener(new botEvents());

        var deleteCommand = Commands.slash("delete-channel","delete channel")
                .addOption(OptionType.CHANNEL,"channel","delete Channel");
        var banUser = Commands.slash("ban-user","ban User")
                .addOption(OptionType.USER,"user","ban User");
        var kickUser = Commands.slash("kick-user","kick User")
                .addOption(OptionType.USER,"user","Kick User");

        actions.getJda().updateCommands().addCommands(deleteCommand, banUser,kickUser).queue(e -> {
            System.out.println("done:");
            e.forEach(see -> System.out.println(see.getAsMention()));
        }, error -> {
            System.out.println("error " + error.getMessage());
        });
        var channel = actions.getJda().getTextChannelById("1249820220617523240");

    }
}