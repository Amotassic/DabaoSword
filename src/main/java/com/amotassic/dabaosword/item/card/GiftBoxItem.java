package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class GiftBoxItem extends Item implements ModTools {
    public GiftBoxItem(Settings settings) {super(settings);}

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.dabaosword.gift_box.tooltip").formatted(Formatting.GOLD));
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            ItemStack stack = user.getOffHandStack();
            float chance;
            if (!stack.isEmpty() && stack.getItem() == Items.GOLD_INGOT) {
                chance = 0.01f + 0.01f * stack.getCount();
                stack.setCount(0);
                giftBox(user, chance);
            }
            if (!stack.isEmpty() && stack.getItem() == Items.GOLD_BLOCK) {
                chance = 0.01f + 0.09f * stack.getCount();
                stack.decrement(Math.min(stack.getCount(), 11));
                giftBox(user, chance);
            }
            if (!user.isCreative()) user.getStackInHand(hand).decrement(1);
        }
        return super.use(world, user, hand);
    }

    public void giftBox(@NotNull PlayerEntity player, float chance) {
        if (new Random().nextFloat() < chance) {
            float i = new Random().nextFloat();
            if (i < 0.03) {player.giveItemStack(new ItemStack(SkillCards.YIJI));voice(player, Sounds.GIFTBOX,3);
            } else if (0.05 < i && i < 0.08) {player.giveItemStack(new ItemStack(SkillCards.TAOLUAN));voice(player, Sounds.GIFTBOX,3);
            } else if (0.1 < i && i < 0.13) {player.giveItemStack(new ItemStack(SkillCards.QUANJI));voice(player, Sounds.GIFTBOX,3);
            } else if (0.15 < i && i < 0.18) {player.giveItemStack(new ItemStack(SkillCards.QICE));voice(player, Sounds.GIFTBOX,3);
            } else if (0.2 < i && i < 0.23) {player.giveItemStack(new ItemStack(SkillCards.LUOYI));voice(player, Sounds.GIFTBOX,3);
            } else if (0.25 < i && i < 0.28) {player.giveItemStack(new ItemStack(SkillCards.LUANJI));voice(player, Sounds.GIFTBOX,3);
            } else if (0.3 < i && i < 0.33) {player.giveItemStack(new ItemStack(SkillCards.KUROU));voice(player, Sounds.GIFTBOX,3);
            } else if (0.35 < i && i < 0.38) {player.giveItemStack(new ItemStack(SkillCards.KANPO));voice(player, Sounds.GIFTBOX,3);
            } else if (0.4 < i && i < 0.43) {player.giveItemStack(new ItemStack(SkillCards.HUOJI));voice(player, Sounds.GIFTBOX,3);
            } else if (0.45 < i && i < 0.48) {player.giveItemStack(new ItemStack(SkillCards.GUOSE));voice(player, Sounds.GIFTBOX,3);
            } else if (0.5 < i && i < 0.53) {player.giveItemStack(new ItemStack(SkillCards.LIULI));voice(player, Sounds.GIFTBOX,3);
            } else if (0.55 < i && i < 0.58) {player.giveItemStack(new ItemStack(SkillCards.JIZHI));voice(player, Sounds.GIFTBOX,3);
            } else if (0.6 < i && i < 0.63) {player.giveItemStack(new ItemStack(SkillCards.KUANGGU));voice(player, Sounds.GIFTBOX,3);
            } else if (0.65 < i && i < 0.68) {player.giveItemStack(new ItemStack(SkillCards.POJUN));voice(player, Sounds.GIFTBOX,3);
            } else if (0.7 < i && i < 0.73) {player.giveItemStack(new ItemStack(SkillCards.JUEQING));voice(player, Sounds.GIFTBOX,3);
            } else if (0.75 < i && i < 0.78) {player.giveItemStack(new ItemStack(SkillCards.MASHU));voice(player, Sounds.GIFTBOX,3);
            } else if (0.99 < i) {player.giveItemStack(new ItemStack(SkillCards.FEIYING));voice(player, Sounds.GIFTBOX,3);
            }
        }
    }
}
