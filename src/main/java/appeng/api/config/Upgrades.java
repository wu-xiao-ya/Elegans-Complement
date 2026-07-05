package appeng.api.config;

import net.minecraft.item.ItemStack;

public enum Upgrades {
    CAPACITY,
    REDSTONE,
    SPEED,
    FUZZY,
    INVERTER,
    CRAFTING,
    PATTERN_CAPACITY;

    public void registerItem(ItemStack stack, int amount) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        try {
            ae2.api.upgrades.Upgrades.add(ae2.core.definitions.AEItems.CAPACITY_CARD.item(), stack.getItem(), amount);
        } catch (LinkageError ignored) {
            // AE2S upgrade registration is best-effort for this legacy facade.
        }
    }
}
