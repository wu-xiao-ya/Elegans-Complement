# Elegans Complement

Forge `1.12.2` mod project.

## Current feature

- Configurable MMCE controller NBT persistence when harvesting and placing controller blocks.

## Config

Generated config file: `config/eleganscomplement.cfg`

- `features.mmceControllerNbtPersistence=true`

## Notes

- The MMCE integration is implemented with reflection, so the project can compile without bundling MMCE as a hard development dependency.
- The current implementation preserves the `customData.parallelUpgrades` payload and copies root controller tags `owner`, `parentMachine`, `casingColor` onto the dropped item, matching the requested behavior baseline.
