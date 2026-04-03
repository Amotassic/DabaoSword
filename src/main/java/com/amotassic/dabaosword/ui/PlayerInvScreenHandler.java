package com.amotassic.dabaosword.ui;

import com.amotassic.dabaosword.api.CardEvents;
import com.amotassic.dabaosword.api.skill.ExData;
import com.amotassic.dabaosword.api.skill.Skill;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.network.SimplePayload;
import com.amotassic.dabaosword.util.TempInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.Consumer;

import static com.amotassic.dabaosword.api.CardEvents.cardDiscard;
import static com.amotassic.dabaosword.util.ModTools.*;
import static net.minecraft.world.inventory.InventoryMenu.*;

public class PlayerInvScreenHandler extends AbstractContainerMenu {
    private final Player target;
    private final TempInventory inv;
    private final ItemStack stack;
    private final Skill skill;
    private final List<Integer> clicks = new ArrayList<>();
    public final int rows;

    public PlayerInvScreenHandler(int syncId, Inventory inv, SimplePayload buf) {
        this(syncId, new TempInventory(inv.player, paibei(), List.of()), (Player) inv.player.level().getEntity(Integer.parseInt(buf.name())), stringToSet(buf.value()));
    }
    private static Set<Integer> stringToSet(String str) {
        String trimmed = str.substring(1, str.length() - 1);
        String[] parts = trimmed.split(", ");
        Set<Integer> set = new HashSet<>();
        for (String part : parts) if (!part.isEmpty()) set.add(Integer.parseInt(part));
        return set;
    }

    public PlayerInvScreenHandler(int syncId, TempInventory inventory, Player target, Set<Integer> rowsToShow) {
        super(ModItems.PLAYER_INV_SCREEN_HANDLER, syncId);
        this.target = target;
        this.inv = inventory;
        this.stack = inv.getItem(81);
        this.skill = s(stack);
        this.rows = rowsToShow.size();
        int line = 0;
        for (int j = 0; j < 9; j++) {
            if (rowsToShow.contains(j)) {
                if (j == 0) {
                    for (int i = 0; i < 4; ++i) addSlot(new Slot(inv, i, 8 + i * 18, 18));
                    List<Object[]> slotData = Arrays.asList(
                            new Object[]{4, EMPTY_ARMOR_SLOT_HELMET},
                            new Object[]{5, EMPTY_ARMOR_SLOT_CHESTPLATE},
                            new Object[]{6, EMPTY_ARMOR_SLOT_LEGGINGS},
                            new Object[]{7, EMPTY_ARMOR_SLOT_BOOTS},
                            new Object[]{8, EMPTY_ARMOR_SLOT_SHIELD}
                    ); //护甲和副手的格子
                    for (Object[] data : slotData) {
                        int slotId = (int) data[0]; Identifier texture = (Identifier) data[1];
                        addSlot(new Slot(inv, slotId, 8 + slotId * 18, 18) {
                            public Identifier getNoItemIcon() {return texture;}
                        });
                    }
                }
                else {
                    for (int i = 0; i < 9; ++i) addSlot(new Slot(inv, i + j * 9, 8 + i * 18, 18 + line * 18));
                }
                line++;
            } else {
                for (int i = 0; i < 9; ++i) addSlot(new Slot(inv, i + j * 9, 114514, 114514));
            }
        }
        inv.setItem(82, paibei());
        for (int i = 0; i < 2; i++) addSlot(new Slot(inv, 81 + i, 114514, 114514));
    }

    @Override
    public void clicked(int index, int button, @NonNull ContainerInput action, @NonNull Player player) {
        if (index >= 0 && index < 81 && !player.level().isClientSide()) {
            //System.out.println(index + " button: " + button + " action: " + action);
            var selected = getStack(index);
            if (button == 65 && maxSelect() >= 100) forEachNonEmptySlot(s -> addClick(s.getContainerSlot(), 99)); //全选
            if (button == 90) forEachNonEmptySlot(s -> setClick(s.getContainerSlot(), 0)); //清空
            if (!selected.isEmpty()) skill.item.onSlotClick(this, player, skill, target, index, button, action);
            writeClicks();

            if (stack.isEmpty()) {
                boolean right = action == ContainerInput.PICKUP && button == 1;
                ItemStack toReplace = right ? player.getMainHandItem() : player.getOffhandItem();
                var mainCopy = toReplace.copy(); var copy = selected.copy();

                if (!selected.isEmpty() && !ItemStack.matches(toReplace, selected)) {
                    toReplace.setCount(0); selected.setCount(0);
                    if (index >= 45) getCardPack(player).removeItemNoUpdate(index - 45);
                    player.setItemInHand(right ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, copy);
                    give(player, mainCopy);
                } closeGUI(player);
            }

            if (!selected.isEmpty()) {
                if (stack.is(ModItems.STEAL)) {
                    Component message = Component.translatable("dabaosword.steal", player.getDisplayName(), target.getDisplayName(), selected.getDisplayName());
                    player.sendSystemMessage(message);
                    target.sendSystemMessage(message);
                    if (isCard(selected)) { //如果选择的物品是卡牌才触发事件
                        var exData = d().cards(selected, 1, index < 4);
                        CardEvents.cardMove(target, exData, player);
                    } else {
                        give(player, selected.copyWithCount(1)); /*顺手：复制一个物品*/
                        selected.shrink(1);
                    }
                    closeGUI(player);
                }

                if (stack.is(ModItems.DISCARD)) {
                    Component message = Component.translatable("dabaosword.discard", player.getDisplayName(), target.getDisplayName(), selected.getDisplayName());
                    player.sendSystemMessage(message);
                    target.sendSystemMessage(message);
                    var exData = d().cards(selected, 1, index < 4);
                    cardDiscard(target, exData);
                    closeGUI(player);
                }
            }
        }
    }

    @Override
    public void removed(@NonNull Player player) {skill.item.onGuiClose(this, player, skill, target);}

    /**获取该容器内对应slot上的物品，即使卡牌以牌背形态显示，也返回目标对应的卡牌*/
    public ItemStack getStack(int slot) {
        if (slot < 0 || slot > 80) return ItemStack.EMPTY;
        var item = inv.getItem(slot);
        if (inv.type == 1 && item.is(ModItems.GAIN_CARD) && inv.owner instanceof Player pl) {
            if (slot < 45) return pl.getInventory().getNonEquipmentItems().get(slot - 9);
            else return getCardPack(pl).cards.get(slot - 45);
        }
        return item;
    }

    @Override
    public @NonNull ItemStack quickMoveStack(@NonNull Player player, int i) {return ItemStack.EMPTY;}

    @Override
    public boolean stillValid(Player player) {
        var effect = player.getEffect(ModItems.COOLDOWN2);
        return effect == null || effect.getAmplifier() != 2;
    }

    public void forEachNonEmptySlot(Consumer<Slot> action) {
        for (int i = 0; i < 81; i++) {
            Slot slot = getSlot(i);
            if (slot.hasItem()) action.accept(slot);
        }
    }

    private void writeClicks() {
        ItemStack cinfo = getSlot(82).getItem();
        var nbt = new CompoundTag();
        nbt.putString("Clicks", getClickMap().toString());
        setNbt(cinfo, nbt);
    }

    public int maxSelect() {return skill.getMaxSelect();}

    private int getSlotClicked(int slot) {
        if (!clicks.contains(slot)) return 0;
        return getClickMap().get(slot);
    }

    /**使点击的槽位物品选择数量增加，如果增加后已选择物品数量大于最大可选数量，会自动移除最早的选择项*/
    public void addClick(int slot, int... count) {
        int num = count.length > 0 ? count[0] : 1;
        int n = getSlot(slot).getItem().getCount();
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
        dropClick(clicks.getFirst(), count);
    }

    /**@return GUI中已选择卡牌的数量*/
    public int getSelectedCount() {return clicks.size();}

    public List<ItemStack> getSelected() {
        List<ItemStack> selected = new ArrayList<>();
        clicks.forEach(slot -> {
            ItemStack stack = getSlot(slot).getItem();
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

    /**将GUI中已选的卡牌直接输出为ExData，省去手动获取已选卡牌的功夫，一般用于{@link com.amotassic.dabaosword.api.skill.ISkill#onGuiClose(PlayerInvScreenHandler, Player, Skill, Player)}*/
    public ExData toExData() {
        var exData = d(); var clickMap = getClickMap();
        clickMap.forEach((slot, count) -> {
            ItemStack stack = getStack(slot);
            if (isCard(stack)) exData.cards(stack, count, slot < 4);
        });
        return exData;
    }

}
