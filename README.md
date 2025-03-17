# Custom-Crops 🌱

![CodeFactor Grade](https://img.shields.io/codefactor/grade/github/Xiao-MoMi/Custom-Crops)
<a href="https://mo-mi.gitbook.io/xiaomomi-plugins/plugin-wiki/customcrops" alt="GitBook">
<img src="https://img.shields.io/badge/docs-gitbook-brightgreen" alt="Gitbook"/>
</a>
[![Scc Count Badge](https://sloc.xyz/github/Xiao-MoMi/Custom-Crops/?category=codes)](https://github.com/Xiao-MoMi/Custom-Crops/)
![Code Size](https://img.shields.io/github/languages/code-size/Xiao-MoMi/Custom-Crops)
![bStats Servers](https://img.shields.io/bstats/servers/16593)
![bStats Players](https://img.shields.io/bstats/players/16593)
![GitHub](https://img.shields.io/github/license/Xiao-MoMi/Custom-Crops)

## 📌 Overview

CustomCrops is a high-performance **Paper plugin** designed to enhance the **planting experience** on Minecraft servers. It focuses on **customization** and **efficiency**, utilizing advanced techniques for optimal performance. 🌾

### 🔥 Key Features

- **Zstd Compression**: Efficient data serialization comparable to Minecraft's native methods.
- **⚡ Multi-threaded Tick System**: Improves server performance by distributing tasks across multiple threads.
- **🛠️ Comprehensive API**: Enables developers to create custom block mechanisms with specific interactions and behaviors.

---
## 🔧 Building the Project

### 💻 Command Line

1. Install **JDK 17 & 21**.
2. Open a terminal and navigate to the project directory.
3. Run:
   ```sh
   ./gradlew build
   ```
4. The generated artifact can be found in the `/target` folder.

### 🛠️ Using an IDE

1. Import the project into your preferred IDE.
2. Execute the **Gradle build** action.
3. Locate the artifact in the **/target** folder.

---
## 🤝 Contributing

### 🌍 Translations

1. Clone the repository.
2. Create a new language file in:
   ```
   /plugin/src/main/resources/translations
   ```
3. Submit a **pull request** with your changes for review. We appreciate your contributions! 💖

### 🚀 Areas for Improvement

- Enhance **thread scheduler efficiency** and reduce `ConcurrentHashMap` usage.
- Optimize **map storage** in sections using a **palette system**.
- Replace the current **sponge flow-nbt** library with a more efficient alternative (e.g., `sparrow-nbt`).
- Implement an improved **region file format** with file headers and sectors for **random read/write operations** (**4.0 milestone**).

---
## 💖 Support the Developer

If you enjoy using **CustomCrops**, consider supporting the developer! 🥰

- [Polymart](https://polymart.org/resource/customcrops.2625/)
- [BuiltByBit](https://builtbybit.com/resources/customcrops.36363/)
- [Afdian](https://afdian.com/@xiaomomi/)

---
## 📚 CustomCrops API

### 📌 Repository
```kotlin
repositories {
    maven("https://repo.momirealms.net/releases/")
}
```

### 📌 Dependency
```kotlin
dependencies {
    compileOnly("net.momirealms:custom-crops:3.6.29")
}
```

---
## 🎉 Fun Fact

I misspelled "mechanism" as "mechanic"—I should have caught that earlier! 😆
