package me.taylorkelly.myhome.permissions;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.permission.Permission;

public class VaultHandler implements IPermissionsHandler {
    public static Permission perms = null;
       
	public VaultHandler(final Plugin vaultPlugin, Plugin myhome) {
		RegisteredServiceProvider<Permission> rsp = myhome.getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
	}

	@Override
	public boolean hasPermission(final Player player, final String node, boolean defaultPerm) {
		return perms.has(player, node);
	}

	@Override
	public int getInteger(final Player player, final String node, final int defaultInt) {
		// We cannot do this via Vault.
		return defaultInt;
	}
}

