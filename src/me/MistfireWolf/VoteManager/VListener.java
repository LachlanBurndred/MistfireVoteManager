package me.MistfireWolf.VoteManager;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

public class VListener implements Listener {
	
	Main configGetter;
	
	public VListener(Main plugin)
	{
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		configGetter = plugin;
	}
	
	@EventHandler
	public void onPlayerVote(VotifierEvent e)
	{
		Vote vote = e.getVote();
		
		if (configGetter.getConfig().contains(vote.getUsername().toLowerCase()))
		{
			
			String playerWorld = configGetter.voteConfig.getString("vote.world").toLowerCase();
			String playerName = vote.getUsername().toLowerCase();
			
			if (configGetter.getConfig().contains(playerName.toLowerCase() + ".worlds." + playerWorld.toLowerCase()))
			{
				String desiredWorld = configGetter.voteConfig.getString("vote.world").toLowerCase();
				String desiredCommand = configGetter.voteConfig.getString("vote.command").toLowerCase();
				int i = 0;
				
				while (i < desiredCommand.length())
				{
					char c = desiredCommand.charAt(i);
					if (c == '{')
					{
						desiredCommand = desiredCommand.replace("{username}", vote.getUsername().toLowerCase());
					}
					else
					{
						i+=1;
					}
				}
				String fullCommand = "rewardset " + vote.getUsername().toLowerCase() + " " + desiredWorld + " " + desiredCommand.toLowerCase();
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), fullCommand);
				Log.info("command executed: " + fullCommand);
			}
			else
			{
				Log.info("A player who has never joined voted. Username: " + vote.getUsername());
			}
		}
		else
		{
			Log.info("A player who has never joined voted. Username: " + vote.getUsername());
		}
	}
}
