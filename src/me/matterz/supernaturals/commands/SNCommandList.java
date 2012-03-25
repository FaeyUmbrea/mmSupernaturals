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
import me.matterz.supernaturals.manager.SuperNManager;
import me.matterz.supernaturals.util.TextUtil;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SNCommandList extends SNCommand {

	public SNCommandList() {
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		senderMustBePlayer = false;
		permissions = "supernatural.command.list";
		helpNameAndParams = "list";
		helpDescription = "List supernaturals on the server.";
	}

	@Override
	public void perform() {
		if (!(sender instanceof Player)) {
			List<String> vampires = new ArrayList<String>();
			List<String> werewolves = new ArrayList<String>();
			List<String> ghouls = new ArrayList<String>();
			List<String> priests = new ArrayList<String>();
			List<String> hunters = new ArrayList<String>();
			List<String> demons = new ArrayList<String>();
			List<String> enderborns = new ArrayList<String>();
			List<String> angels = new ArrayList<String>();

			for (SuperNPlayer snplayer : SuperNManager.findAllOnline()) {
				if (snplayer.isVampire()) {
					vampires.add(snplayer.getName());
				} else if (snplayer.isPriest()) {
					priests.add(snplayer.getName());
				} else if (snplayer.isWere()) {
					werewolves.add(snplayer.getName());
				} else if (snplayer.isGhoul()) {
					ghouls.add(snplayer.getName());
				} else if (snplayer.isHunter()) {
					hunters.add(snplayer.getName());
				} else if (snplayer.isDemon()) {
					demons.add(snplayer.getName());
				} else if (snplayer.isEnderBorn()) {
					enderborns.add(snplayer.getName());
				} else if (snplayer.isAngel()) {
					angels.add(snplayer.getName());
				}
			}

			// Create Messages
			List<String> messages = new ArrayList<String>();
			List<String> messagesSpanish = new ArrayList<String>();
			messages.add("*** " + ChatColor.WHITE
					+ "Online Supernatural Players " + ChatColor.RED + "***");
			messages.add("Vampires: " + ChatColor.WHITE
					+ TextUtil.implode(vampires, ", "));
			messages.add("Werewolves: " + ChatColor.WHITE
					+ TextUtil.implode(werewolves, ", "));
			messages.add("Ghouls: " + ChatColor.WHITE
					+ TextUtil.implode(ghouls, ", "));
			messages.add("Priests: " + ChatColor.WHITE
					+ TextUtil.implode(priests, ", "));
			messages.add("WitchHunters: " + ChatColor.WHITE
					+ TextUtil.implode(hunters, ", "));
			messages.add("Demons: " + ChatColor.WHITE
					+ TextUtil.implode(demons, ", "));
			messages.add("EnderBorns: " + ChatColor.WHITE
					+ TextUtil.implode(enderborns, ", "));
			messagesSpanish.add("*** " + ChatColor.WHITE
					+ "Seres Místicos conectados: " + ChatColor.RED + "***");
			messagesSpanish.add("Vampiros: " + ChatColor.WHITE
					+ TextUtil.implode(vampires, ", "));
			messagesSpanish.add("Hombres Lobos: " + ChatColor.WHITE
					+ TextUtil.implode(werewolves, ", "));
			messagesSpanish.add("Muertos Vivientes: " + ChatColor.WHITE
					+ TextUtil.implode(ghouls, ", "));
			messagesSpanish.add("Sacerdotes: " + ChatColor.WHITE
					+ TextUtil.implode(priests, ", "));
			messagesSpanish.add("Cazadores de Brujas: " + ChatColor.WHITE
					+ TextUtil.implode(hunters, ", "));
			messagesSpanish.add("Demonios: " + ChatColor.WHITE
					+ TextUtil.implode(demons, ", "));
			messagesSpanish.add("EnderBorns: " + ChatColor.WHITE
					+ TextUtil.implode(enderborns, ", "));

			// Send them
			if (!SNConfigHandler.spanish) {
				this.sendMessage(messages);
			} else {
				this.sendMessage(messagesSpanish);
			}
			return;
		}
		Player senderPlayer = (Player) sender;
		if (!SupernaturalsPlugin.hasPermissions(senderPlayer, permissions)) {
			if (!SNConfigHandler.spanish) {
				this.sendMessage("You do not have permissions to use this command.");
			} else {
				this.sendMessage("No tienes permiso para este comando.");
			}
			return;
		}

		List<String> vampires = new ArrayList<String>();
		List<String> werewolves = new ArrayList<String>();
		List<String> ghouls = new ArrayList<String>();
		List<String> priests = new ArrayList<String>();
		List<String> hunters = new ArrayList<String>();
		List<String> demons = new ArrayList<String>();
		List<String> enderborns = new ArrayList<String>();
		List<String> angels = new ArrayList<String>();

		for (SuperNPlayer snplayer : SuperNManager.findAllOnline()) {
			if (snplayer.isVampire()) {
				vampires.add(snplayer.getName());
			} else if (snplayer.isPriest()) {
				priests.add(snplayer.getName());
			} else if (snplayer.isWere()) {
				werewolves.add(snplayer.getName());
			} else if (snplayer.isGhoul()) {
				ghouls.add(snplayer.getName());
			} else if (snplayer.isHunter()) {
				hunters.add(snplayer.getName());
			} else if (snplayer.isDemon()) {
				demons.add(snplayer.getName());
			} else if (snplayer.isEnderBorn()) {
				enderborns.add(snplayer.getName());
			} else if (snplayer.isAngel()) {
				angels.add(snplayer.getName());
			}
		}

		// Create Messages
		List<String> messages = new ArrayList<String>();
		List<String> messagesSpanish = new ArrayList<String>();
		messages.add("*** " + ChatColor.WHITE + "Online Supernatural Players "
				+ ChatColor.RED + "***");
		messages.add("Vampires: " + ChatColor.WHITE
				+ TextUtil.implode(vampires, ", "));
		messages.add("Werewolves: " + ChatColor.WHITE
				+ TextUtil.implode(werewolves, ", "));
		messages.add("Ghouls: " + ChatColor.WHITE
				+ TextUtil.implode(ghouls, ", "));
		messages.add("Priests: " + ChatColor.WHITE
				+ TextUtil.implode(priests, ", "));
		messages.add("WitchHunters: " + ChatColor.WHITE
				+ TextUtil.implode(hunters, ", "));
		messages.add("Demons: " + ChatColor.WHITE
				+ TextUtil.implode(demons, ", "));
		messages.add("EnderBorns: " + ChatColor.WHITE
				+ TextUtil.implode(enderborns, ", "));
		messages.add("Angels: " + ChatColor.WHITE + TextUtil.implode(angels, ", "));
		messagesSpanish.add("*** " + ChatColor.WHITE
				+ "Seres Místicos conectados: " + ChatColor.RED + "***");
		messagesSpanish.add("Vampiros: " + ChatColor.WHITE
				+ TextUtil.implode(vampires, ", "));
		messagesSpanish.add("Hombres Lobos: " + ChatColor.WHITE
				+ TextUtil.implode(werewolves, ", "));
		messagesSpanish.add("Muertos Vivientes: " + ChatColor.WHITE
				+ TextUtil.implode(ghouls, ", "));
		messagesSpanish.add("Sacerdotes: " + ChatColor.WHITE
				+ TextUtil.implode(priests, ", "));
		messagesSpanish.add("Cazadores de Brujas: " + ChatColor.WHITE
				+ TextUtil.implode(hunters, ", "));
		messagesSpanish.add("Demonios: " + ChatColor.WHITE
				+ TextUtil.implode(demons, ", "));
		messagesSpanish.add("EnderBorns: " + ChatColor.WHITE
				+ TextUtil.implode(enderborns, ", "));

		// Send them
		if (!SNConfigHandler.spanish) {
			this.sendMessage(messages);
		} else {
			this.sendMessage(messagesSpanish);
		}
	}
}
