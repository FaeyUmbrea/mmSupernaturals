package me.matterz.supernaturals.listeners;

import me.matterz.supernaturals.SuperNPlayer;
import me.matterz.supernaturals.SupernaturalsPlugin;
import me.matterz.supernaturals.events.SupernaturalConvertEvent;
import me.matterz.supernaturals.io.SNWhitelistHandler;
import me.matterz.supernaturals.manager.SuperNManager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SNSupernaturalListener implements Listener {

	public SNSupernaturalListener(SupernaturalsPlugin instance) {
		instance.getServer().getPluginManager().registerEvents(this, instance);
	}

	@EventHandler
	public void onSupernaturalConvert(SupernaturalConvertEvent event) {
		SuperNPlayer convertedPlayer = event.getConvertedPlayer();
		SuperNPlayer converterPlayer = event.getConverter();
		if (!SNWhitelistHandler.isWhitelisted(convertedPlayer)) {
			event.setCancelled(true);
			if (!converterPlayer.equals(convertedPlayer)) {
				SuperNManager.sendMessage(converterPlayer, "The player you have tried to convert has not used the join command!");
			}
			SuperNManager.sendMessage(convertedPlayer, "You have not used the sn join command!");
		}
	}

}
