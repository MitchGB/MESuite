package com.buoobuoo.mesuite.meitems.gamehandler.listener;

import com.buoobuoo.mesuite.meitems.CustomItem;
import com.buoobuoo.mesuite.meitems.CustomItemManager;
import com.buoobuoo.mesuite.meitems.MEItemsPlugin;
import com.buoobuoo.mesuite.meitems.interf.AttributedItem;
import com.buoobuoo.mesuite.meplayerdata.gamehandler.event.PlayerLevelUpEvent;
import com.buoobuoo.mesuite.meplayerdata.model.ProfileData;
import com.buoobuoo.mesuite.meutils.Colour;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ItemRequirementEventListener implements Listener {

    private final MEItemsPlugin plugin;

    public ItemRequirementEventListener(MEItemsPlugin plugin){
        this.plugin = plugin;
    }

    private boolean canUse(Player player){
        ItemStack item = player.getInventory().getItemInMainHand();

        CustomItemManager itemManager = plugin.getCustomItemManager();
        CustomItem handler = itemManager.getRegistry().getHandler(item);
        if(handler == null)
            return true;

        if(handler instanceof AttributedItem attributedItem){
            ProfileData profileData = plugin.getPlayerDataManager().getProfile(player);
            if(attributedItem.meetsRequirement(profileData))
                return true;

            return false;
        }
        return true;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event){
        if(event.getHand() != EquipmentSlot.HAND)
            return;


        Player player = event.getPlayer();

        boolean cancelled = !canUse(player);
        event.setCancelled(cancelled);
        if(cancelled)
            player.sendMessage(Colour.format("&cYou do not meet this item's requirements"));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player player) {
            event.setCancelled(!canUse(player));
        }
    }

    //Item updates

    private void updateInventory(Player player){
        ProfileData profileData = plugin.getPlayerDataManager().getProfile(player);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            CustomItemManager itemManager = plugin.getCustomItemManager();
            ItemStack[] items = player.getInventory().getContents();
            ItemStack[] armour = player.getInventory().getArmorContents();

            for(int i = 0; i < items.length; i++){
                ItemStack item = items[i];
                if(item == null)
                    continue;

                CustomItem handler = itemManager.getRegistry().getHandler(item);
                if(handler == null)
                    continue;

                if(handler instanceof AttributedItem attributedItem){
                    items[i] = attributedItem.update(plugin, profileData, items[i]);
                }
            }

            for(int i = 0; i < armour.length; i++){
                ItemStack item = armour[i];
                if(item == null)
                    continue;

                CustomItem handler = itemManager.getRegistry().getHandler(item);
                if(handler == null)
                    continue;

                if(handler instanceof AttributedItem attributedItem){
                    armour[i] = attributedItem.update(plugin, profileData, armour[i]);
                }

            }

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.getInventory().setContents(items);
                player.getInventory().setArmorContents(armour);
            }, 1);
        });
    }

    private ItemStack updateItem(Player player, ItemStack item){
        ProfileData profileData = plugin.getPlayerDataManager().getProfile(player);
        CustomItemManager itemManager = plugin.getCustomItemManager();

        CustomItem handler = itemManager.getRegistry().getHandler(item);
        if(handler == null)
            return item;

        if(handler instanceof AttributedItem attributedItem){
            item = attributedItem.update(plugin, profileData, item);
        }
        return item;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLevelup(PlayerLevelUpEvent event){
        updateInventory(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPickup(PlayerPickupItemEvent event){
        Player player = event.getPlayer();
        ItemStack item = event.getItem().getItemStack();
        item = updateItem(player, item);

        event.getItem().setItemStack(item);
    }

}

























