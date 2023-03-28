package net.maed12.rideablemobs;

import net.maed12.rideablemobs.command.ReloadCommand;
import net.maed12.rideablemobs.listener.PacketListener;
import net.maed12.rideablemobs.listener.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class RideableMobs extends JavaPlugin {

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        PacketListener.onEnable(this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        getCommand("reload").setExecutor(new ReloadCommand());
    }

    @Override
    public void onDisable() {
    }
}
