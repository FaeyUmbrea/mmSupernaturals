package me.matterz.supernaturals.events;

import me.matterz.supernaturals.SuperNPlayer;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SupernaturalConvertEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private SuperNPlayer converted;
	private boolean cancelled;

	public SupernaturalConvertEvent(SuperNPlayer snplayer) {
		converted = snplayer;
	}

	/**
	 * Gets the player converted in the event.
	 * 
	 * @return The converted player.
	 */
	public SuperNPlayer getConvertedPlayer() {
		return converted;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

}
