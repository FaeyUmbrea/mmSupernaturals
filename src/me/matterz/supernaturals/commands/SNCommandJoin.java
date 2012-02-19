package me.matterz.supernaturals.commands;

import me.matterz.supernaturals.io.SNConfigHandler;
import me.matterz.supernaturals.io.SNWhitelistHandler;

import org.bukkit.entity.Player;

public class SNCommandJoin extends SNCommand {

	public SNCommandJoin() {
		permissions = "";
		senderMustBePlayer = true;
		senderMustBeSupernatural = false;
		helpNameAndParams = "sn join";
		helpDescription = "Join in on the mmSupernatuals fun!";
	}

	@Override
	public void perform() {
		if (!SNConfigHandler.enableJoinCommand) {
			this.sendMessage("This is not enabled, you are automatically in the mmSupernaturals fun!");
			return;
		}
		Player senderPlayer = (Player) sender;
		if (SNWhitelistHandler.playersInWhitelist.contains(senderPlayer.getName())) {
			this.sendMessage("You are already whitelisted!");
			return;
		}
		SNWhitelistHandler.addPlayer(senderPlayer.getName());
	}

}