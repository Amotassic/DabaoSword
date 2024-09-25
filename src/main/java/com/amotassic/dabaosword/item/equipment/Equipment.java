package com.amotassic.dabaosword.item.equipment;

import com.amotassic.dabaosword.event.callback.CardDiscardCallback;
import com.amotassic.dabaosword.event.callback.CardUsePostCallback;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.Skill;
import com.amotassic.dabaosword.util.Sounds;
import dev.emi.trinkets.TrinketSlot;
import dev.emi.trinkets.api.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.amotassic.dabaosword.util.ModTools.*;

public class Equipment extends TrinketItem implements Skill {
    public Equipment(Settings settings) {super(settings);}

    public static class FangtianWeapon extends Equipment {
        public FangtianWeapon(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
            tooltip.add(Text.translatable("item.dabaosword.fangtian.tooltip1"));
            tooltip.add(Text.translatable("item.dabaosword.fangtian.tooltip2").formatted(Formatting.AQUA));
            super.appendTooltip(stack, world, tooltip, context);
        }

        @Override
        public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            if (entity.getWorld() instanceof ServerWorld world) {
                int cd = getCD(stack);
                if (cd > 0 && world.getTime() % 20 == 0) cd--; setCD(stack, cd);
            }
        }

        @Override
        public void preAttack(ItemStack stack, LivingEntity target, PlayerEntity player) {
            //方天画戟：打中生物后触发特效，给予CD和持续时间
            int cd = getCD(stack);
            if (cd == 0) {
                setCD(stack, 20);
                voice(player, Sounds.FANGTIAN);
                player.sendMessage(Text.translatable("dabaosword.fangtian").formatted(Formatting.RED), true);
            }
        }
    }

    public static class HanbingWeapon extends Equipment {
        public HanbingWeapon(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
            tooltip.add(Text.translatable("item.dabaosword.hanbing.tooltip").formatted(Formatting.AQUA));
            super.appendTooltip(stack, world, tooltip, context);
        }

        @Override
        public void postAttack(ItemStack stack, LivingEntity entity, LivingEntity attacker, float amount) {
            voice(attacker, Sounds.HANBING);
            entity.timeUntilRegen = 0;
            entity.setFrozenTicks(500);
        }
    }

    public static class QinggangWeapon extends Equipment {
        public QinggangWeapon(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
            tooltip.add(Text.translatable("item.dabaosword.qinggang.tooltip1"));
            tooltip.add(Text.translatable("item.dabaosword.qinggang.tooltip2").formatted(Formatting.AQUA));
            super.appendTooltip(stack, world, tooltip, context);
        }

        @Override
        public void preAttack(ItemStack stack, LivingEntity target, PlayerEntity player) {
            //青釭剑额外伤害
            float extraDamage = Math.min(20, 0.2f * target.getMaxHealth());
            target.damage(player.getDamageSources().genericKill(), extraDamage); target.timeUntilRegen = 0;
            voice(player, Sounds.QINGGANG);
        }
    }

    public static class QinglongWeapon extends Equipment {
        public QinglongWeapon(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
            tooltip.add(Text.translatable("item.dabaosword.qinglong.tooltip1"));
            tooltip.add(Text.translatable("item.dabaosword.qinglong.tooltip2").formatted(Formatting.AQUA));
            super.appendTooltip(stack, world, tooltip, context);
        }

        @Override
        public void preAttack(ItemStack stack, LivingEntity target, PlayerEntity player) {
            voice(player, Sounds.QINGLONG);
            player.addStatusEffect(new StatusEffectInstance(ModItems.INVULNERABLE,10,0,false,false,false));
            player.teleport(target.getX(), target.getY(), target.getZ());
            Vec3d momentum = player.getRotationVector().multiply(2);
            target.velocityModified = true; target.setVelocity(momentum.getX(),0 ,momentum.getZ());
        }
    }

    public static class RattanArmor extends Equipment {
        public RattanArmor(Settings settings) {super(settings);}

        @Override
        public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
            tooltip.add(Text.translatable("item.dabaosword.rattanarmor.tooltip"));
            super.appendTooltip(stack, world, tooltip, context);
        }

        //实现渡江不沉的效果，代码来自https://github.com/focamacho/RingsOfAscension/中的水上行走戒指
        @Override
        public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            if(entity.isSneaking()) return;

            BlockPos entityPos = entity.getBlockPos();

            boolean water = entity.getWorld().getFluidState(new BlockPos(entityPos.getX(),
                            (int) (entity.getBoundingBox().getMin(Direction.Axis.Y)), entityPos.getZ()))
                    .isOf(Fluids.WATER);

            if(water) {
                Vec3d motion = entity.getVelocity();
                entity.setVelocity(motion.x, 0.0D, motion.z);
                entity.fallDistance = 0;
                entity.setOnGround(true);
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {

        if (stack.getItem() == ModItems.GUDING_WEAPON) {
            tooltip.add(Text.translatable("item.dabaosword.gudingdao.tooltip").formatted(Formatting.GREEN));
            tooltip.add(Text.translatable("item.dabaosword.gudingdao.tooltip2").formatted(Formatting.AQUA));
        }

        if (stack.getItem() == ModItems.BAGUA) {
            tooltip.add(Text.translatable("item.dabaosword.bagua.tooltip"));
        }

        if (stack.getItem() == ModItems.BAIYIN) {
            tooltip.add(Text.translatable("item.dabaosword.baiyin.tooltip"));
        }

        if (stack.getItem() == ModItems.CHITU) {
            tooltip.add(Text.translatable("item.dabaosword.chitu.tooltip"));
        }

        if (stack.getItem() == ModItems.DILU) {
            tooltip.add(Text.translatable("item.dabaosword.dilu.tooltip"));
        }

        if (stack.getItem() == ModItems.CARD_PILE) {
            tooltip.add(Text.translatable("item.dabaosword.card_pile.tooltip"));
        }

        if (stack.getItem() != ModItems.CARD_PILE) {
            if(Screen.hasShiftDown()) {
                tooltip.add(Text.translatable("equipment.tip1").formatted(Formatting.BOLD));
                tooltip.add(Text.translatable("equipment.tip2").formatted(Formatting.BOLD));
            } else tooltip.add(Text.translatable("dabaosword.shifttooltip"));
        }
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity.getWorld() instanceof ServerWorld world) {
            world.getPlayers().forEach(player -> player.sendMessage(
                    Text.literal(entity.getEntityName()).append(Text.literal("装备了 ").append(stack.toHoverableText()))
            ));
        }
        super.onEquip(stack, slot, entity);
    }

    @Override
    public boolean canUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity instanceof PlayerEntity player && player.isCreative()) return true;
        if (stack.getItem() != ModItems.CARD_PILE) return false;
        return super.canUnequip(stack, slot, entity);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (useEquip(user, stack)) return TypedActionResult.success(stack, world.isClient);
        if (replaceEquip(user, stack)) return TypedActionResult.success(stack, world.isClient);
        return TypedActionResult.pass(stack);
    }

    public static boolean useEquip(PlayerEntity user, ItemStack stack) {
        var optional = TrinketsApi.getTrinketComponent(user);
        if (optional.isPresent()) {
            TrinketComponent comp = optional.get();
            for (var group : comp.getInventory().values()) {
                for (TrinketInventory inv : group.values()) {
                    for (int i = 0; i < inv.size(); i++) {
                        if (inv.getStack(i).isEmpty()) {
                            SlotReference ref = new SlotReference(inv, i);
                            if (TrinketSlot.canInsert(stack, ref, user)) {
                                ItemStack newStack = stack.copy();
                                inv.setStack(i, newStack);
                                SoundEvent soundEvent = stack.getItem() instanceof net.minecraft.item.Equipment eq ? eq.getEquipSound() : null;
                                if (!stack.isEmpty() && soundEvent != null) {
                                    user.emitGameEvent(GameEvent.EQUIP);
                                    user.playSound(soundEvent, 1.0F, 1.0F);
                                }
                                CardUsePostCallback.EVENT.invoker().cardUsePost(user, stack, user);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean replaceEquip(PlayerEntity player, ItemStack stack) {
        Map<Integer, TrinketInventory> map = replaceSlot(player, stack);
        if (!map.isEmpty() && stack.getItem() != ModItems.CARD_PILE) {
            List<Integer> slots = map.keySet().stream().toList();
            int index = new Random().nextInt(slots.size()); int i = slots.get(index);
            ItemStack preStack = map.values().stream().toList().get(index).getStack(i);
            CardDiscardCallback.EVENT.invoker().cardDiscard(player, preStack, preStack.getCount(), true);
            ItemStack newStack = stack.copy();
            map.values().stream().toList().get(index).setStack(i, newStack);
            CardUsePostCallback.EVENT.invoker().cardUsePost(player, stack, player);
            return true;
        }
        return false;
    }

    private static Map<Integer, TrinketInventory> replaceSlot(PlayerEntity player, ItemStack stack) {
        Map<Integer, TrinketInventory> m = new HashMap<>();
        var optional = TrinketsApi.getTrinketComponent(player);
        if (optional.isPresent()) {
            TrinketComponent comp = optional.get();
            for (var group : comp.getInventory().values()) {
                for (TrinketInventory inv : group.values()) {
                    for (int i = 0; i < inv.size(); i++) {
                        //如果对应装备栏的物品与待装备的物品有完全相同的标签，则将该饰品栏添加到map中
                        if (!inv.getStack(i).isEmpty() && inv.getStack(i).streamTags().toList().equals(stack.streamTags().toList())) {
                            m.put(i, inv);
                        }
                    }
                }
            }
        }
        return m;
    }
}
