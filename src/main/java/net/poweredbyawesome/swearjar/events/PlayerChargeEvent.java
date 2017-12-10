package net.poweredbyawesome.swearjar.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Lax on 12/9/2017.
 */
public class PlayerChargeEvent extends Event {

    public static final HandlerList panHandlers = new HandlerList();
    private Player player;
    private double swearPrice;
    private boolean perWord;

    public PlayerChargeEvent(Player player, double swearPrice, boolean perWord) {
        this.player = player;
        this.swearPrice = swearPrice;
        this.perWord = perWord;
    }

    public static HandlerList getHandlerList() {
        return panHandlers;
    }

    @Override
    public HandlerList getHandlers() {
        return panHandlers;
    }

    public Player getPlayer() {
        return this.player;
    }

    public double getSwearPrice() {
        return this.swearPrice;
    }

    public boolean isPerWord() {
        return this.perWord;
    }

    public void setSwearPrice(double swearPrice) {
        this.swearPrice = swearPrice;
    }
}
