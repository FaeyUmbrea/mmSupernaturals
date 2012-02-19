package me.matterz.supernaturals.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Armor {
	/**
	 * http://www.minecraftwiki.net/wiki/Item_Durability#Armor_durability We
	 * rely on getArmorContents() to return 4 armor pieces in the order of:
	 * boots, pants, chest, helmet
	 */
	private static final double armorPoints[] = { 1.5, 3.0, 4.0, 1.5 };
	private static final double armorPointReduction = 0.04;

	public static int getArmorPoints(Player player) {
		int currentDurability = 0;
		int baseDurability = 0;
		double baseArmorPoints = 0;
		ItemStack inventory[] = player.getInventory().getArmorContents();
		for (int i = 0; i < inventory.length; i++) {
			if (inventory[i] == null) {
				continue;
			}
			Material m = inventory[i].getType();
			if (m == null) {
				continue;
			}
			final short maxDurability = m.getMaxDurability();
			if (maxDurability < 0) {
				continue;
			}
			final short durability = inventory[i].getDurability();
			baseDurability += maxDurability;
			currentDurability += maxDurability - durability;
			baseArmorPoints += armorPoints[i];
		}
		return (int) Math.round(2 * baseArmorPoints * currentDurability
				/ baseDurability);
	}

	public static int getReducedDamage(Player player, int damage) {
		int armorPoints = getArmorPoints(player);
		return (int) Math.round(damage - armorPointReduction * armorPoints
				* damage);
	}
}
