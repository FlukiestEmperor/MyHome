package me.taylorkelly.myhome.permissions;

import java.util.LinkedHashMap;
import java.util.Map;

import me.taylorkelly.myhome.utils.HomeLogger;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class PermissionsHandler implements IPermissionsHandler {
	private enum PermHandler {
		PERMISSIONSEX, PERMISSIONS3, PERMISSIONS2, GROUPMANAGER, BPERMISSIONS, BPERMISSIONS2, SUPERPERMS, VAULT, NONE
	}
	private static PermHandler permplugin = PermHandler.NONE;
	private transient IPermissionsHandler handler = new NullHandler();
	private final transient Plugin plugin;
	private static PluginManager pm;
	
	public PermissionsHandler(final Plugin plugin) {
		this.plugin = plugin;
		PermissionsHandler.pm = plugin.getServer().getPluginManager();
		registerPermissions();
		checkPermissions();
	}

	@Override
	public boolean hasPermission(final Player player, final String node, boolean defaultPerm) {
		return handler.hasPermission(player, node, defaultPerm);
	}

	@Override
	public int getInteger(final Player player, final String node, int defaultInt) {
		return handler.getInteger(player, node, defaultInt);
	}
	
	public void checkPermissions() {
		final PluginManager pluginManager = plugin.getServer().getPluginManager();

		final Plugin permExPlugin = pluginManager.getPlugin("PermissionsEx");
		if (permExPlugin != null && permExPlugin.isEnabled()) {
			if (!(handler instanceof PermissionsExHandler)) {
				permplugin = PermHandler.PERMISSIONSEX;
				String version = permExPlugin.getDescription().getVersion();
				HomeLogger.info("Access Control: Using PermissionsEx v"+ version);
				handler = new PermissionsExHandler();
			}
			return;
		}

		final Plugin bPermPlugin = pluginManager.getPlugin("bPermissions");
		if (bPermPlugin != null && bPermPlugin.isEnabled()) {
			if (bPermPlugin.getDescription().getVersion().charAt(0) == '2') {
				if (!(handler instanceof BPermissions2Handler)) {
					permplugin = PermHandler.BPERMISSIONS2;
					String version = bPermPlugin.getDescription().getVersion();
					HomeLogger.info("Access Control: Using bPermissions"+ version);
					handler = new BPermissions2Handler();
				}
			} else {
				if (!(handler instanceof BPermissionsHandler)) {
					permplugin = PermHandler.BPERMISSIONS;
					String version = bPermPlugin.getDescription().getVersion();
					HomeLogger.info("Access Control: Using bPermissions"+ version);
					handler = new BPermissionsHandler();
				}
			}
			return;
		}

		final Plugin GMplugin = pluginManager.getPlugin("GroupManager");
		if (GMplugin != null && GMplugin.isEnabled()) {
			if (!(handler instanceof GroupManagerHandler)) {
				permplugin = PermHandler.GROUPMANAGER;
				String version = GMplugin.getDescription().getVersion();
				HomeLogger.info("Access Control: Using GroupManager v"+ version);
				handler = new GroupManagerHandler(GMplugin);
			}
			return;
		}

		final Plugin permPlugin = pluginManager.getPlugin("Permissions");
		if (permPlugin != null && permPlugin.isEnabled()) {
			if (permPlugin.getDescription().getVersion().charAt(0) == '3') {
				if (!(handler instanceof Permissions3Handler)) {
					permplugin = PermHandler.PERMISSIONS3;
					String version = permPlugin.getDescription().getVersion();
					HomeLogger.info("Access Control: Using Permissions v"+ version);
					handler = new Permissions3Handler(permPlugin);
				}
			} else {
				if (!(handler instanceof Permissions2Handler)) {
					permplugin = PermHandler.PERMISSIONS2;
					String version = permPlugin.getDescription().getVersion();
					HomeLogger.info("Access Control: Using Permissions v"+ version);
					handler = new Permissions2Handler(permPlugin);
				}
			}
			return;
		}

		final Plugin Vaultplugin = pluginManager.getPlugin("Vault");
		if (Vaultplugin != null && Vaultplugin.isEnabled()) {
			if (!(handler instanceof VaultHandler)) {
				permplugin = PermHandler.VAULT;
				String version = Vaultplugin.getDescription().getVersion();
				HomeLogger.info("Access Control: Using Vault v"+ version);
				handler = new VaultHandler(Vaultplugin, plugin);
			}
			return;
		}
		
		if (permplugin == PermHandler.NONE) {
			if (!(handler instanceof SuperpermsHandler)) {
				HomeLogger.info("Access Control: Using Bukkit's Permssion System (Vault is recommended!)");
				handler = new SuperpermsHandler(this.plugin);
				permplugin = PermHandler.SUPERPERMS;
			}
		}
	}
	
	private static void registerPermissions() {
		registerAdminPerms();
		registerUserPerms();
		registerEcoPerms();
		registerBypassPerms();
		overallPerm();
		}

		private static void registerAdminPerms() {
		pm.addPermission(new org.bukkit.permissions.Permission("myhome.admin.reload", "Admin: Reload settings", PermissionDefault.OP));
		pm.addPermission(new org.bukkit.permissions.Permission("myhome.admin.home.delete", "Admin: Delete homes of users", PermissionDefault.OP));
		pm.addPermission(new org.bukkit.permissions.Permission("myhome.admin.home.any", "Admin: Teleport to any user's /home", PermissionDefault.OP));
		pm.addPermission(new org.bukkit.permissions.Permission("myhome.admin.convert", "Admin: Convert from old homes.txt", PermissionDefault.OP));
		pm.addPermission(new org.bukkit.permissions.Permission("myhome.admin.home.list", "Admin: See a full list of /homes", PermissionDefault.OP));
		Map<String, Boolean> adminmap = new LinkedHashMap<String, Boolean>();
		adminmap.put("myhome.admin.home.delete", true);
		adminmap.put("myhome.admin.home.any", true);
		adminmap.put("myhome.admin.convert", true);
		adminmap.put("myhome.admin.home.list", true);
		pm.addPermission(new org.bukkit.permissions.Permission("myhome.admin.*", "Admin: All admin commands", PermissionDefault.OP, adminmap));
		}

		private static void registerUserPerms() {
		pm.addPermission(new org.bukkit.permissions.Permission("myhome.home.basic.home", "Usage of /home", PermissionDefault.TRUE));
		pm.addPermission(new org.bukkit.permissions.Permission("myhome.home.basic.set", "Usage of /sethome", PermissionDefault.TRUE));
		pm.addPermission(new org.bukkit.permissions.Permission("myhome.home.basic.delete", "Usage of /home delete", PermissionDefault.TRUE));
		pm.addPermission(new org.bukkit.permissions.Permission("myhome.home.soc.list", "Can see a list of homes", PermissionDefault.TRUE));
		pm.addPermission(new org.bukkit.permissions.Permission("myhome.home.soc.others", "Can /home to other homes if invited", PermissionDefault.TRUE));
		pm.addPermission(new org.bukkit.permissions.Permission("myhome.home.soc.invite", "Can invite to your /home", PermissionDefault.TRUE));
		pm.addPermission(new org.bukkit.permissions.Permission("myhome.home.soc.uninvite", "Can uninvite people from your /home", PermissionDefault.TRUE));
		pm.addPermission(new org.bukkit.permissions.Permission("myhome.home.soc.public", "Allow anyone to use your /home", PermissionDefault.TRUE));
		pm.addPermission(new org.bukkit.permissions.Permission("myhome.home.soc.private", "Disallow anyone to use your /home", PermissionDefault.TRUE));
		Map<String, Boolean> userbasicmap = new LinkedHashMap<String, Boolean>();
		Map<String, Boolean> usersocmap = new LinkedHashMap<String, Boolean>();
		Map<String, Boolean> userallmap = new LinkedHashMap<String, Boolean>();
		userbasicmap.put("myhome.home.basic.home", true);
		userbasicmap.put("myhome.home.basic.set", true);
		userbasicmap.put("myhome.home.basic.delete", true);
		usersocmap.put("myhome.home.soc.list", true);
		usersocmap.put("myhome.home.soc.others", true);
		usersocmap.put("myhome.home.soc.invite", true);
		usersocmap.put("myhome.home.soc.uninvite", true);
		usersocmap.put("myhome.home.soc.public", true);
		usersocmap.put("myhome.home.soc.private", true);
		userallmap.putAll(userbasicmap);
		userallmap.putAll(usersocmap);
		pm.addPermission(new org.bukkit.permissions.Permission("myhome.home.basic.*", "Basic /home commands", PermissionDefault.TRUE, userbasicmap));
		pm.addPermission(new org.bukkit.permissions.Permission("myhome.home.soc.*", "Social /home commands", PermissionDefault.TRUE, usersocmap));
		pm.addPermission(new org.bukkit.permissions.Permission("myhome.home.*", "All user /home commands", PermissionDefault.TRUE, userallmap));
		}


		private static void registerEcoPerms() {
		pm.addPermission(new org.bukkit.permissions.Permission("myhome.econ.free.sethome", "Free usage of /sethome", PermissionDefault.OP));
		pm.addPermission(new org.bukkit.permissions.Permission("myhome.econ.free.home", "Free usage of /home", PermissionDefault.OP));
		Map<String, Boolean> econmap = new LinkedHashMap<String, Boolean>();
		econmap.put("myhome.econ.free.sethome", true);
		econmap.put("myhome.econ.free.home", true);
		pm.addPermission(new org.bukkit.permissions.Permission("myhome.home.free.*", "Free usage of commands", PermissionDefault.OP, econmap));
		}

		private static void registerBypassPerms() {
		pm.addPermission(new org.bukkit.permissions.Permission("myhome.bypass.bedsethome", "Bypass: can use /sethome when beds are enforced", PermissionDefault.OP));
		pm.addPermission(new org.bukkit.permissions.Permission("myhome.bypass.cooldown", "Bypass: Do not have to wait for /home cooldown timers", PermissionDefault.OP));
		pm.addPermission(new org.bukkit.permissions.Permission("myhome.bypass.warmup", "Bypass: Do not wait for /home to warm up", PermissionDefault.OP));
		pm.addPermission(new org.bukkit.permissions.Permission("myhome.bypass.sethomecooldown", "Bypass: Do not have to wait for cooldown to use /sethome", PermissionDefault.OP));
		pm.addPermission(new org.bukkit.permissions.Permission("myhome.bypass.dmgaborting", "Bypass: Do not abort /home if damaged", PermissionDefault.OP));
		pm.addPermission(new org.bukkit.permissions.Permission("myhome.bypass.moveaborting", "Bypass: Do not abort /home if moving", PermissionDefault.OP));
		Map<String, Boolean> bypassmap = new LinkedHashMap<String, Boolean>();
		bypassmap.put("myhome.bypass.bedsethome", true);
		bypassmap.put("myhome.bypass.cooldown", true);
		bypassmap.put("myhome.bypass.warmup", true);
		bypassmap.put("myhome.bypass.sethomecooldown", true);
		bypassmap.put("myhome.bypass.dmgaborting", true);
		bypassmap.put("myhome.bypass.moveaborting", true);
		pm.addPermission(new org.bukkit.permissions.Permission("myhome.bypass.*", "Bypass all timers and restrictions", PermissionDefault.OP, bypassmap));
		}

		public static void overallPerm() {
		Map<String, Boolean> fullmap = new LinkedHashMap<String, Boolean>();
		fullmap.put("myhome.econ.free.*", true);
		fullmap.put("myhome.bypass.*", true);
		fullmap.put("myhome.home.soc.*", true);
		fullmap.put("myhome.home.basic.*", true);
		pm.addPermission(new org.bukkit.permissions.Permission("myhome.*", "Full access", PermissionDefault.OP, fullmap));
		}
	
}
