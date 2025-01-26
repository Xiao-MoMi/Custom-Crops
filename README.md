# Custom-Crops
![CodeFactor Grade](https://img.shields.io/codefactor/grade/github/Xiao-MoMi/Custom-Crops)
<a href="https://mo-mi.gitbook.io/xiaomomi-plugins/plugin-wiki/customcrops" alt="GitBook">
<img src="https://img.shields.io/badge/docs-gitbook-brightgreen" alt="Gitbook"/>
</a>
[![Scc Count Badge](https://sloc.xyz/github/Xiao-MoMi/Custom-Crops/?category=codes)](https://github.com/Xiao-MoMi/Custom-Crops/)
![Code Size](https://img.shields.io/github/languages/code-size/Xiao-MoMi/Custom-Crops)
![bStats Servers](https://img.shields.io/bstats/servers/16593)
![bStats Players](https://img.shields.io/bstats/players/16593)
![GitHub](https://img.shields.io/github/license/Xiao-MoMi/Custom-Crops)

CustomCrops is a Paper plugin crafted to deliver an exceptional planting experience for Minecraft servers, with a strong emphasis on customization and performance. It employs Zstd compression for data serialization, ensuring high efficiency comparable to Minecraft's own serialization techniques. The plugin optimizes server performance by running its tick system across multiple threads, reverting to the main thread only when required. Additionally, CustomCrops offers a comprehensive API that enables developers to create custom block mechanism with specific interaction and tick behaviors, such as a fish trap block that periodically provides players with fish.

## How to Build

#### Command Line
Install JDK 17 & 21. \
Start terminal and change directory to the project folder.\
Execute ".\gradlew build" and get the artifact under /target folder

#### IDE
Import the project and execute gradle build action. \
Get the artifact under /target folder

## How to Contribute

#### Translations
Clone this project and create a new language file in the /plugin/src/main/resources/translations directory. \
Once your changes are ready, open a pull request for review. We appreciate your works!

#### Areas for improvement
[1] Further improve the thread scheduler and reduce the use of ConcurrentHashMap. \
[2] Optimize the map storage in the section as a palette. \
[3] Use other NBT libraries (such as sparrow-nbt) to replace the current sponge flow-nbt (because this library is really a bit bad).
I used it for compatibility with AdvancedSlimePaper in the beginning. In fact, we can use the same IO stream operation to convert sparrow-nbt to flow-nbt and store it in the Slime world. \
[4] Improve the region file format and use file headers and sectors to perform random read and write of region files. (4.0 milestone)

## Support the Developer

Polymart: https://polymart.org/resource/customcrops.2625/ \
BuiltByBit: https://builtbybit.com/resources/customcrops.36363/ \
Afdian: https://afdian.com/@xiaomomi/

## CustomCrops API

```kotlin
repositories {
    maven("https://repo.momirealms.net/releases/")
}
```
```kotlin
dependencies {
    compileOnly("net.momirealms:custom-crops:3.6.29")
}
```
#### Fun Facts
I misspelled mechanism as mechanic. I should have realized this earlier XD