# Elegans Complement

Forge `1.12.2` addon mod for pack-side compatibility patches.

## Features

- Configurable MMCE controller NBT persistence when controller blocks are harvested and placed again.
- Configurable Hatchery nest extension recipes through CraftTweaker.
- Configurable MMCE AE2S startup compatibility — suppresses legacy AE2 integration paths when AE2 Supergiant is detected.
- Configurable ecoaeextension bridge patches for ECalculator, EFabricator, and EStorage.

All feature hooks are disabled by default and must be enabled in config before they affect gameplay.

## Config

Generated config file: `config/eleganscomplement.cfg`

```properties
features.mmceControllerNbtPersistence=false
features.hatcheryNestRecipes=false
features.hatcheryNestCraftTweakerRecipes=false
features.mmceAe2sStartupCompat=true
features.ecoAe2sBridge=false
features.ecoAe2sBridgeEcalculator=false
features.ecoAe2sBridgeEfabricator=false
features.ecoAe2sBridgeEstorage=false
```

`hatcheryNestRecipes` enables the Hatchery nest Mixin/runtime hook.
`hatcheryNestCraftTweakerRecipes` enables the CraftTweaker registration API.
Both Hatchery nest switches must be enabled for CraftTweaker nest recipes to register and run.
`mmceAe2sStartupCompat` disables MMCE legacy AE2 integration paths when AE2 Supergiant is detected at startup, preventing class-loading conflicts.
`ecoAe2sBridge` is the master switch for all ecoaeextension bridge patches.
`ecoAe2sBridgeEcalculator` / `ecoAe2sBridgeEfabricator` / `ecoAe2sBridgeEstorage` enable the respective sub-bridge patches.
Both the master switch and the corresponding sub-switch must be on for a sub-bridge to activate.

## MMCE AE2S Startup Compatibility

When both MMCE and AE2 Supergiant are present at runtime, MMCE's legacy AE2 integration can throw class-loading errors. Enable `mmceAe2sStartupCompat` to suppress those paths during Forge startup.

## ecoaeextension Bridge

The ecoaeextension bridge provides runtime patches for the external `ecoaeextension` mod when it runs together with MMCE and AE2 Supergiant. The three sub-switches control the internal bridge lines for that single mod:

| Config switch | Bridge line | Purpose |
|---------------|-------------|---------|
| `ecoAe2sBridgeEcalculator` | ECalculator | Bridge patches for ECalculator energy / CPU integration |
| `ecoAe2sBridgeEfabricator` | EFabricator | Bridge patches for EFabricator crafting integration |
| `ecoAe2sBridgeEstorage` | EStorage | Bridge patches for EStorage AE2 network integration |

The master switch `ecoAe2sBridge` must be enabled for any sub-bridge to take effect. Mixin configs are loaded lazily only when the corresponding switch is on and the external `ecoaeextension` mod is present, so no side effects occur when the feature is disabled.

## CraftTweaker: Hatchery Nest

Requires Hatchery, CraftTweaker, and MixinBooter at runtime.

ZenClass:

```zenscript
mods.eleganscomplement.HatcheryNest
```

### Add Recipes

```zenscript
// Uses defaults: hatchTime=300, chance=1.0, babyAge=-24000.
mods.eleganscomplement.HatcheryNest.addRecipe(<minecraft:egg>, "minecraft:chicken");

// String item ids are also accepted. This is useful when a generated ContentTweaker
// item is easier to pass as a name string.
mods.eleganscomplement.HatcheryNest.addRecipe("contenttweaker:oak_egg", "chickens:LogChicken", 10, 1.0, 0);

// Explicit baby age, with default chance=1.0.
mods.eleganscomplement.HatcheryNest.addRecipe(<minecraft:bone>, "minecraft:skeleton", 300, 0);

// Full form: input, entity id, hatch time, output chance, baby age.
mods.eleganscomplement.HatcheryNest.addRecipe(<minecraft:rotten_flesh>, "minecraft:zombie", 600, 0.75, -24000);
```

Parameter notes:

- `input`: item accepted by the nest. Stack size is normalized to `1`; meta and NBT are respected.
  `IItemStack`, plain ids like `"contenttweaker:oak_egg"`, bracket strings like `"<contenttweaker:oak_egg>"`, and translation keys like `"item.contenttweaker.oak_egg.name"` are accepted.
- `entityId`: normal entity id such as `"minecraft:zombie"`, or a supported compatibility id such as `"chickens:LogChicken"`.
- `hatchTime`: Hatchery internal hatch progress threshold. Hatchery advances progress every `80` ticks by a small random amount, so this is not seconds or Minecraft ticks.
- `chance`: final output chance from `0.0` to `1.0`. `1.0` is guaranteed output. `0.0` will never spawn an entity.
- `babyAge`: applied only to entities that support `EntityAgeable`. Use `-24000` for a baby and `0` for default adult behavior. Non-ageable entities ignore it.

### Chickens Mod Types

For Chickens mod chickens, use the chicken type name:

```zenscript
mods.eleganscomplement.HatcheryNest.addRecipe(
    "contenttweaker:oak_egg",
    "chickens:LogChicken",
    10,
    1.0,
    0
);
```

`"chickens:LogChicken"` is not a normal entity registry id. Elegans Complement treats `chickens:*` ids as Chickens type registry names and spawns `EntityChickensChicken` with that type. Minecraft normalizes resource-location paths to lower case, and Elegans Complement also ignores `_` / `-`, so `"chickens:LogChicken"` becomes the Chickens 6.x type key `"chickens:logchicken"`.

If a Chickens recipe reaches completion but does not spawn, check the log for `Hatchery nest hatch ENTITY_CREATION_FAILED`. That usually means the chicken type name does not match the name registered by the Chickens config.

### Remove Recipes

```zenscript
mods.eleganscomplement.HatcheryNest.removeRecipe(<minecraft:egg>);
mods.eleganscomplement.HatcheryNest.removeRecipe("contenttweaker:oak_egg");
mods.eleganscomplement.HatcheryNest.clear();
```

CraftTweaker registrations for the same input override previous registrations. When two matching recipes have the same specificity, the later registration wins.

## Troubleshooting

- In `latest.log`, CraftTweaker must report that it loaded at least one script. If it says `Loaded 0/0 scripts`, the `.zs` file is not being read and no nest recipe can register.
- With both config switches enabled, `latest.log` should contain `Registered CraftTweaker Hatchery nest recipe` for every accepted registration. If it does not, check `crafttweaker.log` for script errors and skipped registration messages.
- Check `crafttweaker.log` for the registered `hatchTime`, `chance`, and `babyAge` values.
- If the nest reaches completion but no entity spawns, check the main log for `Hatchery nest hatch ... failed`.
- `ENTITY_CREATION_FAILED` means the entity id or Chickens type could not be resolved.
- `ENTITY_SPAWN_FAILED` means the entity was created but the world rejected spawning it.
- If `chance=0.0`, the recipe will complete checks but never produce an entity. Use `1.0` for guaranteed output.

## Notes

- The MMCE integration is implemented with reflection, so the project can compile without bundling MMCE as a hard development dependency.
- The Hatchery Chickens compatibility is implemented with reflection, so Chickens is optional at runtime unless a script uses `chickens:*` ids.
- Hatchery itself is not modified.
