package org.bot;

import command.CommandManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.EventListener;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class Actions {

    ConnectionsInfo conn = new ConnectionsInfo();
    JDA jda;
    JDABuilder builder;

    public Actions() {
        builder = JDABuilder.createDefault(conn.getToken()).addEventListeners(new CommandManager());
    }

    public void start() throws InterruptedException {
        if(jda != null) throw new IllegalStateException("JDA is already running");
        jda = builder.build().awaitReady();
    }

    public void sendDmMessage(String UserID, String Message) {
        User user = jda.retrieveUserById(UserID).complete();
        if (user != null) {
            user.openPrivateChannel().queue(channel -> {channel.sendMessage(Message).queue();},
                    error -> System.out.println("something went wrong: " + error.getMessage()));
        }
        else {
            System.out.println("User not found");
        }
    }
    public Actions status(){
        builder.setActivity(Activity.watching("You"));
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setAutoReconnect(true);
        return this;
    }
    public Actions permission(){
        builder.enableIntents(
                GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES ,GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_PRESENCES
        );
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.setChunkingFilter(ChunkingFilter.ALL);
        builder.enableCache(CacheFlag.ONLINE_STATUS);
        return this;
    }
    private void sendMessageViaChannel(String ChannelID, String Message) {
        TextChannel channel = jda.getTextChannelById(ChannelID);
        if (channel != null) {
            channel.sendMessage(Message).queue();

        }
    }

    public void sendMessage(String ChannelID) {

        var executor = Executors.newSingleThreadExecutor();
        Scanner scanner = new Scanner(System.in);
        Actions actions = new Actions();
        executor.execute(()->{
            var value = scanner.nextLine();
            while (true){
                actions.sendMessageViaChannel(ChannelID,value);
                if (value.equals("exit")) break;
                value = scanner.nextLine();

            }
            executor.shutdown();
        });
    }
   /* public void messageWithBottom(String ChannelID, String Message,String buttonLabel) {
        TextChannel channel = jda.getTextChannelById(ChannelID);
                if (channel != null) {
                channel.sendMessage(Message)
                        .setActionRow(
                                Button.secondary("button1",buttonLabel))
                        .queue();
                }
    }
    */

    public JDA getJda() {
        return jda;
    }
}
