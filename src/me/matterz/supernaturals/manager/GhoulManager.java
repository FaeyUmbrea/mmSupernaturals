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

import java.util.HashMap;

import me.matterz.supernaturals.SuperNPlayer;
import me.matterz.supernaturals.SupernaturalsPlugin;
import me.matterz.supernaturals.io.SNConfigHandler;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class GhoulManager extends ClassManager {

	public GhoulManager() {
		super();
	}

	private String permissions = "supernatural.player.preventwaterdamage";
	private HashMap<SuperNPlayer, SuperNPlayer> bonds = new HashMap<SuperNPlayer, SuperNPlayer>();

	// -------------------------------------------- //
	// Damage Events //
	// -------------------------------------------- //

	@Override
	public double victimEvent(EntityDamageEvent event, double damage) {
		if (event.getCause().equals(DamageCause.FALL)) {
			event.setCancelled(true);
			return 0;
		} else if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent edbeEvent = (EntityDamageByEntityEvent) event;
			Entity damager = edbeEvent.getDamager();
			if (damager instanceof Player) {
				Player pDamager = (Player) damager;
				SuperNPlayer snDamager = SuperNManager.get(pDamager);
				Player victim = (Player) event.getEntity();
				SuperNPlayer snVictim = SuperNManager.get(victim);
				ItemStack item = pDamager.getItemInHand();

				if (item != null) {
					if (SNConfigHandler.ghoulWeaponImmunity.contains(item.getType())) {
						damage = 0;
						SuperNManager.sendMessage(snDamager, "Ghouls are immune to that weapon!");
					} else {
						damage -= damage
								* snVictim.scale(1 - SNConfigHandler.ghoulDamageReceivedFactor);
					}
				}
			}
		}
		return damage;
	}

	@Override
	public double damagerEvent(EntityDamageByEntityEvent event, double damage) {
		Entity damager = event.getDamager();
		Player pDamager = (Player) damager;
		SuperNPlayer snDamager = SuperNManager.get(pDamager);
		ItemStack item = pDamager.getItemInHand();

		if (item != null) {
			if (SNConfigHandler.ghoulWeapons.contains(item.getType())) {
				if (SNConfigHandler.debugMode) {
					SupernaturalsPlugin.log(pDamager.getName()
							+ " was not allowed to use "
							+ item.getType().toString());
				}
				SuperNManager.sendMessage(snDamager, "Ghouls cannot use this weapon!");
				damage = 0;
			} else {
				damage += damage
						* snDamager.scale(SNConfigHandler.ghoulDamageFactor);
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
		SuperNManager.alterPower(snplayer, -SNConfigHandler.ghoulDeathPowerPenalty, "You died!");
	}

	@Override
	public void killEvent(Player pDamager, SuperNPlayer damager, SuperNPlayer victim) {
		if (victim == null) {
			SuperNManager.alterPower(damager, SNConfigHandler.ghoulKillPowerCreatureGain, "Creature death!");
		} else {
			double random = Math.random();
			if (victim.getPower() > SNConfigHandler.ghoulKillPowerPlayerGain) {
				SuperNManager.alterPower(damager, SNConfigHandler.ghoulKillPowerPlayerGain, "Player killed!");
			} else {
				SuperNManager.sendMessage(damager, "You cannot gain power from a player with no power themselves.");
			}
			if (SNConfigHandler.ghoulKillSpreadCurse && !victim.isSuper()) {
				if (random < SNConfigHandler.spreadChance) {
					SuperNManager.sendMessage(victim, "Your body dies... You feel a deep hatred for the living.");
					SuperNManager.convert(victim, "ghoul");
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

		Material itemMaterial = event.getMaterial();

		if ((SNConfigHandler.ghoulRightClickSummon && (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)))
				|| (!SNConfigHandler.ghoulRightClickSummon && (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)))) {
			if (itemMaterial.toString().equalsIgnoreCase(SNConfigHandler.ghoulMaterial)) {
				summon(player);
				event.setCancelled(true);
				return true;
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
			if (!SNConfigHandler.ghoulArmor.contains(helmet.getType())) {
				inv.setHelmet(null);
				dropItem(player, helmet);
			}
		}
		if (chest != null) {
			if (!SNConfigHandler.ghoulArmor.contains(chest.getType())) {
				inv.setChestplate(null);
				dropItem(player, chest);
			}
		}
		if (leggings != null) {
			if (!SNConfigHandler.ghoulArmor.contains(leggings.getType())) {
				inv.setLeggings(null);
				dropItem(player, leggings);
			}
		}
		if (boots != null) {
			if (!SNConfigHandler.ghoulArmor.contains(boots.getType())) {
				inv.setBoots(null);
				dropItem(player, boots);
			}
		}
	}

	// -------------------------------------------- //
	// Water Damage //
	// -------------------------------------------- //

	public void waterAdvanceTime(Player player) {
		if (player.isDead()) {
			return;
		}
		if (SupernaturalsPlugin.hasPermissions(player, permissions)) {
			return;
		}
		if (player.isInsideVehicle()) {
			if (player.getVehicle() instanceof Boat) {
				return;
			}
		}

		Material material = player.getLocation().getBlock().getType();

		if (material == Material.STATIONARY_WATER || material == Material.WATER) {
			int health = player.getHealth() - SNConfigHandler.ghoulDamageWater;
			if (health < 0) {
				health = 0;
			}
			player.setHealth(health);
			EntityDamageEvent event = new EntityDamageEvent(player, DamageCause.DROWNING, SNConfigHandler.ghoulDamageWater);
			player.setLastDamageCause(event);
			SuperNManager.sendMessage(SuperNManager.get(player), "Ghouls disintegrate in water!  Get Out Quick!");
		}
	}

	// -------------------------------------------- //
	// Spells //
	// -------------------------------------------- //

	@Override
	public void spellEvent(EntityDamageByEntityEvent event, Player target) {
		Player player = (Player) event.getDamager();
		SuperNPlayer snplayer = SuperNManager.get(player);

		Material itemMaterial = player.getItemInHand().getType();

		if (player.getItemInHand() == null) {
			return;
		}

		if (itemMaterial.toString().equalsIgnoreCase(SNConfigHandler.ghoulBondMaterial)) {
			if (SNConfigHandler.debugMode) {
				SupernaturalsPlugin.log(snplayer.getName()
						+ " is attempting to bond...");
			}
			boolean success = createBond(player, target);
			if (!success) {
				return;
			}
			event.setCancelled(true);
		}
	}

	public void removeBond(SuperNPlayer player) {
		if (bonds.containsKey(player)) {
			SuperNManager.sendMessage(player, "Removed Unholy Bond from "
					+ ChatColor.WHITE + bonds.get(player).getName());
			SuperNManager.sendMessage(bonds.get(player), "Unholy Bond removed!");
			if (SNConfigHandler.debugMode) {
				SupernaturalsPlugin.log("Unholy Bond removed from "
						+ player.getName() + " with "
						+ bonds.get(player).getName());
			}
			bonds.remove(player);
			return;
		}
		if (bonds.containsValue(player)) {
			for (SuperNPlayer ghoul : bonds.keySet()) {
				if (bonds.get(ghoul).equals(player)) {
					SuperNManager.sendMessage(player, "Removed Unholy Bond from "
							+ ChatColor.WHITE + bonds.get(ghoul).getName());
					SuperNManager.sendMessage(bonds.get(player), "Unholy Bond removed!");
					if (SNConfigHandler.debugMode) {
						SupernaturalsPlugin.log("Unholy Bond removed from "
								+ player.getName() + " with "
								+ bonds.get(player).getName());
					}
					bonds.remove(ghoul);
					return;
				}
			}
		}

	}

	public boolean createBond(Player player, Player victim) {
		SuperNPlayer ghoul = SuperNManager.get(player);

		if (victim == null) {
			if (bonds.containsKey(ghoul)) {
				SuperNManager.sendMessage(ghoul, "Removed Unholy Bond from "
						+ ChatColor.WHITE + bonds.get(ghoul).getName());
				SuperNManager.sendMessage(bonds.get(ghoul), "Unholy Bond removed!");
				if (SNConfigHandler.debugMode) {
					SupernaturalsPlugin.log("Unholy Bond removed from "
							+ ghoul.getName() + " with "
							+ bonds.get(ghoul).getName());
				}
				bonds.remove(ghoul);
			}
			return false;
		}

		SuperNPlayer snvictim = SuperNManager.get(victim);

		if (snvictim.isSuper()) {
			if (bonds.containsKey(ghoul)) {
				SuperNManager.sendMessage(ghoul, "Removed Unholy Bond from "
						+ ChatColor.WHITE + bonds.get(ghoul).getName());
				SuperNManager.sendMessage(bonds.get(ghoul), "Unholy Bond removed!");
				if (SNConfigHandler.debugMode) {
					SupernaturalsPlugin.log("Unholy Bond removed from "
							+ ghoul.getName() + " with " + snvictim.getName());
				}
				bonds.remove(ghoul);
			}

			SuperNManager.sendMessage(ghoul, "You now have an Unholy Bond with "
					+ victim.getName());
			SuperNManager.sendMessage(snvictim, "You now have an Unholy Bond with "
					+ ghoul.getName() + "!");
			if (SNConfigHandler.debugMode) {
				SupernaturalsPlugin.log("Unholy Bond formed for "
						+ ghoul.getName() + " with " + snvictim.getName());
			}
			bonds.put(ghoul, snvictim);

			ItemStack item = player.getItemInHand();
			if (item.getAmount() == 1) {
				player.setItemInHand(null);
			} else {
				item.setAmount(player.getItemInHand().getAmount() - 1);
			}
			return true;
		}
		SuperNManager.sendMessage(ghoul, "You cannot form a bond with a human.");
		return false;
	}

	public boolean checkBond(Player player) {
		SuperNPlayer snvictim = SuperNManager.get(player);
		SuperNPlayer snplayer = null;

		if (bonds.containsValue(snvictim)) {
			for (SuperNPlayer ghoul : bonds.keySet()) {
				if (bonds.get(ghoul).equals(snvictim)) {
					snplayer = ghoul;
					break;
				}
			}
		}

		if (snplayer == null) {
			return false;
		}

		if (!snplayer.isOnline() || snplayer.isDead()) {
			return false;
		}

		Player gPlayer = SupernaturalsPlugin.instance.getServer().getPlayer(snplayer.getName());

		if (!gPlayer.getWorld().equals(player.getWorld())) {
			return false;
		}

		double distance = gPlayer.getLocation().distance(player.getLocation());

		if (distance > 10) {
			return false;
		}

		if (snplayer.getPower() > SNConfigHandler.ghoulPowerBond) {
			if (SNConfigHandler.debugMode) {
				SupernaturalsPlugin.log("Unholy Bond activated for "
						+ snplayer.getName() + " with " + snvictim.getName());
			}
			return true;
		} else {
			if (SNConfigHandler.debugMode) {
				SupernaturalsPlugin.log("Not enough power for "
						+ snplayer.getName() + " to maintain bond with "
						+ snvictim.getName());
			}
			return false;
		}
	}

	public SuperNPlayer getGhoul(SuperNPlayer snplayer) {
		if (bonds.containsValue(snplayer)) {
			for (SuperNPlayer ghoul : bonds.keySet()) {
				if (bonds.get(ghoul).equals(snplayer)) {
					return ghoul;
				}
			}
		}
		return null;
	}

	public boolean summon(Player player) {
		SuperNPlayer snplayer = SuperNManager.get(player);
		ItemStack item = player.getItemInHand();
		if (!SupernaturalsPlugin.instance.getSpawn(player)) {
			SuperNManager.sendMessage(snplayer, "You cannot summon here.");
			return false;
		}
		if (snplayer.getPower() > SNConfigHandler.ghoulPowerSummonCost) {
			player.getWorld().spawnCreature(player.getLocation(), EntityType.ZOMBIE);
			SuperNManager.alterPower(snplayer, -SNConfigHandler.ghoulPowerSummonCost, "Summoning a Zombie!");
			if (SNConfigHandler.debugMode) {
				SupernaturalsPlugin.log(snplayer.getName()
						+ " summoned a Zombie!");
			}
			if (item.getAmount() == 1) {
				player.setItemInHand(null);
			} else {
				item.setAmount(player.getItemInHand().getAmount() - 1);
			}
			return true;
		} else {
			SuperNManager.sendMessage(snplayer, "Not enough power to summon.");
			return false;
		}
	}

	// -------------------------------------------- //
	// Rain Check //
	// -------------------------------------------- //

	public boolean isUnderRoof(Player player) {
		/*
		 * We start checking opacity 2 blocks up. As Max Y is 127 there CAN be a
		 * roof over the player if he is standing in block 125: 127 Solid Block
		 * 126 125 Player However if he is standing in 126 there is no chance.
		 */
		boolean retVal = false;
		Block blockCurrent = player.getLocation().getBlock();

		if (player.getLocation().getY() >= 126) {
			retVal = false;
		} else {
			// blockCurrent = blockCurrent.getFace(BlockFace.UP, 1); //What was
			// the point anyway?
			while (blockCurrent.getY() + 1 <= 127) {
				blockCurrent = blockCurrent.getRelative(BlockFace.UP);

				if (!blockCurrent.getType().equals(Material.AIR)) {
					retVal = true;
					break;
				}
			}
		}
		return retVal;
	}
}
