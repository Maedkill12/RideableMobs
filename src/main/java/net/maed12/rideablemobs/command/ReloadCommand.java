package net.maed12.rideablemobs.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class ReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("rideablemobs.reload")) {
            return false;
        }
        PluginManager pluginManager = Bukkit.getPluginManager();
        Plugin plugin = pluginManager.getPlugin("RideableMobs");
        assert plugin != null;
        pluginManager.disablePlugin(plugin);
        pluginManager.enablePlugin(plugin);
        sender.sendMessage(ChatColor.GREEN + "RideableMobs reload complete");
        return true;
    }
}
