package myrrhion.identityorigins.powers;


import io.github.apace100.origins.power.PowerType;
import net.minecraft.entity.player.PlayerEntity;
import io.github.apace100.origins.power.Power;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;

import java.util.function.Predicate;

public class EdiblePower extends Power {
    private final Predicate<ItemStack> itemTag;
    private final int hunger;
    private final float saturation;
    private final int damage;

    public EdiblePower(PowerType<?> type, PlayerEntity player, Predicate<ItemStack> itemTag, int hunger, float saturation,int damage) {
        super(type, player);
        this.hunger = hunger;
        this.itemTag = itemTag;
        this.saturation = saturation;
        this.damage = damage;
    }

    public int getHunger() {
        return hunger;
    }

    public float getSaturation() {
        return saturation;
    }
    public boolean doesApply(ItemStack itemStack){
        return itemTag.test(itemStack);
    }

    public int getDamage() {
        return damage;
    }

    public Predicate<ItemStack> getItemTag() {
        return itemTag;
    }
}
