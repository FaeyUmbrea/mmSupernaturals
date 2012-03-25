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
import java.util.List;

import me.matterz.supernaturals.SupernaturalsPlugin;
import me.matterz.supernaturals.io.SNConfigHandler;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SNCommandClasses extends SNCommand {
	private static List<String> classMessages = new ArrayList<String>();
	private static List<String> spanishClassMessages = new ArrayList<String>();

	public SNCommandClasses() {
		super();
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		senderMustBePlayer = false;
		senderMustBeSupernatural = false;
		permissions = "supernatural.command.classes";
	}

	static {
		classMessages.add("*** " + ChatColor.WHITE + "Supernatural Classes "
				+ ChatColor.RED + "***");
		classMessages.add("Human: " + ChatColor.WHITE
				+ "- Your standard run of the mill person.");
		classMessages.add("Priest: " + ChatColor.WHITE
				+ "- A person with significant power over the unholy.");
		classMessages.add("Vampire: " + ChatColor.WHITE
				+ "- No they don't sparkle!");
		classMessages.add("Ghoul: " + ChatColor.WHITE
				+ "- Slow and very durable.");
		classMessages.add("Werewolf: " + ChatColor.WHITE
				+ "- Gain significant powers at night.");
		classMessages.add("WitchHunter: " + ChatColor.WHITE
				+ "- Expert at bows and stealth.");
		classMessages.add("Demon: " + ChatColor.WHITE
				+ "- Possesses an unholy union with fire.");
		classMessages.add("EnderBorn: " + ChatColor.WHITE
				+ "A possessed human with strange powers.");
		classMessages.add("Angel: " + ChatColor.WHITE + "A Human with a free spirit.");
		spanishClassMessages.add("*** " + ChatColor.WHITE
				+ "Clases de Seres Místicos " + ChatColor.RED + "***");
		spanishClassMessages.add("Humano: " + ChatColor.WHITE
				+ "- De carne y hueso, solo sirven para destruir el mundo.");
		spanishClassMessages.add("Sacerdote: " + ChatColor.WHITE
				+ "- Humano bendecido por el mismisimo Dios.");
		spanishClassMessages.add("Vampiro: " + ChatColor.WHITE
				+ "- Criatura sin alma, dan miedo!");
		spanishClassMessages.add("Muerto Viviente: " + ChatColor.WHITE
				+ "- Feo, aterrador y sin cerebro.");
		spanishClassMessages.add("Hombre Lobo: " + ChatColor.WHITE
				+ "- Peludo y muy funcional durante la noche.");
		spanishClassMessages.add("Cazador de Brujas: " + ChatColor.WHITE
				+ "- Experto con arcos y sigiloso.");
		spanishClassMessages.add("Demonio: " + ChatColor.WHITE
				+ "- Tiene una extraña union con el infierno.");
		spanishClassMessages.add("EnderBorn: " + ChatColor.WHITE
				+ "- Un ser humano poseído por poderes extraños.");
	}

	@Override
	public void perform() {
		if (!(sender instanceof Player)) {
			this.sendMessage(classMessages);
			return;
		}
		Player senderPlayer = (Player) sender;

		if (!SupernaturalsPlugin.hasPermissions(senderPlayer, permissions)) {
			if (!SNConfigHandler.spanish) {
				this.sendMessage("You do not have permissions to use this command.");
			} else {
				this.sendMessage("No tienes permiso para este comando.");
			}
			return;
		}

		if (!SNConfigHandler.spanish) {
			this.sendMessage(classMessages);
		} else {
			this.sendMessage(spanishClassMessages);
		}
	}

}
