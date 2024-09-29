package com.amotassic.dabaosword.mixin;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    public ItemEntityMixin(EntityType<?> type, World world) {super(type, world);}

    @Shadow public abstract ItemStack getStack();

    @Shadow public abstract void setStack(ItemStack stack);

    @Shadow public abstract void resetPickupDelay();

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (ModTools.isCard(this.getStack())) this.resetPickupDelay();

        ItemStack stack = this.getStack();
        if (stack.getItem() == Items.ARROW && stack.getCount() == 64) {
            var entity = getClosestEntity(this);
            if (entity instanceof ItemEntity item && item.getStack().getItem() == Items.BOW) {
                item.setStack(new ItemStack(ModItems.ARROW_RAIN));
                this.discard();
            }
        }

        if (stack.getItem() == Items.EMERALD && stack.getCount() == 64) {
            var entity = getClosestEntity(this);
            if (entity instanceof VillagerEntity villager && villager.getVillagerData().getProfession() == VillagerProfession.NITWIT) {
                this.setStack(new ItemStack(ModItems.GIFTBOX, 1));
            }
        }
    }

    @Unique private @Nullable Entity getClosestEntity(ItemEntityMixin entity) {
        if (entity.getWorld() instanceof ServerWorld world) {
            Box box = new Box(entity.getBlockPos()).expand(0.2);
            List<Entity> entities = world.getEntitiesByClass(Entity.class, box, entity1 -> entity1 != entity);
            if (!entities.isEmpty()) {
                Map<Float, Entity> map = new HashMap<>();
                for (var e : entities) {map.put(e.distanceTo(entity), e);}
                float min = Collections.min(map.keySet());
                return map.values().stream().toList().get(map.keySet().stream().toList().indexOf(min));
            }
        }
        return null;
    }
}
