# Changelog - PowerNukkitX 26_u4-hotfix4

> **Release:** [26_u4-hotfix4](https://github.com/PowerNukkitX/PowerNukkitX/releases/tag/26_u4-hotfix4)

## 📋 Release Info

| Property | Value |
|----------|-------|
| PowerNukkitX Version | 26_u4-hotfix4 |
| API Version | 3.0.3 |
| Minecraft Version | 1.26.45 |
| Protocol Version | 2168 |


## 📊 Summary

| Metric | Value |
|--------|-------|
| Commits since 3.0.3 | 35 |
| Files changed | 160 |
| Full diff | [3.0.3...26_u4-hotfix4](https://github.com/PowerNukkitX/PowerNukkitX/compare/3.0.3...26_u4-hotfix4) |

## ✨ Changes since 3.0.3


### 🐛 Bug Fixes

- paramList NPE and wrong args parsing in CommandTree (#3022)

### 📦 Other

- Add BadPacketHandler (#3047)
- bump the minor-and-patch group across 1 directory with 8 updates (#2983)

### 🔧 Chores

- Throw an error when a thread dies (#3025)

### 🚀 Features

- Add 1.26.45 version support
- CDN pack system (#3029)
- Improved spline progress and animation validation APIs (#3033)
- add /transfer command (#2959)
- custom level provider registry (#3028)

### 🐛 Bug Fixes

- Always send skin geometry data as a JSON Object (#3016)
- Custom items generating duplicate tags (#3023)
- Fix ender-chest open animation (#3035)
- Furnace descriptor matching and soul campfire recipes (#3046)
- Held item desync on startup (#3032)
- LevelException in BlockEntityLectern#onUpdate when level is invalid (#2996)
- NPE when dripleaf is broken (#2994)
- Packet order and payload parity with BDS (#3012)
- Sulfur Cube bounds (#3011)
- accept self-signed login chain when xboxAuth is disabled (#3027)
- armor stand item can't be larger than 1 (#3031)
- baby entity size scaled twice (#3038)
- bound async entity-prepare wait to avoid main thread stall (#3014)
- bundles not working at all (#3036)
- chest splitting and item loss (#3030)
- client crash on /execute chained command parameter (#3026)
- discard a vehicle that falls out of the world instead of killing it (#3060)
- dry grass drops and break when support block is broken (#3042)
- formatting issues in language files (#3019)
- item air sync with client / mobile cursor inventory (#3045)
- portal frame rotation in stronghold generation (#2991)
- portal opening wrongly (#2993)
- prevent NPE crash in InternalPackManager on null resource pack chunk (#2986)
- summon entity autocomplete missing custom entities (#3037)
- zombie attacking grown turtle instead of baby turtle (#3017)

### 📦 Other

- improved readme a little and added note where to get plugins (#3040)
