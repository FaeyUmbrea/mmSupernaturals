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

import me.matterz.supernaturals.SuperNPlayer;
import me.matterz.supernaturals.SupernaturalsPlugin;
import me.matterz.supernaturals.io.SNConfigHandler;

import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;

public class HumanManager extends ClassManager {

	public HumanManager(SupernaturalsPlugin instance) {
		super();
		plugin = instance;
	}

	public HumanManager() {
		super();
	}

	private SupernaturalsPlugin plugin;

	// -------------------------------------------- //
	// Damage Events //
	// -------------------------------------------- //

	@Override
	public double victimEvent(EntityDamageEvent event, double damage) {
		return damage;
	}

	@Override
	public double damagerEvent(EntityDamageByEntityEvent event, double damage) {
		return damage;
	}

	@Override
	public void deathEvent(Player player) {
		if (SNConfigHandler.debugMode) {
			SupernaturalsPlugin.log("Player died.");
		}

		SuperNPlayer snplayer = SuperNManager.get(player);
		LivingEntity lDamager = null;
		EntityDamageEvent e = player.getLastDamageCause();

		plugin.getDataHandler().removePlayerApp(snplayer);

		if (e == null) {
			return;
		}

		if (e.getCause().equals(DamageCause.FALL)) {
			if(player.getItemInHand().getType().equals(Material.FEATHER)) {
				SuperNManager.sendMessage(snplayer, "Your spirit is lifted");
				SuperNManager.convert(snplayer, "angel");
			}
		}

		if (e.getCause().equals(DamageCause.LAVA)
				|| e.getCause().equals(DamageCause.FIRE)
				|| e.getCause().equals(DamageCause.FIRE_TICK)) {
			if (player.getWorld().getEnvironment().equals(Environment.NETHER)) {
				if (plugin.getDemonManager().checkPlayerApp(player) || plugin.getDemonManager().checkInventory(player)) {
					SuperNManager.sendMessage(snplayer, "Hellfire races through your veins!");
					SuperNManager.convert(snplayer, "demon", SNConfigHandler.demonPowerStart);
				}
			}
		}

		if (e instanceof EntityDamageByEntityEvent) {
			if (((EntityDamageByEntityEvent) e).getDamager() instanceof LivingEntity) {
				lDamager = (LivingEntity) ((EntityDamageByEntityEvent) e).getDamager();
			} else if (((EntityDamageByEntityEvent) e).getDamager() instanceof Projectile) {
				lDamager = ((Projectile) ((EntityDamageByEntityEvent) e).getDamager()).getShooter();
			}
		}

		if (lDamager != null) {
			if (player.getWorld().getEnvironment().equals(Environment.NETHER)) {
				if (lDamager instanceof PigZombie) {
					SuperNManager.convert(snplayer, "ghoul", SNConfigHandler.ghoulPowerStart);
					SuperNManager.sendMessage(snplayer, "You have been transformed into a Ghoul!");
				}
			}
			if (lDamager instanceof Wolf) {
				if (!((Wolf) lDamager).isTamed()
						&& SuperNManager.worldTimeIsNight(player)) {
					SuperNManager.convert(snplayer, "werewolf", SNConfigHandler.werePowerStart);
					SuperNManager.sendMessage(snplayer, "You have mutated into a werewolf!");
				}
			}
		}
	}

	@Override
	public void killEvent(Player pDamager, SuperNPlayer damager, SuperNPlayer victim) {
	}

	// -------------------------------------------- //
	// Interact //
	// -------------------------------------------- //

	@Override
	public boolean playerInteract(PlayerInteractEvent event) {
		return false;
	}

	// -------------------------------------------- //
	// Armor //
	// -------------------------------------------- //

	@Override
	public void armorCheck(Player player) {
	}

}
