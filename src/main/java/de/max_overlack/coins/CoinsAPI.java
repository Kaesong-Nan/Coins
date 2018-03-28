package de.max_overlack.coins;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

public class CoinsAPI {
	
	public void getTopPositonAsynchronously(final String username, final MySQLResponseReceiveListener<Integer> responseListener) {
		
		Bukkit.getScheduler().runTaskAsynchronously(Coins.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				
				responseListener.onMySQLResponseReceived(getTopPositon(username));
			}
		});
	}
	
	public int getTopPositon(final String username) {
		
		return getTopUsernames().indexOf(username) + 1;
	}
	
	public void getTopUsernamesAsynchronously(final MySQLResponseReceiveListener<List<String>> responseListener) {
		
		getTopUsernamesAsynchronously(Integer.MAX_VALUE, responseListener);
	}
	
	public void getTopUsernamesAsynchronously(final int limit, final MySQLResponseReceiveListener<List<String>> responseListener) {
		
		Bukkit.getScheduler().runTaskAsynchronously(Coins.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				
				responseListener.onMySQLResponseReceived(getTopUsernames(limit));
			}
		});
	}
	
	public List<String> getTopUsernames() {
		
		return getTopUsernames(Integer.MAX_VALUE);
	}
	
	public List<String> getTopUsernames(final int limit) {
		
		final ArrayList<String> top = new ArrayList<String>();
		final MySQL mysql = Coins.getInstance().getMySQL();
		final ResultSet result = mysql.query("SELECT username FROM coins ORDER BY coins DESC LIMIT " + limit);
		
		try {
			
			while (result.next()) {
				
				top.add(result.getString("username"));
			}
			result.getStatement().close();
			
		} catch (SQLException e) {
			
			mysql.resultError(e);
		}
		return top;
	}
	
	public void transferCoinsAsynchronously(final String usernameFrom, final String usernameTo, final long amount) {
		
		Bukkit.getScheduler().runTaskAsynchronously(Coins.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				
				transferCoins(usernameFrom, usernameTo, amount);
			}
		});
	}
	
	public void transferCoins(final String usernameFrom, final String usernameTo, final long amount) {
		
		if (this.hasEnoughMoney(usernameFrom, amount)) {
			
			this.addCoins(usernameTo, amount);
			this.removeCoins(usernameFrom, amount);
			
		} else {
			
			this.addCoins(usernameTo, this.getCoins(usernameFrom));
			this.resetCoins(usernameFrom);
		}
	}
	
	public void removeCoinsAsynchronously(final String username, final long amount) {
		
		Bukkit.getScheduler().runTaskAsynchronously(Coins.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				
				removeCoins(username, amount);
			}
		});
	}
	
	public void removeCoins(final String username, final long amount) {
		
		if (this.hasEnoughMoney(username, amount)) this.setCoins(username, this.getCoins(username) - amount);
		else this.setCoins(username, 0);
	}
	
	public void hasEnoughMoneyAsynchronously(final String username, final long amount, final MySQLResponseReceiveListener<Boolean> responseListener) {
		
		this.getCoinsAsynchronously(username, new MySQLResponseReceiveListener<Long>() {
			
			@Override
			public void onMySQLResponseReceived(Long result) {
				
				responseListener.onMySQLResponseReceived(amount <= result);
			}
		});
	}
	
	public boolean hasEnoughMoney(final String username, final long amount) {
		
		return amount <= this.getCoins(username);
	}
	
	public void addCoinsAsynchronously(final String username, final long amount) {
		
		Bukkit.getScheduler().runTaskAsynchronously(Coins.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				
				addCoins(username, amount);
			}
		});
	}
	
	public void addCoins(final String username, final long amount) {
		
		this.setCoins(username, this.getCoins(username) + amount);
	}
	
	public void getCoinsAsynchronously(final String username, final MySQLResponseReceiveListener<Long> responseListener) {
		
		Bukkit.getScheduler().runTaskAsynchronously(Coins.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				
				responseListener.onMySQLResponseReceived(getCoins(username));
			}
		});
	}
	
	public long getCoins(final String username) {
		
		final MySQL mysql = Coins.getInstance().getMySQL();
		
		this.insertUsernameIfNotExists(username);
		
		final ResultSet result = mysql.query("SELECT * FROM coins WHERE username='" + username + "'");
		long coins = -1;
		
		if (result != null) {
			
			try {
				
				result.first();
				coins = result.getLong("coins");
				result.getStatement().close();
				
			} catch (SQLException e) {
				
				mysql.resultError(e);
			}
		}
		return coins;
	}
	
	public void resetCoinsAsynchronously(final String username) {
		
		this.setCoinsAsynchronously(username, 0);
	}
	
	public void resetCoins(final String username) {
		
		this.setCoins(username, 0);
	}
	
	public void setCoinsAsynchronously(final String username, final long amount) {
		
		Bukkit.getScheduler().runTaskAsynchronously(Coins.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				
				setCoins(username, amount);
			}
		});
	}
	
	public void setCoins(final String username, final long amount) {
		
		final MySQL mysql = Coins.getInstance().getMySQL();
		
		insertUsernameIfNotExists(username);
		mysql.update("UPDATE coins SET coins='" + amount + "' WHERE username='" + username + "'");
	}
	
	public void doesUsernameExistAsynchronously(final String username, final MySQLResponseReceiveListener<Boolean> receiveListener) {
		
		Bukkit.getScheduler().runTaskAsynchronously(Coins.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				
				receiveListener.onMySQLResponseReceived(doesUsernameExist(username));
			}
		});
	}
	
	public boolean doesUsernameExist(final String username) {
		
		return Coins.getInstance().getMySQL().hasQueryResult("SELECT * FROM coins WHERE username='" + username + "'");
	}
	
	private void insertUsernameIfNotExists(final String username) {
		
		if (!doesUsernameExist(username)) Coins.getInstance().getMySQL().update("INSERT INTO coins VALUES ('" + username + "', '0')");
	}
}
