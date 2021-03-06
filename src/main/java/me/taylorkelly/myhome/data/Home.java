package me.taylorkelly.myhome.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.taylorkelly.myhome.HomeSettings;
import me.taylorkelly.myhome.locale.LocaleManager;

import org.bukkit.*;
import org.bukkit.entity.Player;

public class Home {
	public int index;
	public String name;
	public String world;
	public double x;
	public double y;
	public double z;
	public int yaw;
	public int pitch;
	public int publicAll;
	public String welcomeMessage;
	public ArrayList<String> permissions;
	public static int nextIndex = 1;
	Location getLocation;
	private Map<String, String> localedata = new HashMap<String, String>();

	public Home(int index, String name, String world, double x, double y, double z, int yaw, int pitch, int publicAll, String permissions, String welcomeMessage) {
		this.index = index;
		this.name = name;
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
		this.publicAll = publicAll;
		this.permissions = processList(permissions);
		this.welcomeMessage = welcomeMessage;
		if (index > nextIndex) {
			nextIndex = index;
		}
		nextIndex++;
	}

	public Home(Player creator) { 
		this.index = nextIndex;
		nextIndex++;
		this.name = creator.getName();
		this.world = creator.getWorld().getName();
		this.x = creator.getLocation().getX();
		this.y = creator.getLocation().getY();
		this.z = creator.getLocation().getZ();
		this.yaw = Math.round(creator.getLocation().getYaw()) % 360;
		this.pitch = Math.round(creator.getLocation().getPitch()) % 360;
		this.publicAll = HomeSettings.homesArePublic ? 1 : 0;
		this.permissions = new ArrayList<String>();
		localedata.put("TARGET", name);
		this.welcomeMessage = LocaleManager.getString("home.default.welcomemessage", localedata);
		localedata.clear();	
	}

	public Home(String name, Location location) {
		this.index = nextIndex;
		nextIndex++;
		this.name = name;
		this.world = location.getWorld().getName();
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
		this.yaw = Math.round(location.getYaw()) % 360;
		this.pitch = Math.round(location.getPitch()) % 360;
		this.publicAll = HomeSettings.homesArePublic ? 1 : 0;
		this.permissions = new ArrayList<String>();
		localedata.put("TARGET", name);
		this.welcomeMessage = LocaleManager.getString("home.default.welcomemessage", localedata);
		localedata.clear();	
	}

	private ArrayList<String> processList(String permissions) {
		String[] names = permissions.split(",");
		ArrayList<String> ret = new ArrayList<String>();
		for (String permissionee : names) {
			if (permissionee.equals("")) {
				continue;
			}
			ret.add(permissionee.trim());
		}
		return ret;
	}

	public String permissionsString() {
		StringBuilder ret = new StringBuilder();
		for (String permisionee : permissions) {
			ret.append(permisionee);
			ret.append(",");
		}
		return ret.toString();
	}

	public boolean playerCanWarp(Player player) {
		if (name.equals(player.getName())) {
			return true;
		}
		if (permissions.contains(player.getName())) {
			return true;
		}

		return (publicAll == 1) ? true : false;  
	}

	public void warp(Player player, Server server) {
		World currWorld = null;
		if (world.equals("0")) {
			currWorld = server.getWorlds().get(0);
		} else {
			currWorld = server.getWorld(world);
		}
		if (currWorld == null) {
			player.sendMessage(LocaleManager.getString("error.nosuchworld"));
		} else {
			Location location = new Location(currWorld, x, y, z, yaw, pitch);
			player.teleport(location);
			player.sendMessage(ChatColor.AQUA + this.welcomeMessage);
		}
	}

	public boolean playerIsCreator(String player) {
		if (name.equals(player)) {
			return true;
		}
		return false;
	}

	public void invite(String player) {
		permissions.add(player);
	}

	public boolean playerIsInvited(String player) {
		return permissions.contains(player);
	}

	public void uninvite(String inviteeName) {
		permissions.remove(inviteeName);
	}

	public String invitees() {
		if (permissions.size() == 1) {
			return permissions.get(0) + " is";
		}
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < permissions.size(); i++) {
			builder.append(permissions.get(i));
			if (i + 2 < permissions.size()) {
				builder.append(", ");
			} else if (i + 1 < permissions.size()) {
				builder.append(" and ");
			}
		}
		builder.append(" are ");
		return builder.toString();
	}

	@Override
	public String toString() {
		return name;
	}

	public void setLocation(Location location) {
		this.world = location.getWorld().getName();
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
		this.yaw = Math.round(location.getYaw()) % 360;
		this.pitch = Math.round(location.getPitch()) % 360;
	}

	public Location getLocation(Server server) {
		World currWorld = server.getWorld(world);
		if(currWorld == null) {
			return null;
		} else {
			return new Location(currWorld, x, y, z, yaw, pitch);
		}
	}
}
