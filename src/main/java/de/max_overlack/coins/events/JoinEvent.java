package de.max_overlack.coins.events;


import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.max_overlack.coins.Coins;
import de.max_overlack.coins.MySQLResponseReceiveListener;

public class JoinEvent implements Listener {
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		
		final Player p = event.getPlayer();
		
		Coins.getInstance().getAPI().getCoinsAsynchronously(p.getName(), new MySQLResponseReceiveListener<Long>() {
			
			@Override
			public void onMySQLResponseReceived(Long result) {
				
				p.sendMessage(ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.GREEN + "You currently have " + ChatColor.DARK_GREEN + result + " Coins" + ChatColor.GREEN + "!");
			}
		});
	}
}
