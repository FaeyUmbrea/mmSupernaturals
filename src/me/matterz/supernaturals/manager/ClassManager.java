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

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public abstract class ClassManager {

	public ClassManager() {
	}

	public double damagerEvent(EntityDamageByEntityEvent event, double damage) {
		return damage;
	}

	public boolean shootArrow(Player shooter, EntityShootBowEvent event) {
		return false;
	}

	public void spellEvent(EntityDamageByEntityEvent event, Player target) {
	}

	public double victimEvent(EntityDamageEvent event, double damage) {
		return damage;
	}

	public void deathEvent(Player player) {
	}

	public void killEvent(Player pDamager, SuperNPlayer damager, SuperNPlayer victim) {
	}

	public boolean playerInteract(PlayerInteractEvent event) {
		return false;
	}

	public void armorCheck(Player player) {
	}

	public void dropItem(Player player, ItemStack item) {
		SuperNPlayer snplayer = SuperNManager.get(player);
		SuperNManager.sendMessage(snplayer, "Your class cannot wear this type of armor!");
		Item newItem = player.getWorld().dropItem(player.getLocation(), item);
		newItem.setItemStack(item);
	}
}
