package net.maed12.rideablemobs.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.maed12.rideablemobs.util.Util;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class PacketListener {
    private static JavaPlugin plugin;
    private static ProtocolManager manager;

    public static void onEnable(JavaPlugin plugin) {
        PacketListener.plugin = plugin;
        PacketListener.manager = ProtocolLibrary.getProtocolManager();
        keyBoardListener();
    }

    private static void keyBoardListener() {
        manager.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Client.STEER_VEHICLE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                Entity vehicle = player.getVehicle();
                if (vehicle instanceof Vehicle || vehicle == null) {
                    return;
                }
                PacketContainer packet = event.getPacket();
                float sideways = packet.getFloat().read(0);
                float forward = packet.getFloat().read(1);
                boolean jump = packet.getBooleans().read(0);
                float yaw = player.getLocation().getYaw();
                float pitch = player.getLocation().getPitch();

                vehicle.setRotation(yaw, pitch);

                double radians = Math.toRadians(yaw);
                double x = -forward * Math.sin(radians) + sideways * Math.cos(radians);
                double z = forward * Math.cos(radians) + sideways * Math.sin(radians);

                Vector velocity = new Vector(x, vehicle.getVelocity().getY(), z).normalize().multiply(0.5);

                velocity.setY(vehicle.getVelocity().getY());

                if (vehicle.isInWater() && !Util.canSwim(vehicle) && !vehicle.isOnGround()) {
                    velocity.setY(-0.08);
                }

                if (Util.canFly(vehicle) && !vehicle.isOnGround()) {
                    velocity.setY(-0.08);
                }

                if (jump) {
                    if (Util.canFly(vehicle)) {
                        velocity.setY(0.5);
                    } else if (Util.canSwim(vehicle) && vehicle.isInWater()) {
                        velocity.setY(0.5);
                    } else if (vehicle.isOnGround()) {
                        velocity.setY(0.5);
                    }
                }
                if (Double.isFinite(velocity.getX()) && Double.isFinite(velocity.getY()) && Double.isFinite(velocity.getX())) {
                    vehicle.setVelocity(velocity);
                }
            }
        });
    }
}
