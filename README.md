![2022-08-15_02 51 28](https://user-images.githubusercontent.com/70987828/184551011-7da1dca5-faab-473c-b6a5-d2489b135ca9.png)


# Custom-Crops
StardewValley Like Farming System

### How to buy

https://afdian.net/@xiaomomi

https://polymart.org/resource/customcrops.2625

### How to compile
Just compile it with gradle build. Some premium plugins are used as 
local libraries. If you don't need those integrations just remove them!
Default ItemsAdder Config is also included in this project, which will 
provide a template and help you understand how this plugin works.

### Game Mechanics
Crops will grow at a specified time which you will see in the config.\
1000 is default (7am) As we know, Minecraft has 24000 ticks / Day\
All crops will grow successively if their pot is watered.

### Season & Greenhouse
Season is now an important part of StardewValley Farming System.
Crops only grow in a suitable season and inproper
seasons will make crops into a dead stage. 
You can use greenhouse glass to allow them to grow all year.\
Season change has two modes: Automatic and Command\
You might use the command to change the season to sync another plugin's season, for example RealisticSeason.

### Fertilizer
There are three templates of fertiziliers: \
SpeedGrow: Crops have a small chance to grow two stages at a time\
RetainingSoil: Pot have a small chance to retain its water after crops grow\
QuailityCrops: When haveresting, players have a higher chance to get high quality crops.

### Sprinkler & WateringCan
Sprinkler is a semi-automatic way of watering pot. You can add water to sprinkler with
water bucket or watering can. Max storage and range can be customized.\
Watering-can also has its max storage and effective range. 1x1 1x3 3x3 and even 9x99 is supported!

### OverWeight
If configurated, crops will still absorb water every day and have a very little chance to be OverWeight(gigantic) before it's dead.

### Quality
Crops have three qualities, if you don't want this feature just disable it in config. 
Quality is determined by the fertizilier players use and their luck!

### Harvest Repeatedly
If configurated, crops can be harvested repeatedly and return to a specified stage.\
This is useful for crops like grape.

### Highly Optimizied
1.Crops only grow at the specified time and won't impact the performance in other times.\
2.Growing judge is async and only the last step ** replace blcoks ** is sync.\
3.Crops will not actually grow at the same time. It's laggy to replace so many blocks at the same time. They will grow in a random time(in seconds) which you can specified in the config after "grow-time"(7am default)\
4.Four grow modes to choose.

### Commands
/customcrops setseason [world] [season] # set a specified world's season\
/customcrops reload # reload the plugin\
/customcrops backup # back up the data\
/customcrops forcegrow [world] # force a specified world's crops to grow a stage\
/customcrops forcewater [world] # force a specified world's sprinklers to work\
/customcrops forcesave [file] # save the cache to file

### Placeholders
%customcrops_season% show the season in the world\
%customcrops_season_[world]%\
%customcrops_nextseason% show the days to the next season\
%customcrops_nextseason_[world]%\
%customcrops_current% show the days the current season has gone\
%customcrops_current_[world]%
