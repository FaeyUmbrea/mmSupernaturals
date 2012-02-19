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

import me.matterz.supernaturals.SuperNPlayer;
import me.matterz.supernaturals.SupernaturalsPlugin;
import me.matterz.supernaturals.io.SNConfigHandler;
import me.matterz.supernaturals.manager.SuperNManager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SNCommandConvert extends SNCommand {

	public SNCommandConvert() {
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		senderMustBePlayer = false;
		optionalParameters.add("playername");
		requiredParameters.add("supernaturalType");
		permissions = "supernatural.admin.command.curse";
		helpNameAndParams = "convert [playername] [supernaturalType]";
		helpDescription = "Instantly turn a player into a supernatural.";
	}

	public static String permission2 = "supernatural.admin.partial.curse";

	@Override
	public void perform() {

		if (!(sender instanceof Player)) {
			if (parameters.size() == 1) {
				this.sendMessage("Missing player!");
			} else {
				String playername = parameters.get(0);
				String superType = parameters.get(1).toLowerCase();
				Player player = SupernaturalsPlugin.instance.getServer().getPlayer(playername);

				if (player == null) {
					if (!SNConfigHandler.spanish) {
						this.sendMessage("Player not found!");
					} else {
						this.sendMessage("Jugador no encontrado!");
					}
					return;
				}

				if (!SNConfigHandler.supernaturalTypes.contains(superType)) {
					if (!SNConfigHandler.spanish) {
						this.sendMessage("Supernatural Type invalid!");
					} else {
						this.sendMessage("Ser M�stico invalido!");
					}
					return;
				}

				SuperNPlayer snplayer = SuperNManager.get(player);

				if (!SNConfigHandler.spanish) {
					if (snplayer.getType().equalsIgnoreCase(superType)) {
						this.sendMessage(ChatColor.WHITE + player.getName()
								+ ChatColor.RED + " is already a "
								+ ChatColor.WHITE + superType + ChatColor.RED
								+ " !");
					} else if (snplayer.getOldType().equalsIgnoreCase(superType)) {
						this.sendMessage(ChatColor.WHITE + player.getName()
								+ ChatColor.RED + " was turned BACK into a "
								+ ChatColor.WHITE + superType + ChatColor.RED
								+ " !");
						SuperNManager.revert(snplayer);
					} else {
						this.sendMessage(ChatColor.WHITE + player.getName()
								+ ChatColor.RED + " was turned into a "
								+ ChatColor.WHITE + superType + ChatColor.RED
								+ " !");
						SuperNManager.convert(snplayer, superType);
					}
				} else {
					if (snplayer.getType().equalsIgnoreCase(superType)) {
						this.sendMessage(ChatColor.WHITE + sender.getName()
								+ ChatColor.RED + " ya es un "
								+ ChatColor.WHITE + superType + ChatColor.RED
								+ " !");
					} else if (snplayer.getOldType().equalsIgnoreCase(superType)) {
						this.sendMessage(ChatColor.WHITE + sender.getName()
								+ ChatColor.RED + " se convirtio de nuevo en "
								+ ChatColor.WHITE + superType + ChatColor.RED
								+ " !");
						SuperNManager.revert(snplayer);
					} else {
						this.sendMessage(ChatColor.WHITE + sender.getName()
								+ ChatColor.RED + " se convirtio en un "
								+ ChatColor.WHITE + superType + ChatColor.RED
								+ " !");
						SuperNManager.convert(snplayer, superType);
					}
				}
			}
			return;
		}

		Player senderPlayer = (Player) sender;
		if (parameters.size() == 1) {
			if (!SupernaturalsPlugin.hasPermissions(senderPlayer, permission2)) {
				if (!SNConfigHandler.spanish) {
					this.sendMessage("You do not have permissions to use this command.");
				} else {
					this.sendMessage("No tienes permiso para este comando.");
				}
				return;
			}
			String superType = parameters.get(0).toLowerCase();

			if (!SNConfigHandler.supernaturalTypes.contains(superType)) {
				if (!SNConfigHandler.spanish) {
					this.sendMessage("Supernatural Type invalid!");
				} else {
					this.sendMessage("Ser M�stico invalido!");
				}
				return;
			}

			SuperNPlayer snplayer = SuperNManager.get(senderPlayer);

			if (!SNConfigHandler.spanish) {
				if (snplayer.getType().equalsIgnoreCase(superType)) {
					this.sendMessage(ChatColor.WHITE + senderPlayer.getName()
							+ ChatColor.RED + " is already a "
							+ ChatColor.WHITE + superType + ChatColor.RED
							+ " !");
				} else if (snplayer.getOldType().equalsIgnoreCase(superType)) {
					this.sendMessage(ChatColor.WHITE + senderPlayer.getName()
							+ ChatColor.RED + " was turned BACK into a "
							+ ChatColor.WHITE + superType + ChatColor.RED
							+ " !");
					SuperNManager.revert(snplayer);
				} else {
					this.sendMessage(ChatColor.WHITE + senderPlayer.getName()
							+ ChatColor.RED + " was turned into a "
							+ ChatColor.WHITE + superType + ChatColor.RED
							+ " !");
					SuperNManager.convert(snplayer, superType);
				}
			} else {
				if (snplayer.getType().equalsIgnoreCase(superType)) {
					this.sendMessage(ChatColor.WHITE + senderPlayer.getName()
							+ ChatColor.RED + " ya es un " + ChatColor.WHITE
							+ superType + ChatColor.RED + " !");
				} else if (snplayer.getOldType().equalsIgnoreCase(superType)) {
					this.sendMessage(ChatColor.WHITE + senderPlayer.getName()
							+ ChatColor.RED + " se convirtio de nuevo en "
							+ ChatColor.WHITE + superType + ChatColor.RED
							+ " !");
					SuperNManager.revert(snplayer);
				} else {
					this.sendMessage(ChatColor.WHITE + senderPlayer.getName()
							+ ChatColor.RED + " se convirtio en un "
							+ ChatColor.WHITE + superType + ChatColor.RED
							+ " !");
					SuperNManager.convert(snplayer, superType);
				}
			}
		} else {
			if (!SupernaturalsPlugin.hasPermissions(senderPlayer, permissions)) {
				if (!SNConfigHandler.spanish) {
					this.sendMessage("You do not have permissions to use this command.");
				} else {
					this.sendMessage("No tienes permiso para este comando.");
				}
				return;
			}
			String playername = parameters.get(0);
			String superType = parameters.get(1).toLowerCase();
			Player player = SupernaturalsPlugin.instance.getServer().getPlayer(playername);

			if (player == null) {
				if (!SNConfigHandler.spanish) {
					this.sendMessage("Player not found!");
				} else {
					this.sendMessage("Jugador no encontrado!");
				}
				return;
			}

			if (!SNConfigHandler.supernaturalTypes.contains(superType)) {
				if (!SNConfigHandler.spanish) {
					this.sendMessage("Supernatural Type invalid!");
				} else {
					this.sendMessage("Ser M�stico invalido!");
				}
				return;
			}

			SuperNPlayer snplayer = SuperNManager.get(player);

			if (!SNConfigHandler.spanish) {
				if (snplayer.getType().equalsIgnoreCase(superType)) {
					this.sendMessage(ChatColor.WHITE + player.getName()
							+ ChatColor.RED + " is already a "
							+ ChatColor.WHITE + superType + ChatColor.RED
							+ " !");
				} else if (snplayer.getOldType().equalsIgnoreCase(superType)) {
					this.sendMessage(ChatColor.WHITE + player.getName()
							+ ChatColor.RED + " was turned BACK into a "
							+ ChatColor.WHITE + superType + ChatColor.RED
							+ " !");
					SuperNManager.revert(snplayer);
				} else {
					this.sendMessage(ChatColor.WHITE + player.getName()
							+ ChatColor.RED + " was turned into a "
							+ ChatColor.WHITE + superType + ChatColor.RED
							+ " !");
					SuperNManager.convert(snplayer, superType);
				}
			} else {
				if (snplayer.getType().equalsIgnoreCase(superType)) {
					this.sendMessage(ChatColor.WHITE + senderPlayer.getName()
							+ ChatColor.RED + " ya es un " + ChatColor.WHITE
							+ superType + ChatColor.RED + " !");
				} else if (snplayer.getOldType().equalsIgnoreCase(superType)) {
					this.sendMessage(ChatColor.WHITE + senderPlayer.getName()
							+ ChatColor.RED + " se convirtio de nuevo en "
							+ ChatColor.WHITE + superType + ChatColor.RED
							+ " !");
					SuperNManager.revert(snplayer);
				} else {
					this.sendMessage(ChatColor.WHITE + senderPlayer.getName()
							+ ChatColor.RED + " se convirtio en un "
							+ ChatColor.WHITE + superType + ChatColor.RED
							+ " !");
					SuperNManager.convert(snplayer, superType);
				}
			}
		}
	}
}
