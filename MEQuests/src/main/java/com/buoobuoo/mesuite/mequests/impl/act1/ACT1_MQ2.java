package com.buoobuoo.mesuite.mequests.impl.act1;

import com.buoobuoo.mesuite.mecore.MECorePlugin;
import com.buoobuoo.mesuite.meentities.EntityManager;
import com.buoobuoo.mesuite.meentities.impl.ItemDropEntity;
import com.buoobuoo.mesuite.meentities.impl.npc.AramoreBlacksmithNpc;
import com.buoobuoo.mesuite.meitems.CustomItemManager;
import com.buoobuoo.mesuite.meitems.CustomItems;
import com.buoobuoo.mesuite.meitems.impl.quest.act1.ACT1_MQ2_NecklaceItem;
import com.buoobuoo.mesuite.mequests.MEQuestsPlugin;
import com.buoobuoo.mesuite.mequests.QuestLine;
import com.buoobuoo.mesuite.mequests.gamehandler.event.ACT1_MQ2PickItemEvent;
import com.buoobuoo.mesuite.mequests.inventory.questrelated.ACT1_MQ2Inventory;
import com.buoobuoo.mesuite.meutils.unicode.CharRepo;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ACT1_MQ2 extends QuestLine {
    public ACT1_MQ2(MEQuestsPlugin plugin) {
        super(plugin, "Foul Play", "ACT1_MQ2", "");

        whenNpcInteract(AramoreBlacksmithNpc.class);
        dialogueNext(CharRepo.UI_PORTRAIT_ARAMORE_BLACKSMITH, "Blacksmith \n ...I don't recognise you.");
        dialogueNext(CharRepo.UI_PORTRAIT_ARAMORE_BLACKSMITH, "Blacksmith \n Ah, Jayce sent you? Well, sorry to say but you aren't getting gear for free. Jayce still owes me for an axe, 4 weeks overdue");
        dialogueNext(CharRepo.UI_PORTRAIT_ARAMORE_BLACKSMITH, "Blacksmith \n However, there is something you can do for me... tel you what; a week ago, an expedition party of ours headed North and we haven't heard from them since.");
        dialogueNext(CharRepo.UI_PORTRAIT_ARAMORE_BLACKSMITH, "Blacksmith \n In exchange for a weapon, I need you to go and investigate. The rest of us are busy at work, but we're starting to get worried.");
        checkpoint();
        execute(player -> {
           Inventory inv = new ACT1_MQ2Inventory(plugin, player).getInventory();
           player.openInventory(inv);
        });
        when("PICKED_ITEM");
        checkpoint("&7Find the missing expedition");
        execute(player -> {
            Location spawnLoc = new Location(MECorePlugin.getMainWorld(), 83.5,  66, -161.5);

            EntityManager entityManager = plugin.getEntityManager();
            CustomItemManager customItemManager = plugin.getCustomItemManager();

            ItemStack itemStack = customItemManager.getItem(null, CustomItems.getHandler("ACT1_MQ3_NECKLACE"));
            ItemDropEntity necklace = (ItemDropEntity) entityManager.instantiateEntity(ItemDropEntity.class, spawnLoc, new Class<?>[]{ItemStack.class}, itemStack);

            Entity bukkitEntity = necklace.asEntity().getBukkitEntity();

            bukkitEntity.setGravity(false);
            bukkitEntity.setVelocity(new Vector(0, 0, 0));
            bukkitEntity.setCustomName(itemStack.getItemMeta().getDisplayName());
            bukkitEntity.setCustomNameVisible(true);

            necklace.setInvertHide(true);
            necklace.hideToPlayer(player);
            entityManager.spawnInstance(necklace, spawnLoc);
        });
        whenPickupCustomItem(ACT1_MQ2_NecklaceItem.class);
        execute(p -> {});
        checkpoint("&7Return to the Blacksmith in Aramore.");
        whenNpcInteract(AramoreBlacksmithNpc.class);
        dialogueNext(CharRepo.UI_PORTRAIT_ARAMORE_BLACKSMITH, "Blacksmith \n You're back! Did you find anything?");
        dialogueNext(CharRepo.UI_PORTRAIT_ARAMORE_BLACKSMITH, "Blacksmith \n So... they're all dead?");
        dialogueNext(CharRepo.UI_PORTRAIT_ARAMORE_BLACKSMITH, "Blacksmith \n  ...");
        dialogueNext(CharRepo.UI_PORTRAIT_ARAMORE_BLACKSMITH, "Blacksmith \n  I... No... Thank you for helping us.");
        dialogueNext(CharRepo.UI_PORTRAIT_ARAMORE_BLACKSMITH, "Blacksmith \n There were some people around town who wanted to see you");
        finish();


    }

    @EventHandler
    public void pickItem(ACT1_MQ2PickItemEvent event){
        setDeterminant(event.getPlayer(), "PICKED_ITEM", true);
    }
}
