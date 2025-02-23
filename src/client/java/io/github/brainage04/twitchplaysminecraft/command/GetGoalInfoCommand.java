package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.AdvancementUtils;
import io.github.brainage04.twitchplaysminecraft.util.RomanNumber;
import io.github.brainage04.twitchplaysminecraft.util.StringUtils;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.advancement.criterion.*;
import net.minecraft.block.Block;
import net.minecraft.component.Component;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.inventory.SlotRange;
import net.minecraft.item.Item;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.potion.Potion;
import net.minecraft.predicate.*;
import net.minecraft.predicate.entity.*;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GetGoalInfoCommand {
    private static List<MutableText> textList = new ArrayList<>();

    // general util
    private static void addIfNotEmpty(List<MutableText> first, MutableText... second) {
        for (MutableText text : second) {
            if (!text.getString().isEmpty()) {
                first.add(text);
            }
        }
    }

    private static <T extends Number> String parseNumberRange(NumberRange<T> range) {
        boolean min = range.min().isPresent();
        boolean max = range.max().isPresent();

        if (min && max) return "%s-%s".formatted(range.min().get(), range.max().get());
        if (!min && max) return "%s or less".formatted(range.max().get());
        if (min && !max) return "%s or more".formatted(range.min().get());
        else return "?";
    }

    private static String intRangeToRomanNumberals(NumberRange.IntRange range) {
        boolean min = range.min().isPresent();
        boolean max = range.max().isPresent();

        if (min && max) return "%s-%s".formatted(RomanNumber.toRoman(range.min().get()), RomanNumber.toRoman(range.max().get()));
        if (!min && max) return "%s or less".formatted(RomanNumber.toRoman(range.max().get()));
        if (min && !max) return "%s or more".formatted(RomanNumber.toRoman(range.min().get()));
        else return "";
    }

    private static void parseDistancePredicate(DistancePredicate predicate) {
        textList.add(Text.literal("Distance:"));

        String x = parseNumberRange(predicate.x());
        if (!x.isEmpty()) textList.add(Text.literal("X: %s".formatted(x)));

        String y = parseNumberRange(predicate.y());
        if (!x.isEmpty()) textList.add(Text.literal("Y: %s".formatted(y)));

        String z = parseNumberRange(predicate.z());
        if (!x.isEmpty()) textList.add(Text.literal("Z: %s".formatted(z)));

        String horizontal = parseNumberRange(predicate.horizontal());
        if (!x.isEmpty()) textList.add(Text.literal("Horizontal Distance: %s".formatted(horizontal)));

        String absolute = parseNumberRange(predicate.absolute());
        if (!x.isEmpty()) textList.add(Text.literal("Absolute Distance: %s".formatted(absolute)));
    }

    private static void parseEntityTypePredicate(EntityTypePredicate predicate) {
        if (predicate.types().size() < 1) return;

        textList.add(Text.literal("Valid entities: ").append(
                String.join(
                        ", ",
                        predicate.types().stream().map(
                                type -> type.value().getBaseClass().toString()
                        ).toArray(String[]::new)
                )
        ));
    }

    private static void parseEntityPredicate(EntityPredicate predicate, String prefix) {
        textList.add(Text.literal(prefix));

        predicate.type().ifPresent(GetGoalInfoCommand::parseEntityTypePredicate);
        predicate.distance().ifPresent(GetGoalInfoCommand::parseDistancePredicate);

        //predicate.movement().ifPresent(p -> {});
        //
        //EntityPredicate.PositionalPredicates location = p.location();
        //
        //predicate.effects().ifPresent(p -> {});
        //predicate.nbt().ifPresent(p -> {});
        //predicate.flags().ifPresent(p -> {});
        //predicate.equipment().ifPresent(p -> {});
        //predicate.typeSpecific().ifPresent(p -> {});
        //predicate.periodicTick().ifPresent(p -> {});
        //predicate.vehicle().ifPresent(p -> {});
        //predicate.passenger().ifPresent(p -> {});
        //predicate.targetedEntity().ifPresent(p -> {});
        //predicate.team().ifPresent(p -> {});
        //predicate.slots().ifPresent(p -> {});
    }

    private static void parseTagPredicates(List<TagPredicate<DamageType>> tags) {
        if (tags.isEmpty()) return;

        textList.add(Text.literal("Damage types: ").append(
                String.join(
                        ", ",
                        tags.stream().map(tag ->
                                "%s (%s)".formatted(
                                        tag.tag().id().toString(),
                                        tag.expected() ? "expected" : "not expected"
                                )
                        ).toArray(String[]::new)
                )
        ));
    }

    private static void parseDamageSourcePredicate(DamageSourcePredicate damageSource, String prefix) {
        textList.add(Text.literal(prefix));

        parseTagPredicates(damageSource.tags());
        damageSource.directEntity().ifPresent(p -> textList.add(Text.literal(p.toString())));
        damageSource.sourceEntity().ifPresent(p -> textList.add(Text.literal(p.toString())));
        damageSource.isDirect().ifPresent(p -> textList.add(Text.literal(p ? "Damage must be direct" : "Damage can be direct or non-direct")));
    }

    private static void parseDamagePredicate(DamagePredicate predicate, String prefix) {
        textList.add(Text.literal(prefix));

        textList.add(Text.literal("Deal %s damage".formatted(parseNumberRange(predicate.dealt()))));
        textList.add(Text.literal("Take %s damage".formatted(parseNumberRange(predicate.taken()))));
        predicate.sourceEntity().ifPresent(p -> parseEntityPredicate(p, "Entity requirements:"));
        predicate.blocked().ifPresent(p -> textList.add(Text.literal("Damage blocked: %s".formatted(p.toString()))));
        predicate.type().ifPresent(p -> parseDamageSourcePredicate(p, "Damage source requirements:"));
    }

    private static void parseLootContextPredicate(LootContextPredicate predicate, String prefix) {
        textList.add(Text.literal(prefix));

        for (LootCondition condition : predicate.conditions) {
            Identifier conditionId = Registries.LOOT_CONDITION_TYPE.getId(condition.getType());
            if (conditionId == null) continue;
            textList.add(Text.literal(StringUtils.screamingSnakeCaseToPascalCase(conditionId.getPath())));
        }
    }

    private static String getDimensionName(RegistryKey<World> key) {
        return StringUtils.snakeCaseToPascalCase(key.getValue().getPath());
    }

    private static void parseItemPredicate(ItemPredicate predicate, String prefix, String suffix) {
        if (predicate.items().isEmpty()) return;

        textList.add(Text.literal("%s %s %s".formatted(prefix, parseNumberRange(predicate.count()), suffix)));
        RegistryEntryList<Item> items = predicate.items().get();
        for (int i = 0; i < items.size(); i++) {
            RegistryEntry<Item> entry = items.get(i);

            MutableText text = Text.literal("> ")
                    .append(entry.value().getName());
            if (i != items.size() - 1) text.append(", ");

            textList.add(text);
        }

        textList.add(Text.literal("With the following components:"));
        List<Component<?>> components = predicate.components().components;
        for (Component<?> component : components) {
            Identifier componentId = Registries.DATA_COMPONENT_TYPE.getId(component.type());
            if (componentId == null) continue;

            textList.add(Text.literal("> ")
                    .append(StringUtils.snakeCaseToPascalCase(componentId.getPath()))
                    .append(": ")
                    .append(component.value().toString()));
        }
    }

    public static void parseBlockAndStatePredicate(Block block, Optional<StatePredicate> state, String prefix) {
        MutableText text = Text.literal("%s a ".formatted(prefix))
                .append(block.getName())
                .append(" block");

        state.ifPresentOrElse(p ->
            text.append(" with the following state: %s".formatted(
                    String.join(
                            ", ",
                            p.conditions().stream().map(StatePredicate.Condition::key).toArray(String[]::new)
                    )
            )), () -> textList.add(text));
    }

    private static void parseLocationPredicate(LocationPredicate predicate, String prefix) {
        textList.add(Text.literal(prefix));

        predicate.position().ifPresent(p -> {return;});
        predicate.biomes().ifPresent(p -> {return;});
        predicate.structures().ifPresent(p -> {return;});
        predicate.dimension().ifPresent(p -> {return;});
        predicate.smokey().ifPresent(p -> {return;});
        predicate.light().ifPresent(p -> {return;});
        predicate.block().ifPresent(p -> {return;});
        predicate.fluid().ifPresent(p -> {return;});
        predicate.canSeeSky().ifPresent(p -> {return;});
    }

    private static void parseSlots(SlotsPredicate predicate) {
        for (Map.Entry<SlotRange, ItemPredicate> entry : predicate.slots().entrySet()) {
            textList.add(Text.literal("Slots %s:".formatted(
                    String.join(
                            ", ",
                            entry.getKey().getSlotIds().stream().map(Object::toString).toArray(String[]::new)
                    )
            )));

            parseItemPredicate(entry.getValue(), "", "of the following items:");
        }
    }

    // todo: account for ALL criterion in the net.minecraft.advancement.criterion package
    private static List<MutableText> parseCriterion(String id, AdvancementCriterion<?> advancementCriterion) {
        textList.clear();

        CriterionConditions conditions = advancementCriterion.conditions();

        if (conditions instanceof AnyBlockUseCriterion.Conditions) textList.add(Text.literal("Use any block"));
        if (conditions instanceof BeeNestDestroyedCriterion.Conditions) textList.add(Text.literal("Destroy a Bee Nest"));
        if (conditions instanceof BredAnimalsCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<LootContextPredicate> parent,
                Optional<LootContextPredicate> partner,
                Optional<LootContextPredicate> child
        )) {
            EntityType<?> entityType = Registries.ENTITY_TYPE.get(Identifier.of(id));
            Identifier entityId = Registries.ENTITY_TYPE.getId(entityType);

            textList.add(Text.literal("Breed two ")
                    .append(Text.translatable("entity.%s.%s".formatted(entityId.getNamespace(), entityId.getPath())))
                    .append("s"));

            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
            parent.ifPresent(p -> parseLootContextPredicate(p, "First parent requirements:"));
            partner.ifPresent(p -> parseLootContextPredicate(p, "Second parent requirements:"));
            child.ifPresent(p -> parseLootContextPredicate(p, "Child requirements:"));
        }
        if (conditions instanceof BrewedPotionCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<RegistryEntry<Potion>> potion
        )) {
            potion.flatMap(RegistryEntry::getKey).ifPresent(key -> {
                Potion potionItem = Registries.POTION.get(key);
                if (potionItem == null) return;

                textList.add(Text.literal("Brew a ")
                        .append(Text.translatable("item.minecraft.potion.effect.%s".formatted(potionItem.getBaseName()))));
            });

            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
        }
        if (conditions instanceof ChangedDimensionCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<RegistryKey<World>> from,
                Optional<RegistryKey<World>> to
        )) {
            MutableText text = Text.literal("Travel ");

            from.ifPresent(key -> {
                text.append("from the ")
                        .append(getDimensionName(key))
                        .append(" Dimension");
            });
            to.ifPresent(key -> {
                text.append("to the ")
                        .append(getDimensionName(key))
                        .append(" Dimension");
            });

            textList.add(text);

            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
        }
        if (conditions instanceof ChanneledLightningCriterion.Conditions(
                Optional<LootContextPredicate> player,
                List<LootContextPredicate> victims
        )) {
            textList.add(Text.literal("Throw a Trident with Channeling at an entity"));

            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
            for (int i = 0; i < victims.size(); i++) {
                LootContextPredicate lootContextPredicate = victims.get(i);
                parseLootContextPredicate(lootContextPredicate, "Victim %d/%d:".formatted(i + 1, victims.size()));
            }
        }
        if (conditions instanceof ConstructBeaconCriterion.Conditions) textList.add(Text.literal("Construct a Beacon"));
        if (conditions instanceof ConsumeItemCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<ItemPredicate> item
        )) {
            textList.add(Text.literal("Consume items"));

            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
            item.ifPresent(p -> parseItemPredicate(p, "Consume", "of the following items:"));
        }
        if (conditions instanceof CuredZombieVillagerCriterion.Conditions) textList.add(Text.literal("Cure a Zombie Villager"));
        if (conditions instanceof DefaultBlockUseCriterion.Conditions) textList.add(Text.literal("Use any block"));
        if (conditions instanceof EffectsChangedCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<EntityEffectPredicate> effects,
                Optional<LootContextPredicate> source
        )) {
            textList.add(Text.literal("Obtain status effects"));

            effects.ifPresent(p -> {
                textList.add(Text.literal("Effects:"));

                p.effects().forEach(((statusEffectRegistryEntry, effectData) -> {
                    if (statusEffectRegistryEntry.getKey().isEmpty()) return;
                    StatusEffect statusEffect = Registries.STATUS_EFFECT.get(statusEffectRegistryEntry.getKey().get());
                    if (statusEffect == null) return;

                    textList.add(Text.literal("> ")
                            .append(statusEffect.getName())
                            .append(" ")
                            .append(intRangeToRomanNumberals(effectData.amplifier()))
                            .append(", ")
                            .append("duration of %s".formatted(parseNumberRange(effectData.duration()))));
                }));
            });
            source.ifPresent(p -> parseLootContextPredicate(p, "Requirements for the status effect source:"));
            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
        }
        if (conditions instanceof EnchantedItemCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<ItemPredicate> item,
                NumberRange.IntRange levels
        )) {
            textList.add(Text.literal("Enchant an item"));

            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
            item.ifPresent(p -> {
                String levelsString = parseNumberRange(levels);
                levelsString = !levelsString.isEmpty() ? " with %s levels".formatted(levelsString) : "";

                parseItemPredicate(p, "Enchant", "of the following items%s:".formatted(levelsString));
            });
        }
        if (conditions instanceof EnterBlockCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<RegistryEntry<Block>> block,
                Optional<StatePredicate> state
        )) {
            block.flatMap(RegistryEntry::getKey).ifPresent(registryKey -> {
                Block actualBlock = Registries.BLOCK.get(registryKey);
                if (actualBlock == null) return;

                parseBlockAndStatePredicate(actualBlock, state, "Enter");
            });

            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
        }
        if (conditions instanceof EntityHurtPlayerCriterion.Conditions(
                Optional<LootContextPredicate> player, Optional<DamagePredicate> damage
        )) {
            textList.add(Text.literal("Get hurt by an entity"));

            damage.ifPresent(p -> parseDamagePredicate(p, "Damage requirements:"));
            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
        }
        if (conditions instanceof FallAfterExplosionCriterion.Conditions(
                Optional<LootContextPredicate> player, Optional<LocationPredicate> startPosition,
                Optional<DistancePredicate> distance, Optional<LootContextPredicate> cause
        )) {
            textList.add(Text.literal("Fall after an explosion"));

            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
            startPosition.ifPresent(p -> parseLocationPredicate(p, "Starting position:"));
            cause.ifPresent(p -> parseLootContextPredicate(p, "Cause for explosion:"));
            distance.ifPresent(GetGoalInfoCommand::parseDistancePredicate);
        }
        if (conditions instanceof FilledBucketCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<ItemPredicate> item
        )) {
            textList.add(Text.literal("Fill an empty Bucket"));

            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
            item.ifPresent(p -> parseItemPredicate(p, "Obtain", "of the following items:"));
        }
        if (conditions instanceof FishingRodHookedCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<ItemPredicate> rod,
                Optional<LootContextPredicate> entity,
                Optional<ItemPredicate> item
        )) {
            textList.add(Text.literal("Catch an item or an entity with a Fishing Rod"));

            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
            rod.ifPresent(p -> parseItemPredicate(p, "Hook", "Fishing Rods with:"));
            entity.ifPresent(p -> parseLootContextPredicate(p, "Entity requirements:"));
            item.ifPresent(p -> parseItemPredicate(p, "Catch", "of the following items:"));
        }
        if (conditions instanceof ImpossibleCriterion.Conditions) textList.add(Text.literal("Impossible"));
        if (conditions instanceof InventoryChangedCriterion.Conditions(
                Optional<LootContextPredicate> player,
                InventoryChangedCriterion.Conditions.Slots slots,
                List<ItemPredicate> items
        )) {
            textList.add(Text.literal("Experience inventory changes"));

            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
            parseSlots(slots, "Inventory slot requirements:");
            for (int i = 0; i < items.size(); i++) {
                ItemPredicate itemPredicate = items.get(i);
                parseItemPredicate(itemPredicate, "Obtain", "of the following items (Requirement %d/%d):".formatted(i + 1, items.size()));
            }
        }
        if (conditions instanceof ItemCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<LootContextPredicate> location
        )) {
            textList.add(Text.literal("Obtain an item under certain conditions"));

            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
            location.ifPresent(p -> parseLootContextPredicate(p, "Location requirements:"));
        }
        if (conditions instanceof ItemDurabilityChangedCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<ItemPredicate> item,
                NumberRange.IntRange durability,
                NumberRange.IntRange delta
        )) {
            textList.add(Text.literal("Damage an item"));

            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
            item.ifPresent(p -> parseItemPredicate(p, "Damage", "of the following items:"));
            textList.add(Text.literal("Durability range: %s (change of %s)".formatted(parseNumberRange(durability), parseNumberRange(delta))));
        }
        if (conditions instanceof KilledByArrowCriterion.Conditions(
                Optional<LootContextPredicate> player,
                List<LootContextPredicate> victims,
                NumberRange.IntRange uniqueEntityTypes,
                Optional<ItemPredicate> firedFromWeapon
        )) {
            textList.add(Text.literal("Kill entities with arrows"));

            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));

            textList.add(Text.literal("Required entities (%s types):".formatted(parseNumberRange(uniqueEntityTypes))));
            for (int i = 0; i < victims.size(); i++) {
                LootContextPredicate lootContextPredicate = victims.get(i);
                parseLootContextPredicate(lootContextPredicate, "Victim %d/%d:".formatted(i + 1, victims.size()));
            }

            firedFromWeapon.ifPresent(p -> parseItemPredicate(p, "Use", "of the following weapon:"));
        }
        if (conditions instanceof LevitationCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<DistancePredicate> distance,
                NumberRange.IntRange duration
        )) {
            textList.add(Text.literal("Levitate for %s seconds".formatted(parseNumberRange(duration))));

            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
            distance.ifPresent(GetGoalInfoCommand::parseDistancePredicate);
        }
        if (conditions instanceof LightningStrikeCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<LootContextPredicate> lightning,
                Optional<LootContextPredicate> bystander
        )) {
            textList.add(Text.literal("Lightning strike event"));

            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
            lightning.ifPresent(p -> parseLootContextPredicate(p, "Lightning requirements:"));
            bystander.ifPresent(p -> parseLootContextPredicate(p, "Bystander requirements:"));
        }
        if (conditions instanceof OnKilledCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<LootContextPredicate> entity,
                Optional<DamageSourcePredicate> killingBlow
        )) {
            textList.add(Text.literal("Kill an entity"));

            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
            entity.ifPresent(p -> parseLootContextPredicate(p, "Entity requirements:"));
            killingBlow.ifPresent(p -> parseDamageSourcePredicate(p, "Killing blow requirements:"));
        }
        if (conditions instanceof PlayerGeneratesContainerLootCriterion.Conditions(
                Optional<LootContextPredicate> player,
                RegistryKey<LootTable> lootTable
        )) {
            textList.add(Text.literal("Generate the \"%s\" loot table by opening a container (such as a Chest)".formatted(lootTable.getValue().toString())));

            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
        }
        if (conditions instanceof PlayerHurtEntityCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<DamagePredicate> damage,
                Optional<LootContextPredicate> entity
        )) {
            textList.add(Text.literal("Hurt an entity"));

            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
            damage.ifPresent(p -> parseDamagePredicate(p, "Damage requirements:"));
            entity.ifPresent(p -> parseLootContextPredicate(p, "Entity requirements:"));
        }
        if (conditions instanceof PlayerInteractedWithEntityCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<ItemPredicate> item,
                Optional<LootContextPredicate> entity
        )) {
            textList.add(Text.literal("Interact with an entity using a specific item"));

            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
            item.ifPresent(p -> parseItemPredicate(p, "Use", "of the following items:"));
            entity.ifPresent(p -> parseLootContextPredicate(p, "Entity requirements:"));
        }
        if (conditions instanceof RecipeCraftedCriterion.Conditions(
                Optional<LootContextPredicate> player,
                RegistryKey<Recipe<?>> recipeId,
                List<ItemPredicate> ingredients
        )) {
            textList.add(Text.literal("Craft the \"%s\" recipe using the following ingredients:".formatted(recipeId.getValue().toString())));

            for (int i = 0; i < ingredients.size(); i++) {
                ItemPredicate itemPredicate = ingredients.get(i);
                parseItemPredicate(itemPredicate, "Ingredient %d/%d".formatted(i + 1, ingredients.size()), ":");
            }

            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
        }
        if (conditions instanceof RecipeUnlockedCriterion.Conditions(
                Optional<LootContextPredicate> player,
                RegistryKey<Recipe<?>> recipe
        )) {
            textList.add(Text.literal("Unlock the \"%s\" recipe".formatted(recipe.getValue().toString())));

            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
        }
        if (conditions instanceof ShotCrossbowCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<ItemPredicate> item
        )) {
            item.ifPresent(p -> parseItemPredicate(p, "Shoot", "of the following items with a crossbow:"));
            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
        }
        if (conditions instanceof SlideDownBlockCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<RegistryEntry<Block>> block,
                Optional<StatePredicate> state
        )) {
            block.flatMap(RegistryEntry::getKey).ifPresent(registryKey -> {
                Block actualBlock = Registries.BLOCK.get(registryKey);
                if (actualBlock == null) return;

                parseBlockAndStatePredicate(actualBlock, state, "Slide down");
            });
            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
        }
        if (conditions instanceof StartedRidingCriterion.Conditions(
                Optional<LootContextPredicate> player
        )) {
            textList.add(Text.literal("Start riding an entity"));

            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
        }
        if (conditions instanceof SummonedEntityCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<LootContextPredicate> entity
        )) {
            textList.add(Text.literal("Summon an entity"));

            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
            entity.ifPresent(p -> parseLootContextPredicate(p, "Entity requirements:"));
        }
        if (conditions instanceof TameAnimalCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<LootContextPredicate> entity
        )) {
            EntityType<?> entityType = Registries.ENTITY_TYPE.get(Identifier.of(id));
            Identifier entityId = Registries.ENTITY_TYPE.getId(entityType);

            textList.add(Text.literal("Tame a ")
                    .append(Text.translatable("entity.%s.%s".formatted(entityId.getNamespace(), entityId.getPath())))
                    .append("s"));

            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
            entity.ifPresent(p -> parseLootContextPredicate(p, "Entity requirements:"));
        }
        if (conditions instanceof TargetHitCriterion.Conditions(
                Optional<LootContextPredicate> player,
                NumberRange.IntRange signalStrength,
                Optional<LootContextPredicate> projectile
        )) {
            textList.add(Text.literal("Hit a Target Block and make it output a redstone signal of %s".formatted(parseNumberRange(signalStrength))));

            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
            projectile.ifPresent(p -> parseLootContextPredicate(p, "Projectile requirements:"));
        }
        if (conditions instanceof ThrownItemPickedUpByEntityCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<ItemPredicate> item,
                Optional<LootContextPredicate> entity
        )) {
            textList.add(Text.literal("Pick up an item that was thrown by an entity"));

            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
            item.ifPresent(p -> parseItemPredicate(p, "Pick up", "of the following items:"));
            entity.ifPresent(p -> parseLootContextPredicate(p, "Entity requirements:"));
        }
        if (conditions instanceof TickCriterion.Conditions(
                Optional<LootContextPredicate> player
        )) {
            textList.add(Text.literal("Tick (?)"));

            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
        }
        if (conditions instanceof TravelCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<LocationPredicate> startPosition,
                Optional<DistancePredicate> distance
        )) {
            textList.add(Text.literal("Travel a certain distance from a certain starting position"));

            startPosition.ifPresent(p -> parseLocationPredicate(p, "Start position:"));
            distance.ifPresent(GetGoalInfoCommand::parseDistancePredicate);
            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
        }
        if (conditions instanceof UsedEnderEyeCriterion.Conditions(
                Optional<LootContextPredicate> player,
                NumberRange.DoubleRange distance
        )) {
            textList.add(Text.literal("Use an Eye of Ender"));

            textList.add(Text.literal("Distance travelled: %s blocks".formatted(parseNumberRange(distance))));
            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
        }
        if (conditions instanceof UsedTotemCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<ItemPredicate> item
        )) {
            textList.add(Text.literal("Use a Totem of Undying"));

            item.ifPresent(p -> parseItemPredicate(p, "Consume", "totems with the following properties:"));
            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
        }
        if (conditions instanceof UsingItemCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<ItemPredicate> item
        )) {
            textList.add(Text.literal("Use an item"));

            item.ifPresent(p -> parseItemPredicate(p, "Consume", "or more of the following items:"));
            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
        }
        if (conditions instanceof VillagerTradeCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<LootContextPredicate> villager,
                Optional<ItemPredicate> item
        )) {
            textList.add(Text.literal("Receive items from a Villager trade"));

            villager.ifPresent(p -> parseLootContextPredicate(p, "Villager requirements:"));
            item.ifPresent(p -> parseItemPredicate(p, "Receive", "or more of the following items:"));
            player.ifPresent(p -> parseLootContextPredicate(p, "Player requirements:"));
        } else {
            textList.add(Text.literal("Unknown criterion type: %s".formatted(conditions.getClass().toString())));
        }

        return textList;
    }

    public static int execute(FabricClientCommandSource source, Identifier advancementId) {
        PlacedAdvancement placedAdvancement = AdvancementUtils.getAdvancementById(advancementId);
        if (placedAdvancement == null) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("Advancement with ID \"%s\" does not exist! Please try again.".formatted(advancementId.toString()))
                    .execute();
            return 0;
        }

        return execute(source, placedAdvancement);
    }

    private static void sendGoalFeedback(FabricClientCommandSource source, PlacedAdvancement placedAdvancement) {
        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text("ID: %s".formatted(placedAdvancement.getAdvancementEntry().id().toString()))
                .execute();

        if (placedAdvancement.getAdvancement().name().isPresent()) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.INFO)
                    .text(Text.literal("Advancement Name: ")
                            .append(placedAdvancement.getAdvancement().name().get()))
                    .execute();
        }

        if (placedAdvancement.getAdvancement().display().isPresent()) {
            AdvancementDisplay advancementDisplay = placedAdvancement.getAdvancement().display().get();

            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.INFO)
                    .text(Text.literal("Display Title: ")
                            .append(advancementDisplay.getTitle()))
                    .execute();

            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.INFO)
                    .text(Text.literal("Display Description: ")
                            .append(advancementDisplay.getDescription()))
                    .execute();
        }

        Map<String, AdvancementCriterion<?>> criteria = placedAdvancement.getAdvancement().criteria();
        List<Map.Entry<String, AdvancementCriterion<?>>> criteriaList = criteria.entrySet().stream().toList();
        for (int i = 0; i < criteria.size(); i++) {
            Map.Entry<String, AdvancementCriterion<?>> entry = criteriaList.get(i);

            String id = entry.getKey();
            AdvancementCriterion<?> criterion = entry.getValue();

            List<MutableText> textList = parseCriterion(id, criterion);

            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.INFO)
                    .text("Criteria %d/%d: ".formatted(i + 1, criteria.size()))
                    .execute();

            for (MutableText text : textList) {
                new ClientFeedbackBuilder().source(source)
                        .messageType(MessageType.INFO)
                        .text(Text.literal("> ")
                                .append(text))
                        .execute();
            }
        }

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text("Requirements: ")
                .execute();
        AdvancementRequirements advancementRequirements = placedAdvancement.getAdvancement().requirements();
        for (int i = 0; i < advancementRequirements.requirements().size(); i++) {
            List<String> list = advancementRequirements.requirements().get(i);

            String suffix = i != advancementRequirements.requirements().size() - 1 ? " OR" : "";

            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.INFO)
                    .text("> %s%s".formatted(String.join(" and ", list), suffix))
                    .execute();
        }
    }

    public static int execute(FabricClientCommandSource source, PlacedAdvancement placedAdvancement) {
        sendGoalFeedback(source, placedAdvancement);

        if (placedAdvancement.getParent() != null) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.INFO)
                    .text("Prerequisite:")
                    .execute();

            sendGoalFeedback(source, placedAdvancement.getParent());
        }

        return 1;
    }
}
