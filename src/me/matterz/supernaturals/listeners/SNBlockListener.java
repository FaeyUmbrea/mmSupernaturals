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

import me.matterz.supernaturals.SupernaturalsPlugin;
import me.matterz.supernaturals.io.SNConfigHandler;
import me.matterz.supernaturals.manager.SuperNManager;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

public class SNBlockListener implements Listener {

	private SupernaturalsPlugin plugin;
	private String permissions = "supernatural.player.witchhuntersign";
	private String worldPermission = "supernatural.world.enabled";

	public SNBlockListener(SupernaturalsPlugin instance) {
		instance.getServer().getPluginManager().registerEvents(this, instance);
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onBlockBreak(BlockBreakEvent event) {
		Block eventBlock = event.getBlock();
		if (eventBlock.getType().equals(Material.WEB)) {
			for (Block block : plugin.getDemonManager().getWebs().keySet()) {
				if (block.equals(eventBlock)) {
					event.setCancelled(true);
					block.setType(Material.AIR);
					plugin.getDemonManager().removeWeb(block);
					if (SNConfigHandler.debugMode) {
						SupernaturalsPlugin.log("Removed web block through destruction.");
					}
					return;
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onSignChange(SignChangeEvent event) {
		Player player = event.getPlayer();
		String[] text = event.getLines();
		if (!SupernaturalsPlugin.hasPermissions(player, worldPermission)
				&& SNConfigHandler.multiworld) {
			return;
		}
		for (int i = 0; i < text.length; i++) {
			if (text[i].contains(SNConfigHandler.hunterHallMessage)) {
				if (!SupernaturalsPlugin.hasPermissions(player, permissions)) {
					SuperNManager.sendMessage(SuperNManager.get(player), "You do not have permission to create WitchHunter signs");
					event.setCancelled(true);
					event.getBlock().setTypeId(0);
					player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.SIGN, 1));
				}
				return;
			}
		}
	}
}
