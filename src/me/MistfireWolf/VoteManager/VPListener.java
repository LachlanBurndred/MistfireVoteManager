package me.MistfireWolf.VoteManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

public class VPListener implements Listener {
	Main configGetter;
	
	public VPListener(Main plugin)
	{
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		configGetter = plugin;
	}
	
	@EventHandler
	public void onPlayerVote(VotifierEvent e)
	{
		Log.info("VPListener Class Functioning");
		Vote vote = e.getVote();
		
		int counter = configGetter.votePartyConfig.getInt("voteParty.remainingVotes");
		counter = counter - 1;
		
		if (counter > 0) // > 1!~!@!
		{
			Log.info("One more towards VoteParty");
			configGetter.votePartyConfig.set("voteParty.remainingVotes", counter);
			configGetter.saveVPConfig();
			Bukkit.broadcastMessage(ChatColor.GOLD + "["+ChatColor.AQUA+"Vote Party"+ChatColor.GOLD+"] "+ChatColor.DARK_AQUA + vote.getUsername() + " has voted! " + ChatColor.GOLD + configGetter.votePartyConfig.getInt("voteParty.remainingVotes")+ChatColor.DARK_AQUA+ " votes until vote party!");
		}
		else if (counter == 0) // == 1!~!@!
		{
			Log.info("VoteParty Start");
			//String VotePartyTriggerPlayer = vote.getUsername().toLowerCase();
			String VotePartyCommand = configGetter.votePartyConfig.getString("voteParty.votePartyCommand"); //pcrates give {username} 2 1
			String playerWorld = configGetter.voteConfig.getString("vote.world").toLowerCase();
			
			for(Player player : Bukkit.getServer().getOnlinePlayers()) //3
			{
				String FullCommand = "";
				int x = 0;
				
				while (x < VotePartyCommand.length())
				{
					char c = VotePartyCommand.charAt(x);
					if (c == '{')
					{
						FullCommand = VotePartyCommand.replace("{username}", player.getName().toLowerCase());
					}
					x = x + 1;
				}
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rewardset" + " " + player.getName() + " " + playerWorld + " " + FullCommand);
			}
			
			/*while (y < VotePartyCommand.length())
			{
				char c = VotePartyCommand.charAt(y);
				if (c == '{')
				{
					VotePartyCommand = VotePartyCommand.replace("{username}", player.getName().toLowerCase()); //removed break;
				}
				y+=1;
			}*/
			
			Log.info("Stage 2 Vote Party");
			counter = configGetter.votePartyConfig.getInt("voteParty.maxVotes");
			
			configGetter.votePartyConfig.set("voteParty.remainingVotes", counter);
			configGetter.saveVPConfig();
			Log.info("Saved Config Reset.");
			Bukkit.broadcastMessage(ChatColor.GOLD + "["+ChatColor.AQUA+"Vote Party"+ChatColor.GOLD+"] "+ChatColor.DARK_AQUA + vote.getUsername() + " has voted!");
			Bukkit.broadcastMessage(ChatColor.GOLD + "["+ChatColor.AQUA+"Vote Party"+ChatColor.GOLD+"] "+ChatColor.DARK_AQUA + "A vote party has begun!");
		}
	}
}
