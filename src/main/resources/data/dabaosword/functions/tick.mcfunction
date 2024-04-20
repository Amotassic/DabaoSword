kill @e[type=minecraft:arrow,nbt={inGround:true,CustomName:'{"text":"a"}'}]
execute as @e[nbt={Item:{id:"minecraft:arrow",Count:64b}}] at @s if entity @e[distance=..1,nbt={Item:{id:"minecraft:bow",Count:1b}}] run function dabaosword:arrow_rain

execute as @e[type=minecraft:villager,nbt={VillagerData:{profession:"minecraft:nitwit"}}] at @s if entity @e[distance=..1,nbt={Item:{id:"minecraft:emerald",Count:64b}}] run function dabaosword:gift_box
