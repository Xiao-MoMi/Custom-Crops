# BetonQuest 3.0

**Required:** CustomCrops 3.6.47+ & BetonQuest 3.0-DEV+

### **Objective Structures**

```yaml
objective:
  # [Crops]
  <objective name>: customcrops_harvest_crop <crop_stage_id> <amount>
  <objective name>: customcrops_plant_crop <crop_id> <amount>
  
  # [Pots]
  <objective name>: customcrops_place_pot <pot_id> <amount>
  <objective name>: customcrops_break_pot <pot_id> <amount>

  # [Watering Can] - Coming soon. Structure subject to change.
  <objective name>: customcrops_fill_can <can_id> <amount>
  <objective name>: customcrops_water_pot <can_id> <pot_id> <amount>
  <objective name>: customcrops_sprinkler_setup <can_id> <sprinkler_id> <amount>

```

---

# Example Usage

### **1. Crops**

```yaml
objectives:
  harvestTomato: "customcrops_harvest_crop customcrops:tomato_stage_4 5"
  plantTomato: "customcrops_plant_crop tomato 3"
```

* **Harvest:** Harvest 5 fully grown tomatoes (stage 4).
* **Plant:** Plant 3 tomato seeds.

### **2. Farming Pots**

```yaml
objectives:
  placePot: "customcrops_place_pot default 4"
  breakPot: "customcrops_break_pot default 1"
```

* **Place:** Place 4 'default' type farming pots.
* **Break:** Break/Remove 1 'default' type farming pot.

### **3. Watering Can (Work in Progress)**

```yaml
objectives:
  fillCan: "customcrops_fill_can watering_can_1 3"
  waterPot: "customcrops_water_pot watering_can_2 default 5"
  sprinklerSetup: "customcrops_sprinkler_setup watering_can_3 sprinkler_1 1"
```

* **Fill:** Refill 'watering_can_1' from a water source 3 times.
* **Water:** Water 'default' pots 5 times using 'watering_can_2'.
* **Sprinkler:** Activate/Setup 'sprinkler_1' using 'watering_can_3'.

---

# Message Configuration

**Location:** `yourServer/plugins/BetonQuest/lang/<your_language>.yml`

```yaml
customcrops:
    crop_harvested: "@[legacy]&2{amount} crops left to harvest"
    crop_planted: "@[legacy]&2{amount} seeds left to plant"
    pot_placed: "@[legacy]&2{amount} pots left to place"
    pot_broken: "@[legacy]&e{amount} pots left to break"
    can_fill: "@[legacy]&bWatering can refilled!"
    can_pot: "@[legacy]&b{amount} pots left to water"
    can_sprinkler: "@[legacy]&3{amount} sprinklers left to activate"
```