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

package me.matterz.supernaturals.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.matterz.supernaturals.SuperNPlayer;
import me.matterz.supernaturals.SupernaturalsPlugin;
import me.matterz.supernaturals.io.SNConfigHandler;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class WereManager extends ClassManager {

	public WereManager() {
		super();
	}

	private String permissions2 = "supernatural.player.wolfbane";
	private static HashMap<Wolf, SuperNPlayer> wolvesMap = new HashMap<Wolf, SuperNPlayer>();

	// -------------------------------------------- //
	// Damage Events //
	// -------------------------------------------- //

	@Override
	public double victimEvent(EntityDamageEvent event, double damage) {
		if (event.getCause().equals(DamageCause.FALL)) {
			damage *= SNConfigHandler.wereDamageFall;
		}
		return damage;
	}

	@Override
	public double damagerEvent(EntityDamageByEntityEvent event, double damage) {
		Entity damager = event.getDamager();
		Player pDamager = (Player) damager;
		SuperNPlayer snDamager = SuperNManager.get(pDamager);
		ItemStack item = pDamager.getItemInHand();

		if (SuperNManager.worldTimeIsNight(pDamager)) {
			if (item != null) {
				if (SNConfigHandler.wereWeapons.contains(item.getType())) {
					if (SNConfigHandler.debugMode) {
						SupernaturalsPlugin.log(pDamager.getName()
								+ " was not allowed to use "
								+ item.getType().toString());
					}
					SuperNManager.sendMessage(snDamager, "Werewolves cannot use this weapon at night!");
					damage = 0;
				} else {
					damage += damage
							* snDamager.scale(SNConfigHandler.wereDamageFactor);
				}
			}
		}
		return damage;
	}

	@Override
	public void deathEvent(Player player) {
		if (SNConfigHandler.debugMode) {
			SupernaturalsPlugin.log("Player died.");
		}

		SuperNPlayer snplayer = SuperNManager.get(player);

		SuperNManager.alterPower(snplayer, -SNConfigHandler.wereDeathPowerPenalty, "You died!");
	}

	@Override
	public void killEvent(Player pDamager, SuperNPlayer damager, SuperNPlayer victim) {
		if (victim == null) {
			SuperNManager.alterPower(damager, SNConfigHandler.wereKillPowerCreatureGain, "Creature death!");
		} else {
			double random = Math.random();
			if (victim.getPower() > SNConfigHandler.wereKillPowerPlayerGain) {
				SuperNManager.alterPower(damager, SNConfigHandler.wereKillPowerPlayerGain, "Player killed!");
			} else {
				SuperNManager.sendMessage(damager, "You cannot gain power from a player with no power themselves.");
			}
			if (SNConfigHandler.wereKillSpreadCurse
					&& !victim.isSuper()
					&& SuperNManager.worldTimeIsNight(SupernaturalsPlugin.instance.getServer().getPlayer(victim.getName()))) {
				if (random < SNConfigHandler.spreadChance) {
					SuperNManager.sendMessage(victim, "Your basic nature changes... You feel more in touch with your animal side.");
					SuperNManager.convert(victim, "werewolf");
				}
			}
		}

	}

	// -------------------------------------------- //
	// Interact //
	// -------------------------------------------- //

	@Override
	public boolean playerInteract(PlayerInteractEvent event) {

		Action action = event.getAction();
		Player player = event.getPlayer();
		SuperNPlayer snplayer = SuperNManager.get(player);
		Material itemMaterial = event.getMaterial();

		if (action.equals(Action.LEFT_CLICK_AIR)
				|| action.equals(Action.LEFT_CLICK_BLOCK)) {
			if (player.getItemInHand() == null) {
				return false;
			}

			if (itemMaterial.toString().equalsIgnoreCase(SNConfigHandler.wolfMaterial)) {
				if (SuperNManager.worldTimeIsNight(player)) {
					summon(player);
					event.setCancelled(true);
					return true;
				} else {
					SuperNManager.sendMessage(snplayer, "Cannot use this ability during the day.");
					return false;
				}
			} else if (itemMaterial.toString().equalsIgnoreCase(SNConfigHandler.wolfbaneMaterial)) {
				if (!SupernaturalsPlugin.hasPermissions(player, permissions2)) {
					return false;
				}
				if (SuperNManager.worldTimeIsNight(player)) {
					SuperNManager.sendMessage(snplayer, "Cannot cure lycanthropy during the night.");
					return false;
				} else {
					wolfbane(player);
					event.setCancelled(true);
					return true;
				}
			} else if (itemMaterial.toString().equalsIgnoreCase(SNConfigHandler.dashMaterial)) {
				if (SuperNManager.worldTimeIsNight(player)) {
					SuperNManager.jump(event.getPlayer(), SNConfigHandler.dashDeltaSpeed, false);
					event.setCancelled(true);
					return true;
				} else {
					SuperNManager.sendMessage(snplayer, "Cannot use this ability during the day.");
					return false;
				}
			}
		}

		if (!(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK))) {
			return false;
		}

		if (action.equals(Action.RIGHT_CLICK_AIR)) {
			if (SuperNManager.worldTimeIsNight(player)) {
				if (itemMaterial != null) {
					if (SNConfigHandler.foodMaterials.contains(itemMaterial)) {
						if (itemMaterial.equals(Material.BREAD)) {
							SuperNManager.sendMessage(snplayer, "Werewolves do not gain power from Bread.");
							return false;
						} else {
							SuperNManager.alterPower(snplayer, SNConfigHandler.werePowerFood, "Eating!");
							if (SNConfigHandler.debugMode) {
								SupernaturalsPlugin.log(snplayer.getName()
										+ " ate " + itemMaterial.toString()
										+ " to gain "
										+ SNConfigHandler.werePowerFood
										+ " power!");
							}
							player.setFoodLevel(player.getFoodLevel() + 6); // Hardcoded
																			// value
																			// :D
							Inventory inv = player.getInventory();
							inv.removeItem(new ItemStack(itemMaterial, 1));
							player.updateInventory();
							return true;
						}
					}
				}
			}
			if (itemMaterial != null) {
				if (SNConfigHandler.foodMaterials.contains(itemMaterial)) {
					if (player.getFoodLevel() == 20) {
						return false;
					}
					if (itemMaterial.equals(Material.BREAD)) {
						SuperNManager.sendMessage(snplayer, "Werewolves do not gain power from Bread.");
						return false;
					} else {
						SuperNManager.alterPower(snplayer, SNConfigHandler.werePowerFood, "Eating!");
						if (SNConfigHandler.debugMode) {
							SupernaturalsPlugin.log(snplayer.getName()
									+ " ate " + itemMaterial.toString()
									+ " to gain "
									+ SNConfigHandler.werePowerFood + " power!");
						}
						player.setFoodLevel(player.getFoodLevel() + 6); // Hardcoded
																		// value
																		// :D
						Inventory inv = player.getInventory();
						inv.removeItem(new ItemStack(itemMaterial, 1));
						player.updateInventory();
						return true;
					}
				}
			}
		}
		return false;
	}

	// -------------------------------------------- //
	// Armor //
	// -------------------------------------------- //

	@Override
	public void armorCheck(Player player) {
		PlayerInventory inv = player.getInventory();
		ItemStack helmet = inv.getHelmet();
		ItemStack chest = inv.getChestplate();
		ItemStack leggings = inv.getLeggings();
		ItemStack boots = inv.getBoots();

		if (helmet != null) {
			if (!SNConfigHandler.wereArmor.contains(helmet.getType())) {
				inv.setHelmet(null);
				dropItem(player, helmet);
			}
		}
		if (chest != null) {
			if (!SNConfigHandler.wereArmor.contains(chest.getType())) {
				inv.setChestplate(null);
				dropItem(player, chest);
			}
		}
		if (leggings != null) {
			if (!SNConfigHandler.wereArmor.contains(leggings.getType())) {
				inv.setLeggings(null);
				dropItem(player, leggings);
			}
		}
		if (boots != null) {
			if (!SNConfigHandler.wereArmor.contains(boots.getType())) {
				inv.setBoots(null);
				dropItem(player, boots);
			}
		}
	}

	// -------------------------------------------- //
	// Wolfbane //
	// -------------------------------------------- //

	public boolean wolfbane(Player player) {
		SuperNPlayer snplayer = SuperNManager.get(player);
		if (SNConfigHandler.wereWolfbaneRecipe.playerHasEnough(player)) {
			SuperNManager.sendMessage(snplayer, "You create a wolfbane potion!");
			SuperNManager.sendMessage(snplayer, SNConfigHandler.wereWolfbaneRecipe.getRecipeLine());
			SNConfigHandler.wereWolfbaneRecipe.removeFromPlayer(player);
			SuperNManager.cure(snplayer);
			return true;
		} else {
			SuperNManager.sendMessage(snplayer, "You cannot create a Wolfbane potion without the following: ");
			SuperNManager.sendMessage(snplayer, SNConfigHandler.wereWolfbaneRecipe.getRecipeLine());
			return false;
		}
	}

	// -------------------------------------------- //
	// Summonings //
	// -------------------------------------------- //

	public boolean summon(Player player) {
		SuperNPlayer snplayer = SuperNManager.get(player);
		ItemStack item = player.getItemInHand();
		if (!SupernaturalsPlugin.instance.getSpawn(player)) {
			SuperNManager.sendMessage(snplayer, "You cannot summon here.");
			return false;
		}
		if (SuperNManager.worldTimeIsNight(player)) {
			if (snplayer.getPower() >= SNConfigHandler.werePowerSummonCost) {
				int i = 0;
				for (Wolf wolf : wolvesMap.keySet()) {
					if (wolvesMap.get(wolf).equals(snplayer)) {
						i++;
					}
				}
				if (i <= 4) {
					Wolf wolf = (Wolf) player.getWorld().spawnCreature(player.getLocation(), EntityType.WOLF);
					wolf.setTamed(true);
					wolf.setOwner(player);
					wolf.setHealth(20);
					wolvesMap.put(wolf, snplayer);
					SuperNManager.alterPower(snplayer, -SNConfigHandler.werePowerSummonCost, "Summoning wolf!");
					if (SNConfigHandler.debugMode) {
						SupernaturalsPlugin.log(snplayer.getName()
								+ " summoned a wolf pet!");
					}
					if (item.getAmount() == 1) {
						player.setItemInHand(null);
					} else {
						item.setAmount(player.getItemInHand().getAmount() - 1);
					}
					return true;
				} else {
					SuperNManager.sendMessage(snplayer, "You already have all the wolves you can control.");
					return false;
				}
			} else {
				SuperNManager.sendMessage(snplayer, "Not enough power to summon.");
				return false;
			}
		} else {
			SuperNManager.sendMessage(snplayer, "Cannot use werewolf abilities during the day!");
			return false;
		}
	}

	public static HashMap<Wolf, SuperNPlayer> getWolves() {
		return wolvesMap;
	}

	public static void removeWolf(Wolf wolf) {
		if (wolvesMap.containsKey(wolf)) {
			wolvesMap.remove(wolf);
		}
	}

	public static void removePlayer(SuperNPlayer player) {
		List<Wolf> removeWolf = new ArrayList<Wolf>();
		for (Wolf wolf : wolvesMap.keySet()) {
			if (wolvesMap.get(wolf).equals(player)) {
				wolf.setTamed(false);
				removeWolf.add(wolf);
			}
		}
		for (Wolf wolf : removeWolf) {
			wolvesMap.remove(wolf);
		}
	}
}
