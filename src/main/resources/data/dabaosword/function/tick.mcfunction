kill @e[type=minecraft:arrow,nbt={inGround:true,CustomName:'"a"'}]
execute as @e[nbt={Item:{id:"minecraft:arrow",count:64}}] at @s if entity @e[distance=..1,nbt={Item:{id:"minecraft:bow",count:1}}] run function dabaosword:arrow_rain

execute as @e[type=minecraft:villager,nbt={VillagerData:{profession:"minecraft:nitwit"}}] at @s if entity @e[distance=..1,nbt={Item:{id:"minecraft:emerald",count:64}}] run function dabaosword:gift_box
