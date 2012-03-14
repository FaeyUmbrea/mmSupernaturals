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
import me.matterz.supernaturals.manager.SuperNManager;

import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Door;

public class SNPlayerListener implements Listener {

	public SupernaturalsPlugin plugin;
	private String worldPermission = "supernatural.world.enabled";

	public SNPlayerListener(SupernaturalsPlugin instance) {
		instance.getServer().getPluginManager().registerEvents(this, instance);
		plugin = instance;
	}

	// @Override
	// public void onPlayerToggleSneak(PlayerToggleSneakEvent event){
	// Player player = event.getPlayer();
	// SuperNPlayer snplayer = SupernaturalManager.get(player);
	// if(snplayer.isHunter()){
	// player.setSneaking(true);
	// event.setCancelled(true);
	// }
	// }

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		Player player = event.getPlayer();
		SuperNPlayer snplayer = SuperNManager.get(player);

		ItemStack item = player.getItemInHand();
		Material itemMaterial = item.getType();

		if (action.equals(Action.RIGHT_CLICK_BLOCK)
				&& player.getTargetBlock(null, 20).getType() == Material.CLAY) {
			if (itemMaterial.equals(Material.ENDER_PEARL)) {
				SuperNManager.sendMessage(snplayer, "The clay changes... it moves...");
				SuperNManager.sendMessage(snplayer, "It wraps around you, takes over you.");
				player.playEffect(EntityEffect.HURT);
				player.playEffect(player.getLocation(), Effect.SMOKE, 5);
				SuperNManager.convert(snplayer, "enderborn");
				if (item.getAmount() == 1) {
					player.setItemInHand(null);
				} else {
					item.setAmount(item.getAmount() - 1);
				}
				event.setCancelled(true);
			}
		}
		if (!(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_AIR))
				&& event.isCancelled()) {
			return;
		}

		if (!SupernaturalsPlugin.hasPermissions(player, worldPermission)
				&& SNConfigHandler.multiworld) {
			return;
		}

		Location blockLoc;
		Block block = event.getClickedBlock();
		if (action.equals(Action.RIGHT_CLICK_BLOCK)
				|| action.equals(Action.LEFT_CLICK_BLOCK)) {
			try {
				blockLoc = block.getLocation();
			} catch (NullPointerException e) {
				SupernaturalsPlugin.log("Door trying to close.");
				event.setCancelled(true);
				return;
			}

			if (block.getType().equals(Material.IRON_DOOR_BLOCK)) {
				if (SNConfigHandler.debugMode) {
					SupernaturalsPlugin.log(snplayer.getName()
							+ " activated an Iron Door.");
				}
				for (int x = blockLoc.getBlockX() - 2; x < blockLoc.getBlockX() + 3; x++) {
					for (int y = blockLoc.getBlockY() - 2; y < blockLoc.getBlockY() + 3; y++) {
						for (int z = blockLoc.getBlockZ() - 2; z < blockLoc.getBlockZ() + 3; z++) {
							Location newLoc = new Location(block.getWorld(), x, y, z);
							Block newBlock = newLoc.getBlock();
							if (newBlock.getType().equals(Material.SIGN)
									|| newBlock.getType().equals(Material.WALL_SIGN)) {
								if (SNConfigHandler.debugMode) {
									SupernaturalsPlugin.log(snplayer.getName()
											+ " found a sign.");
								}
								Sign sign = (Sign) newBlock.getState();
								String[] text = sign.getLines();
								for (int i = 0; i < text.length; i++) {
									if (SNConfigHandler.debugMode) {
										SupernaturalsPlugin.log("The sign says: "
												+ text[i]);
									}
									if (text[i].contains(SNConfigHandler.hunterHallMessage)) {
										if (plugin.getHunterManager().doorIsOpening(blockLoc)) {
											if (SNConfigHandler.debugMode) {
												SupernaturalsPlugin.log("Cancelled door event.");
											}
											event.setCancelled(true);
											return;
										}
										Door door = (Door) block.getState().getData();
										boolean open = plugin.getHunterManager().doorEvent(player, block, door);
										event.setCancelled(open);
										return;
									}
								}
							}
						}
					}
				}
			}
		}

		boolean cancelled = false;

		cancelled = plugin.getClassManager(player).playerInteract(event);

		if (cancelled) {
			return;
		}

		if (!action.equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		}

		Material blockMaterial = event.getClickedBlock().getType();

		if (blockMaterial == Material.getMaterial(SNConfigHandler.vampireAltarInfectMaterial)) {
			if (SNConfigHandler.debugMode) {
				SupernaturalsPlugin.log(snplayer.getName()
						+ " triggered a Vampire Infect Altar.");
			}
			plugin.getVampireManager().useAltarInfect(player, event.getClickedBlock());
		} else if (blockMaterial == Material.getMaterial(SNConfigHandler.vampireAltarCureMaterial)) {
			if (SNConfigHandler.debugMode) {
				SupernaturalsPlugin.log(snplayer.getName()
						+ " triggered a Vampire Cure Altar.");
			}
			plugin.getVampireManager().useAltarCure(player, event.getClickedBlock());
		} else if (blockMaterial == Material.getMaterial(SNConfigHandler.priestAltarMaterial)) {
			if (SNConfigHandler.debugMode) {
				SupernaturalsPlugin.log(snplayer.getName()
						+ " triggered a Priest Altar.");
			}
			plugin.getPriestManager().useAltar(player);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerKick(PlayerKickEvent event) {
		if (event.isCancelled()) {
			return;
		}

		if (!SupernaturalsPlugin.hasPermissions(event.getPlayer(), worldPermission)
				&& SNConfigHandler.multiworld) {
			return;
		}

		if (event.getLeaveMessage().contains("Flying")
				|| event.getReason().contains("Flying")) {
			SuperNPlayer snplayer = SuperNManager.get(event.getPlayer());
			if (snplayer.isVampire()
					&& event.getPlayer().getItemInHand().getType().toString().equalsIgnoreCase(SNConfigHandler.jumpMaterial)) {
				event.setCancelled(true);
				if (SNConfigHandler.debugMode) {
					SupernaturalsPlugin.log(event.getPlayer().getName()
							+ " was not kicked for flying as a vampire.");
				}
			}
		}
	}
}
