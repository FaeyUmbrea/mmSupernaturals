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

package me.matterz.supernaturals.listeners;

import me.matterz.supernaturals.SuperNPlayer;
import me.matterz.supernaturals.SupernaturalsPlugin;
import me.matterz.supernaturals.io.SNConfigHandler;
import me.matterz.supernaturals.io.SNWhitelistHandler;
import me.matterz.supernaturals.manager.SuperNManager;

import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;

public class SNPlayerMonitor implements Listener {

	private SupernaturalsPlugin plugin;
	private String worldPermission = "supernatural.world.enabled";

	public SNPlayerMonitor(SupernaturalsPlugin instance) {
		instance.getServer().getPluginManager().registerEvents(this, instance);
		plugin = instance;
	}

	// @Override
	// public void onPlayerRespawn(PlayerRespawnEvent event){
	// if(SupernaturalManager.get(event.getPlayer()).isHunter()){
	// event.getPlayer().setSneaking(true);
	// }
	// }

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerPortal(PlayerPortalEvent event) {
		Player player = event.getPlayer();
		if (!SupernaturalsPlugin.hasPermissions(event.getPlayer(), worldPermission)
				&& SNConfigHandler.multiworld) {
			return;
		}
		if (event.getTo().getWorld().getEnvironment().equals(Environment.NETHER)) {
			if (SNConfigHandler.debugMode) {
				SupernaturalsPlugin.log("Player inventory logged.");
			}
			plugin.getDemonManager().checkInventory(player);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (!SupernaturalsPlugin.hasPermissions(event.getPlayer(), worldPermission)
				&& SNConfigHandler.multiworld) {
			return;
		}
		SuperNPlayer snplayer = SuperNManager.get(player);
		if (!SNWhitelistHandler.isWhitelisted(SuperNManager.get(player))) {
			SuperNManager.sendMessage(snplayer, "Your class has been reset because you are trying to bypass");
			SuperNManager.sendMessage(snplayer, "The mmSupernaturals whitelist!");
		}

		if (SupernaturalsPlugin.hasPermissions(player, "supernatural.admin.infinitepower")) {
			snplayer.setPower(10000); // Making power really infinite.
		}

		if (!SNConfigHandler.enableLoginMessage) {
			return;
		}
		SuperNManager.updateName(snplayer);

		if (SNConfigHandler.enableColors) {
			if (SNConfigHandler.spanish) {
				if (snplayer.isHuman()) {
					player.setDisplayName(player.getDisplayName().trim().replace(player.getName(), ChatColor.WHITE
							+ player.getName()));
					plugin.getServer().broadcastMessage(ChatColor.WHITE
							+ "El Humano " + player.getName() + ChatColor.GOLD
							+ " ha entrado al juego.");
				} else if (snplayer.isVampire()) {
					player.setDisplayName(player.getDisplayName().trim().replace(player.getName(), ChatColor.DARK_PURPLE
							+ player.getName()));
					plugin.getServer().broadcastMessage(ChatColor.DARK_PURPLE
							+ "El Vampiro " + player.getName() + ChatColor.GOLD
							+ " ha entrado al juego.");
				} else if (snplayer.isWere()) {
					player.setDisplayName(player.getDisplayName().trim().replace(player.getName(), ChatColor.BLUE
							+ player.getName()));
					plugin.getServer().broadcastMessage(ChatColor.BLUE
							+ "El Hombre Lobo " + player.getName()
							+ ChatColor.GOLD + " ha entrado al juego.");
				} else if (snplayer.isGhoul()) {
					player.setDisplayName(player.getDisplayName().trim().replace(player.getName(), ChatColor.DARK_GRAY
							+ player.getName()));
					plugin.getServer().broadcastMessage(ChatColor.DARK_GRAY
							+ "El Muerto Viviente " + player.getName()
							+ ChatColor.GOLD + " ha entrado al juego.");
				} else if (snplayer.isPriest()) {
					player.setDisplayName(player.getDisplayName().trim().replace(player.getName(), ChatColor.GOLD
							+ player.getName()));
					plugin.getServer().broadcastMessage(ChatColor.GOLD
							+ "El Sacerdote " + player.getName()
							+ ChatColor.GOLD + " ha entrado al juego.");
				} else if (snplayer.isHunter()) {
					// player.setSneaking(true);
					player.setDisplayName(player.getDisplayName().trim().replace(player.getName(), ChatColor.GREEN
							+ player.getName()));
					plugin.getServer().broadcastMessage(ChatColor.GREEN
							+ "El Cazador de Brujas " + player.getName()
							+ ChatColor.GOLD + " ha entrado al juego.");
				} else if (snplayer.isDemon()) {
					player.setDisplayName(player.getDisplayName().trim().replace(player.getName(), ChatColor.RED
							+ player.getName()));
					plugin.getServer().broadcastMessage(ChatColor.RED
							+ "El Demonio " + player.getName() + ChatColor.GOLD
							+ " ha entrado al juego.");
				} else if (snplayer.isEnderBorn()) {
					player.setDisplayName(player.getDisplayName().trim().replace(player.getName(), ChatColor.LIGHT_PURPLE
							+ player.getName()));
					plugin.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE
							+ "El EnderBorn " + player.getName()
							+ ChatColor.GOLD + " ha entrado al juego.");
				} else if (snplayer.isAngel()) {
					player.setDisplayName(player.getDisplayName().trim().replace(player.getName(), ChatColor.AQUA
							+ player.getName()));
					plugin.getServer().broadcastMessage(ChatColor.AQUA
							+ "El Angel " + player.getName() + ChatColor.GOLD
							+ " ha entrado al juego.");
				}
				return;
			}
			if (snplayer.isHuman()) {
				player.setDisplayName(player.getDisplayName().trim().replace(player.getName(), ChatColor.WHITE
						+ player.getName()));
				plugin.getServer().broadcastMessage(ChatColor.WHITE + "Human "
						+ player.getName() + ChatColor.GOLD
						+ " has joined the server.");
			} else if (snplayer.isVampire()) {
				player.setDisplayName(player.getDisplayName().trim().replace(player.getName(), ChatColor.DARK_PURPLE
						+ player.getName()));
				plugin.getServer().broadcastMessage(ChatColor.DARK_PURPLE
						+ "Vampire " + player.getName() + ChatColor.GOLD
						+ " has joined the server.");
			} else if (snplayer.isWere()) {
				player.setDisplayName(player.getDisplayName().trim().replace(player.getName(), ChatColor.BLUE
						+ player.getName()));
				plugin.getServer().broadcastMessage(ChatColor.BLUE
						+ "Werewolf " + player.getName() + ChatColor.GOLD
						+ " has joined the server.");
			} else if (snplayer.isGhoul()) {
				player.setDisplayName(player.getDisplayName().trim().replace(player.getName(), ChatColor.DARK_GRAY
						+ player.getName()));
				plugin.getServer().broadcastMessage(ChatColor.DARK_GRAY
						+ "Ghoul " + player.getName() + ChatColor.GOLD
						+ " has joined the server.");
			} else if (snplayer.isPriest()) {
				player.setDisplayName(player.getDisplayName().trim().replace(player.getName(), ChatColor.GOLD
						+ player.getName()));
				plugin.getServer().broadcastMessage(ChatColor.GOLD + "Priest "
						+ player.getName() + ChatColor.GOLD
						+ " has joined the server.");
			} else if (snplayer.isHunter()) {
				// player.setSneaking(true);
				player.setDisplayName(player.getDisplayName().trim().replace(player.getName(), ChatColor.GREEN
						+ player.getName()));
				plugin.getServer().broadcastMessage(ChatColor.GREEN
						+ "WitchHunter " + player.getName() + ChatColor.GOLD
						+ " has joined the server.");
			} else if (snplayer.isDemon()) {
				player.setDisplayName(player.getDisplayName().trim().replace(player.getName(), ChatColor.RED
						+ player.getName()));
				plugin.getServer().broadcastMessage(ChatColor.RED + "Demon "
						+ player.getName() + ChatColor.GOLD
						+ " has joined the server.");
			} else if (snplayer.isEnderBorn()) {
				player.setDisplayName(player.getDisplayName().trim().replace(player.getName(), ChatColor.LIGHT_PURPLE
						+ player.getName()));
				plugin.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE
						+ "EnderBorn " + player.getName() + ChatColor.GOLD
						+ " has joined the server.");
			} else if (snplayer.isAngel()) {
				player.setDisplayName(player.getDisplayName().trim().replace(player.getName(), ChatColor.AQUA
						+ player.getName()));
				plugin.getServer().broadcastMessage(ChatColor.AQUA + "Angel "
						+ player.getName() + ChatColor.GOLD
						+ " has joined the server.");
			}
		}
	}
}