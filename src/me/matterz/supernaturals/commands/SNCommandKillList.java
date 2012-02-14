/*
 * Supernatural Players Plugin for Bukkit
 * Copyright (C) 2011  Matt Walker <mmw167@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package me.matterz.supernaturals.commands;

import java.util.ArrayList;
import java.util.List;

import me.matterz.supernaturals.SuperNPlayer;
import me.matterz.supernaturals.SupernaturalsPlugin;
import me.matterz.supernaturals.io.SNConfigHandler;
import me.matterz.supernaturals.manager.HunterManager;
import me.matterz.supernaturals.manager.SuperNManager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SNCommandKillList extends SNCommand {

	public SNCommandKillList() {
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		senderMustBePlayer = true;
		permissions = "supernatural.command.killlist";
		helpNameAndParams = "convert [playername] [supernaturalType]";
		helpDescription = "Instantly turn a player into a supernatural.";
	}

	@Override
	public void perform() {

		if (!SNConfigHandler.spanish) {
			Player senderPlayer = (Player) sender;
			SuperNPlayer snSender = SuperNManager.get(senderPlayer);
			if (!SupernaturalsPlugin.hasPermissions(senderPlayer, permissions)) {
				if (!SNConfigHandler.spanish) {
					this.sendMessage("You do not have permissions to use this command.");
				} else {
					this.sendMessage("No tienes permiso para este comando.");
				}
				return;
			}

			if (!snSender.isHunter()) {
				this.sendMessage("You are not a WitchHunter!");
			}

			ArrayList<SuperNPlayer> bountyList = HunterManager.getBountyList();

			// Create Messages
			List<String> messages = new ArrayList<String>();
			messages.add("*** " + ChatColor.WHITE
					+ "Current WitchHunter Targets " + ChatColor.RED + "***");
			for (SuperNPlayer snplayer : bountyList) {
				messages.add(ChatColor.WHITE + snplayer.getName());
			}

			// Send them
			this.sendMessage(messages);
		} else {
			Player senderPlayer = (Player) sender;
			if (!SupernaturalsPlugin.hasPermissions(senderPlayer, permissions)) {
				this.sendMessage("No tienes permiso para este comando.");
				return;
			}

			ArrayList<SuperNPlayer> bountyList = HunterManager.getBountyList();

			// Create Messages
			List<String> messages = new ArrayList<String>();
			messages.add("*** " + ChatColor.WHITE
					+ "Objetivos para Cazadores de Brujas: " + ChatColor.RED
					+ "***");
			for (SuperNPlayer snplayer : bountyList) {
				messages.add(ChatColor.WHITE + snplayer.getName());
			}

			// Send them
			this.sendMessage(messages);
		}
	}
}
