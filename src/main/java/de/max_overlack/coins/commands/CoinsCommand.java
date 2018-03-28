package de.max_overlack.coins.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.max_overlack.coins.Coins;
import de.max_overlack.coins.CoinsAPI;
import de.max_overlack.coins.MySQLResponseReceiveListener;

public class CoinsCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		try {
			
			final CoinsAPI api = Coins.getInstance().getAPI();
			
			switch (args.length) {
			
			case 0:
				
				if (sender.hasPermission("coins.see.coins")) {
					
					if (sender instanceof Player) {
						
						final Player p = (Player) sender;
						
						api.getCoinsAsynchronously(p.getName(), new MySQLResponseReceiveListener<Long>() {
							
							@Override
							public void onMySQLResponseReceived(Long result) {
								
								p.sendMessage(ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.GREEN + "You currently have " + ChatColor.DARK_GREEN + result + " Coins" + ChatColor.GREEN + "!");
							}
						});
						
					} else {
						
						sender.sendMessage("[Coins] This command can not be used from console! Use '/coins <player>'!");
					}
					
				} else {
					
					sender.sendMessage(ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.RED + "Your are not allowed to use this command!");
				}
				break;
				
			case 1:
				
				if (args[0].equalsIgnoreCase("help")) {
					
					this.showHelp(sender);
					
				} else if (sender.hasPermission("coins.see.coins.others")) {
					
					api.doesUsernameExistAsynchronously(args[0], new MySQLResponseReceiveListener<Boolean>() {

						@Override
						public void onMySQLResponseReceived(Boolean result) {
							
							if (result) {
								
								sender.sendMessage(ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.GREEN + "This player currently has " + ChatColor.DARK_GREEN + api.getCoins(args[0]) + " Coins" + ChatColor.GREEN + "!");
								
							} else {
								
								sender.sendMessage(ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.RED + "This player does not exist!");
							}
						}
					});
					
				} else {
					
					sender.sendMessage(ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.RED + "Your are not allowed to use this command!");
				}
				break;
				
			case 2:
				
				if (args[0].equalsIgnoreCase("reset")) {
					
					if (sender.hasPermission("coins.edit.coins.others") || (((Player) sender).getName().equals(args[1]) && sender.hasPermission("coins.edit.coins"))) {
						
						api.resetCoinsAsynchronously(args[1]);
						sender.sendMessage(ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.GREEN + "The coins of that person werde successfully reset!");
						
					} else {
						
						sender.sendMessage(ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.RED + "Your are not allowed to use this command!");
					}
					
				} else {
					
					this.showHelp(sender);
				}
				break;
				
			case 3:
				
				if (args[0].equalsIgnoreCase("set")) {
					
					if (this.checkIfHasEditRights(sender, args[1])) {
						
						api.setCoinsAsynchronously(args[1], Long.parseLong(args[2]));
						sender.sendMessage(ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.GREEN + "Set coins successfully to " + ChatColor.DARK_GREEN + args[2] + " Coins" + ChatColor.GREEN + "!");
					}
					
				} else if (args[0].equalsIgnoreCase("add")) {
					
					if (this.checkIfHasEditRights(sender, args[1])) {
						
						api.addCoinsAsynchronously(args[1], Long.parseLong(args[2]));
						sender.sendMessage(ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.GREEN + "Successfully added " + ChatColor.DARK_GREEN + args[2] + " Coins" + ChatColor.GREEN + "!");
					}
					
				} else if (args[0].equalsIgnoreCase("remove")) {
					
					if (this.checkIfHasEditRights(sender, args[1])) {
						
						api.removeCoinsAsynchronously(args[1], Long.parseLong(args[2]));
						sender.sendMessage(ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.GREEN + "Successfully removed " + ChatColor.DARK_GREEN + args[2] + " Coins" + ChatColor.GREEN + "!");
					}
					
				} else {
					
					this.showHelp(sender);
				}
				break;
				
			case 4:
				
				if (args[0].equalsIgnoreCase("transfer")) {
					
					if (sender.hasPermission("coins.edit.coins.others")) {
						
						api.transferCoinsAsynchronously(args[1], args[2], Long.parseLong(args[3]));
						sender.sendMessage(ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.GREEN + "Successfully transferred " + ChatColor.DARK_GREEN + args[2] + " Coins" + ChatColor.GREEN + "!");
						
					} else {
						
						sender.sendMessage(ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.RED + "Your are not allowed to use this command!");
					}
					
				} else {
					
					this.showHelp(sender);
				}
				break;
				
			default:
				
				this.showHelp(sender);
				break;
			}
			
		} catch (NumberFormatException e) {
			
			sender.sendMessage(ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.RED + "Please enter a valid amount!");
		}
		return true;
	}
	
	private void showHelp(CommandSender sender) {
		
		sender.sendMessage(ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.GREEN + " - /coins help - \n"
				+ ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.GREEN + "/coins\n"
				+ ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.GREEN + "/coins <player>\n"
				+ ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.GREEN + "/coins transfer <player1> <player2> <amount>\n"
				+ ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.GREEN + "/coins set <player> <amount>\n"
				+ ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.GREEN + "/coins add <player> <amount>\n"
				+ ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.GREEN + "/coins remove <player> <amount>\n"
				+ ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.GREEN + "/coins reset <player>");
	}
	
	private boolean checkIfHasEditRights(CommandSender sender, String person) {
		
		if (!(sender.hasPermission("coins.edit.coins.others") || (((Player) sender).getName().equalsIgnoreCase(person) && sender.hasPermission("coins.edit.coins")))) {
			
			sender.sendMessage(ChatColor.BLUE + "[" + ChatColor.GOLD + "Coins" + ChatColor.BLUE + "] " + ChatColor.RED + "Your are not allowed to use this command!");
			return false;	
		}
		return true;
	}
}
