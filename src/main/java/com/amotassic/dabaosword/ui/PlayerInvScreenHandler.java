package com.amotassic.dabaosword.ui;

import com.amotassic.dabaosword.api.CardEvents;
import com.amotassic.dabaosword.api.skill.ExData;
import com.amotassic.dabaosword.api.skill.Skill;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.TempInventory;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Consumer;

import static com.amotassic.dabaosword.api.CardEvents.cardDiscard;
import static com.amotassic.dabaosword.util.ModTools.*;
import static net.minecraft.screen.PlayerScreenHandler.*;

public class PlayerInvScreenHandler extends ScreenHandler {
    private final PlayerEntity target;
    private final TempInventory inv;
    private final ItemStack stack;
    private final Skill skill;
    private final List<Integer> clicks = new ArrayList<>();
    public final int rows;

    public PlayerInvScreenHandler(int syncId, PlayerInventory inv, PacketByteBuf buf) {
        this(syncId, new TempInventory(inv.player, paibei(), List.of()), (PlayerEntity) inv.player.getWorld().getEntityById(buf.readInt()), stringToSet(buf.readString()));
    }
    private static Set<Integer> stringToSet(String str) {
        String trimmed = str.substring(1, str.length() - 1);
        String[] parts = trimmed.split(", ");
        Set<Integer> set = new HashSet<>();
        for (String part : parts) if (!part.isEmpty()) set.add(Integer.parseInt(part));
        return set;
    }

    public PlayerInvScreenHandler(int syncId, TempInventory inventory, PlayerEntity target, Set<Integer> rowsToShow) {
        super(ModItems.PLAYER_INV_SCREEN_HANDLER, syncId);
        this.target = target;
        this.inv = inventory;
        this.stack = inv.getStack(81);
        this.skill = s(stack);
        this.rows = rowsToShow.size();
        int line = 0;
        for (int j = 0; j < 9; j++) {
            if (rowsToShow.contains(j)) {
                if (j == 0) {
                    for (int i = 0; i < 4; ++i) addSlot(new Slot(inv, i, 8 + i * 18, 18));
                    addSlot(new Slot(inv, 4, 8 + 4 * 18, 18) {
                        public Pair<Identifier, Identifier> getBackgroundSprite() {
                            return Pair.of(BLOCK_ATLAS_TEXTURE, EMPTY_HELMET_SLOT_TEXTURE);
                        }
                    });
                    addSlot(new Slot(inv, 5, 8 + 5 * 18, 18) {
                        public Pair<Identifier, Identifier> getBackgroundSprite() {
                            return Pair.of(BLOCK_ATLAS_TEXTURE, EMPTY_CHESTPLATE_SLOT_TEXTURE);
                        }
                    });
                    addSlot(new Slot(inv, 6, 8 + 6 * 18, 18) {
                        public Pair<Identifier, Identifier> getBackgroundSprite() {
                            return Pair.of(BLOCK_ATLAS_TEXTURE, EMPTY_LEGGINGS_SLOT_TEXTURE);
                        }
                    });
                    addSlot(new Slot(inv, 7, 8 + 7 * 18, 18) {
                        public Pair<Identifier, Identifier> getBackgroundSprite() {
                            return Pair.of(BLOCK_ATLAS_TEXTURE, EMPTY_BOOTS_SLOT_TEXTURE);
                        }
                    });
                    addSlot(new Slot(inv, 8, 8 + 8 * 18, 18) {
                        public Pair<Identifier, Identifier> getBackgroundSprite() {
                            return Pair.of(BLOCK_ATLAS_TEXTURE, EMPTY_OFFHAND_ARMOR_SLOT);
                        }
                    });
                }
                else {
                    for (int i = 0; i < 9; ++i) addSlot(new Slot(inv, i + j * 9, 8 + i * 18, 18 + line * 18));
                }
                line++;
            } else {
                for (int i = 0; i < 9; ++i) addSlot(new Slot(inv, i + j * 9, 114514, 114514));
            }
        }
        inv.setStack(82, paibei());
        for (int i = 0; i < 2; i++) addSlot(new Slot(inv, 81 + i, 114514, 114514));
    }

    @Override
    public void onSlotClick(int index, int button, SlotActionType action, PlayerEntity player) {
        if (index >= 0 && index < 81 && !player.getWorld().isClient) {
            //System.out.println(index + " button: " + button + " action: " + action);
            var selected = getStack(index);
            if (button == 65 && maxSelect() >= 100) forEachNonEmptySlot(s -> addClick(s.getIndex(), 99)); //全选
            if (button == 90) forEachNonEmptySlot(s -> setClick(s.getIndex(), 0)); //清空
            if (!selected.isEmpty()) skill.item.onSlotClick(this, player, skill, target, index, button, action);
            writeClicks();

            if (stack.isEmpty()) {
                ItemStack mainHand = player.getMainHandStack(); var mainCopy = mainHand.copy();
                var copy = selected.copy();

                if (!selected.isEmpty() && !ItemStack.areEqual(mainHand, selected)) {
                    mainHand.setCount(0); selected.setCount(0);
                    if (index >= 45) getCardPack(player).removeStack(index - 45);
                    player.setStackInHand(Hand.MAIN_HAND, copy);
                    give(player, mainCopy);
                } closeGUI(player);
            }

            if (!selected.isEmpty()) {
                if (stack.isOf(ModItems.STEAL)) {
                    Text message = Text.translatable("dabaosword.steal", player.getDisplayName(), target.getDisplayName(), selected.toHoverableText());
                    player.sendMessage(message);
                    target.sendMessage(message);
                    if (isCard(selected)) { //如果选择的物品是卡牌才触发事件
                        var exData = d().cards(selected, 1, index < 4);
                        CardEvents.cardMove(target, exData, player);
                    } else {
                        give(player, selected.copyWithCount(1)); /*顺手：复制一个物品*/
                        selected.decrement(1);
                    }
                    closeGUI(player);
                }

                if (stack.isOf(ModItems.DISCARD)) {
                    Text message = Text.translatable("dabaosword.discard", player.getDisplayName(), target.getDisplayName(), selected.toHoverableText());
                    player.sendMessage(message);
                    target.sendMessage(message);
                    var exData = d().cards(selected, 1, index < 4);
                    cardDiscard(target, exData);
                    closeGUI(player);
                }
            }
        }
    }

    @Override
    public void onClosed(PlayerEntity player) {skill.item.onGuiClose(this, player, skill, target);}

    /**获取该容器内对应slot上的物品，即使卡牌以牌背形态显示，也返回目标对应的卡牌*/
    public ItemStack getStack(int slot) {
        if (slot < 0 || slot > 80) return ItemStack.EMPTY;
        var item = inv.getStack(slot);
        if (inv.type == 1 && item.isOf(ModItems.GAIN_CARD) && inv.owner instanceof PlayerEntity pl) {
            if (slot < 45) return pl.getInventory().main.get(slot - 9);
            else return getCardPack(pl).cards.get(slot - 45);
        }
        return item;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {return ItemStack.EMPTY;}

    @Override
    public boolean canUse(PlayerEntity player) {
        var effect = player.getStatusEffect(ModItems.COOLDOWN2);
        return effect == null || effect.getAmplifier() != 2;
    }

    public void forEachNonEmptySlot(Consumer<Slot> action) {
        for (int i = 0; i < 81; i++) {
            Slot slot = getSlot(i);
            if (slot.hasStack()) action.accept(slot);
        }
    }

    private void writeClicks() {
        ItemStack cinfo = getSlot(82).getStack();
        NbtCompound nbt = new NbtCompound();
        nbt.putString("Clicks", getClickMap().toString());
        cinfo.setNbt(nbt);
    }

    public int maxSelect() {return skill.getMaxSelect();}

    private int getSlotClicked(int slot) {
        if (!clicks.contains(slot)) return 0;
        return getClickMap().get(slot);
    }

    /**使点击的槽位物品选择数量增加，如果增加后已选择物品数量大于最大可选数量，会自动移除最早的选择项*/
    public void addClick(int slot, int... count) {
        int num = count.length > 0 ? count[0] : 1;
        int n = getSlot(slot).getStack().getCount();
        num = Math.min(n - getSlotClicked(slot), num);
        for (int i = 0; i < num; i++) {
            clicks.add(slot);
            if (getSelectedCount() > maxSelect()) dropFirst();
        }
    }

    /**使点击的槽位物品选择数量减少*/
    public void dropClick(int slot, int... count) {
        if (!clicks.contains(slot)) return;
        int num = count.length > 0 ? count[0] : 1;
        for (int i = 0; i < num; i++) {
            if (!clicks.remove(Integer.valueOf(slot))) break;
        }
    }

    /**设置该槽位的选择数*/
    public void setClick(int slot, int count) {
        if (clicks.contains(slot)) dropClick(slot, 114514);
        if (count > 0) addClick(slot, count);
    }

    public void dropFirst(int... count) {
        if (clicks.isEmpty()) return;
        dropClick(clicks.get(0), count);
    }

    /**@return GUI中已选择卡牌的数量*/
    public int getSelectedCount() {return clicks.size();}

    public List<ItemStack> getSelected() {
        List<ItemStack> selected = new ArrayList<>();
        clicks.forEach(slot -> {
            ItemStack stack = getSlot(slot).getStack();
            if (!stack.isEmpty()) selected.add(stack.copyWithCount(1));
        });
        return selected;
    }

    public Map<Integer, Integer> getClickMap() {
        Map<Integer, Integer> map = new HashMap<>();
        for (Integer num : clicks) {
            if (map.containsKey(num)) {
                map.put(num, map.get(num) + 1);
            } else map.put(num, 1);
        }
        return map;
    }

    /**将GUI中已选的卡牌直接输出为ExData，省去手动获取已选卡牌的功夫，一般用于{@link com.amotassic.dabaosword.api.skill.ISkill#onGuiClose(PlayerInvScreenHandler, PlayerEntity, Skill, PlayerEntity)}*/
    public ExData toExData() {
        var exData = d(); var clickMap = getClickMap();
        clickMap.forEach((slot, count) -> {
            ItemStack stack = getStack(slot);
            if (isCard(stack)) exData.cards(stack, count, slot < 4);
        });
        return exData;
    }

}
