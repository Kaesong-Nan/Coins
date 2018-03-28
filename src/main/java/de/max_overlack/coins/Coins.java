package de.max_overlack.coins;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import de.max_overlack.coins.commands.CoinsCommand;
import de.max_overlack.coins.commands.CoinsTopCommand;
import de.max_overlack.coins.commands.PayCommand;
import de.max_overlack.coins.events.JoinEvent;

public class Coins extends JavaPlugin {
	
	private static Coins instance;
	private CoinsAPI api = new CoinsAPI();
	private MySQL mysql;
	
	public static Coins getInstance() {
		
		return instance;
	}
	
	public CoinsAPI getAPI() {
		
		return api;
	}
	
	public MySQL getMySQL() {
		
		return mysql;
	}
	
	@Override
	public void onEnable() {
		
		instance = this;
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		this.mysql = new MySQL(this.getConfig().getString("mysql.address"),
				this.getConfig().getInt("mysql.port"),
				this.getConfig().getString("mysql.database"),
				this.getConfig().getString("mysql.username"),
				this.getConfig().getString("mysql.password"));
		Bukkit.getPluginManager().registerEvents(new JoinEvent(), this);
		this.getCommand("coins").setExecutor(new CoinsCommand());
		this.getCommand("coinstop").setExecutor(new CoinsTopCommand());
		this.getCommand("pay").setExecutor(new PayCommand());
		Bukkit.getLogger().info("[Coins] Enabled");
	}
	
	@Override
	public void onDisable() {
		
		if (this.mysql.isConnected()) this.mysql.disconnect();
		Bukkit.getLogger().info("[Coins] Disabled");
	}
}
