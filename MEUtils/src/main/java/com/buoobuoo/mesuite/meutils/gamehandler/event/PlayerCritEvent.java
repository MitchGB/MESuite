package com.buoobuoo.mesuite.meutils.gamehandler.event;

import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

@Getter
public class PlayerCritEvent extends PlayerEvent {

    private final Entity entity;

    public PlayerCritEvent(Player who, Entity entity) {
        super(who);
        this.entity = entity;
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
