package org.bot;

import listeners.EventListener;
import listeners.TicTacToeGame;

public class Bot {
    public static void main(String[] args) throws InterruptedException {
        Actions actions = new Actions();
        actions.permission()
                .status()
                .start();
        actions.getJda().addEventListener(new EventListener());

        var channel = actions.getJda().getTextChannelById("1249820220617523240");
    }
}