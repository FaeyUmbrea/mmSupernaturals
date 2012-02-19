package me.matterz.supernaturals.util;

import java.io.Serializable;

import me.matterz.supernaturals.SupernaturalsPlugin;

import org.bukkit.World;

public class Location implements Serializable {

	/**
	 * Auto-Generated serialVersionUID
	 */
	private static final long serialVersionUID = 8884729998863928105L;

	private double x;
	private double y;
	private double z;
	private String world;

	public Location(org.bukkit.Location location) {
		x = location.getX();
		y = location.getY();
		z = location.getZ();
		world = location.getWorld().getName();
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public World getWorld() {
		return SupernaturalsPlugin.instance.getServer().getWorld(world);
	}
}
