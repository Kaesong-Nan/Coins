package de.max_overlack.coins;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

public class MySQL {
	
	private Connection connection = null;
	
	public MySQL(final String address, final int port, final String database, final String user, final String password) {
		
		final Coins main = Coins.getInstance();
		final Logger logger = Bukkit.getLogger();
		
		logger.info("[Coins] Connecting to MySQL database...");
		
		Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
			
			@Override
			public void run() {
				
				try {
					
					connection = DriverManager.getConnection("jdbc:mysql://" + address + ":" + port + "/" + database + "?autoReconnect=true", user, password);
					
				} catch (SQLException e) {
					
					e.printStackTrace();
					logger.severe("[Coins] Failed to connect to MySQL database!");
					logger.severe("[Coins] Disabling plugin...");
					Bukkit.getPluginManager().disablePlugin(Coins.getInstance());
				}
				
				if (isConnected()) {
					
					logger.info("[Coins] Successfully connected to MySQL database!");
					update("CREATE TABLE IF NOT EXISTS coins (username VARCHAR(16), coins BIGINT)");
				}
			}
		});
	}
	
	public boolean isConnected() {
		
		return this.connection != null;
	}
	
	public void disconnect() {
		
		Bukkit.getLogger().info("[Coins] Disconnecting from MySQL database...");
		
		try {
			
			this.connection.close();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	public void update(final String update) {
		
		try {
			
			final PreparedStatement statement = this.connection.prepareStatement(update);
			statement.executeUpdate();
			statement.close();
			
		} catch (SQLException e) {
			
			Bukkit.getLogger().warning("[Coins] An Error occured while trying to update the MySQL database:");
			e.printStackTrace();
		}
	}
	
	public boolean hasQueryResult(final String query) {
		
		final ResultSet result = this.query(query);
		boolean hasResult = false;
		
		if (result != null) {
			
			try {
				
				hasResult = result.first();
				result.getStatement().close();
				
			} catch (SQLException e) {
				
				this.resultError(e);
			}
		}
		return hasResult;
	}
	
	public ResultSet query(final String query) {
		
		ResultSet result = null;
		
		try {
			
			final PreparedStatement statement = this.connection.prepareStatement(query);
			result = statement.executeQuery();
			
		} catch (SQLException e) {
			
			Bukkit.getLogger().warning("[Coins] An Error occured while trying to query the MySQL database:");
			e.printStackTrace();
		}
		return result;
	}
	
	public void resultError(SQLException e) {
		
		Bukkit.getLogger().warning("[Coins] An Error occured while trying to get a result of a query:");
		e.printStackTrace();
	}
}
