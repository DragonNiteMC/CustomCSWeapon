package com.ericlam.mc.csweapon;

import com.ericlam.mc.csweapon.api.MolotovManager;
import com.hypernite.mc.hnmc.core.utils.Tools;
import com.hypernite.mc.hnmc.core.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class MolotovManagerImpl implements MolotovManager {

    private final ConcurrentLinkedDeque<Location> fireBlocks = new ConcurrentLinkedDeque<>();
    private final CWSConfig cwsConfig;

    public MolotovManagerImpl(CWSConfig cwsConfig) {
        this.cwsConfig = cwsConfig;
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

    @Override
    public void spawnFires(Block hitBlock){
        final Location center = hitBlock.getLocation();
        center.getWorld().spawnParticle(Particle.FLAME,center,5,20,20,20);
        ConcurrentLinkedDeque<Location> fires = new ConcurrentLinkedDeque<>();
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
        }
        if (fires.size() == 0 ){
            return;
        }
        fireBlocks.addAll(fires);
        Bukkit.getScheduler().runTaskLater(CustomCSWeapon.getPlugin(CustomCSWeapon.class), () -> {
            while (!fires.isEmpty()) {
                Location fire = fires.poll();
                Block fireBlock = fire.getBlock();
                if (fireBlock.getType() == Material.FIRE) fire.getBlock().setType(Material.AIR);
                fireBlocks.remove(fire);
            }
        }, cwsConfig.molotov_duration * 20L);
    }

    @Override
    public void resetFires(){
        while (!fireBlocks.isEmpty()) {
            Location fire = fireBlocks.poll();
            Block fireBlock = fire.getBlock();
            if (fireBlock.getType() == Material.FIRE) fire.getBlock().setType(Material.AIR);
        }
    }


}
