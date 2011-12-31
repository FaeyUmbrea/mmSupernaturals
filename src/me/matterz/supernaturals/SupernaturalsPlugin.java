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

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.matterz.supernaturals.commands.SNCommand;
import me.matterz.supernaturals.commands.SNCommandAdmin;
import me.matterz.supernaturals.commands.SNCommandClasses;
import me.matterz.supernaturals.commands.SNCommandConvert;
import me.matterz.supernaturals.commands.SNCommandCure;
import me.matterz.supernaturals.commands.SNCommandHelp;
import me.matterz.supernaturals.commands.SNCommandKillList;
import me.matterz.supernaturals.commands.SNCommandList;
import me.matterz.supernaturals.commands.SNCommandPower;
import me.matterz.supernaturals.commands.SNCommandReload;
import me.matterz.supernaturals.commands.SNCommandReset;
import me.matterz.supernaturals.commands.SNCommandRestartTask;
import me.matterz.supernaturals.commands.SNCommandRmTarget;
import me.matterz.supernaturals.commands.SNCommandSave;
import me.matterz.supernaturals.commands.SNCommandSetBanish;
import me.matterz.supernaturals.commands.SNCommandSetChurch;
import me.matterz.supernaturals.io.SNConfigHandler;
import me.matterz.supernaturals.io.SNDataHandler;
import me.matterz.supernaturals.io.SNPlayerHandler;
import me.matterz.supernaturals.listeners.SNBlockListener;
import me.matterz.supernaturals.listeners.SNEntityListener;
import me.matterz.supernaturals.listeners.SNEntityMonitor;
import me.matterz.supernaturals.listeners.SNPlayerListener;
import me.matterz.supernaturals.listeners.SNPlayerMonitor;
import me.matterz.supernaturals.manager.ClassManager;
import me.matterz.supernaturals.manager.DemonManager;
import me.matterz.supernaturals.manager.GhoulManager;
import me.matterz.supernaturals.manager.HumanManager;
import me.matterz.supernaturals.manager.HunterManager;
import me.matterz.supernaturals.manager.PriestManager;
import me.matterz.supernaturals.manager.SuperNManager;
import me.matterz.supernaturals.manager.VampireManager;
import me.matterz.supernaturals.manager.WereManager;
import me.matterz.supernaturals.util.TextUtil;
import net.minecraft.server.Packet103SetSlot;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class SupernaturalsPlugin extends JavaPlugin {
	public static SupernaturalsPlugin instance;

	private final SNConfigHandler snConfig = new SNConfigHandler(this);
	private SNDataHandler snData = new SNDataHandler();

	private final SNEntityListener entityListener = new SNEntityListener(this);
	private final SNPlayerListener playerListener = new SNPlayerListener(this);
	private final SNPlayerMonitor playerMonitor = new SNPlayerMonitor(this);
	private final SNEntityMonitor entityMonitor = new SNEntityMonitor(this);
	private final SNBlockListener blockListener = new SNBlockListener(this);

	private SuperNManager superManager = new SuperNManager(this);
	private HumanManager humanManager = new HumanManager();
	private VampireManager vampManager = new VampireManager();
	private PriestManager priestManager = new PriestManager();
	private WereManager wereManager = new WereManager();
	private GhoulManager ghoulManager = new GhoulManager();
	private HunterManager hunterManager = new HunterManager();
	private DemonManager demonManager = new DemonManager();

	public List<SNCommand> commands = new ArrayList<SNCommand>();

	public static Plugin permissionsPlugin;

	private static File dataFolder;

	public static boolean bukkitperms = false;

	public static PermissionHandler permissionHandler;
	public static PermissionManager permissionExManager;

	private PluginManager pm;

	public SupernaturalsPlugin(){
		SupernaturalsPlugin.instance = this;
	}

	public SNDataHandler getDataHandler(){
		return snData;
	}

	// -------------------------------------------- //
	// 					Managers					//
	// -------------------------------------------- //

	public SuperNManager getSuperManager(){
		return superManager;
	}

	public SNConfigHandler getConfigManager(){
		return snConfig;
	}

	public VampireManager getVampireManager(){
		return vampManager;
	}

	public PriestManager getPriestManager(){
		return priestManager;
	}

	public WereManager getWereManager(){
		return wereManager;
	}

	public GhoulManager getGhoulManager(){
		return ghoulManager;
	}

	public HunterManager getHunterManager(){
		return hunterManager;
	}

	public DemonManager getDemonManager(){
		return demonManager;
	}

	public ClassManager getClassManager(Player player){
		SuperNPlayer snplayer = SuperNManager.get(player);
		if(snplayer.getType().equalsIgnoreCase("demon")) {
			return demonManager;
		} else if(snplayer.getType().equalsIgnoreCase("ghoul")) {
			return ghoulManager;
		} else if(snplayer.getType().equalsIgnoreCase("witchhunter")) {
			return hunterManager;
		} else if(snplayer.getType().equalsIgnoreCase("priest")) {
			return priestManager;
		} else if(snplayer.getType().equalsIgnoreCase("vampire")) {
			return vampManager;
		} else if(snplayer.getType().equalsIgnoreCase("werewolf")) {
			return wereManager;
		} else {
			return humanManager;
		}
	}

	// -------------------------------------------- //
	// 			Plugin Enable/Disable				//
	// -------------------------------------------- //

	@Override
	public void onDisable() {
		SuperNManager.cancelTimer();
		snData.write();

		saveData();
		demonManager.removeAllWebs();
		PluginDescriptionFile pdfFile = this.getDescription();
		log(pdfFile.getName() + " version " + pdfFile.getVersion() + " disabled.");

	}

	@Override
	public void onEnable() {

		this.getDataFolder().mkdir();
		this.pm = this.getServer().getPluginManager();

		// Add the commands
		commands.add(new SNCommandHelp());
		commands.add(new SNCommandAdmin());
		commands.add(new SNCommandPower());
		commands.add(new SNCommandReload());
		commands.add(new SNCommandSave());
		commands.add(new SNCommandConvert());
		commands.add(new SNCommandCure());
		commands.add(new SNCommandList());
		commands.add(new SNCommandClasses());
		commands.add(new SNCommandSetChurch());
		commands.add(new SNCommandSetBanish());
		commands.add(new SNCommandReset());
		commands.add(new SNCommandKillList());
		commands.add(new SNCommandRmTarget());
		commands.add(new SNCommandRestartTask());

		pm.registerEvent(Type.PLAYER_INTERACT, this.playerListener, Priority.Low, this);
		pm.registerEvent(Type.PLAYER_KICK, this.playerListener, Priority.Low, this);

		pm.registerEvent(Type.PLAYER_JOIN, this.playerMonitor, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_PORTAL, this.playerMonitor, Priority.Monitor, this);

		pm.registerEvent(Type.ENTITY_DAMAGE, this.entityListener, Priority.Normal, this);
		pm.registerEvent(Type.ENTITY_TARGET, this.entityListener, Priority.Normal, this);
		pm.registerEvent(Type.ENTITY_EXPLODE, this.entityListener, Priority.Normal, this);

		pm.registerEvent(Type.ENTITY_DAMAGE, this.entityMonitor, Priority.Monitor, this);
		pm.registerEvent(Type.ENTITY_DEATH, this.entityMonitor, Priority.Monitor, this);
		pm.registerEvent(Type.PROJECTILE_HIT, this.entityMonitor, Priority.Monitor, this);

		pm.registerEvent(Type.BLOCK_BREAK, this.blockListener, Priority.Low, this);
		pm.registerEvent(Type.SIGN_CHANGE, this.blockListener, Priority.Low, this);

		PluginDescriptionFile pdfFile = this.getDescription();
		log(pdfFile.getName() + " version " + pdfFile.getVersion() + " enabled.");

		dataFolder = getDataFolder();
		SNConfigHandler.getConfiguration();

		loadData();
		snData = SNDataHandler.read();
		if(snData == null) {
			snData = new SNDataHandler();
		}

		SuperNManager.startTimer();
		HunterManager.createBounties();
		setupPermissions();
	}

	// -------------------------------------------- //
	// 				Chat Commands					//
	// -------------------------------------------- //

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if(sender instanceof Player)
		{
			List<String> parameters = new ArrayList<String>(Arrays.asList(args));
			if(SNConfigHandler.debugMode) {
				SupernaturalsPlugin.log(((Player) sender).getName() + " used command: " + commandLabel
						+ " with args: " + TextUtil.implode(parameters, ", "));
			}
			this.handleCommand(sender, parameters);
			return true;
		}
		return false;
	}

	public void handleCommand(CommandSender sender, List<String> parameters) {
		if (parameters.size() == 0) {
			for (SNCommand vampcommand : this.commands) {
				if (vampcommand.getName().equalsIgnoreCase("help")) {
					vampcommand.execute(sender, parameters);
					return;
				}
			}
			sender.sendMessage(ChatColor.RED+"Unknown command. Try /sn help");
			return;
		}

		String command = parameters.get(0).toLowerCase();
		parameters.remove(0);

		for (SNCommand vampcommand : this.commands) {
			if (command.equals(vampcommand.getName())) {
				vampcommand.execute(sender, parameters);
				return;
			}
		}

		sender.sendMessage(ChatColor.RED+"Unknown command \""+command+"\". Try /sn help");
	}

	// -------------------------------------------- //
	// 				Data Management					//
	// -------------------------------------------- //

	public static void saveData(){
		File file = new File(dataFolder, "data.yml");
		SNPlayerHandler.save(SuperNManager.getSupernaturals(), file);

		SNConfigHandler.saveConfig();
	}

	public static void loadData(){
		File file = new File(dataFolder, "data.yml");
		SuperNManager.setSupernaturals(SNPlayerHandler.load(file));
	}

	public static void reConfig(){
		if(SNConfigHandler.debugMode) {
			SupernaturalsPlugin.log("Reloading config...");
		}
		SNConfigHandler.reloadConfig();
	}

	public static void reloadData(){
		File file = new File(dataFolder, "data.yml");
		SuperNManager.setSupernaturals(SNPlayerHandler.load(file));
	}

	public static void restartTask(){
		SuperNManager.cancelTimer();
		SuperNManager.startTimer();
	}

	// -------------------------------------------- //
	// 				Permissions						//
	// -------------------------------------------- //

	private void setupPermissions() {
		if (permissionHandler != null) {
			return;
		}
		if(pm.isPluginEnabled("PermissionsEx")) {
			permissionsPlugin = pm.getPlugin("PermissionsEx");
			permissionExManager = PermissionsEx.getPermissionManager();
		} else if(pm.isPluginEnabled("Permissions") && pm.isPluginEnabled("PermissionsEx")) {
			permissionsPlugin = pm.getPlugin("Permissions");
			permissionHandler = ((Permissions) permissionsPlugin).getHandler();
		} else if(pm.isPluginEnabled("PermissionsBukkit")) {
			log("Found PermissionsBukkit!");
			bukkitperms = true;
		} else if(pm.isPluginEnabled("bPermissions")) {
			log("Found bPermissions.");
			log(Level.WARNING, "If something goes wrong with bPermissions and this plugin, I will not help!");
			bukkitperms = true;
		} else if(pm.isPluginEnabled("GroupManager")) {
			log("Found GroupManager");
			bukkitperms = true;
		} else if(pm.isPluginEnabled("EssentialsGroupManager")) {
			log("Found EssentialsGroupManager");
			bukkitperms = true;
		}

		if (permissionsPlugin == null && !bukkitperms) {
			log("Permission system not detected, defaulting to SuperPerms");
			bukkitperms = true;
		}
		if(bukkitperms) {
			pm.addPermission(new Permission("supernatural.command.help"));
			pm.addPermission(new Permission("supernatural.command.list"));
			pm.addPermission(new Permission("supernatural.command.power"));
			pm.addPermission(new Permission("supernatural.command.classes"));
			pm.addPermission(new Permission("supernatural.command.killlist"));
			pm.addPermission(new Permission("supernatural.player.shrineuse"));
			pm.addPermission(new Permission("supernatural.player.wolfbane"));
			pm.addPermission(new Permission("supernatural.player.preventwaterdamage"));
			pm.addPermission(new Permission("supernatural.player.preventsundamage"));
			pm.addPermission(new Permission("supernatural.player.witchhuntersign"));
			pm.addPermission(new Permission("supernatural.admin.infinitepower"));
			pm.addPermission(new Permission("supernatural.admin.partial.curse"));
			pm.addPermission(new Permission("supernatural.world.disabled"));
			pm.addPermission(new Permission("supernatural.admin.command.adminhelp"));
			pm.addPermission(new Permission("supernatural.admin.command.cure"));
			pm.addPermission(new Permission("supernatural.admin.command.curse"));
			pm.addPermission(new Permission("supernatural.admin.command.power"));
			pm.addPermission(new Permission("supernatural.admin.command.reset"));
			pm.addPermission(new Permission("supernatural.admin.command.reload"));
			pm.addPermission(new Permission("supernatural.admin.command.save"));
			pm.addPermission(new Permission("supernatural.admin.command.setchurch"));
			pm.addPermission(new Permission("supernatural.admin.command.setbanish"));
			return;
		}

		log("Found and will use plugin "+ permissionsPlugin.getDescription().getFullName());
	}

	public static boolean hasPermissions(Player player, String permissions){
		if(bukkitperms){
			return player.hasPermission(permissions);
		}else{
			if(permissionHandler != null) {
				return permissionHandler.has(player, permissions);
			} else {
				return permissionExManager.has(player, permissions);
			}
		}
	}

	private WorldGuardPlugin getWorldGuard() {
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

		// WorldGuard may not be loaded
		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null; // Maybe you want throw an exception instead
		}

		return (WorldGuardPlugin) plugin;
	}

	public boolean getPvP(Player player){
		WorldGuardPlugin worldGuard = SupernaturalsPlugin.instance.getWorldGuard();
		if(worldGuard == null) {
			return true;
		}
		Vector pt = toVector(player.getLocation());
		RegionManager regionManager = worldGuard.getRegionManager(player.getWorld());
		ApplicableRegionSet set = regionManager.getApplicableRegions(pt);
		return set.allows(DefaultFlag.PVP);
	}

	public boolean getSpawn(Player player){
		WorldGuardPlugin worldGuard = SupernaturalsPlugin.instance.getWorldGuard();
		if(worldGuard == null) {
			return true;
		}
		Vector pt = toVector(player.getLocation());
		RegionManager regionManager = worldGuard.getRegionManager(player.getWorld());
		ApplicableRegionSet set = regionManager.getApplicableRegions(pt);
		return set.allows(DefaultFlag.MOB_SPAWNING);
	}

	// -------------------------------------------- //
	// 					Logging						//
	// -------------------------------------------- //

	public static void log(String msg) {
		log(Level.INFO, msg);
	}

	public static void log(Level level, String msg) {
		Logger.getLogger("Minecraft").log(level, "["+instance.getDescription().getFullName()+"] "+msg);
	}

	// -------------------------------------------- //
	//                 Inventory                    //
	// -------------------------------------------- //

	public static void updateInventory(Player p) {
		CraftPlayer c = (CraftPlayer) p;
		for (int i = 0;i < 36;i++) {
			int nativeindex = i;
			if (i < 9) {
				nativeindex = i + 36;
			}
			ItemStack olditem =  c.getInventory().getItem(i);
			net.minecraft.server.ItemStack item = null;
			if (olditem != null && olditem.getType() != Material.AIR) {
				item = new net.minecraft.server.ItemStack(0, 0, 0);
				item.id = olditem.getTypeId();
				item.count = olditem.getAmount();
				item.b = olditem.getDurability();
			}
			Packet103SetSlot pack = new Packet103SetSlot(0, nativeindex, item);
			c.getHandle().netServerHandler.sendPacket(pack);
		}
	}

}
