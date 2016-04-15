package me.MistfireWolf.VoteManager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
	
	Main configGetter;

	public PlayerListener(Main plugin)
	{
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		configGetter = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		//event.setJoinMessage(ChatColor.GOLD + "Test1, do not need if statement.");
		if (player.hasPlayedBefore())
		{
			configGetter.log.info("PLAYER HAS JOINED BEFORE");
			if (configGetter.getConfig().contains(player.getName().toLowerCase() + ".worlds." + player.getWorld().getName().toLowerCase()))
			{
				configGetter.log.info("PLAYER EXISTS");
				if (configGetter.getConfig().getStringList(player.getName().toLowerCase() + ".worlds." + player.getWorld().getName().toLowerCase()).isEmpty())
				{
					
				}
				else
				{
					player.sendMessage(ChatColor.GOLD + "["+ChatColor.AQUA+"Rewards"+ChatColor.GOLD+"] "+ChatColor.DARK_AQUA + "You have pending rewards for this world.");
					player.sendMessage(ChatColor.DARK_AQUA + "Type " + ChatColor.GREEN + ChatColor.BOLD + "/claim" +ChatColor.DARK_AQUA + " to get them.");
				}
			}
			else
			{
				configGetter.log.info("PLAYER DOES NOT EXIST");
				List<String> temp = new ArrayList<String>();
				configGetter.getConfig().set(player.getName().toLowerCase() + ".worlds." + player.getWorld().getName().toLowerCase(), temp);
				configGetter.saveConfig();
			}
		}
		else if (!player.hasPlayedBefore())
		{
			List<String> temp = new ArrayList<String>();
			configGetter.getConfig().set(player.getName().toLowerCase() + ".worlds." + player.getWorld().getName().toLowerCase(), temp);
			configGetter.saveConfig();
		}
		else
		{
			return;
		}
		
	}
	
	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event)
	{
		Player player = event.getPlayer();
		//event.setJoinMessage(ChatColor.GOLD + "Test1, do not need if statement.");
		if (configGetter.getConfig().getStringList(player.getName().toLowerCase() + ".worlds." + player.getWorld().getName().toLowerCase()).isEmpty())
		{
			return;
		}
		else
		{
			player.sendMessage(ChatColor.GOLD + "["+ChatColor.AQUA+"Rewards"+ChatColor.GOLD+"] "+ChatColor.DARK_AQUA + "You have pending rewards for this world.");
			player.sendMessage(ChatColor.DARK_AQUA + "Type " + ChatColor.AQUA + "/claim" + ChatColor.DARK_AQUA + " to get them.");
		}
	}
	
}
