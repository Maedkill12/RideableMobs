package net.maed12.rideablemobs;

import net.maed12.rideablemobs.command.RideableMobsCommand;
import net.maed12.rideablemobs.listener.PacketListener;
import net.maed12.rideablemobs.listener.PlayerListener;
import net.maed12.rideablemobs.util.Util;
import org.bukkit.plugin.java.JavaPlugin;

public final class RideableMobs extends JavaPlugin {

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        Util.onEnable(this);

        RideableMobsCommand rideableMobsCommand = new RideableMobsCommand(this);
        getCommand("rideablemobs").setExecutor(rideableMobsCommand);
        getCommand("rideablemobs").setTabCompleter(rideableMobsCommand);

        PacketListener.onEnable(this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    @Override
    public void onDisable() {
    }
}
