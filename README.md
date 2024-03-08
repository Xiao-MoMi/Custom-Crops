# Custom-Crops
![CodeFactor Grade](https://img.shields.io/codefactor/grade/github/Xiao-MoMi/Custom-Crops)
![bStats Servers](https://img.shields.io/bstats/servers/16593)
![bStats Players](https://img.shields.io/bstats/players/16593)
![GitHub](https://img.shields.io/github/license/Xiao-MoMi/Custom-Crops)
[![](https://jitpack.io/v/Xiao-MoMi/Custom-Crops.svg)](https://jitpack.io/#Xiao-MoMi/Custom-Crops)
<a href="https://mo-mi.gitbook.io/xiaomomi-plugins/plugin-wiki/customcrops" alt="GitBook">
<img src="https://img.shields.io/badge/docs-gitbook-brightgreen" alt="Gitbook"/>
</a>

Ultra-customizable planting experience for Minecraft servers

### Support the developer

https://afdian.net/@xiaomomi

https://polymart.org/resource/customcrops.2625

## How to build

### Windows

#### Command Line
Install JDK 17 and set the JDK installation path to JAVA_HOME as an environment variable.\
Start powershell and change directory to the project folder.\
Execute ".\gradlew build" and get the jar at /target/CustomCrops-plugin-version.jar.

#### IDE
Import the project and execute gradle build action.

## Use CustomCrops API

### Maven

```
<repositories>
  <repository>
    <id>jitpack</id>
    <url>https://jitpack.io/</url>
  </repository>
</repositories>
```
```
<dependencies>
  <dependency>
    <groupId>com.github.Xiao-MoMi</groupId>
    <artifactId>Custom-Crops</artifactId>
    <version>{LATEST}</version>
    <scope>provided</scope>
  </dependency>
</dependencies>
```
### Gradle (Groovy)

```
repositories {
    maven { url 'https://jitpack.io' }
}
```
```
dependencies {
    compileOnly 'com.github.Xiao-MoMi:Custom-Crops:{LATEST}'
}
```
### Gradle (Kotlin)

```
repositories {
    maven("https://jitpack.io/")
}
```
```
dependencies {
    compileOnly("com.github.Xiao-MoMi:Custom-Crops:{LATEST}")
}
```