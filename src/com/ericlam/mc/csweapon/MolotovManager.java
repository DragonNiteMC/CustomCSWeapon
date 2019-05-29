package com.ericlam.mc.csweapon;

import com.hypernite.mc.hnmc.core.utils.Tools;
import com.hypernite.mc.hnmc.core.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MolotovManager {
    private static MolotovManager molotovManager;
    private HashSet<Location> fireblocks = new HashSet<>();
    private HashMap<Location,BlockData> lavaBlockCacahes = new HashMap<>();

    public static MolotovManager getInstance() {
        if (molotovManager == null) molotovManager = new MolotovManager();
        return molotovManager;
    }

    private List<Location> getRandomSpread(List<Location> circle, int amount){
        List<Location> result = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            if (i >= circle.size()) return result;
            Location loc;
            do{
                loc = circle.get(Tools.randomWithRange(0, circle.size() - 1));
            }while(result.contains(loc));
            result.add(loc);
        }
        return result;
    }

    public void spawnFires(Block hitBlock){
        final Location center = hitBlock.getLocation();
        center.getWorld().spawnParticle(Particle.FLAME,center,5,20,20,20);
        double y = center.getY();
        double x = center.getX();
        double z = center.getZ();
        HashSet<Location> fires = new HashSet<>();
        HashMap<Location, BlockData> lavas = new HashMap<>();
        int size = Tools.randomWithRange(100, 200);
        List<Location> circle = Utils.circle(center, Tools.randomWithRange(5, 10) + 1, 3, false, false, 0);
       for (Location loc : getRandomSpread(circle,size)){
           Location under;
           if (loc.getBlock().getType() == Material.FIRE) continue;
           under = loc.clone();
           under.setY(loc.getY()-1);
           if (under.getBlock().getType() == Material.AIR || loc.getBlock().getType() != Material.AIR) continue;
           loc.getBlock().setType(Material.FIRE);
           fires.add(loc);
           final BlockData original = under.getBlock().getBlockData();
           Location set = under.clone();
           lavas.put(set,original);
           if (loc.getBlock().getType() == Material.FIRE ) set.getBlock().setType(Material.MAGMA_BLOCK);
       }
       if (fires.size() == 0 ){
           return;
       }
        if (lavas.size() == 0 )return;
       fireblocks.addAll(fires);
        lavaBlockCacahes.putAll(lavas);
        Bukkit.getScheduler().runTaskLater(CustomCSWeapon.getPlugin(),()->{
            for (Location fire : fires) {
                Block fireBlock = fire.getBlock();
                if (fireBlock.getType() == Material.FIRE) fire.getBlock().setType(Material.AIR);
                fireblocks.remove(fire);
            }
            fires.clear();

        }, ConfigManager.molo_duration * 20L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(CustomCSWeapon.getPlugin(),()->{
            if (lavas.size() == 0) return;
            for (Location location : lavas.keySet()) {
                Block lavaBlock = location.getBlock();
                restore(lavas, location, lavaBlock);
                lavaBlockCacahes.remove(location);
            }
            lavas.clear();
        }, (ConfigManager.molo_duration + ConfigManager.lava_duration) * 20L);
    }

    private void restore(HashMap<Location, BlockData> lavas, Location location, Block lavaBlock) {
        if (lavaBlock.getType() == Material.MAGMA_BLOCK) {
            lavaBlock.setType(lavas.get(location).getMaterial());
            lavaBlock.setBlockData(lavas.get(location));
            Block up = lavaBlock.getRelative(BlockFace.UP);
            if (up.getType() == Material.FIRE) up.setType(Material.AIR);
        }
    }

    public void resetFires(){
        for (Location fire : fireblocks) {
            Block fireBlock = fire.getBlock();
            if (fireBlock.getType() == Material.FIRE) fire.getBlock().setType(Material.AIR);
        }
        fireblocks.clear();
    }

    public void resetLavaBlocks(){
        for (Location location : lavaBlockCacahes.keySet()) {
            Block lavaBlock = location.getBlock();
            restore(lavaBlockCacahes, location, lavaBlock);
        }
        lavaBlockCacahes.clear();
    }


}
