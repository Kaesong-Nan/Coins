package de.max_overlack.coins.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.max_overlack.coins.Coins;
import de.max_overlack.coins.CoinsAPI;
import de.max_overlack.coins.MySQLResponseReceiveListener;

public class CoinsTopCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (args.length == 0) {
			
			if (sender.hasPermission("coins.see.top")) {
				
				final CoinsAPI api = Coins.getInstance().getAPI();
				
				sender.sendMessage(ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.GREEN + " - Top 10 Players - ");
				
				api.getTopUsernamesAsynchronously(10, new MySQLResponseReceiveListener<List<String>>() {
					
					@Override
					public void onMySQLResponseReceived(List<String> result) {
						
						byte i = 1;
						
						for (String username : result) {
							
							sender.sendMessage(ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.GREEN + i + ". " + username + ": " + api.getCoins(username));
							i++;
						}
						
						if (sender instanceof Player) {
							
							final Player p = (Player) sender;
							
							p.sendMessage(ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.GREEN + "Dein Platz: " + ChatColor.DARK_GREEN + api.getTopPositon(p.getName()));
						}
					}
				});
				
			} else {
				
				sender.sendMessage(ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.RED + "Your are not allowed to use this command!");
			}
			return true;
		}
		return false;
	}
}
