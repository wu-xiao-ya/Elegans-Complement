package appeng.api.networking.crafting;

public interface ICraftingProviderHelper {
    void addCraftingOption(ICraftingProvider provider, ICraftingPatternDetails api);

    void addCraftingOption(ICraftingMedium medium, ICraftingPatternDetails api);
}
