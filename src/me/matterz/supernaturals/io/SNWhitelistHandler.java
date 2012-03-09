package me.matterz.supernaturals.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.matterz.supernaturals.SuperNPlayer;
import me.matterz.supernaturals.SupernaturalsPlugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class SNWhitelistHandler {

	public static FileConfiguration whitelistYaml = null; // Using bukkit's
															// system for easy
															// access :D
	public static File whitelistYamlFile = null;
	public static SupernaturalsPlugin plugin;
	public static List<String> playersInWhitelist = new ArrayList<String>();

	public SNWhitelistHandler(SupernaturalsPlugin instance) {
		SNWhitelistHandler.plugin = instance;
	}

	public static void reloadWhitelist() {
		if (whitelistYamlFile == null) {
			whitelistYamlFile = new File(plugin.getDataFolder(), "whitelistYaml.yml");
		}
		whitelistYaml = YamlConfiguration.loadConfiguration(whitelistYamlFile);
		playersInWhitelist = getPlayersInWhitelistYAML();
	}

	public static FileConfiguration getWhitelist() {
		if (whitelistYaml == null) {
			reloadWhitelist();
		}
		return whitelistYaml;
	}

	public static void saveWhitelist() {
		if (whitelistYaml == null || whitelistYamlFile == null) {
			return;
		}
		try {
			whitelistYaml.save(whitelistYamlFile);
		} catch (IOException ex) {
			Logger.getLogger("Minecraft").log(Level.SEVERE, "Could not save config to "
					+ whitelistYamlFile, ex);
		}
	}

	public static void addPlayer(String playerName) {
		playersInWhitelist.add(playerName);
		whitelistYaml.set("WhitelistedPlayers", playersInWhitelist);
	}

	public static boolean isWhitelisted(SuperNPlayer player) {
		if (!SNConfigHandler.enableJoinCommand) {
			return true;
		}
		return SNWhitelistHandler.getPlayersInWhitelistYAML().contains(player.getName());
	}

	public static List<String> getPlayersInWhitelistYAML() {
		return getWhitelist().getStringList("WhitelistedPlayers");
	}

}