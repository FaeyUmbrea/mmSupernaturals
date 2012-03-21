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

package me.matterz.supernaturals.commands;

import java.util.ArrayList;

import me.matterz.supernaturals.SupernaturalsPlugin;
import me.matterz.supernaturals.io.SNConfigHandler;

import org.bukkit.entity.Player;

public class SNCommandSetChurch extends SNCommand {
	public SNCommandSetChurch() {
		super();
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		senderMustBePlayer = true;
		senderMustBeSupernatural = true;
		permissions = "supernatural.admin.command.setchurch";
		helpNameAndParams = "";
		helpDescription = "Sets the current location as the church";
	}

	@Override
	public void perform() {

		Player senderPlayer = (Player) sender;
		if (SNConfigHandler.spanish) {
			if (!SupernaturalsPlugin.hasPermissions(senderPlayer, permissions)) {
				this.sendMessage("No tienes permiso para usar este comando.");
				return;
			}

			double currentX = senderPlayer.getLocation().getX();
			double currentY = senderPlayer.getLocation().getY();
			double currentZ = senderPlayer.getLocation().getZ();

			SNConfigHandler.priestChurchWorld = senderPlayer.getWorld().getName();
			SNConfigHandler.priestChurchLocationX = (int) currentX;
			SNConfigHandler.priestChurchLocationY = (int) currentY;
			SNConfigHandler.priestChurchLocationZ = (int) currentZ;
			SNConfigHandler.priestChurchLocation = senderPlayer.getLocation();

			SNConfigHandler.getConfig().set("Priest.Church.World", SNConfigHandler.priestChurchWorld);
			SNConfigHandler.getConfig().set("Priest.Church.Location.X", SNConfigHandler.priestChurchLocationX);
			SNConfigHandler.getConfig().set("Priest.Church.Location.Y", SNConfigHandler.priestChurchLocationY);
			SNConfigHandler.getConfig().set("Priest.Church.Location.Z", SNConfigHandler.priestChurchLocationZ);

			SNConfigHandler.saveConfig();

			this.sendMessage("La localizaci�n de la Iglesia ha sido definida.");
			return;
		}
		if (!SupernaturalsPlugin.hasPermissions(senderPlayer, permissions)) {
			this.sendMessage("You do not have permissions to use this command.");
			return;
		}

		double currentX = senderPlayer.getLocation().getX();
		double currentY = senderPlayer.getLocation().getY();
		double currentZ = senderPlayer.getLocation().getZ();

		SNConfigHandler.priestChurchWorld = senderPlayer.getWorld().getName();
		SNConfigHandler.priestChurchLocationX = (int) currentX;
		SNConfigHandler.priestChurchLocationY = (int) currentY;
		SNConfigHandler.priestChurchLocationZ = (int) currentZ;
		SNConfigHandler.priestChurchLocation = senderPlayer.getLocation();

		SNConfigHandler.getConfig().set("Priest.Church.World", SNConfigHandler.priestChurchWorld);
		SNConfigHandler.getConfig().set("Priest.Church.Location.X", SNConfigHandler.priestChurchLocationX);
		SNConfigHandler.getConfig().set("Priest.Church.Location.Y", SNConfigHandler.priestChurchLocationY);
		SNConfigHandler.getConfig().set("Priest.Church.Location.Z", SNConfigHandler.priestChurchLocationZ);

		SNConfigHandler.saveConfig();

		this.sendMessage("Church location set.");
	}
}
