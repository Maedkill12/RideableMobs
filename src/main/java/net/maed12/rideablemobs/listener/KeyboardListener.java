package net.maed12.rideablemobs.listener;


import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.maed12.rideablemobs.util.Util;
import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class KeyboardListener extends PacketAdapter {
    public KeyboardListener(Plugin plugin) {
        super(plugin, PacketType.Play.Client.STEER_VEHICLE);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        Player player = event.getPlayer();
        if (!Util.isWorldEnabled(player)) {
            return;
        }
        Entity vehicle = player.getVehicle();
        if (vehicle == null || vehicle instanceof Vehicle) {
            return;
        }
        PacketContainer packet = event.getPacket();
        float sideways = packet.getFloat().read(0);
        float forward = packet.getFloat().read(1);
        boolean jump = packet.getBooleans().read(0);
        boolean unmount = packet.getBooleans().read(1);

        if (unmount) {
            return;
        }

        Location playerLocation = player.getLocation();

        float yaw = playerLocation.getYaw();
        float pitch = playerLocation.getPitch();

        vehicle.setRotation(yaw, pitch);

        double radians = Math.toRadians(yaw);
        double x = -forward * Math.sin(radians) + sideways * Math.cos(radians);
        double z = forward * Math.cos(radians) + sideways * Math.sin(radians);

        Vector velocity = new Vector(x, 0, z).normalize().multiply(0.5);

        velocity.setY(vehicle.getVelocity().getY());

        if (!Double.isFinite(velocity.getX())) {
            velocity.setX(0);
        }
        if (!Double.isFinite(velocity.getZ())) {
            velocity.setZ(0);
        }

        if (vehicle.isInWater() && !Util.canSwim(vehicle) && !vehicle.isOnGround()) {
            velocity.setY(-0.08);
        }

        if (Util.canFly(vehicle) && !vehicle.isOnGround()) {
            velocity.setY(-0.08);
        }

        if (jump) {
            if (Util.canFly(vehicle) || (Util.canSwim(vehicle) && vehicle.isInWater()) || vehicle.isOnGround()) {
                velocity.setY(0.5);
            }
        }

        try {
            velocity.checkFinite();
            if (vehicle instanceof EnderDragon enderDragon) {
                enderDragon.setRotation(yaw + 180, pitch);
            }
            vehicle.setVelocity(velocity);

        } catch (Exception ignored) {

        }
    }
}
