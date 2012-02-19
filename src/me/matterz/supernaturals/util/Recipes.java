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

package me.matterz.supernaturals.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Recipes {
	public Map<Material, Integer> materialQuantities = new HashMap<Material, Integer>();

	public void removeFromPlayer(Player player) {
		Inventory inventory = player.getInventory();
		for (Material material : materialQuantities.keySet()) {
			inventory.removeItem(new ItemStack(material.getId(), materialQuantities.get(material)));
		}
		player.updateInventory();
	}

	public boolean playerHasEnough(Player player) {
		Inventory inventory = player.getInventory();
		for (Material material : materialQuantities.keySet()) {
			if (getMaterialCountFromInventory(material, inventory) < materialQuantities.get(material)) {
				return false;
			}
		}
		return true;
	}

	public static int getMaterialCountFromInventory(Material material, Inventory inventory) {
		int count = 0;
		for (ItemStack stack : inventory.all(material).values()) {
			count += stack.getAmount();
		}
		return count;
	}

	public String getRecipeLine() {
		ArrayList<String> lines = new ArrayList<String>();
		for (Entry<Material, Integer> item : entriesSortedByValues(materialQuantities)) {
			lines.add("" + item.getValue() + " "
					+ TextUtil.getMaterialName(item.getKey()));
		}
		return TextUtil.implode(lines, ", ");
	}

	// http://stackoverflow.com/questions/2864840/treemap-sort-by-value
	public static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
		SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
				int res = e1.getValue().compareTo(e2.getValue());
				return res != 0 ? res : 1; // Special fix to preserve
											// items with equal values
			}
		});
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}
}
