package com.ericlam.mc.csweapon;

import com.shampaggon.crackshot.events.WeaponExplodeEvent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.Collection;

public class FlashBangListeners implements Listener {

    @EventHandler
    public void onFlashbang(WeaponExplodeEvent e) {
        if (!ConfigManager.getFlashbangs().contains(e.getWeaponTitle())) return;
        Location flash = e.getLocation();
        Collection<Player> nearbyPlayers = flash.getNearbyPlayers(ConfigManager.flash_radius);
        for (Player player : nearbyPlayers) {
            Location loc = player.getEyeLocation();
            Vector vec = new Vector(loc.getX() - flash.getX(), loc.getY() - flash.getY(), loc.getZ() - flash.getZ());
            double dot = loc.getDirection().dot(vec);
            BlockIterator iterator = new BlockIterator(player.getWorld(), flash.toVector(), vec, 0, (int) flash.distance(loc));
            while (iterator.hasNext()) {
                Material current = iterator.next().getType();
                if (current != Material.AIR && !ConfigManager.getFlashBypass().contains(current.toString())) return;
            }
            if (dot < 0) {
                if (player.getGameMode() == GameMode.SPECTATOR || player.getGameMode() == GameMode.CREATIVE) continue;
                if (player.hasPotionEffect(PotionEffectType.BLINDNESS))
                    player.removePotionEffect(PotionEffectType.BLINDNESS);
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 120, 255));
                player.spawnParticle(Particle.CLOUD, 30, 30, 30, 30);
            }
        }
    }
}
