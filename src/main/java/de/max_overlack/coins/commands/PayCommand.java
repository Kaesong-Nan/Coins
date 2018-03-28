package de.max_overlack.coins.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.max_overlack.coins.Coins;
import de.max_overlack.coins.CoinsAPI;
import de.max_overlack.coins.MySQLResponseReceiveListener;

public class PayCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (sender instanceof Player) {
			
			if (args.length == 2) {
				
				if (sender.hasPermission("coins.pay")) {
					
					final Player p = (Player) sender;
					final String name = p.getName();
					final CoinsAPI api = Coins.getInstance().getAPI();
					
					api.doesUsernameExistAsynchronously(args[0], new MySQLResponseReceiveListener<Boolean>() {
						
						@Override
						public void onMySQLResponseReceived(Boolean result) {
							
							if (result) {
								
								try {
									
									if (api.hasEnoughMoney(name, Long.parseLong(args[1]))) {
										
										api.transferCoins(name, args[0], Long.parseLong(args[1]));
										p.sendMessage(ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.GREEN + "You successfully payed " + ChatColor.DARK_GREEN + args[1] + " Coins" + ChatColor.GREEN + "!");
										
										for (Player op : Bukkit.getOnlinePlayers()) {
											
											if (op.getName().equalsIgnoreCase(args[0])) {
												
												op.sendMessage(ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.GREEN + "You received " + ChatColor.DARK_GREEN + args[1] + " Coins" + ChatColor.GREEN + " from " + ChatColor.DARK_GREEN + name + ChatColor.GREEN + "!");
												break;
											}
										}
										
									} else {
										
										p.sendMessage(ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.RED + "You do not have enough coins!");
									}
									
								} catch (NumberFormatException e) {
									
									p.sendMessage(ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.RED + "Please enter a valid amount!");
									
								}
								
							} else {
								
								p.sendMessage(ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.RED + "This player does not exist!");
							}
						}
					});
					
				} else {
					
					sender.sendMessage(ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.RED + "Your are not allowed to use this command!");
				}
				return true;
			}
			
		} else {
			
			sender.sendMessage("[Coins] This command can not be used from console! Use '/coins help' to see alternative commands!");
			return true;
		}
		return false;
	}
}
