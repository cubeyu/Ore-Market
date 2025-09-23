# Ore-Market
Ore Market is a realistic stock market system for ores in Minecraft

You can find the plugin on Spigot here -> https://www.spigotmc.org/resources/91015/
本插件由cubeyu进行维护

权限：
/openmarket	or /om oremarket.open	Opens the OreMarket GUI
/om-reload	oremarket.reload	Reloads the OreMarket Config
/om-stats	oremarket.stats	View the server specific stats
/om-crash	oremarket.crash	Crashes the market

变量
Placeholder	Description
[name]	Ore's name
[stock]	Ore's stock amount
[value]	Ore's current value
[cost]	Ore's current cost
[change]	Ore's value change (Value-Cost)
[percent]	Ore's value change percentage
[balance]	Player's balance
[player]	Player's name
Placeholder API (Works everywhere)
(No download command)

Placeholder	Description
%oremarket_name_{item-slot}%	Ore's name
%oremarket_stock_{item-slot}%	Ore's stock amount
%oremarket_value_{item-slot}%	Ore's current value
%oremarket_cost_{item-slot}%	Ore's current cost
%oremarket_change_{item-slot}%	Ore's value change (Value-Cost)
%oremarket_percent_{item-slot}%	Ore's value change percentage

making a gui

Template item:
0:                                -- Slot
  item: DIAMOND                   -- Material
  name: '&bDiamond Ore'           -- Item Name
  lore:                           -- Lore line(s)
    - '&aValue: $[value]'         -- l1
    - '&aOriginal: $[cost]'       -- l2
    - '&aStock: [stock]'          -- l3
    - '&aChange: [percent]%'      -- l4
    - '&7Right-Click to buy'      -- l5
    - '&7Left-Click to sell'      -- l6
  cost: 1000                      -- Original cost of ore
  value: 1000                     -- Current cost of ore
  stock: 100                      -- Amount in stock
  
Player head
1:                                -- Slot
  item: PLAYER_HEAD               -- Material
  head: 'Player_name'             -- Head name
  name: '&bHead'                  -- Item Name
  flags:
    copymeta: true                -- Required (Copies name and lore too)
    
Item flags
  flags:
    copymeta: true   -- Copy item meta (Required for heads)
    buyonly: true    -- Can only buy this item
    sellonly: true   -- Can only sell this item
    hide: true       -- Item is hidden from chat messages
    
Item commands
  commands:
    - '[close]'  -- Close GUI
    - '[msg]'    -- Send player a message
    - '/command' -- Send any command
