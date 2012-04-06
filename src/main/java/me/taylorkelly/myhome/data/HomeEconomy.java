package me.taylorkelly.myhome.data;

import me.taylorkelly.myhome.HomeSettings;
import me.taylorkelly.myhome.utils.HomeLogger;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class HomeEconomy {
	private static Economy economy = null;
	private static boolean econEnabled = false;
	private static Plugin myhome;
	private static String econname = null; 
	private static String econver = null;
	
	public static void init(Plugin plugin) {
		HomeEconomy.myhome = plugin;
		if(HomeSettings.eConomyEnabled) {
	        if (myhome.getServer().getPluginManager().getPlugin("Vault") == null) {
				HomeLogger.warning("===============================================");
				HomeLogger.warning("Economy support is enabled in config but Vault was not found. Is it installed?");
				HomeLogger.warning("===============================================");
				disableEconomy();
				HomeSettings.eConomyEnabled = false;
				return;
	        }
	        RegisteredServiceProvider<Economy> rsp = myhome.getServer().getServicesManager().getRegistration(Economy.class);
	        if (rsp == null) {
	        	HomeLogger.warning("===============================================");
				HomeLogger.warning("Vault could not hook into an economy plugin.");
				HomeLogger.warning("===============================================");
				disableEconomy();
				HomeSettings.eConomyEnabled = false;
				return;
	        }
	        economy = rsp.getProvider();
			enableEconomy();
			HomeLogger.info("Economy: Using Vault for economy services");
		}
	}
	
	public static void disableEconomy() {
		econEnabled = false;
	}
	
	public static void enableEconomy() {
		econEnabled = true;
	}

	public static boolean hookedEconomy() {
		return econEnabled;
	}
	
	public static boolean economyReady() {
		if(!econEnabled && HomeSettings.eConomyEnabled == true) {
			HomeSettings.eConomyEnabled = false;
			return false;
		} else {
			return true;
		}
	}
	
	//------------------------------------------------------
		
	public static boolean hasAccount(String name){
		if(!economyReady()) { return false; }
		
		return economy.hasAccount(name);
	}

	public static boolean chargePlayer(String name, float amount){
		if(!economyReady()) { return false; }
		
		if(hasAccount(name)) {
			if(hasEnough(name, amount)) {
				EconomyResponse resp = economy.withdrawPlayer(name,amount);
				return resp.transactionSuccess();
			} else {
				return false;
			}
		} else {
			HomeLogger.warning("Could not fetch economy details for " + name);
			return false;
		}
	}

	public static boolean hasEnough(String name, float amount) {
		if(!economyReady()) { return false; }
		
		return economy.has(name, amount);
	}

	public static double balance(String name){
		if(!economyReady()) { return 0; }
		
		return economy.getBalance(name);
	}

	public static String formattedBalance(double amount){
		if(!economyReady()) { return "Error"; }
		
		return economy.format(amount);
	}
}