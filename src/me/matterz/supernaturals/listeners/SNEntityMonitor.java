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

import java.util.ArrayList;

import me.matterz.supernaturals.SuperNPlayer;
import me.matterz.supernaturals.SupernaturalsPlugin;
import me.matterz.supernaturals.io.SNConfigHandler;
import me.matterz.supernaturals.manager.SuperNManager;
import me.matterz.supernaturals.manager.WereManager;
import me.matterz.supernaturals.util.EntityUtil;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class SNEntityMonitor implements Listener {

	private SupernaturalsPlugin plugin;
	private String worldPermission = "supernatural.world.enabled";

	public SNEntityMonitor(SupernaturalsPlugin instance) {
		instance.getServer().getPluginManager().registerEvents(this, instance);
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onProjectileHit(ProjectileHitEvent event) {
		if (event.getEntity() instanceof Arrow) {
			Arrow arrow = (Arrow) event.getEntity();
			if (plugin.getHunterManager().getArrowMap().containsKey(arrow)) {
				Player player = (Player) arrow.getShooter();
				if (!SupernaturalsPlugin.hasPermissions(player, worldPermission)
						&& SNConfigHandler.multiworld) {
					return;
				}
				String arrowType = plugin.getHunterManager().getArrowMap().get(arrow);
				if (arrowType.equalsIgnoreCase("grapple")) {
					plugin.getHunterManager().startGrappling(player, arrow.getLocation());
				} else if (arrowType.equalsIgnoreCase("fire")) {
					arrow.getLocation();
					Block block = arrow.getWorld().getBlockAt(arrow.getLocation());
					if (block != null) {
						if (SNConfigHandler.burnableBlocks.contains(block.getType())) {
							block.setType(Material.FIRE);
						}
					}
				}
				plugin.getHunterManager().removeArrow(arrow);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent edbeEvent = (EntityDamageByEntityEvent) event;

			// Define local fields
			Entity victim = event.getEntity();

			Entity damager = edbeEvent.getDamager();
			Player pDamager = null;

			// For further interest that attacker must be a player.
			if (damager instanceof Projectile) {
				if (((Projectile) damager).getShooter() instanceof Player) {
					pDamager = (Player) ((Projectile) damager).getShooter();
				}
			} else if (damager instanceof Player) {
				pDamager = (Player) damager;
			}
			if (damager == null) {
				return;
			}
			if (pDamager == null) {
				return;
			}
			SuperNPlayer snDamager = SuperNManager.get(pDamager);

			if (victim instanceof Creature) {
				Creature cVictim = (Creature) victim;

				// Break vampire truce
				if (snDamager.isVampire()
						&& SNConfigHandler.vampireTruce.contains(EntityUtil.entityTypeFromEntity(cVictim))) {
					plugin.getSuperManager().truceBreak(snDamager);
				} else if (snDamager.isGhoul()
						&& SNConfigHandler.ghoulTruce.contains(EntityUtil.entityTypeFromEntity(cVictim))) {
					plugin.getSuperManager().truceBreak(snDamager);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();

		Player pDamager = null;
		LivingEntity lDamager = null;
		Event e = entity.getLastDamageCause();
		if (e instanceof EntityDamageByEntityEvent) {
			if (((EntityDamageByEntityEvent) e).getDamager() instanceof LivingEntity) {
				lDamager = (LivingEntity) ((EntityDamageByEntityEvent) e).getDamager();
			} else if (((EntityDamageByEntityEvent) e).getDamager() instanceof Projectile) {
				lDamager = ((Projectile) ((EntityDamageByEntityEvent) e).getDamager()).getShooter();
			}
		}

		if (lDamager instanceof Player) {
			pDamager = (Player) lDamager;
		}

		if (entity instanceof Monster) {
			if (pDamager != null) {
				SuperNPlayer snplayer = SuperNManager.get(pDamager);
				if (snplayer.isAngel()) {
					SuperNManager.alterPower(snplayer, SNConfigHandler.angelKillMonsterPowerGain, "Killed Monster");
				}
			}
		}

		if (entity instanceof Wolf) {
			WereManager.removeWolf((Wolf) entity);
		}

		if (entity instanceof Creature) {
			if (pDamager != null) {
				if (!SupernaturalsPlugin.hasPermissions(pDamager, worldPermission)
						&& SNConfigHandler.multiworld) {
					return;
				}
				SuperNPlayer snDamager = SuperNManager.get(pDamager);
				SupernaturalsPlugin.instance.getClassManager(pDamager).killEvent(pDamager, snDamager, null);
			}
		}

		if (!(entity instanceof Player)) {
			return;
		}

		Player pVictim = (Player) entity;

		if (!SupernaturalsPlugin.hasPermissions(pVictim, worldPermission)
				&& SNConfigHandler.multiworld) {
			return;
		}

		if (!pVictim.isOnline()) {
			return;
		}

		SuperNPlayer snplayer = SuperNManager.get(pVictim);

		if (lDamager != null) {
			if (lDamager instanceof Player) {
				pDamager = (Player) lDamager;
				SuperNPlayer snDamager = SuperNManager.get(pDamager);
				if (SNConfigHandler.debugMode) {
					SupernaturalsPlugin.log("Player " + snDamager.getName()
							+ " has killed " + snplayer.getName());
				}
				if (snplayer.isHunter()) {
					if (snDamager.equals(snplayer)) {
						SuperNManager.sendMessage(snplayer, "You have killed yourself!");
						SuperNManager.sendMessage(snplayer, "This action, voluntary or not, has rescinded your status as a WitchHunter.");
						SuperNManager.cure(snplayer);
						if (SNConfigHandler.debugMode) {
							SupernaturalsPlugin.log("Player "
									+ pDamager.getName() + " cured themself.");
						}
					}
				} else if (snDamager.isHuman()) {
					ArrayList<String> supersKilled = new ArrayList<String>();
					if (plugin.getDataHandler().playerHasApp(snDamager)) {
						supersKilled = plugin.getDataHandler().getPlayerApp(snDamager);
						if (!supersKilled.contains(snplayer.getType())) {
							supersKilled.add(snplayer.getType());
							if (supersKilled.size() >= 3) {
								plugin.getHunterManager().invite(snDamager);
							}
						}
					} else {
						supersKilled.add(snplayer.getType());
					}
					plugin.getDataHandler().addPlayerApp(snDamager, supersKilled);
				}
				SupernaturalsPlugin.instance.getClassManager(pDamager).killEvent(pDamager, snDamager, snplayer);
			} else if (lDamager instanceof Wolf) {
				Wolf wolf = (Wolf) lDamager;
				if (!wolf.isTamed()) {
					SupernaturalsPlugin.instance.getClassManager(pVictim).deathEvent(pVictim);
					return;
				}
				if (!(wolf.getOwner() instanceof Player)) {
					SupernaturalsPlugin.instance.getClassManager(pVictim).deathEvent(pVictim);
					return;
				}
				pDamager = (Player) wolf.getOwner();
				if (SNConfigHandler.debugMode) {
					SupernaturalsPlugin.log("Player " + pDamager.getName()
							+ " has killed " + snplayer.getName()
							+ " with wolf.");
				}
				SuperNPlayer snDamager = SuperNManager.get(pDamager);
				SupernaturalsPlugin.instance.getClassManager(pDamager).killEvent(pDamager, snDamager, snplayer);
			}
		}
		SupernaturalsPlugin.instance.getClassManager(pVictim).deathEvent(pVictim);
	}
}
