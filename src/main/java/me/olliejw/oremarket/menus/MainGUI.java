package me.olliejw.oremarket.menus;

import me.olliejw.oremarket.OreMarket;
import me.olliejw.oremarket.utils.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainGUI implements Listener {
    String title = OreMarket.main().getGuiConfig().getString("gui.title");
    int rows = OreMarket.main().getGuiConfig().getInt("gui.rows");
    Inventory inv = Bukkit.createInventory(null, rows*9, ChatColor.translateAlternateColorCodes('&', title));
    Placeholders plh = new Placeholders();
    SkullMeta skullMeta;

    public void createGUI (Player player) {
        // Create a new inventory each time to avoid issues
        inv = Bukkit.createInventory(null, rows*9, ChatColor.translateAlternateColorCodes('&', title));
        
        for (String key : Objects.requireNonNull(OreMarket.main().getGuiConfig().getConfigurationSection("items")).getKeys(false)) {
            ConfigurationSection keySection = Objects.requireNonNull(OreMarket.main().getGuiConfig().getConfigurationSection("items")).getConfigurationSection(key);
            assert keySection != null;

            // Getting the item type
            String materialName = keySection.getString("item");
            assert materialName != null;
            Material material = Material.matchMaterial(materialName);
            if (material == null) {
                // If material is not found, default to stone
                material = Material.STONE;
                OreMarket.main().getLogger().warning("Invalid material: " + materialName + " for item " + key);
            }
            ItemStack item = new ItemStack(material);

            // Getting the item meta
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            String name = keySection.getString("name");

            // Get the lore from config
            List<String> lore = new ArrayList<>();
            for (String loreItem : Objects.requireNonNull(keySection.getStringList("lore"))) {
                String string = plh.format(loreItem, player, keySection);
                lore.add(string);
            }

            // Set values
            if (name == null) {
                // If a name is not given, we will make it blank
                name = " ";
            }

            meta.setDisplayName(plh.format(name, player, keySection));
            meta.setLore(lore);
            item.setItemMeta(meta);

            // Player head?
            if (materialName.equals("PLAYER_HEAD") || materialName.equals("PLAYER_HEAD")) {
                if (meta instanceof SkullMeta) {
                    skullMeta = (SkullMeta) meta;
                    String skullID = keySection.getString("head");
                    if (skullID != null) {
                        Player skullOwner = Bukkit.getPlayer(skullID);
                        if (skullOwner != null) {
                            skullMeta.setOwningPlayer(skullOwner);
                            skullMeta.setLore(lore);
                            skullMeta.setDisplayName(plh.format(name, player, keySection));
                            item.setItemMeta(skullMeta);
                        }
                    }
                }
            }

            // Set items
            try {
                int slot = Integer.parseInt(key);
                inv.setItem(slot, item);
            } catch (NumberFormatException e) {
                OreMarket.main().getLogger().warning("Invalid slot number: " + key);
            }
        }
        
        // Open GUI after all items are set
        player.openInventory(inv);
    }
    public SkullMeta getSkullMeta() {
        return skullMeta;
    }
}

