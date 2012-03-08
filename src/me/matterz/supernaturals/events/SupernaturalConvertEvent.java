package me.matterz.supernaturals.events;

import me.matterz.supernaturals.SuperNPlayer;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SupernaturalConvertEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private SuperNPlayer converted;
	private boolean cancelled;
	private SuperNPlayer converterPlayer;

	public SupernaturalConvertEvent(SuperNPlayer snplayer,
			SuperNPlayer converter) {
		converted = snplayer;
		converterPlayer = converter;
	}

	/**
	 * Gets the player converted in the event.
	 * 
	 * @return The converted player.
	 */
	public SuperNPlayer getConvertedPlayer() {
		return converted;
	}

	/**
	 * Gets the player that converted the player being converted If the player
	 * being converted was not converted by a player, this will return
	 * {@code null}
	 * 
	 * @return The player who converted the player being converted.
	 */
	public SuperNPlayer getConverter() {
		return converterPlayer;
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
