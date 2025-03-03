package io.github.brainage04.twitchplaysminecraft.util;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.effect.EnchantmentEffectEntry;
import net.minecraft.enchantment.effect.EnchantmentValueEffect;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.List;
import java.util.Objects;

public class ItemStackUtils {
    public static double getItemDps(ClientPlayerEntity player, ItemStack stack) {
        AttributeModifiersComponent attributeModifierComponent = stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);

        float baseDamage = computeValue(player.getAttributeBaseValue(EntityAttributes.ATTACK_DAMAGE), attributeModifierComponent, EntityAttributes.ATTACK_DAMAGE);
        float baseSpeed = computeValue(player.getAttributeBaseValue(EntityAttributes.ATTACK_SPEED), attributeModifierComponent, EntityAttributes.ATTACK_SPEED);

        for (RegistryEntry<Enchantment> registryEntry : stack.getEnchantments().getEnchantments()) {
            List<EnchantmentEffectEntry<EnchantmentValueEffect>> list = registryEntry.value().getEffect(EnchantmentEffectComponentTypes.DAMAGE);
            if (list.isEmpty()) continue;

            for (EnchantmentEffectEntry<EnchantmentValueEffect> effectEntry : list) {
                baseDamage = effectEntry.effect().apply(EnchantmentHelper.getLevel(registryEntry, stack), player.getWorld().getRandom(), baseDamage);
            }
        }

        // more than 2 speed does not matter as entities can only be hit 2 times/second
        baseSpeed = Math.min(baseSpeed, 2);

        return baseDamage * baseSpeed;
    }

    private static float computeValue(double baseValue, AttributeModifiersComponent component, RegistryEntry<EntityAttribute> attribute) {
        double d = baseValue;

        List<AttributeModifiersComponent.Entry> list1 = component.modifiers().stream().filter(
                c -> c.modifier().operation() == EntityAttributeModifier.Operation.ADD_VALUE
                        && Objects.equals(c.attribute().getIdAsString(), attribute.getIdAsString())
        ).toList();
        for (AttributeModifiersComponent.Entry entry : list1) {
            d += entry.modifier().value();
        }

        double e = d;

        List<AttributeModifiersComponent.Entry> list2 = component.modifiers().stream().filter(
                c -> c.modifier().operation() == EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                        && Objects.equals(c.attribute().getIdAsString(), attribute.getIdAsString())
        ).toList();
        for (AttributeModifiersComponent.Entry entry : list2) {
            e += d * entry.modifier().value();
        }

        List<AttributeModifiersComponent.Entry> list3 = component.modifiers().stream().filter(
                c -> c.modifier().operation() == EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                        && Objects.equals(c.attribute().getIdAsString(), attribute.getIdAsString())
        ).toList();
        for (AttributeModifiersComponent.Entry entry : list3) {
            e *= 1 + entry.modifier().value();
        }

        return (float) e;
    }
}
