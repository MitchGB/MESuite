package com.buoobuoo.mesuite.mechat.gamehandler.listener;

import com.buoobuoo.mesuite.mechat.ChatManager;
import com.buoobuoo.mesuite.mechat.ChatRestrictionMode;
import com.buoobuoo.mesuite.mechat.MEChatPlugin;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;

public class PlayerChatPacketListener {
    private final MEChatPlugin pluginE;

    public PlayerChatPacketListener(MEChatPlugin pluginE){
        Arrow arrow;
        this.pluginE = pluginE;
        pluginE.getProtocolManager().addPacketListener(new PacketAdapter(
                pluginE,
                ListenerPriority.NORMAL,
                PacketType.Play.Server.SYSTEM_CHAT
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                String message = packet.getStrings().read(0);
                if(message == null)
                    return;

                int id = packet.getIntegers().read(0);

                //1 = chat message
                if(id != 1)
                    return;
                boolean override = message.contains(ChatManager.OVERRIDE_TAG);
                if(override){
                    packet.getStrings().write(0, message.replace(ChatManager.OVERRIDE_TAG,  ""));
                    return;
                }

                ChatRestrictionMode mode = pluginE.getChatManager().getChatMode(event.getPlayer());
                event.setCancelled(mode != ChatRestrictionMode.UNRESTRICTED);

                //delay to run sync
                Bukkit.getScheduler().runTask(pluginE, () -> {
                    BaseComponent[] baseComponents = ComponentSerializer.parse(message);
                    pluginE.getChatManager().receiveMessage(event.getPlayer(), baseComponents);
                });
            }
        });
    }
}


























