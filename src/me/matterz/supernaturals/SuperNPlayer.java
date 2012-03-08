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

package me.matterz.supernaturals;

import java.io.Serializable;

public class SuperNPlayer implements Serializable {

	/**
	 * Auto-Generated serialVersionUID
	 */
	private static final long serialVersionUID = -2693531379993789149L;

	public String playername;
	public String superType = "human";
	public String oldSuperType = "human";
	public double oldSuperPower = 0;
	public double superPower = 0;
	public boolean truce = true;
	public int truceTimer = 0;

	public SuperNPlayer() {
	}

	public SuperNPlayer(String playername) {
		this.playername = playername;
		superType = "human";
		oldSuperType = "human";
		oldSuperPower = 0;
		superPower = 0;
		truce = true;
		truceTimer = 0;
	}

	// -------------------------------------------- //
	// Parameters //
	// -------------------------------------------- //

	public String getName() {
		return playername;
	}

	public void setName(String name) {
		playername = name;
	}

	public String getType() {
		return superType;
	}

	public void setType(String type) {
		superType = type;
	}

	public String getOldType() {
		return oldSuperType;
	}

	public void setOldType(String type) {
		oldSuperType = type;
	}

	public double getOldPower() {
		return oldSuperPower;
	}

	public void setOldPower(double amount) {
		oldSuperPower = amount;
	}

	public double getPower() {
		return superPower;
	}

	public void setPower(double amount) {
		superPower = this.limitDouble(amount);
	}

	public boolean getTruce() {
		return truce;
	}

	public void setTruce(boolean truce) {
		this.truce = truce;
		truceTimer = 0;
	}

	public int getTruceTimer() {
		return truceTimer;
	}

	public void setTruceTimer(int timer) {
		truceTimer = timer;
	}

	// -------------------------------------------- //
	// Booleans //
	// -------------------------------------------- //

	public boolean isSuper() {
		if (getType().equalsIgnoreCase("human")
				|| getType().equalsIgnoreCase("priest")
				|| getType().equalsIgnoreCase("witchhunter")
				|| getType().equalsIgnoreCase("angel")) {
			return false;
		}
		return true;
	}

	public boolean isAngel() {
		if (getType().equalsIgnoreCase("angel")) {
			return true;
		}
		return false;
	}

	public boolean isHuman() {
		if (getType().equalsIgnoreCase("human")) {
			return true;
		}
		return false;
	}

	public boolean isVampire() {
		if (getType().equalsIgnoreCase("vampire")) {
			return true;
		}
		return false;
	}

	public boolean isPriest() {
		if (getType().equalsIgnoreCase("priest")) {
			return true;
		}
		return false;
	}

	public boolean isWere() {
		if (getType().equalsIgnoreCase("werewolf")) {
			return true;
		}
		return false;
	}

	public boolean isEnderBorn() {
		if (getType().equalsIgnoreCase("enderborn")) {
			return true;
		}
		return false;
	}

	public boolean isGhoul() {
		if (getType().equalsIgnoreCase("ghoul")) {
			return true;
		}
		return false;
	}

	public boolean isHunter() {
		if (getType().equalsIgnoreCase("witchhunter")) {
			return true;
		}
		return false;
	}

	public boolean isDemon() {
		if (getType().equalsIgnoreCase("demon")) {
			return true;
		}
		return false;
	}

	public double scale(double input) {
		double powerPercentage = input * (getPower() / 10000);
		return powerPercentage;
	}

	public boolean isOnline() {
		return SupernaturalsPlugin.instance.getServer().getPlayer(playername) != null;
	}

	public boolean isDead() {
		return SupernaturalsPlugin.instance.getServer().getPlayer(playername).isDead();
	}

	@Override
	public int hashCode() {
		return playername.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SuperNPlayer) {
			return playername.equals(((SuperNPlayer) obj).getName());
		}
		return false;
	}

	// -------------------------------------------- //
	// Limiting value of double //
	// -------------------------------------------- //
	public double limitDouble(double d, double min, double max) {
		if (d < min) {
			return min;
		}
		if (d > max) {
			return max;
		}
		return d;
	}

	public double limitDouble(double d) {
		return this.limitDouble(d, 0, 10000);
	}
}
