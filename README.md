![2022-08-15_02 51 28](https://user-images.githubusercontent.com/70987828/184551011-7da1dca5-faab-473c-b6a5-d2489b135ca9.png)

# This project is currently under recoding

# Custom-Crops
StardewValley Like Farming System

### Support the developer

https://afdian.net/@xiaomomi

https://polymart.org/resource/customcrops.2625

## How to build

### Windows

#### Command Line
Install JDK 17 and set the JDK installation path to JAVA_HOME as an environment variable.\
Start powershell and change directory to the project folder.\
Execute ".\gradlew build" and get the jar at /target/CustomFishing-plugin-version.jar.

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

### API Guide
```access transformers
public class YourClass {

    private CustomCropsAPI api;
    
    public YourClass() {
        api = CustomCropsAPI.getInstance();
    }
    
    public void yourMethod() {
        api.xxx();
    }
}
```

#### Events
```
CropBreakEvent
CropInteractEvent
CropPlantEvent
FertilizerUseEvent
GreenhouseGlassBreakEvent
GreenhouseGlassPlaceEvent
PotBreakEvent
PotInfoEvent
PotInteractEvent
PotPlaceEvent
PotWaterEvent
ScarecrowBreakEvent
ScarecrowPlaceEvent
SprinklerFillEvent
SprinklerPlaceEvent
SprinklerInteractEvent
SprinklerBreakEvent
SeasonChangeEvent
```
