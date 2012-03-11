package me.matterz.supernaturals.listeners;

import java.util.logging.Level;

import me.matterz.supernaturals.SupernaturalsPlugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class SNServerMonitor implements Listener {

	public SupernaturalsPlugin plugin;

	public SNServerMonitor(SupernaturalsPlugin instance) {
		instance.getServer().getPluginManager().registerEvents(this, instance);
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginEnable(PluginEnableEvent event) {
		if (SupernaturalsPlugin.foundPerms) {
			return;
		}
		Plugin enabledPlugin = event.getPlugin();
		if (enabledPlugin.toString().startsWith("PermissionsEx")) {
			SupernaturalsPlugin.log("Found PermissionsEx!");
			SupernaturalsPlugin.foundPerms = true;
		} else if (enabledPlugin.toString().startsWith("GroupManager")) {
			SupernaturalsPlugin.log("Found GroupManager");
			SupernaturalsPlugin.foundPerms = true;
		} else if (enabledPlugin.toString().startsWith("bPermissions")) {
			SupernaturalsPlugin.log("Found bPermissions.");
			SupernaturalsPlugin.log(Level.WARNING, "If something goes wrong with bPermissions and this plugin, I will not help!");
			SupernaturalsPlugin.foundPerms = true;
		} else if (enabledPlugin.toString().startsWith("PermissionsBukkit")) {
			SupernaturalsPlugin.log("Found PermissionsBukkit!");
			SupernaturalsPlugin.foundPerms = true;
		}
	}
}
