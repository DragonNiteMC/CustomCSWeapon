package com.ericlam.mc.csweapon;

import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class KnockBackRunnable extends BukkitRunnable {
    private final Vector vector;
    private final Entity Entity;
    private final double kb;

    KnockBackRunnable(Vector bullet, Entity ent, double kb2) {
        this.vector = bullet;
        this.Entity = ent;
        this.kb = kb2;
    }

    public void run() {
        if (this.Entity != null && this.vector != null) {
            this.Entity.setVelocity(this.vector.multiply(this.kb).setY(0));
        }
    }
}
