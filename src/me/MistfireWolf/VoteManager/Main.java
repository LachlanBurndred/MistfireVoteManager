package me.MistfireWolf.VoteManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin 
{
	
	// Setting variable log to shorthand of - Bukkit.getLogger() function
	Logger log = Bukkit.getLogger();
	
	// Creating accessible instance of voteCommand.yml
	public File voteCommandFile = new File(getDataFolder(), "voteCommand.yml");
	public FileConfiguration voteConfig = YamlConfiguration.loadConfiguration(voteCommandFile);
	
	// Creating accessible instance of votePartyConfig.yml
	public File votePartyFile = new File(getDataFolder(), "votePartyConfig.yml"); 
	public FileConfiguration votePartyConfig = YamlConfiguration.loadConfiguration(votePartyFile);

	// Actions when the plugin is first loaded by the server.
	@Override
	public void onEnable()
	{
		log.info("MistfireVoteManager has been started");
		loadConfiguration();
		new PlayerListener(this);
		new VListener(this);
		new VPListener(this);
		loadCMDConfig();
		loadVPConfig();
	}
	
	// Actions when the plugin is disabled on the server.
	@Override
	public void onDisable()
	{
		log.info("MistfireVoteManager has been stopped.");
		loadConfiguration();
		loadCMDConfig();
		loadVPConfig();
	}
	
	// Saves voteCommand.yml
	public void saveCMDConfig()
	{
		try
		{
			voteConfig.save(voteCommandFile);
		}
		catch(Exception e)
		{
			return;
		}
	}
	
	// Loads voteCommand.yml
	public void loadCMDConfig()
	{
		if(voteCommandFile.exists())
		{
			try
			{
				voteConfig.load(voteCommandFile);
			}
			catch (Exception e)
			{
				return;
			}
		}
		else
		{
			try
			{
				voteConfig.set("vote.world", "world");
				voteConfig.set("vote.command", "crates give {username} vote 1");
				voteConfig.save(voteCommandFile);
			}
			catch (Exception e)
			{
				return;
			}
		}
	}
	
	// Saves votePartyConfig.yml
	public void saveVPConfig()
	{
		try
		{
			votePartyConfig.save(votePartyFile);
		}
		catch(Exception e)
		{
			return;
		}
	}
	
	// Loads votePartyConfig.yml
	public void loadVPConfig()
	{
		if(votePartyFile.exists())
		{
			try
			{
				votePartyConfig.load(votePartyFile);
			}
			catch (Exception e)
			{
				return;
			}
		}
		else
		{
			try
			{
				votePartyConfig.set("voteParty.maxVotes", 40);
				votePartyConfig.set("voteParty.remainingVotes", 40);
				votePartyConfig.set("voteParty.votePartyCommand", "crates give {username} bonus 1");
				votePartyConfig.save(votePartyFile);
			}
			catch (Exception e)
			{
				return;
			}
		}
	}

	// Loads config.yml
	public void loadConfiguration()
	{
		getConfig();
		saveConfig();
	}
	
	// Main
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[])
	{
		
		if(commandLabel.equalsIgnoreCase("rewardset"))
		{
			SetReward(sender, args);
		}
		
		if (commandLabel.equalsIgnoreCase("vp") || commandLabel.equalsIgnoreCase("voteparty"))
		{
			DisplayVoteCount(sender);
		}
		
		if (commandLabel.equalsIgnoreCase("claim"))
		{
			ClaimPlayerReward(sender);
		}
		
		if (commandLabel.equalsIgnoreCase("rewardreset"))
		{
			ResetPlayerRewards(sender, args);
		}
		return true;
	}
	// END Main
	
	// Resets a specific player's reward bank to contain 0 rewards.
	@SuppressWarnings("deprecation")
	private void ResetPlayerRewards(CommandSender _sender, String args[])
	{
		if (_sender.hasPermission("mistfirevotemanager.rewardreset") || _sender instanceof ConsoleCommandSender)
		{
			
			try
			{
				loadConfiguration();
				String PlayerName = args[0].toLowerCase();
				String WorldToClear = args[1].toLowerCase();
				
				if(getConfig().contains(PlayerName))
				{
					if (Bukkit.getWorld(WorldToClear) != null)
					{
						if (getConfig().contains(PlayerName + "." + "worlds" + "." + WorldToClear))
						{
							List<String> dummyList = new ArrayList<String>();
							getConfig().set(PlayerName + ".worlds." + WorldToClear, dummyList);
							saveConfig();
							log.info("All rewards reset for player " + PlayerName + ".");
							try
							{
								_sender.sendMessage(ChatColor.GOLD + "["+ChatColor.AQUA+"Rewards"+ChatColor.GOLD+"] "+ChatColor.DARK_AQUA + "All rewards reset for player " + Bukkit.getPlayer(PlayerName).getName());
							}
							catch (Exception e)
							{
								_sender.sendMessage(ChatColor.GOLD + "["+ChatColor.AQUA+"Rewards"+ChatColor.GOLD+"] "+ChatColor.DARK_AQUA + "All rewards reset for player " + Bukkit.getOfflinePlayer(PlayerName).getName());
							}
						}
					}
					else
					{
						_sender.sendMessage(ChatColor.RED + "Invalid World.");
					}
				}
				else
				{
					_sender.sendMessage(ChatColor.RED + "That player does not exist within the config.");
				}
			}
			catch (Exception e)
			{
				_sender.sendMessage(ChatColor.GOLD + "["+ChatColor.AQUA+"Rewards"+ChatColor.GOLD+"] "+ChatColor.DARK_AQUA + " /rewardreset [player] [world]");
			}
			
		}
	}
	
	// Gives player all rewards in their reward bank if all checks pass.
	private void ClaimPlayerReward(CommandSender _sender)
	{
		try
		{
			if(_sender instanceof Player)
			{
				Player player = (Player)_sender;
				String playerName = player.getName().toLowerCase();
				String currentPlayerWorld = player.getWorld().getName().toLowerCase(); //gets name of world
				List<String> tempRewardsList = getConfig().getStringList(playerName + "." + "worlds" + "." + currentPlayerWorld);
					if(tempRewardsList.isEmpty() == false)
					{
						if(player.getInventory().firstEmpty() == -1)
						{
							player.sendMessage(ChatColor.GOLD + "["+ChatColor.AQUA+"Rewards"+ChatColor.GOLD+"] "+ChatColor.DARK_AQUA + "Your inventory is full, please clean it and try again.");
						}
						else
						{
							while (player.getInventory().firstEmpty() != -1) 
							{
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), tempRewardsList.get(0));
								tempRewardsList.remove(0);
								getConfig().set(playerName + "." + "worlds" + "." + currentPlayerWorld, tempRewardsList);
								saveConfig();
								if (tempRewardsList.isEmpty())
								{
									player.sendMessage(ChatColor.GOLD + "["+ChatColor.AQUA+"Rewards"+ChatColor.GOLD+"] "+ChatColor.DARK_AQUA + "All rewards redeemed!");
								}
								else
								{
									log.info("Dispatched rewards item.");
								}
							}
							if (player.getInventory().firstEmpty() == -1 && tempRewardsList.isEmpty())
							{
								player.sendMessage(ChatColor.GOLD + "["+ChatColor.AQUA+"Rewards"+ChatColor.GOLD+"] "+ChatColor.DARK_AQUA + "All rewards redeemed!");
							}
							else if (player.getInventory().firstEmpty() != -1 && tempRewardsList.isEmpty())
							{
								player.sendMessage(ChatColor.GOLD + "["+ChatColor.AQUA+"Rewards"+ChatColor.GOLD+"] "+ChatColor.DARK_AQUA + "All rewards redeemed!");
							}
							else if (player.getInventory().firstEmpty() == -1 && tempRewardsList.isEmpty() == false)
							{
								player.sendMessage(ChatColor.GOLD + "["+ChatColor.AQUA+"Rewards"+ChatColor.GOLD+"] "+ChatColor.DARK_AQUA + " We have placed as many rewards in your inventory that we can fit.");
								player.sendMessage(ChatColor.GOLD + "["+ChatColor.AQUA+"Rewards"+ChatColor.GOLD+"] "+ChatColor.DARK_AQUA + " Please clean your inventory and try again to receive your remaining rewards.");
							}
							else
							{
								log.info("There was an error giving rewards to " + player.getName());
							}
						}
					}
					else
					{
						player.sendMessage(ChatColor.GOLD + "["+ChatColor.AQUA+"Rewards"+ChatColor.GOLD+"] "+ChatColor.DARK_AQUA + "You have no pending rewards.");
					}
			}
			else
			{
				_sender.sendMessage("The console cannot claim rewards.");
				log.info("The console cannot claim rewards.");
			}
		}
		catch (Exception e)
		{
			return;
		}
	}
	
	// Displays the count of remaining votes until vote party starts.
	private void DisplayVoteCount(CommandSender _sender)
	{
		_sender.sendMessage(ChatColor.GOLD + "["+ChatColor.AQUA+"Vote Party"+ChatColor.GOLD+"] "+ChatColor.DARK_AQUA + "Votes needed for party: " + votePartyConfig.getInt("voteParty.remainingVotes"));
	}
	
	// Sets the reward to the players reward bank if all checks pass.
	private void SetReward(CommandSender _sender, String args[])
	{
		if (_sender.hasPermission("mistfirevotemanager.rewardset") || _sender instanceof ConsoleCommandSender)
		{
			loadConfiguration();
			String playerName = args[0].toLowerCase();
			String requiredWorld = args[1].toLowerCase();
			String wrappedCommand = "";
			for (int i = 2; i < args.length;i++)
			{
				wrappedCommand = wrappedCommand + args[i].toString() + " ";
			}
			
			if(getConfig().contains(playerName))
			{
				if (Bukkit.getWorld(requiredWorld) != null)
				{
					if (getConfig().contains(playerName + "." + "worlds" + "." + requiredWorld))
					{
						List<String> tempList = getConfig().getStringList(playerName + "." + "worlds" + "." + requiredWorld);
						tempList.add(wrappedCommand);
						getConfig().set(playerName + "." + "worlds" + "." + requiredWorld, tempList);
						saveConfig();
						log.info(ChatColor.RED + "Command Successfully Added " + wrappedCommand);
						try 
						{
							Player player = Bukkit.getPlayer(playerName);
							player.sendMessage(ChatColor.GOLD + "["+ChatColor.AQUA+"Rewards"+ChatColor.GOLD+"] "+ChatColor.DARK_AQUA + "You have pending rewards. " + ChatColor.DARK_AQUA + "Type " + ChatColor.GREEN + ChatColor.BOLD + "/claim" +ChatColor.DARK_AQUA + " to get them.");
						}
						catch (Exception e)
						{
							log.info("Player not online.");
						}
					}
					else
					{
						getConfig().set(playerName.toLowerCase() + "." + "worlds" + "." + requiredWorld.toLowerCase(), Arrays.asList(wrappedCommand));
						log.info("Player Default and Command Successfully created.");
						saveConfig();
					}
				}
				else
				{
					log.info("The world specified in the command is not a valid world name.");
					_sender.sendMessage(ChatColor.RED + "The world specified in the command is not a valid world name");
				}
			}
			else if (!getConfig().contains(playerName.toLowerCase()))
			{
				_sender.sendMessage("Player " + args[0] + " has never played before");
				log.info("Player " + args[0] + " has never played before");
			}
			else
			{
				log.info("CHECK 1");
				log.info("Something went wrong upon giving reward to " + playerName);
			}
		}
		else
		{
			_sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
		}
	}
	
}