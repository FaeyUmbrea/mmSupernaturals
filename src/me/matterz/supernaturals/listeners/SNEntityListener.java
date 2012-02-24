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
import me.matterz.supernaturals.util.Armor;
import me.matterz.supernaturals.util.EntityUtil;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTargetEvent;

public class SNEntityListener implements Listener {

	private SupernaturalsPlugin plugin;
	private String worldPermission = "supernatural.world.enabled";

	public SNEntityListener(SupernaturalsPlugin instance) {
		instance.getServer().getPluginManager().registerEvents(this, instance);
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityShootBowzBroLol(EntityShootBowEvent event) {
		if (event.isCancelled()) { // We don't want to make any of our
									// plugin-friends mad :D
			return;
		}
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		Player shooter = (Player) event.getEntity();
		boolean cancel = plugin.getClassManager(shooter).shootArrow(shooter, event);
		event.setCancelled(cancel);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityExplode(EntityExplodeEvent event) {
		if (SNConfigHandler.debugMode) {
			SupernaturalsPlugin.log("Entity Explode event with "
					+ event.getEntity().getClass().getSimpleName());
		}
		if (event.getEntity() instanceof Fireball) {
			Fireball fireball = (Fireball) event.getEntity();
			if (fireball.getShooter() instanceof Player) {
				if (!SupernaturalsPlugin.hasPermissions((Player) fireball.getShooter(), worldPermission)
						&& SNConfigHandler.multiworld) {
					return;
				}
				for (Entity entity : fireball.getNearbyEntities(3, 3, 3)) {
					if (entity instanceof LivingEntity) {
						LivingEntity lEntity = (LivingEntity) entity;
						if (entity instanceof Player) {
							Player player = (Player) entity;
							SuperNPlayer snplayer = SuperNManager.get(player);
							if (snplayer.isDemon()) {
								continue;
							}
							if (!SupernaturalsPlugin.instance.getPvP(player)) {
								continue;
							}
						}
						lEntity.damage(SNConfigHandler.demonFireballDamage, fireball.getShooter());
						lEntity.setFireTicks(200);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.isCancelled()) {
			return;
		}

		Entity victim = event.getEntity();
		double damage = event.getDamage();

		// New spells event
		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent edbeEvent = (EntityDamageByEntityEvent) event;
			if (edbeEvent.getDamager() instanceof Player
					&& victim instanceof Player) {
				Player pVictim = (Player) victim;
				plugin.getClassManager((Player) edbeEvent.getDamager()).spellEvent(edbeEvent, pVictim);
			}
		}
		// Player Damager Event
		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent edbeEvent = (EntityDamageByEntityEvent) event;
			Entity damager = edbeEvent.getDamager();

			if (damager instanceof Player) {
				if (!SupernaturalsPlugin.hasPermissions((Player) damager, worldPermission)
						&& SNConfigHandler.multiworld) {
					return;
				}

				damage = plugin.getClassManager((Player) damager).damagerEvent(edbeEvent, damage);
			}
		}

		// Player Victim Event
		if (victim instanceof Player) {
			Player pVictim = (Player) victim;
			if (!SupernaturalsPlugin.hasPermissions(pVictim, worldPermission)
					&& SNConfigHandler.multiworld) {
				return;
			}
			damage = plugin.getClassManager(pVictim).victimEvent(event, damage);

			SuperNPlayer snvictim = SuperNManager.get(pVictim);

			if (plugin.getGhoulManager().checkBond(pVictim)) {
				double damageAfterArmor = Armor.getReducedDamage(pVictim, (int) Math.round(damage));
				if (damageAfterArmor > 1) {
					if (SNConfigHandler.debugMode) {
						SupernaturalsPlugin.log(snvictim.getName()
								+ " has an unholy bond active.");
					}
					damage /= 2;
					damageAfterArmor /= 2;
					SuperNPlayer ghoul = plugin.getGhoulManager().getGhoul(snvictim);
					Player gPlayer = plugin.getServer().getPlayer(ghoul.getName());
					double ghoulDamage = damageAfterArmor;
					ghoulDamage -= ghoulDamage
							* ghoul.scale(1 - SNConfigHandler.ghoulDamageReceivedFactor);
					int health = (int) (gPlayer.getHealth() - ghoulDamage);
					if (health < 0) {
						health = 0;
					}
					gPlayer.setHealth(health);
					SuperNManager.alterPower(ghoul, -SNConfigHandler.ghoulPowerBond, "Unholy Bond!");
				}
			}

			if (plugin.getDataHandler().hasAngel(snvictim)) {
				if (SNConfigHandler.debugMode) {
					SupernaturalsPlugin.log(snvictim.getName()
							+ " has a guardian angel angel.");
				}
				double damageAfterArmor = Armor.getReducedDamage(pVictim, (int) Math.round(damage));
				if (pVictim.getHealth() - damageAfterArmor <= 0) {
					if (SNConfigHandler.debugMode) {
						SupernaturalsPlugin.log(snvictim.getName()
								+ " has used their guardian angel.");
					}
					SuperNManager.sendMessage(snvictim, "Guardian Angel used!");
					plugin.getDataHandler().removeAngel(snvictim);
					pVictim.setHealth(20);
					event.setDamage(0);
					event.setCancelled(true);
					return;
				}
			}
		}
		event.setDamage((int) Math.round(damage));
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityTarget(EntityTargetEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (!(event.getTarget() instanceof Player)) {
			return;
		}

		if (event.getEntity() == null) {
			return;
		}

		if (!SupernaturalsPlugin.hasPermissions((Player) event.getTarget(), worldPermission)
				&& SNConfigHandler.multiworld) {
			return;
		}

		SuperNPlayer snplayer = SuperNManager.get((Player) event.getTarget());

		if (!snplayer.getTruce()) {
			return;
		}

		if (EntityUtil.entityTypeFromEntity(event.getEntity()) == null) {
			return;
		}

		if (snplayer.isVampire()
				&& SNConfigHandler.vampireTruce.contains(EntityUtil.entityTypeFromEntity(event.getEntity()))) {
			event.setCancelled(true);
		} else if (snplayer.isGhoul()
				&& SNConfigHandler.ghoulTruce.contains(EntityUtil.entityTypeFromEntity(event.getEntity()))) {
			event.setCancelled(true);
		} else if (snplayer.isWere() && SNConfigHandler.wolfTruce
				&& event.getEntity() instanceof Wolf) {
			event.setCancelled(true);
		}
	}
}