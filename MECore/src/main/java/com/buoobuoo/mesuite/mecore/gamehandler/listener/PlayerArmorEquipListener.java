package com.buoobuoo.mesuite.mecore.gamehandler.listener;

import com.buoobuoo.mesuite.mecore.gamehandler.event.PlayerArmorEquipEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayerArmorEquipListener implements Listener {

    private final List<String> blockedMaterials;

    public PlayerArmorEquipListener(List<String> blockedMaterials){
        this.blockedMaterials = blockedMaterials;
    }
    //Event Priority is highest because other plugins might cancel the events before we check.

    @EventHandler(priority =  EventPriority.HIGHEST, ignoreCancelled = true)
    public final void inventoryClick(final InventoryClickEvent e){
        boolean shift = false, numberkey = false;
        if(e.isCancelled()) return;
        if(e.getAction() == InventoryAction.NOTHING) return;// Why does this get called if nothing happens??
        if(e.getClick().equals(ClickType.SHIFT_LEFT) || e.getClick().equals(ClickType.SHIFT_RIGHT)){
            shift = true;
        }
        if(e.getClick().equals(ClickType.NUMBER_KEY)){
            numberkey = true;
        }
        if(e.getSlotType() != InventoryType.SlotType.ARMOR && e.getSlotType() != InventoryType.SlotType.QUICKBAR && e.getSlotType() != InventoryType.SlotType.CONTAINER) return;
        if(e.getClickedInventory() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) return;
        if (!e.getInventory().getType().equals(InventoryType.CRAFTING) && !e.getInventory().getType().equals(InventoryType.PLAYER)) return;
        if(!(e.getWhoClicked() instanceof Player)) return;
        PlayerArmorEquipEvent.ArmorType newArmorType = PlayerArmorEquipEvent.ArmorType.matchType(shift ? e.getCurrentItem() : e.getCursor());
        if(!shift && newArmorType != null && e.getRawSlot() != newArmorType.getSlot()){
            // Used for drag and drop checking to make sure you aren't trying to place a helmet in the boots slot.
            return;
        }
        if(shift){
            newArmorType = PlayerArmorEquipEvent.ArmorType.matchType(e.getCurrentItem());
            if(newArmorType != null){
                boolean equipping = true;
                if(e.getRawSlot() == newArmorType.getSlot()){
                    equipping = false;
                }
                if(newArmorType.equals(PlayerArmorEquipEvent.ArmorType.HELMET) && (equipping ? isAirOrNull(e.getWhoClicked().getInventory().getHelmet()) : !isAirOrNull(e.getWhoClicked().getInventory().getHelmet())) || newArmorType.equals(PlayerArmorEquipEvent.ArmorType.CHESTPLATE) && (equipping ? isAirOrNull(e.getWhoClicked().getInventory().getChestplate()) : !isAirOrNull(e.getWhoClicked().getInventory().getChestplate())) || newArmorType.equals(PlayerArmorEquipEvent.ArmorType.LEGGINGS) && (equipping ? isAirOrNull(e.getWhoClicked().getInventory().getLeggings()) : !isAirOrNull(e.getWhoClicked().getInventory().getLeggings())) || newArmorType.equals(PlayerArmorEquipEvent.ArmorType.BOOTS) && (equipping ? isAirOrNull(e.getWhoClicked().getInventory().getBoots()) : !isAirOrNull(e.getWhoClicked().getInventory().getBoots()))){
                    PlayerArmorEquipEvent armorEquipEvent = new PlayerArmorEquipEvent((Player) e.getWhoClicked(), PlayerArmorEquipEvent.EquipMethod.SHIFT_CLICK, newArmorType, equipping ? null : e.getCurrentItem(), equipping ? e.getCurrentItem() : null);
                    Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
                    if(armorEquipEvent.isCancelled()){
                        e.setCancelled(true);
                    }
                }
            }
        }else{
            ItemStack newArmorPiece = e.getCursor();
            ItemStack oldArmorPiece = e.getCurrentItem();
            if(numberkey){
                if(e.getClickedInventory().getType().equals(InventoryType.PLAYER)){// Prevents shit in the 2by2 crafting
                    // e.getClickedInventory() == The players inventory
                    // e.getHotBarButton() == key people are pressing to equip or unequip the item to or from.
                    // e.getRawSlot() == The slot the item is going to.
                    // e.getSlot() == Armor slot, can't use e.getRawSlot() as that gives a hotbar slot ;-;
                    ItemStack hotbarItem = e.getClickedInventory().getItem(e.getHotbarButton());
                    if(!isAirOrNull(hotbarItem)){// Equipping
                        newArmorType = PlayerArmorEquipEvent.ArmorType.matchType(hotbarItem);
                        newArmorPiece = hotbarItem;
                        oldArmorPiece = e.getClickedInventory().getItem(e.getSlot());
                    }else{// Unequipping
                        newArmorType = PlayerArmorEquipEvent.ArmorType.matchType(!isAirOrNull(e.getCurrentItem()) ? e.getCurrentItem() : e.getCursor());
                    }
                }
            }else{
                if(isAirOrNull(e.getCursor()) && !isAirOrNull(e.getCurrentItem())){// unequip with no new item going into the slot.
                    newArmorType = PlayerArmorEquipEvent.ArmorType.matchType(e.getCurrentItem());
                }
                // e.getCurrentItem() == Unequip
                // e.getCursor() == Equip
                // newArmorType = ArmorType.matchType(!isAirOrNull(e.getCurrentItem()) ? e.getCurrentItem() : e.getCursor());
            }
            if(newArmorType != null && e.getRawSlot() == newArmorType.getSlot()){
                PlayerArmorEquipEvent.EquipMethod method = PlayerArmorEquipEvent.EquipMethod.PICK_DROP;
                if(e.getAction().equals(InventoryAction.HOTBAR_SWAP) || numberkey) method = PlayerArmorEquipEvent.EquipMethod.HOTBAR_SWAP;
                PlayerArmorEquipEvent armorEquipEvent = new PlayerArmorEquipEvent((Player) e.getWhoClicked(), method, newArmorType, oldArmorPiece, newArmorPiece);
                Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
                if(armorEquipEvent.isCancelled()){
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority =  EventPriority.HIGHEST)
    public void playerInteractEvent(PlayerInteractEvent e){
        if(e.useItemInHand().equals(Event.Result.DENY))return;
        //
        if(e.getAction() == Action.PHYSICAL) return;
        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
            Player player = e.getPlayer();
            if(!e.useInteractedBlock().equals(Event.Result.DENY)){
                if(e.getClickedBlock() != null && e.getAction() == Action.RIGHT_CLICK_BLOCK && !player.isSneaking()){// Having both of these checks is useless, might as well do it though.
                    // Some blocks have actions when you right click them which stops the client from equipping the armor in hand.
                    Material mat = e.getClickedBlock().getType();
                    for(String s : blockedMaterials){
                        if(mat.name().equalsIgnoreCase(s)) return;
                    }
                }
            }
            PlayerArmorEquipEvent.ArmorType newArmorType = PlayerArmorEquipEvent.ArmorType.matchType(e.getItem());
            if(newArmorType != null){
                if(newArmorType.equals(PlayerArmorEquipEvent.ArmorType.HELMET) && isAirOrNull(e.getPlayer().getInventory().getHelmet()) || newArmorType.equals(PlayerArmorEquipEvent.ArmorType.CHESTPLATE) && isAirOrNull(e.getPlayer().getInventory().getChestplate()) || newArmorType.equals(PlayerArmorEquipEvent.ArmorType.LEGGINGS) && isAirOrNull(e.getPlayer().getInventory().getLeggings()) || newArmorType.equals(PlayerArmorEquipEvent.ArmorType.BOOTS) && isAirOrNull(e.getPlayer().getInventory().getBoots())){
                    PlayerArmorEquipEvent armorEquipEvent = new PlayerArmorEquipEvent(e.getPlayer(), PlayerArmorEquipEvent.EquipMethod.HOTBAR, PlayerArmorEquipEvent.ArmorType.matchType(e.getItem()), null, e.getItem());
                    Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
                    if(armorEquipEvent.isCancelled()){
                        e.setCancelled(true);
                        player.updateInventory();
                    }
                }
            }
        }
    }

    @EventHandler(priority =  EventPriority.HIGHEST, ignoreCancelled = true)
    public void inventoryDrag(InventoryDragEvent event){
        // getType() seems to always be even.
        // Old Cursor gives the item you are equipping
        // Raw slot is the ArmorType slot
        // Can't replace armor using this method making getCursor() useless.
        PlayerArmorEquipEvent.ArmorType type = PlayerArmorEquipEvent.ArmorType.matchType(event.getOldCursor());
        if(event.getRawSlots().isEmpty()) return;// Idk if this will ever happen
        if(type != null && type.getSlot() == event.getRawSlots().stream().findFirst().orElse(0)){
            PlayerArmorEquipEvent armorEquipEvent = new PlayerArmorEquipEvent((Player) event.getWhoClicked(), PlayerArmorEquipEvent.EquipMethod.DRAG, type, null, event.getOldCursor());
            Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
            if(armorEquipEvent.isCancelled()){
                event.setResult(Event.Result.DENY);
                event.setCancelled(true);
            }
        }
        // Debug shit
		/*System.out.println("Slots: " + event.getInventorySlots().toString());
		System.out.println("Raw Slots: " + event.getRawSlots().toString());
		if(event.getCursor() != null){
			System.out.println("Cursor: " + event.getCursor().getType().name());
		}
		if(event.getOldCursor() != null){
			System.out.println("OldCursor: " + event.getOldCursor().getType().name());
		}
		System.out.println("Type: " + event.getType().name());*/
    }

    @EventHandler
    public void itemBreakEvent(PlayerItemBreakEvent e){
        PlayerArmorEquipEvent.ArmorType type = PlayerArmorEquipEvent.ArmorType.matchType(e.getBrokenItem());
        if(type != null){
            Player p = e.getPlayer();
            PlayerArmorEquipEvent armorEquipEvent = new PlayerArmorEquipEvent(p, PlayerArmorEquipEvent.EquipMethod.BROKE, type, e.getBrokenItem(), null);
            Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
            if(armorEquipEvent.isCancelled()){
                ItemStack i = e.getBrokenItem().clone();
                i.setAmount(1);
                i.setDurability((short) (i.getDurability() - 1));
                if(type.equals(PlayerArmorEquipEvent.ArmorType.HELMET)){
                    p.getInventory().setHelmet(i);
                }else if(type.equals(PlayerArmorEquipEvent.ArmorType.CHESTPLATE)){
                    p.getInventory().setChestplate(i);
                }else if(type.equals(PlayerArmorEquipEvent.ArmorType.LEGGINGS)){
                    p.getInventory().setLeggings(i);
                }else if(type.equals(PlayerArmorEquipEvent.ArmorType.BOOTS)){
                    p.getInventory().setBoots(i);
                }
            }
        }
    }

    @EventHandler
    public void playerDeathEvent(PlayerDeathEvent e){
        Player p = e.getEntity();
        if(e.getKeepInventory()) return;
        for(ItemStack i : p.getInventory().getArmorContents()){
            if(!isAirOrNull(i)){
                Bukkit.getServer().getPluginManager().callEvent(new PlayerArmorEquipEvent(p, PlayerArmorEquipEvent.EquipMethod.DEATH, PlayerArmorEquipEvent.ArmorType.matchType(i), i, null));
                // No way to cancel a death event.
            }
        }
    }

    /**
     * A utility method to support versions that use null or air ItemStacks.
     */
    public static boolean isAirOrNull(ItemStack item){
        return item == null || item.getType().equals(Material.AIR);
    }
}