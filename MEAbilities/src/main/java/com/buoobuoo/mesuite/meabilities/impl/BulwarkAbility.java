package com.buoobuoo.mesuite.meabilities.impl;

import com.buoobuoo.mesuite.meabilities.Ability;
import com.buoobuoo.mesuite.meabilities.AbilityCastType;
import com.buoobuoo.mesuite.meabilities.AbilityType;
import com.buoobuoo.mesuite.meabilities.MEAbilitiesPlugin;
import com.buoobuoo.mesuite.meplayerdata.model.ProfileData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class BulwarkAbility extends Ability {

    private static final double damageDecrease = 20;
    private static final double slowDecrease = 20;
    private static final double durationSeconds = 2;

    public BulwarkAbility() {
        super(AbilityType.STRENGTH, 5, 80, "BULWARK", "&fBulwark", "&r&7Decrease damage taken by &f" + damageDecrease + "%&7,", "&r&7but slows you by &f" + slowDecrease + "% &7for &f" + durationSeconds + "s", "", "&r&7Cannot be cast while the &fBulwark &7", "&r&7status effect is active");
    }

    @Override
    public void onCast(MEAbilitiesPlugin plugin, Player player, Entity ent, double effectiveness) {
        ProfileData profileData = plugin.getPlayerDataManager().getProfile(player);
      //  profileData.getStatusEffects().add(new BulwarkStatusEffect(plugin, profileData, (int) Math.ceil(durationSeconds*effectiveness*20d), damageDecrease, slowDecrease));
    }

    @Override
    public String[] getLore(AbilityCastType type){
        double effectiveness = type.getEffectiveness();
        double damageVal = damageDecrease * effectiveness;
        double slowVal = slowDecrease / effectiveness;
        double durationVal = durationSeconds * effectiveness;

        String[] lore = new String[]{
                "&r&7Decrease damage taken by &f" + String.format("%.2f", damageVal)  + "%&7,", "&r&7but slows you by &f" + String.format("%.2f", slowVal) + "% &7for &f" + String.format("%.2f", durationVal) + "s", "", "&r&7Cannot be cast while the &fBulwark &7", "&r&7status effect is active"
        };

        return super.getLore(type, lore);
    }

    @Override
    public boolean canCast(ProfileData profileData, double effectiveness){
       // if(StatusEffect.hasStatusEffect(profileData, BulwarkStatusEffect.class))
       //     return false;

        return true;
    }
}




























