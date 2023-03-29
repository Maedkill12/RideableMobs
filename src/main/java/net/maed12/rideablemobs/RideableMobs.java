package net.maed12.rideablemobs;

import net.maed12.rideablemobs.listener.PacketListener;
import net.maed12.rideablemobs.listener.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class RideableMobs extends JavaPlugin {

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        PacketListener.onEnable(this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    @Override
    public void onDisable() {
    }
}
