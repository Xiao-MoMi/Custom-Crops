# BetonQuest 3.0

**Required:** CustomCrops 3.6.47+ & BetonQuest 3.0-DEV+

### **Objective Structures**

```yaml
# The default value for amount is 1.
# The default value for targets is it anything is acceptable.

# crop_stage_id, crop_id, pot_id, can_id, and sprinkler_id
# are all defined in the CustomCrops configuration files.
# These values can also be provided as a list (A, B, C, ...)

objective:
  # Crops
  <objective name>: customcrops_harvest_crop <crop_stage_id> [amount:int]
  <objective name>: customcrops_plant_crop <crop_id> [amount:int]
  
  # Pots
  <objective name>: customcrops_place_pot <pot_id> [amount:int]
  <objective name>: customcrops_break_pot <pot_id> [amount:int]

  # Watering Cans
  <objective name>: customcrops_fill_can <can_id> [amount:int]
  <objective name>: customcrops_water_pot <can_id> [targets:pot_id] [amount:int]
  <objective name>: customcrops_water_sprinkler <can_id> [targets:sprinkler_id] [amount:int]

  # Sprinklers
  <objective name>: customcrops_place_sprinkler <sprinkler_id> [amount:int]
  <objective name>: customcrops_break_sprinkler <sprinkler_id> [amount:int]

  # Fertilizers
  <objective name>: customcrops_use_fertilizer <fertilizer_id> [targets:pot_id] [amount:int]
```

---

# Example Usage

### **Crops**

```yaml
objectives:
  harvestTomato: customcrops_harvest_crop customcrops:tomato_stage_4 amount:5
  plantTomato: customcrops_plant_crop tomato
```

* **Harvest:** Harvest 5 fully grown tomatoes (stage 4).
* **Plant:** Plant 1 tomato seeds.

### **Pots**

```yaml
objectives:
  placePot: customcrops_place_pot default amount:4
  breakPot: customcrops_break_pot default amount:1
```

* **Place:** Place 4 pots with the ID `default`.
* **Break:** Break 1 pots with the ID `default`.

### **Watering Can**

```yaml
objectives:
  fillCan: customcrops_fill_can watering_can_1 amount:3
  waterPot: customcrops_water_pot watering_can_2 default amount:5
  waterSprinkler: customcrops_water_sprinkler watering_can_3 sprinkler_1
```

* **Fill:** Refill `watering_can_1` from a water source 3 times.
* **Water:** Water `default` pots 5 times using `watering_can_2`.
* **Sprinkler:** Activate or set up `sprinkler_1` using `watering_can_3`.

### **Sprinklers**

```yaml
objectives:
  placeSprinklers: customcrops_place_sprinkler sprinkler_1
  breakSprinklers: customcrops_break_sprinkler sprinkler_2 amount:2
```

* **Place:** Place 1 sprinkler with the ID `sprinkler_1`.
* **Break:** Break 2 sprinkler with the ID `sprinkler_2`.

---

### **Fertilizers**
```yaml
objectives:
  useFertilizer: customcrops_use_fertilizer quality_1 [targets:pot_id] amount:10
```

* **Use:** Use fertilizer 10 times.

# Message Configuration

**Location:** `yourServer/plugins/BetonQuest/lang/<your_language>.yml`

```yaml
customcrops:
  crop_harvested: "@[legacy]&2{amount} crops left to harvest"
  crop_planted: "@[legacy]&2{amount} seeds left to plant"
  pot_placed: "@[legacy]&2{amount} pots left to place"
  pot_broken: "@[legacy]&2{amount} pots left to break"
  can_fill: "@[legacy]&2{amount} watering can refills remaining"
  can_pot: "@[legacy]&2{amount} pots left to water"
  can_sprinkler: "@[legacy]&2{amount} sprinklers left to activate"
  sprinkler_placed: "@[legacy]&2{amount} sprinklers left to place"
  sprinkler_broken: "@[legacy]&2{amount} sprinklers left to break"
  use_fertilizer: "@[legacy]&2{amount} fertilizers left to use"
```