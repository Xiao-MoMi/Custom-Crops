![2022-08-15_02 51 28](https://user-images.githubusercontent.com/70987828/184551011-7da1dca5-faab-473c-b6a5-d2489b135ca9.png)


# Custom-Crops
StardewValley Like Farming System

### How to buy

https://afdian.net/@xiaomomi

https://polymart.org/resource/customcrops.2625

### How to compile

Execute gradle build in your IDLE and get the jar in /build/libs folder

### API Guide
```access transformers
public class YourClass {

    private CustomCropsAPI api;
    
    public YourClass() {
        api = CustomCropsAPI.getInstance();
    }
    
    public yourMethod() {
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
