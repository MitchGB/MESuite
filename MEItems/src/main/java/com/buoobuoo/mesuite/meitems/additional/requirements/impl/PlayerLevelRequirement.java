package com.buoobuoo.mesuite.meitems.additional.requirements.impl;

import com.buoobuoo.mesuite.meitems.additional.requirements.ItemRequirement;
import com.buoobuoo.mesuite.meplayerdata.model.ProfileData;

public class PlayerLevelRequirement extends ItemRequirement {

    public PlayerLevelRequirement(double value) {
        super("Player Level", "PLAYER_LEVEL", value);
    }

    @Override
    public String itemLore(ProfileData profileData) {

        //Cross \u2715
        //Tick \u2714

        String prefix = getLorePrefix(profileData);

        return prefix + "&r &7Level Req: &f" + (int)value;
    }

    @Override
    public boolean meetsRequirement(ProfileData profileData) {
        return profileData != null && !(profileData.getLevel() < value);
    }
}
