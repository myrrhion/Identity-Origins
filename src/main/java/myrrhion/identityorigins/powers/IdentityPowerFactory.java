package myrrhion.identityorigins.powers;

import io.github.apace100.origins.power.Active;
import io.github.apace100.origins.power.VariableIntPower;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.registry.ModRegistries;
import io.github.apace100.origins.util.HudRender;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import myrrhion.identityorigins.IdentityOrigins;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.*;

public class IdentityPowerFactory {
    @SuppressWarnings("unchecked")
    public static void register() {
        register(new PowerFactory<>(IdentityOrigins.identifier("transform"),
                new SerializableData()
                        .add("cooldown", SerializableDataType.INT)
                        .add("key", SerializableDataType.BACKWARDS_COMPATIBLE_KEY, new Active.Key())
                        .add("entity_type", SerializableDataType.ENTITY_TYPE)
                        .add("hud_render", SerializableDataType.HUD_RENDER)
                ,
                data -> (type,player) ->{
                    TransformPower power = new TransformPower(type,player,
                            data.getInt("cooldown"),
                            (HudRender)data.get("hud_render"),
                            (EntityType)data.get("entity_type")
                            );

                    power.setKey((Active.Key)data.get("key"));
                    return power;
                })
                .allowCondition());
        register(new PowerFactory<>(IdentityOrigins.identifier("edible"),
                new SerializableData()
                        .add("item_condition",SerializableDataType.ITEM_CONDITION)
                        .add("hunger",SerializableDataType.INT,1)
                        .add("saturation",SerializableDataType.FLOAT,0.5f)
                ,
                data -> (type,player) ->{
                    return new EdiblePower(type,
                            player,
                            data.isPresent("item_condition") ? (ConditionFactory<ItemStack>.Instance)data.get("item_condition") : item -> true,
                            data.getInt("hunger"),
                            data.getFloat("saturation"),
                            data.isPresent("damage") ? data.getInt("damage"):29);
                }).allowCondition()
        );
         }

    private static void register(PowerFactory<?> factory) {
        Registry.register(ModRegistries.POWER_FACTORY, factory.getSerializerId(), factory);
    }
}
