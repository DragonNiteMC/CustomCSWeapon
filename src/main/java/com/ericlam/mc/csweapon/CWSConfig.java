package com.ericlam.mc.csweapon;

import com.hypernite.mc.hnmc.core.config.yaml.Configuration;
import com.hypernite.mc.hnmc.core.config.yaml.Resource;

import java.util.List;
import java.util.Map;

@Resource(locate = "config.yml")
public class CWSConfig extends Configuration {

    int molotov_duration;

    List<String> molotov;

    Map<String, String> scope_skin;

    List<String> flashbangs;

    int flash_radius;

    List<String> flash_bypass_blacklist;

    Map<String, Double> shotguns;

    HeadShot headshot;

    KnockBack knockback;


    static class HeadShot {
        boolean custom_sound;
        String helmet_sound;
        String no_helmet_sound;
    }

    static class KnockBack {
        boolean disable;
        Custom custom;

        static class Custom {
            boolean damage_percent;
            double value;
            double increase_rate;
        }

    }
}
