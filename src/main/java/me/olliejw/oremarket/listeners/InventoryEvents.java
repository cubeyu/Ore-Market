package me.olliejw.oremarket.listeners;

import me.olliejw.oremarket.OreMarket;
import me.olliejw.oremarket.chat.ValueUpdates;
import me.olliejw.oremarket.menus.MainGUI;
import me.olliejw.oremarket.utils.Placeholders;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class InventoryEvents implements Listener {
    private double calculateTotalWithTax(double price) {
        double tax = OreMarket.main().getConfig().getDouble("tax", 0.0);
        return price - ((price/100) * tax);
    }
    private void changePlayerBalance(double previous, HumanEntity player, boolean operation, int slot) { // Buy
        /**
         @param previous = Ore Value before any changes
         @param player = Player whose balance will be changes
         @param operation = T=Add F=Remove money
         @param slot = Slot's ore that's value will be affected
         */

        double total = calculateTotalWithTax(previous);

        if (previous > 0) {
            if (operation) {
                OreMarket.getEconomy().withdrawPlayer((OfflinePlayer) player, previous);
                OreMarket.main().getGuiConfig().set("items." + slot + ".value",
                        previous+(total*OreMarket.main().getConfig().getDouble("multiplier")));
                        // 1000 + (120 x 0.01)
                        // 1000 + 1.12
                        // 1000 -> 1001.12
            } else {
                OreMarket.getEconomy().depositPlayer((OfflinePlayer) player, total);
                OreMarket.main().getGuiConfig().set("items." + slot + ".value",
                        previous-(total*OreMarket.main().getConfig().getDouble("multiplier")));
                        // 1000 - (80 x 0.01)
                        // 1000 - 0.08
                        // 1000 -> 999.92
            }
        }
        OreMarket.main().saveGuiConfig();
        
        // 检查价格变动并发送提醒
        valueUpdates.checkAndAnnouncePriceChange(slot);
    }
    private double balance (HumanEntity player) {
        return OreMarket.getEconomy().getBalance((OfflinePlayer) player);
    }
    
    // 计算背包中可容纳指定物品的空间数量
    private int calculateFreeSpace(Inventory inventory, ItemStack item) {
        int freeSpace = 0;
        
        for (ItemStack slot : inventory.getContents()) {
            if (slot == null || slot.getType() == Material.AIR) {
                freeSpace += item.getMaxStackSize();
            } else if (slot.isSimilar(item) && slot.getAmount() < slot.getMaxStackSize()) {
                freeSpace += slot.getMaxStackSize() - slot.getAmount();
            }
        }
        
        return freeSpace;
    }

    String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(OreMarket.main().getGuiConfig().getString("gui.title")));
    Placeholders plh = new Placeholders();
    MainGUI mainGUI = new MainGUI();
    ValueUpdates valueUpdates = new ValueUpdates();

    @EventHandler
    public void clickEvent (InventoryClickEvent event) {
        Inventory playerInventory = event.getWhoClicked().getInventory(); // Player's inventory
        InventoryView playerView = event.getView(); // Player's inventory view
        HumanEntity player = event.getWhoClicked(); // Player that clicked
        Player playerObj = (Player) player;

        ConfigurationSection keySection = null;
        for (String key : Objects.requireNonNull(OreMarket.main().getGuiConfig().getConfigurationSection("items")).getKeys(false)) {
            keySection = Objects.requireNonNull(OreMarket.main().getGuiConfig().getConfigurationSection("items")).getConfigurationSection(key);
        }

        if (event.getCurrentItem() == null) { return; } // Null check. Prevents errors
        if (playerView.getTitle().equals(ChatColor.translateAlternateColorCodes('&', title))) {
            event.setCancelled(true); // I know. Its a bad way of checking.

            String itemConfig = OreMarket.main().getGuiConfig().getString("items." + event.getSlot() + ".item"); // Config location of item
            assert itemConfig != null;
            ItemStack clickedItem = new ItemStack(Objects.requireNonNull(Material.matchMaterial(itemConfig))); // Item that user clicked
            int slot = event.getSlot();

            if (OreMarket.main().getGuiConfig().contains("items." + event.getSlot() + ".commands")) { // Commands
                for (String command : Objects.requireNonNull(OreMarket.main().getGuiConfig().getStringList("items." + event.getSlot() + ".commands"))) {
                    if (command != null) {
                        assert keySection != null;
                        String toSend = plh.format(command, player, keySection);
                        if (toSend.equals("[close]")) {
                            player.closeInventory();
                            return;
                        } else if (toSend.contains("[msg]")) {
                            player.sendMessage(toSend.replace("[msg] ", ""));
                        } else {
                            Bukkit.dispatchCommand(player, toSend);
                        }
                        OreMarket.main().logToFile(playerObj.getDisplayName() + " ran command (" + toSend + ") through " + OreMarket.main().getGuiConfig().getString("items." + slot + ".name"));
                    }
                }
                return;
            }

            if ((event.getClick() == ClickType.LEFT)) { // Buy Mode
                if (OreMarket.main().getGuiConfig().getBoolean("items." + event.getSlot() + ".flags.sellonly")) {
                    // You cannot buy this, Sell only item
                    String message = OreMarket.main().getMsgConfig().getString("messages.sell-only", "&cThis item can only be sold");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    return;
                }

                double itemValue = OreMarket.main().getGuiConfig().getDouble("items." + slot + ".value");
                int itemStock = OreMarket.main().getGuiConfig().getInt("items." + slot + ".stock");

                if (itemStock == 0)  {
                    // Is there any stock?
                    return;
                }

                if (balance(player) < itemValue) {
                    // Player has enough money?
                    String message = OreMarket.main().getMsgConfig().getString("messages.insufficient-balance", "&cYou don't have enough money to buy this item!");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    return;
                }

                if (OreMarket.main().getGuiConfig().getBoolean("items." + slot + ".flags.copymeta")) {
                    // Take any type of the clicked item (e.g. custom name)
                    playerInventory.addItem(event.getCurrentItem());
                } else {
                    // Take the EXACT item (e.g. exact custom name)
                    playerInventory.addItem(clickedItem);
                }

                if (OreMarket.main().getGuiConfig().getString("items." + slot + ".flags.buy-sound") != null) {
                    playerObj.playSound(playerObj.getLocation(), Sound.valueOf(OreMarket.main().getGuiConfig().getString("items." + slot + ".flags.buy-sound")), 1f, 1f);
                }

                changePlayerBalance(itemValue, player, true, slot);
                OreMarket.main().getGuiConfig().set("items." + slot + ".stock", itemStock-1);
                OreMarket.main().saveGuiConfig();

                String message = OreMarket.main().getMsgConfig().getString("messages.successfully-bought", "&aYou have successfully bought the item!");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));

                assert keySection != null;
                OreMarket.main().logToFile(playerObj.getDisplayName() + " successfully bought 1x " + OreMarket.main().getGuiConfig().getString("items." + slot + ".name") + " for $" + OreMarket.main().getGuiConfig().getString("items." + slot + ".value"));
            }
            
            if ((event.getClick() == ClickType.SHIFT_LEFT)) { // Bulk Buy Mode (64 items)
                if (OreMarket.main().getGuiConfig().getBoolean("items." + event.getSlot() + ".flags.sellonly")) {
                    // You cannot buy this, Sell only item
                    String message = OreMarket.main().getMsgConfig().getString("messages.sell-only", "&cThis item can only be sold");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    return;
                }

                double itemValue = OreMarket.main().getGuiConfig().getDouble("items." + slot + ".value");
                int itemStock = OreMarket.main().getGuiConfig().getInt("items." + slot + ".stock");
                int buyAmount = Math.min(64, itemStock); // 最多购买64个，或者库存数量
                
                if (buyAmount <= 0) {
                    // Is there any stock?
                    return;
                }

                double totalCost = itemValue * buyAmount;
                if (balance(player) < totalCost) {
                    // Player has enough money?
                    String message = OreMarket.main().getMsgConfig().getString("messages.insufficient-balance", "&cYou don't have enough money to buy this item!");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    return;
                }
                
                // 检查背包空间
                int availableSpace = playerInventory.firstEmpty() != -1 ? calculateFreeSpace(playerInventory, clickedItem) : 0;
                if (availableSpace < buyAmount) {
                    player.sendMessage(ChatColor.RED + "背包空间不足！");
                    return;
                }

                // 创建一组物品
                ItemStack bulkItem = new ItemStack(clickedItem);
                bulkItem.setAmount(buyAmount);
                
                // 添加物品到背包
                playerInventory.addItem(bulkItem);

                if (OreMarket.main().getGuiConfig().getString("items." + slot + ".flags.buy-sound") != null) {
                    playerObj.playSound(playerObj.getLocation(), Sound.valueOf(OreMarket.main().getGuiConfig().getString("items." + slot + ".flags.buy-sound")), 1f, 1f);
                }

                // 多次调用changePlayerBalance以保持价格变动逻辑
                for (int i = 0; i < buyAmount; i++) {
                    itemValue = OreMarket.main().getGuiConfig().getDouble("items." + slot + ".value"); // 获取更新后的价格
                    changePlayerBalance(itemValue, player, true, slot);
                }
                
                OreMarket.main().getGuiConfig().set("items." + slot + ".stock", itemStock - buyAmount);
                OreMarket.main().saveGuiConfig();

                String message = OreMarket.main().getMsgConfig().getString("messages.successfully-bought", "&aYou have successfully bought the item!");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("the item", buyAmount + " items")));

                assert keySection != null;
                OreMarket.main().logToFile(playerObj.getDisplayName() + " successfully bought " + buyAmount + "x " + OreMarket.main().getGuiConfig().getString("items." + slot + ".name") + " for $" + totalCost);
            }

            if ((event.getClick() == ClickType.RIGHT)) { // Sell Mode
                if (OreMarket.main().getGuiConfig().getBoolean("items." + event.getSlot() + ".flags.buyonly")) {
                    // You cannot sell this, Buy only item
                    String message = OreMarket.main().getMsgConfig().getString("messages.buy-only", "&cThis item can only be bought");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    return;
                }

                if (!(playerInventory.containsAtLeast(clickedItem, 1))) {
                    String message = OreMarket.main().getMsgConfig().getString("messages.no-item", "&cYou don't have that item!");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    return;
                }

                double itemValue = OreMarket.main().getGuiConfig().getDouble("items." + slot + ".value");
                int itemStock = OreMarket.main().getGuiConfig().getInt("items." + slot + ".stock");

                if (OreMarket.main().getGuiConfig().getBoolean("items." + slot + ".flags.copymeta")) {
                    // Take the EXACT item (e.g. exact custom name)
                    if (!(playerInventory.containsAtLeast(event.getCurrentItem(), 1))) {
                        String message = OreMarket.main().getMsgConfig().getString("messages.no-item", "&cYou don't have that item!");
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        return;
                    }
                    playerInventory.removeItem(event.getCurrentItem());
                } else {
                    // Take any type of the clicked item (e.g. custom name)
                    playerInventory.removeItem(clickedItem);
                }

                if (OreMarket.main().getGuiConfig().getString("items." + slot + ".flags.sell-sound") != null) {
                    playerObj.playSound(playerObj.getLocation(), Sound.valueOf(OreMarket.main().getGuiConfig().getString("items." + slot + ".flags.sell-sound")), 1f, 1f);
                }

                changePlayerBalance(itemValue, player, false, slot);
                OreMarket.main().getGuiConfig().set("items." + slot + ".stock", itemStock+1);
                OreMarket.main().saveGuiConfig();

                String message = OreMarket.main().getMsgConfig().getString("messages.successfully-sold", "&aYou have successfully sold this item!");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                assert keySection != null;
                OreMarket.main().logToFile(playerObj.getDisplayName() + " successfully sold 1x " + OreMarket.main().getGuiConfig().getString("items." + slot + ".name") + " for $" + OreMarket.main().getGuiConfig().getString("items." + slot + ".value"));
            }
            
            if ((event.getClick() == ClickType.SHIFT_RIGHT)) { // Bulk Sell Mode (sell all items in inventory)
                if (OreMarket.main().getGuiConfig().getBoolean("items." + event.getSlot() + ".flags.buyonly")) {
                    // You cannot sell this, Buy only item
                    String message = OreMarket.main().getMsgConfig().getString("messages.buy-only", "&cThis item can only be bought");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    return;
                }

                // 计算玩家背包中该物品的总数
                int totalAmount = 0;
                for (ItemStack item : playerInventory.getContents()) {
                    if (item != null && item.isSimilar(clickedItem)) {
                        totalAmount += item.getAmount();
                    }
                }
                
                if (totalAmount <= 0) {
                    String message = OreMarket.main().getMsgConfig().getString("messages.no-item", "&cYou don't have that item!");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    return;
                }

                double totalValue = 0;
                int itemStock = OreMarket.main().getGuiConfig().getInt("items." + slot + ".stock");
                boolean hasCopymeta = OreMarket.main().getGuiConfig().getBoolean("items." + slot + ".flags.copymeta");
                ItemStack itemToRemove = hasCopymeta ? event.getCurrentItem() : clickedItem;
                
                // 先移除物品，确保物品确实被移除
                int removedCount = 0;
                ItemStack[] contents = playerInventory.getContents();
                for (int i = 0; i < contents.length; i++) {
                    ItemStack item = contents[i];
                    if (item != null && (hasCopymeta ? item.isSimilar(itemToRemove) : item.isSimilar(clickedItem))) {
                        removedCount += item.getAmount();
                        contents[i] = null; // 直接清空该槽位
                    }
                }
                playerInventory.setContents(contents); // 更新整个背包

                if (removedCount <= 0) {
                    player.sendMessage(ChatColor.RED + "出售失败，无法移除物品！");
                    return;
                }

                if (OreMarket.main().getGuiConfig().getString("items." + slot + ".flags.sell-sound") != null) {
                    playerObj.playSound(playerObj.getLocation(), Sound.valueOf(OreMarket.main().getGuiConfig().getString("items." + slot + ".flags.sell-sound")), 1f, 1f);
                }

                // 多次调用changePlayerBalance以保持价格变动逻辑
                for (int i = 0; i < removedCount; i++) {
                    double itemValue = OreMarket.main().getGuiConfig().getDouble("items." + slot + ".value"); // 获取更新后的价格
                    changePlayerBalance(itemValue, player, false, slot);
                    totalValue += calculateTotalWithTax(itemValue); // 累加税后价值
                }
                
                OreMarket.main().getGuiConfig().set("items." + slot + ".stock", itemStock + removedCount);
                OreMarket.main().saveGuiConfig();

                String message = OreMarket.main().getMsgConfig().getString("messages.successfully-sold", "&aYou have successfully sold this item!");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("this item", removedCount + " items")));
                assert keySection != null;
                OreMarket.main().logToFile(playerObj.getDisplayName() + " successfully sold " + removedCount + "x " + OreMarket.main().getGuiConfig().getString("items." + slot + ".name") + " for $" + totalValue);
            }

            mainGUI.createGUI((Player) player); // Reload GUI
        }
    }

    @EventHandler
    public void moveEvent (InventoryDragEvent event) {
        InventoryView playerView = event.getView(); // Player's inventory view
        if (playerView.getTitle().equals(ChatColor.translateAlternateColorCodes('&', title))) { // Using our GUI
            event.setCancelled(true);
        }
    }
}
