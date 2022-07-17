# Custom-Crops
StardewValley Like Farming System

### How to buy
Unfortunately I only accept Chinese Payments.
https://afdian.net/@xiaomomi

### How to compile
Just compile it with -gradle shadowjar. Some premium plugins are used as 
local libraries. If you don't need those integrations just remove them!
Default ItemsAdder Config is also included in this project, which will 
provide a template and help you understand how this plugin works.

### Commands
/customcrops setseason <world> <season> # set a specified world's season
/customcrops reload # reload the plugin
/customcrops backup # back up the data
/customcrops forcegrow <world> # force a specified world's crops to grow a stage
/customcrops forcewater <world> # force a specified world's sprinklers to work
/customcrops forcesave <file> # save the cache to file

### Placeholders
%customcrops_season% show the season in the world
%customcrops_season_<world>%
%customcrops_nextseason% show the days to the next season
%customcrops_nextseason_<world>%
