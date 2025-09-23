package me.olliejw.oremarket.chat;

import me.olliejw.oremarket.OreMarket;
import me.olliejw.oremarket.utils.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ValueUpdates {
    Placeholders plh = new Placeholders();
    // 存储每个物品的上次提醒时间，用于冷却时间控制
    private final Map<String, Long> lastAnnounceTime = new HashMap<>();

    public void announceValue() {
        // 初始化方法，不再设置定时任务
        if (OreMarket.main().getConfig().getBoolean("valuemessage.enabled")) {
            // 仅记录日志表示功能已启用
            OreMarket.main().getLogger().info("价格提醒功能已启用，将在价格变动超过阈值时触发");
        }
    }

    /**
     * 检查并发送价格变化提醒
     * @param slot 物品槽位
     */
    public void checkAndAnnouncePriceChange(int slot) {
        if (!OreMarket.main().getConfig().getBoolean("valuemessage.enabled")) {
            return;
        }

        String slotKey = String.valueOf(slot);
        ConfigurationSection keySection = Objects.requireNonNull(OreMarket.main().getGuiConfig().getConfigurationSection("items")).getConfigurationSection(slotKey);
        
        if (keySection == null || keySection.getBoolean(".flags.hide")) {
            return;
        }

        // 检查冷却时间
        long cooldownSeconds = OreMarket.main().getConfig().getLong("valuemessage.cooldown_seconds", 60);
        long lastTime = lastAnnounceTime.getOrDefault(slotKey, 0L);
        if (System.currentTimeMillis() - lastTime < cooldownSeconds * 1000) {
            return;
        }

        // 计算价格变化百分比
        double cost = keySection.getDouble("cost");
        double value = keySection.getDouble("value");
        double changePercent = Math.abs((value - cost) / cost * 100);
        double threshold = OreMarket.main().getConfig().getDouble("valuemessage.change_threshold", 5);

        // 如果变化超过阈值，发送提醒
        if (changePercent >= threshold) {
            String message = OreMarket.main().getConfig().getString("valuemessage.format");
            assert message != null;

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(plh.format(message, player, keySection));
            }

            // 更新上次提醒时间
            lastAnnounceTime.put(slotKey, System.currentTimeMillis());
        }
    }
}
