package com.buoobuoo.mesuite.meplayerdata.model;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class PlayerData {
    private UUID ownerID;
    private List<UUID> profileIDs = new ArrayList<>();
    private int maxProfiles = 7;

    //settings
    private boolean setting_gui_sliders = false;
    private boolean setting_view_virtualplayers = true;

    //transients
    private transient UUID activeProfileID;

    public boolean isProfileCapacity(){
        return (profileIDs.size() >= maxProfiles);
    }

    public Player getPlayer(){
        return Bukkit.getPlayer(ownerID);
    }
}
