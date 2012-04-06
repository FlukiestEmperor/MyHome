package me.taylorkelly.myhome.permissions;

import me.taylorkelly.myhome.HomeSettings;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class SuperpermsHandler implements IPermissionsHandler {
	
	public SuperpermsHandler(Plugin plugin) {

	}
	
	@Override
	public boolean hasPermission(final Player player, final String node, boolean defaultPerm) {
		if(player.isOp() && HomeSettings.opPermissions) {
			if (player.hasPermission("-" + node)) {
				return false;
			} 
			return true;
		}
		if (player.hasPermission("-" + node)) {
			return false;
		}
		final String[] parts = node.split("\\.");
		final StringBuilder strbuilder = new StringBuilder(node.length());
		for (String part : parts) {
			strbuilder.append('*');
			if (player.hasPermission(strbuilder.toString())) {
				return true;
			}
			strbuilder.deleteCharAt(strbuilder.length() - 1);
			strbuilder.append(part).append('.');
		}
		return player.hasPermission(node);
	}

	@Override
	public int getInteger(final Player player, final String node, final int defaultInt) {
		if(player.isOp() && HomeSettings.opPermissions) {
			return 0;
		}
		return defaultInt;
	}
}

