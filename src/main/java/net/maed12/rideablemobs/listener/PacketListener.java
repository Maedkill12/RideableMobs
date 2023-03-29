package net.maed12.rideablemobs.listener;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PacketListener {
    private static JavaPlugin plugin;
    private static ProtocolManager manager;

    public static void onEnable(JavaPlugin plugin) {
        PacketListener.plugin = plugin;
        PacketListener.manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new KeyboardListener(plugin));
    }
}
