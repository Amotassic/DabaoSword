package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.util.*;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

import static com.amotassic.dabaosword.item.card.GiftBoxItem.selectRandomEntry;

public class SkillItem extends TrinketItem implements ModTools {
    public SkillItem(Settings settings) {super(settings);}

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {

        if (stack.getItem() == SkillCards.TIEJI) {
            tooltip.add(Text.translatable("item.dabaosword.tieji.tooltip1").formatted(Formatting.RED));
            tooltip.add(Text.translatable("item.dabaosword.tieji.tooltip2").formatted(Formatting.RED));
        }

        if (stack.getItem() == SkillCards.GANGLIE) {
            tooltip.add(Text.translatable("item.dabaosword.ganglie.tooltip1").formatted(Formatting.BLUE));
            tooltip.add(Text.translatable("item.dabaosword.ganglie.tooltip2").formatted(Formatting.BLUE));
        }

        if (stack.getItem() == SkillCards.FANGZHU) {
            tooltip.add(Text.translatable("item.dabaosword.fangzhu.tooltip").formatted(Formatting.BLUE));
        }

        if (stack.getItem() == SkillCards.XINGSHANG) {
            tooltip.add(Text.translatable("item.dabaosword.xingshang.tooltip").formatted(Formatting.BLUE));
        }

        if (stack.getItem() == SkillCards.DUANLIANG) {
            tooltip.add(Text.literal("CD: 5s"));
            tooltip.add(Text.translatable("item.dabaosword.duanliang.tooltip").formatted(Formatting.BLUE));
        }

        if (stack.getItem() == SkillCards.LUOSHEN) {
            int cd = stack.getNbt() == null ? 0 : stack.getNbt().getInt("cooldown");
            tooltip.add(Text.literal(cd == 0 ? "CD: 30s" : "CD: 30s   left: "+ cd +"s"));
            tooltip.add(Text.translatable("item.dabaosword.luoshen.tooltip").formatted(Formatting.BLUE));
        }

        if (stack.getItem() == SkillCards.QINGGUO) {
            tooltip.add(Text.literal("CD: 5s"));
            tooltip.add(Text.translatable("item.dabaosword.qingguo.tooltip").formatted(Formatting.BLUE));
        }

        if (stack.getItem() == SkillCards.QIXI) {
            tooltip.add(Text.literal("CD: 5s"));
            tooltip.add(Text.translatable("item.dabaosword.qixi.tooltip").formatted(Formatting.GREEN));
        }

        if (stack.getItem() == SkillCards.HUOJI) {
            int cd = stack.getNbt() == null ? 0 : stack.getNbt().getInt("cooldown");
            tooltip.add(Text.literal(cd == 0 ? "CD: 15s" : "CD: 15s   left: "+ cd +"s"));
            tooltip.add(Text.translatable("item.dabaosword.huoji.tooltip").formatted(Formatting.RED));
        }

        if (stack.getItem() == SkillCards.LUANJI) {
            int cd = stack.getNbt() == null ? 0 : stack.getNbt().getInt("cooldown");
            tooltip.add(Text.literal(cd == 0 ? "CD: 15s" : "CD: 15s   left: "+ cd +"s"));
            tooltip.add(Text.translatable("item.dabaosword.luanji.tooltip"));
        }

        if (stack.getItem() == SkillCards.QICE) {
            int cd = stack.getNbt() == null ? 0 : stack.getNbt().getInt("cooldown");
            tooltip.add(Text.literal(cd == 0 ? "CD: 20s" : "CD: 20s   left: "+ cd +"s"));
            tooltip.add(Text.translatable("item.dabaosword.qice.tooltip").formatted(Formatting.BLUE));
        }

        if (stack.getItem() == SkillCards.KANPO) {
            int cd = stack.getNbt() == null ? 0 : stack.getNbt().getInt("cooldown");
            tooltip.add(Text.literal(cd == 0 ? "CD: 10s" : "CD: 10s   left: "+ cd +"s"));
            tooltip.add(Text.translatable("item.dabaosword.kanpo.tooltip").formatted(Formatting.RED));
        }

        if (stack.getItem() == SkillCards.GUOSE) {
            int cd = stack.getNbt() == null ? 0 : stack.getNbt().getInt("cooldown");
            tooltip.add(Text.literal(cd == 0 ? "CD: 15s" : "CD: 15s   left: "+ cd +"s"));
            tooltip.add(Text.translatable("item.dabaosword.guose.tooltip").formatted(Formatting.GREEN));
        }

        if (stack.getItem() == SkillCards.GONGAO) {
            tooltip.add(Text.translatable("item.dabaosword.gongao.tooltip1").formatted(Formatting.BLUE));
            tooltip.add(Text.translatable("item.dabaosword.gongao.tooltip2").formatted(Formatting.BLUE));
        }

        if (stack.getItem() == SkillCards.LIEGONG) {
            tooltip.add(Text.translatable("item.dabaosword.liegong.tooltip1").formatted(Formatting.RED));
            tooltip.add(Text.translatable("item.dabaosword.liegong.tooltip2").formatted(Formatting.RED));
        }

        if (stack.getItem() == SkillCards.LEIJI) {
            tooltip.add(Text.translatable("item.dabaosword.leiji.tooltip"));
        }

        if (stack.getItem() == SkillCards.YIJI) {
            tooltip.add(Text.literal("CD: 20s"));
            tooltip.add(Text.translatable("item.dabaosword.yiji.tooltip").formatted(Formatting.BLUE));
        }

        if (stack.getItem() == SkillCards.JIZHI) {
            tooltip.add(Text.translatable("item.dabaosword.jizhi.tooltip").formatted(Formatting.RED));
        }

        if (stack.getItem() == SkillCards.KUROU) {
            tooltip.add(Text.translatable("item.dabaosword.kurou.tooltip").formatted(Formatting.GREEN));
        }

        if (stack.getItem() == SkillCards.LUOYI) {
            tooltip.add(Text.translatable("item.dabaosword.luoyi.tooltip").formatted(Formatting.BLUE));
        }

        if (stack.getItem() == SkillCards.TAOLUAN) {
            tooltip.add(Text.translatable("item.dabaosword.taoluan.tooltip"));
        }

        if (stack.getItem() == SkillCards.JUEQING) {
            tooltip.add(Text.translatable("item.dabaosword.jueqing.tooltip1").formatted(Formatting.BLUE));
            tooltip.add(Text.translatable("item.dabaosword.jueqing.tooltip2").formatted(Formatting.BLUE));
        }

        if (stack.getItem() == SkillCards.POJUN) {
            tooltip.add(Text.literal("CD: 10s"));
            tooltip.add(Text.translatable("item.dabaosword.pojun.tooltip").formatted(Formatting.GREEN));
        }

        if (stack.getItem() == SkillCards.KUANGGU) {
            tooltip.add(Text.literal("CD: 8s"));
            tooltip.add(Text.translatable("item.dabaosword.kuanggu.tooltip").formatted(Formatting.RED));
        }

        if (stack.getItem() == SkillCards.LIULI) {
            tooltip.add(Text.translatable("item.dabaosword.liuli.tooltip").formatted(Formatting.GREEN));
        }

        if (stack.getItem() == SkillCards.MASHU) {
            tooltip.add(Text.translatable("item.dabaosword.chitu.tooltip"));
        }

        if (stack.getItem() == SkillCards.FEIYING) {
            tooltip.add(Text.translatable("item.dabaosword.dilu.tooltip"));
        }
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity.getWorld() instanceof ServerWorld world) {
            world.getPlayers().forEach(player -> player.sendMessage(
                    Text.literal(entity.getEntityName()).append(Text.literal("装备了 ").append(stack.getName()))
            ));
        }
        super.onEquip(stack, slot, entity);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!user.getWorld().isClient && user.getCommandTags().contains("change_skill") && hand == Hand.OFF_HAND && user.isSneaking()) {
            ItemStack stack = user.getStackInHand(hand);
            if (stack.isIn(Tags.Items.SKILL)) {
                stack.setCount(0);
                changeSkill(user);
                user.getCommandTags().remove("change_skill");
            }
        }
        return super.use(world, user, hand);
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity.getWorld() instanceof ServerWorld world) {
            if (stack.getNbt() != null) {
                if (stack.getNbt().contains("cooldown")) {
                    int cd = stack.getNbt().getInt("cooldown");
                    if (world.getTime() % 20 == 0) {//世界时间除以20取余为0时，技能内置CD减一秒
                        NbtCompound nbt = new NbtCompound();
                        if (cd > 0) {cd--; nbt.putInt("cooldown", cd); stack.setNbt(nbt);}
                    }
                }
            }
        }
        super.tick(stack, slot, entity);
    }

    public void changeSkill(PlayerEntity player) {
        List<LootEntry> lootEntries = LootTableParser.parseLootTable(new Identifier("dabaosword", "loot_tables/change_skill.json"));
        LootEntry selectedEntry = selectRandomEntry(lootEntries);

        ItemStack stack = new ItemStack(Registries.ITEM.get(selectedEntry.item()));
        if (stack.getItem() != Items.AIR) voice(player, Sounds.GIFTBOX,3);
        player.giveItemStack(stack);
    }
}
