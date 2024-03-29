package com.buoobuoo.mesuite.meutils;


import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.profile.PlayerProfile;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class ItemBuilder {
    private ItemStack item;
    private Map<Integer, List<String>> loreTierMap = new HashMap<>();

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        ItemMeta meta = this.item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
    }

    public ItemBuilder(ItemStack item) {
        this.item = item;
        ItemMeta meta = this.item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
    }

    public ItemBuilder(OfflinePlayer skullOwner) {
        this.item = (new ItemBuilder(Material.PLAYER_HEAD)).skullOwner(skullOwner).create();
        ItemMeta meta = this.item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
    }

    public ItemBuilder(MatRepo mat){
        this.item = new ItemStack(mat.getMat());
        setCustomModelData(mat.getCustomModelData());
        ItemMeta meta = this.item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
    }

    public ItemBuilder amount(int amount) {
        this.item.setAmount(amount);
        return this;
    }

    public ItemBuilder name(String name) {
        ItemMeta meta = this.item.getItemMeta();
        meta.setDisplayName(Color.format(name));
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setMaterial(Material mat){
        this.item.setType(mat);
        return this;
    }

    public ItemBuilder nbtString(String id, String val){
        ItemMeta meta = this.item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(NamespacedKey.minecraft(id), PersistentDataType.STRING, val);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder nbtDouble(String id, double val) {
        ItemMeta meta = this.item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(NamespacedKey.minecraft(id), PersistentDataType.DOUBLE, val);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder nbtInteger(String id, int val) {
        ItemMeta meta = this.item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(NamespacedKey.minecraft(id), PersistentDataType.INTEGER, val);
        this.item.setItemMeta(meta);
        return this;
    }


    public ItemBuilder nbtInt(String id, int val) {
        ItemMeta meta = this.item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(NamespacedKey.minecraft(id), PersistentDataType.INTEGER, val);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(String... lore) {
        List<String> tierList = getLoreTier(0);
        tierList.addAll(List.of(lore));
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        List<String> tierList = getLoreTier(0);
        tierList.addAll(lore);
        return this;
    }

    public ItemBuilder lore(int tier, String... lore) {
        List<String> tierList = getLoreTier(tier);
        tierList.addAll(List.of(lore));
        return this;
    }

    public ItemBuilder lore(int tier, List<String> lore) {
        List<String> tierList = getLoreTier(tier);
        tierList.addAll(lore);
        return this;
    }

    public ItemBuilder clearLore(){
        loreTierMap.clear();
        this.item.getItemMeta().setLore(new ArrayList<>());
        return this;
    }

    public ItemBuilder skullOwner(OfflinePlayer player) {
        SkullMeta meta = (SkullMeta)this.item.getItemMeta();
        meta.setOwningPlayer(player);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder skullTexture(String base64Texture) {
        PlayerProfile profile = Bukkit.getServer().createPlayerProfile(UUID.randomUUID(), "");

        try {
            profile.getTextures().setSkin(new URL(getURLFromBase64(base64Texture)));
            SkullMeta meta = (SkullMeta) this.item.getItemMeta();
            meta.setOwnerProfile(profile);
            this.item.setItemMeta(meta);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return this;
    }

    public ItemBuilder skullTexture(String base64Texture, String sign) {

        SkullMeta headMeta = (SkullMeta) this.item.getItemMeta();

        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        profile.getProperties().put("textures", new Property("textures", base64Texture, sign));

        Field profileField = null;

        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }

        this.item.setItemMeta(headMeta);
        return this;
    }

    private String getURLFromBase64(String base64) {

        return new String(Base64.getDecoder().decode(base64.getBytes())).replace("{\"textures\":{\"SKIN\":{\"url\":\"", "").replace("\"}}}", "");
    }

    public ItemBuilder leatherArmorColor(org.bukkit.Color color) {
        LeatherArmorMeta meta = (LeatherArmorMeta)this.item.getItemMeta();
        meta.setColor(color);
        this.item.setItemMeta((ItemMeta)meta);
        return this;
    }

    public ItemBuilder enchant(Enchantment type, int level) {
        this.item.addEnchantment(type, level);
        return this;
    }

    public ItemBuilder unsafeEnchant(Enchantment type, int level) {
        this.item.addUnsafeEnchantment(type, level);
        return this;
    }

    public ItemBuilder material(Material material) {
        this.item.setType(material);
        return this;
    }

    public ItemBuilder type(Material type) {
        this.item.setType(type);
        return this;
    }

    public ItemBuilder unbreakable() {
        ItemMeta meta = this.item.getItemMeta();
        meta.setUnbreakable(true);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder damage(int damage) {
        Damageable damageable = (Damageable)this.item.getItemMeta();
        damageable.setDamage(damage);
        this.item.setItemMeta((ItemMeta)damageable);
        return this;
    }

    public ItemBuilder namePlaceholder(String key, String value) {
        if (this.item.getItemMeta() == null || this.item.getItemMeta().getDisplayName() == null)
            return this;
        return name(this.item.getItemMeta().getDisplayName().replace(key, value));
    }

    public ItemBuilder namePlaceholders(Map<String, String> placeholders) {
        if (this.item.getItemMeta() == null || this.item.getItemMeta().getDisplayName() == null)
            return this;
        placeholders.forEach(this::namePlaceholder);
        return this;
    }

    public ItemBuilder lorePlaceholder(String key, String value) {
        if (this.item.getItemMeta() == null || this.item.getItemMeta().getLore() == null)
            return this;
        return lore((List<String>)this.item.getItemMeta().getLore().stream().map(s -> s.replace(key, value)).collect(Collectors.toList()));
    }

    public ItemBuilder lorePlaceholders(Map<String, String> placeholders) {
        if (this.item.getItemMeta() == null || this.item.getItemMeta().getLore() == null)
            return this;
        placeholders.forEach(this::lorePlaceholder);
        return this;
    }

    public ItemBuilder placeholder(String key, String value) {
        return namePlaceholder(key, value).lorePlaceholder(key, value);
    }

    public ItemBuilder placeholders(Map<String, String> placeholders) {
        return namePlaceholders(placeholders).lorePlaceholders(placeholders);
    }

    public ItemBuilder setCustomModelData(int data) {
        ItemMeta meta = this.item.getItemMeta();
        meta.setCustomModelData(data);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemStack create() {
        ItemMeta meta = this.item.getItemMeta();


        int highest = 0;
        for(int i : loreTierMap.keySet()){
            highest = Math.max(highest, i);
        }

        List<String> lore = new ArrayList<>();
        for(int i = 0; i < highest+1; i++){
            List<String> list = loreTierMap.getOrDefault(i, null);
            if(list == null)
                continue;

            lore.addAll(list);
        }

        meta.setLore(Color.format(lore));
        this.item.setItemMeta(meta);
        return this.item;
    }

    private List<String> getLoreTier(int tier){
        if(loreTierMap.get(tier) == null){
            List<String> tierList = new ArrayList<>();
            loreTierMap.put(tier, tierList);
        }

        return loreTierMap.get(tier);
    }

    public ItemMeta getMetaRaw(){
        return this.item.getItemMeta();
    }

    public ItemStack getItemStackRaw(){
        return this.item;
    }
}


































