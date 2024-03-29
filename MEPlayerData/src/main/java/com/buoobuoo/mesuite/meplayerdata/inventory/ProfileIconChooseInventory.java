package com.buoobuoo.mesuite.meplayerdata.inventory;

import com.buoobuoo.mesuite.meinventories.CustomInventory;
import com.buoobuoo.mesuite.meplayerdata.MEPlayerDataPlugin;
import com.buoobuoo.mesuite.meplayerdata.PlayerDataManager;
import com.buoobuoo.mesuite.meplayerdata.model.ProfileData;
import com.buoobuoo.mesuite.meutils.Colour;
import com.buoobuoo.mesuite.meutils.ItemBuilder;
import com.buoobuoo.mesuite.meutils.MatRepo;
import com.buoobuoo.mesuite.meutils.unicode.CharRepo;
import com.buoobuoo.mesuite.meutils.unicode.UnicodeSpaceUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ProfileIconChooseInventory extends CustomInventory {
    private UUID uuid;
    private final MEPlayerDataPlugin plugin;

    public ProfileIconChooseInventory(MEPlayerDataPlugin plugin, Player player, UUID profileUUID) {
        super(plugin.getInventoryManager(), player, UnicodeSpaceUtil.getNeg(8) + "&r&f" + CharRepo.UI_INVENTORY_PROFILE_ICON_EDIT + UnicodeSpaceUtil.getNeg(169)
                +"&8Profile Icon Selection", 54);
        this.plugin = plugin;
        this.uuid = profileUUID;

        this.addDefaultHandler(event -> {
            if(event.getCurrentItem() == null)
                return;
            ItemStack item = event.getCurrentItem();

            PlayerDataManager dataManager = plugin.getPlayerDataManager();
            ProfileData profileData = dataManager.getProfile(uuid);

            profileData.setProfileIcon(item.getType());
            dataManager.saveProfile(profileData);

            Inventory inv = new ProfileEditInventory(plugin, player, uuid).getInventory();
            player.openInventory(inv);
        });

        this.addHandler(event -> {
            Inventory i = new ProfileEditInventory(plugin, player, uuid).getInventory();
            player.openInventory(i);
        }, 0);
    }


    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, size, Colour.format(title));
        Material mats[] = new Material[]{
                Material.WOODEN_SWORD, Material.WOODEN_AXE, Material.WOODEN_HOE, Material.WOODEN_SHOVEL, Material.WOODEN_PICKAXE
        };
        int index = 9;
        for(Material mat : mats){
            ItemStack item = new ItemBuilder(mat).name("&7Click to select as profile icon").create();
            inv.setItem(index++, item);
        }

        ItemStack back = new ItemBuilder(MatRepo.INVISIBLE).name("&7Return to profile edit").create();
        inv.setItem(0, back);

        return inv;
    }
}









































