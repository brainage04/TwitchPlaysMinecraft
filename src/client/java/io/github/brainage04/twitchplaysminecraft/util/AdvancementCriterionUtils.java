package io.github.brainage04.twitchplaysminecraft.util;

import net.minecraft.advancement.AdvancementCriterion;
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
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.Structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AdvancementCriterionUtils {
    public static final List<MutableText> textList = new ArrayList<>();

    private static void parseDistancePredicate(DistancePredicate predicate) {
        if (predicate == null) return;

        textList.add(Text.literal("Distance:"));

        String x = MathUtils.parseNumberRange(predicate.x());
        if (!x.isEmpty()) textList.add(Text.literal("X: %s".formatted(x)));

        String y = MathUtils.parseNumberRange(predicate.y());
        if (!x.isEmpty()) textList.add(Text.literal("Y: %s".formatted(y)));

        String z = MathUtils.parseNumberRange(predicate.z());
        if (!x.isEmpty()) textList.add(Text.literal("Z: %s".formatted(z)));

        String horizontal = MathUtils.parseNumberRange(predicate.horizontal());
        if (!x.isEmpty()) textList.add(Text.literal("Horizontal Distance: %s".formatted(horizontal)));

        String absolute = MathUtils.parseNumberRange(predicate.absolute());
        if (!x.isEmpty()) textList.add(Text.literal("Absolute Distance: %s".formatted(absolute)));
    }

    private static void parseEntityTypePredicate(EntityTypePredicate predicate) {
        if (predicate == null || predicate.types().size() < 1) return;

        textList.add(Text.literal("Valid entities: ").append(
                String.join(
                        ", ",
                        predicate.types().stream().map(
                                type -> type.value().getBaseClass().toString()
                        ).toArray(String[]::new)
                )
        ));
    }

    private static void parseMovementPredicate(MovementPredicate predicate) {
        if (predicate == null) return;

        textList.add(Text.literal("X: %s".formatted(MathUtils.parseNumberRange(predicate.x()))));
        textList.add(Text.literal("Y: %s".formatted(MathUtils.parseNumberRange(predicate.y()))));
        textList.add(Text.literal("Z: %s".formatted(MathUtils.parseNumberRange(predicate.z()))));
        textList.add(Text.literal("Fall distance: %s".formatted(MathUtils.parseNumberRange(predicate.fallDistance()))));
        textList.add(Text.literal("Speed: %s".formatted(MathUtils.parseNumberRange(predicate.speed()))));
        textList.add(Text.literal("Horizontal speed: %s".formatted(MathUtils.parseNumberRange(predicate.horizontalSpeed()))));
        textList.add(Text.literal("Vertical speed: %s".formatted(MathUtils.parseNumberRange(predicate.verticalSpeed()))));
    }

    private static void parseEntityPredicate(EntityPredicate predicate) {
        if (predicate == null) return;

        textList.add(Text.literal("Entity requirements:"));

        parseEntityTypePredicate(predicate.type().orElse(null));
        parseDistancePredicate(predicate.distance().orElse(null));
        parseMovementPredicate(predicate.movement().orElse(null));

        EntityPredicate.PositionalPredicates location = predicate.location();
        parseLocationPredicate(location.affectsMovement().orElse(null), "Affects movement: ");
        parseLocationPredicate(location.steppingOn().orElse(null), "Stepping on: ");
        parseLocationPredicate(location.located().orElse(null), "Located: ");

        parseEntityEffectPredicate(predicate.effects().orElse(null));
        predicate.nbt().ifPresent(p -> {});
        predicate.flags().ifPresent(p -> {});
        predicate.equipment().ifPresent(p -> {});
        predicate.typeSpecific().ifPresent(p -> {});
        predicate.periodicTick().ifPresent(p -> textList.add(Text.literal("Periodic tick value: %d".formatted(p))));
        parseEntityPredicate(predicate.vehicle().orElse(null));
        parseEntityPredicate(predicate.passenger().orElse(null));
        parseEntityPredicate(predicate.targetedEntity().orElse(null));
        predicate.team().ifPresent(p -> textList.add(Text.literal("Team name: %s".formatted(p))));
        parseSlotsPredicate(predicate.slots().orElse(null));
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

    private static void parseDamageSourcePredicate(DamageSourcePredicate predicate, String prefix) {
        if (predicate == null) return;

        textList.add(Text.literal(prefix));

        parseTagPredicates(predicate.tags());
        parseEntityPredicate(predicate.directEntity().orElse(null));
        parseEntityPredicate(predicate.sourceEntity().orElse(null));
        predicate.isDirect().ifPresent(p -> textList.add(Text.literal(p ? "Damage must be direct" : "Damage can be direct or non-direct")));
    }

    private static void parseDamagePredicate(DamagePredicate predicate) {
        if (predicate == null) return;

        textList.add(Text.literal("Damage requirements:"));

        textList.add(Text.literal("Deal %s damage".formatted(MathUtils.parseNumberRange(predicate.dealt()))));
        textList.add(Text.literal("Take %s damage".formatted(MathUtils.parseNumberRange(predicate.taken()))));
        parseEntityPredicate(predicate.sourceEntity().orElse(null));
        predicate.blocked().ifPresent(p -> textList.add(Text.literal("Damage blocked: %s".formatted(p.toString()))));
        parseDamageSourcePredicate(predicate.type().orElse(null), "Damage source requirements:");
    }

    private static void parseLootContextPredicate(LootContextPredicate predicate, String prefix) {
        if (predicate == null) return;

        textList.add(Text.literal(prefix));

        for (LootCondition condition : predicate.conditions) {
            Identifier conditionId = Registries.LOOT_CONDITION_TYPE.getId(condition.getType());
            if (conditionId == null) continue;
            textList.add(Text.literal(StringUtils.snakeCaseToHumanReadable(conditionId.getPath(), true, true)));
        }
    }

    private static void parsePlayerLootContextPredicate(LootContextPredicate predicate) {
        parseLootContextPredicate(predicate, "Player requirements:");
    }

    private static void parseItemPredicate(ItemPredicate predicate, String prefix, String suffix) {
        if (predicate == null || predicate.items().isEmpty()) return;

        textList.add(Text.literal("%s %s %s".formatted(prefix, MathUtils.parseNumberRange(predicate.count()), suffix)));
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
                    .append(StringUtils.snakeCaseToHumanReadable(componentId.getPath(), true, true))
                    .append(": ")
                    .append(component.value().toString()));
        }
    }

    public static void parseRegistryEntryBlock(RegistryEntry<Block> block, String prefix) {
        if (block == null) return;

        block.getKey().ifPresent(registryKey -> {
            Block actualBlock = Registries.BLOCK.get(registryKey);
            if (actualBlock == null) return;

            textList.add(Text.literal("%s a ".formatted(prefix))
                    .append(actualBlock.getName())
                    .append(" block"));
        });
    }

    public static void parseStatePredicate(StatePredicate predicate) {
        if (predicate == null) return;

        textList.add(Text.literal(" with the following state: %s".formatted(
                String.join(
                        ", ",
                        predicate.conditions().stream().map(StatePredicate.Condition::key).toArray(String[]::new)
                )
        )));
    }

    private static void parsePositionRange(LocationPredicate.PositionRange predicate) {
        if (predicate == null) return;

        textList.add(Text.literal("Position range | X = %s, Y = %s, Z = %s".formatted(
                MathUtils.parseNumberRange(predicate.x()),
                MathUtils.parseNumberRange(predicate.y()),
                MathUtils.parseNumberRange(predicate.z())
        )));
    }

    private static void parseBiomes(RegistryEntryList<Biome> predicate) {
        if (predicate == null) return;

        textList.add(Text.literal("Biomes:"));

        for (RegistryEntry<Biome> registryEntry : predicate) {
            Identifier biomeId = Identifier.of(registryEntry.getIdAsString());
            textList.add(Text.translatable("biome.%s.%s".formatted(biomeId.getNamespace(), biomeId.getPath())));
        }
    }

    private static void parseStructures(RegistryEntryList<Structure> predicate) {
        if (predicate == null) return;

        textList.add(Text.literal("Structures:"));

        for (RegistryEntry<Structure> entry : predicate) {
            textList.add(Text.literal(RegistryUtils.getEntryName(entry)));
        }
    }

    private static void parseDimensionPredicate(RegistryKey<World> from, RegistryKey<World> to) {
        MutableText text = Text.literal("Travel ");

        if (from != null) {
            text.append("from the ")
                        .append(RegistryUtils.getKeyName(from))
                        .append(" Dimension");
        }
        if (to != null) {
            text.append("to the ")
                        .append(RegistryUtils.getKeyName(to))
                        .append(" Dimension");
        }

        textList.add(text);
    }

    private static void parseFluidPredicate(FluidPredicate predicate) {
        if (predicate == null) return;

        predicate.fluids().ifPresent(fluids ->
            fluids.forEach(fluid ->
                textList.add(TextUtils.getTranslatableFromId(Identifier.of(fluid.getIdAsString()), "block.%s.%s"))
            )
        );
    }

    private static void parseBlockPredicate(BlockPredicate predicate) {
        if (predicate == null) return;

        predicate.blocks().ifPresent(list -> {
            textList.add(Text.literal("Blocks:"));

            list.forEach(registryEntry ->
                textList.add(TextUtils.getTranslatableFromId(Identifier.of(registryEntry.getIdAsString()), "block.%s.%s"))
            );
        });
    }

    private static void parseLocationPredicate(LocationPredicate predicate, String prefix) {
        if (predicate == null) return;

        textList.add(Text.literal(prefix));

        parsePositionRange(predicate.position().orElse(null));
        parseBiomes(predicate.biomes().orElse(null));
        parseStructures(predicate.structures().orElse(null));
        predicate.dimension().ifPresent(registryKey -> textList.add(Text.literal("Dimension: %s".formatted(RegistryUtils.getKeyName(registryKey)))));
        predicate.smokey().ifPresent(p -> textList.add(Text.literal("Smokey: %s".formatted(p.toString()))));
        predicate.light().ifPresent(p -> textList.add(Text.literal("Light level: %s".formatted(MathUtils.parseNumberRange(p.range())))));
        parseBlockPredicate(predicate.block().orElse(null));
        parseFluidPredicate(predicate.fluid().orElse(null));
        predicate.canSeeSky().ifPresent(p -> textList.add(Text.literal("Can see sky: %s".formatted(p.toString()))));
    }

    private static void parseSlotsPredicate(SlotsPredicate predicate) {
        if (predicate == null) return;

        for (Map.Entry<SlotRange, ItemPredicate> entry : predicate.slots().entrySet()) {
            textList.add(Text.literal("Slots %s:".formatted(
                    String.join(
                            ", ",
                            entry.getKey().getSlotIds().intStream().mapToObj(String::valueOf).toArray(String[]::new)
                    )
            )));

            parseItemPredicate(entry.getValue(), "", "of the following items:");
        }
    }

    private static void parseEntityEffectPredicate(EntityEffectPredicate predicate) {
        if (predicate == null) return;

        textList.add(Text.literal("Effects:"));

        predicate.effects().forEach(((statusEffectRegistryEntry, effectData) -> {
            if (statusEffectRegistryEntry.getKey().isEmpty()) return;
            StatusEffect statusEffect = Registries.STATUS_EFFECT.get(statusEffectRegistryEntry.getKey().get());
            if (statusEffect == null) return;

            textList.add(Text.literal("> ")
                    .append(statusEffect.getName())
                    .append(" ")
                    .append(MathUtils.parseNumberRangeRoman(effectData.amplifier()))
                    .append(", ")
                    .append("duration of %s".formatted(MathUtils.parseNumberRangeDuration(effectData.duration()))));
        }));
    }

    public static void parseCriterion(String id, AdvancementCriterion<?> advancementCriterion) {
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

            parsePlayerLootContextPredicate(player.orElse(null));
            parseLootContextPredicate(parent.orElse(null), "First parent requirements:");
            parseLootContextPredicate(partner.orElse(null), "Second parent requirements:");
            parseLootContextPredicate(child.orElse(null), "Child requirements:");
        }
        if (conditions instanceof BrewedPotionCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<RegistryEntry<Potion>> potion
        )) {
            potion.flatMap(RegistryEntry::getKey).ifPresent(key -> {
                Potion potionItem = Registries.POTION.get(key);
                if (potionItem == null) return;

                textList.add(Text.literal("Brew a ")
                        .append(TextUtils.getTranslatableFromId(key.getValue(), "item.%s.potion.effect.%s")));
            });

            parsePlayerLootContextPredicate(player.orElse(null));
        }
        if (conditions instanceof ChangedDimensionCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<RegistryKey<World>> from,
                Optional<RegistryKey<World>> to
        )) {
            parseDimensionPredicate(from.orElse(null), to.orElse(null));

            parsePlayerLootContextPredicate(player.orElse(null));
        }
        if (conditions instanceof ChanneledLightningCriterion.Conditions(
                Optional<LootContextPredicate> player,
                List<LootContextPredicate> victims
        )) {
            textList.add(Text.literal("Throw a Trident with Channeling at an entity"));

            parsePlayerLootContextPredicate(player.orElse(null));
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

            parsePlayerLootContextPredicate(player.orElse(null));
            parseItemPredicate(item.orElse(null), "Consume", "of the following items:");
        }
        if (conditions instanceof CuredZombieVillagerCriterion.Conditions) textList.add(Text.literal("Cure a Zombie Villager"));
        if (conditions instanceof DefaultBlockUseCriterion.Conditions) textList.add(Text.literal("Use any block"));
        if (conditions instanceof EffectsChangedCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<EntityEffectPredicate> effects,
                Optional<LootContextPredicate> source
        )) {
            textList.add(Text.literal("Obtain status effects"));

            parseEntityEffectPredicate(effects.orElse(null));
            parseLootContextPredicate(source.orElse(null), "Requirements for the status effect source:");
            parsePlayerLootContextPredicate(player.orElse(null));
        }
        if (conditions instanceof EnchantedItemCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<ItemPredicate> item,
                NumberRange.IntRange levels
        )) {
            textList.add(Text.literal("Enchant an item"));

            parsePlayerLootContextPredicate(player.orElse(null));
            item.ifPresent(p -> {
                String levelsString = MathUtils.parseNumberRange(levels);
                levelsString = !levelsString.isEmpty() ? " with %s levels".formatted(levelsString) : "";

                parseItemPredicate(p, "Enchant", "of the following items%s:".formatted(levelsString));
            });
        }
        if (conditions instanceof EnterBlockCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<RegistryEntry<Block>> block,
                Optional<StatePredicate> state
        )) {
            parseRegistryEntryBlock(block.orElse(null), "Enter");
            parseStatePredicate(state.orElse(null));

            parsePlayerLootContextPredicate(player.orElse(null));
        }
        if (conditions instanceof EntityHurtPlayerCriterion.Conditions(
                Optional<LootContextPredicate> player, Optional<DamagePredicate> damage
        )) {
            textList.add(Text.literal("Get hurt by an entity"));

            parseDamagePredicate(damage.orElse(null));
            parsePlayerLootContextPredicate(player.orElse(null));
        }
        if (conditions instanceof FallAfterExplosionCriterion.Conditions(
                Optional<LootContextPredicate> player, Optional<LocationPredicate> startPosition,
                Optional<DistancePredicate> distance, Optional<LootContextPredicate> cause
        )) {
            textList.add(Text.literal("Fall after an explosion"));

            parsePlayerLootContextPredicate(player.orElse(null));
            parseLocationPredicate(startPosition.orElse(null), "Starting position:");
            parseLootContextPredicate(cause.orElse(null), "Cause for explosion:");
            parseDistancePredicate(distance.orElse(null));
        }
        if (conditions instanceof FilledBucketCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<ItemPredicate> item
        )) {
            textList.add(Text.literal("Fill an empty Bucket"));

            parsePlayerLootContextPredicate(player.orElse(null));
            parseItemPredicate(item.orElse(null), "Obtain", "of the following items:");
        }
        if (conditions instanceof FishingRodHookedCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<ItemPredicate> rod,
                Optional<LootContextPredicate> entity,
                Optional<ItemPredicate> item
        )) {
            textList.add(Text.literal("Catch an item or an entity with a Fishing Rod"));

            parsePlayerLootContextPredicate(player.orElse(null));
            parseItemPredicate(rod.orElse(null), "Hook", "Fishing Rods with:");
            parseLootContextPredicate(entity.orElse(null), "Entity requirements:");
            parseItemPredicate(item.orElse(null), "Catch", "of the following items:");
        }
        if (conditions instanceof ImpossibleCriterion.Conditions) textList.add(Text.literal("Impossible"));
        if (conditions instanceof InventoryChangedCriterion.Conditions(
                Optional<LootContextPredicate> player,
                InventoryChangedCriterion.Conditions.Slots slots,
                List<ItemPredicate> items
        )) {
            textList.add(Text.literal("Experience inventory changes"));

            parsePlayerLootContextPredicate(player.orElse(null));
            textList.add(Text.literal("%s empty slots, %s full slots, %s occupied slots".formatted(
                    MathUtils.parseNumberRange(slots.empty()),
                    MathUtils.parseNumberRange(slots.full()),
                    MathUtils.parseNumberRange(slots.occupied())
            )));
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

            parsePlayerLootContextPredicate(player.orElse(null));
            parseLootContextPredicate(location.orElse(null), "Location requirements:");
        }
        if (conditions instanceof ItemDurabilityChangedCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<ItemPredicate> item,
                NumberRange.IntRange durability,
                NumberRange.IntRange delta
        )) {
            textList.add(Text.literal("Damage an item"));

            parsePlayerLootContextPredicate(player.orElse(null));
            parseItemPredicate(item.orElse(null), "Damage", "of the following items:");
            textList.add(Text.literal("Durability range: %s (change of %s)".formatted(MathUtils.parseNumberRange(durability), MathUtils.parseNumberRange(delta))));
        }
        if (conditions instanceof KilledByArrowCriterion.Conditions(
                Optional<LootContextPredicate> player,
                List<LootContextPredicate> victims,
                NumberRange.IntRange uniqueEntityTypes,
                Optional<ItemPredicate> firedFromWeapon
        )) {
            textList.add(Text.literal("Kill entities with arrows"));

            parsePlayerLootContextPredicate(player.orElse(null));

            textList.add(Text.literal("Required entities (%s types):".formatted(MathUtils.parseNumberRange(uniqueEntityTypes))));
            for (int i = 0; i < victims.size(); i++) {
                LootContextPredicate lootContextPredicate = victims.get(i);
                parseLootContextPredicate(lootContextPredicate, "Victim %d/%d:".formatted(i + 1, victims.size()));
            }

            parseItemPredicate(firedFromWeapon.orElse(null), "Use", "of the following weapon:");
        }
        if (conditions instanceof LevitationCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<DistancePredicate> distance,
                NumberRange.IntRange duration
        )) {
            textList.add(Text.literal("Levitate for %s seconds".formatted(MathUtils.parseNumberRange(duration))));

            parsePlayerLootContextPredicate(player.orElse(null));
            parseDistancePredicate(distance.orElse(null));
        }
        if (conditions instanceof LightningStrikeCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<LootContextPredicate> lightning,
                Optional<LootContextPredicate> bystander
        )) {
            textList.add(Text.literal("Lightning strike event"));

            parsePlayerLootContextPredicate(player.orElse(null));
            parseLootContextPredicate(lightning.orElse(null), "Lightning requirements:");
            parseLootContextPredicate(bystander.orElse(null), "Bystander requirements:");
        }
        if (conditions instanceof OnKilledCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<LootContextPredicate> entity,
                Optional<DamageSourcePredicate> killingBlow
        )) {
            textList.add(Text.literal("Kill an entity"));

            parsePlayerLootContextPredicate(player.orElse(null));
            parseLootContextPredicate(entity.orElse(null), "Entity requirements:");
            parseDamageSourcePredicate(killingBlow.orElse(null), "Killing blow requirements:");
        }
        if (conditions instanceof PlayerGeneratesContainerLootCriterion.Conditions(
                Optional<LootContextPredicate> player,
                RegistryKey<LootTable> lootTable
        )) {
            textList.add(Text.literal("Generate the \"%s\" loot table by opening a container (such as a Chest)".formatted(lootTable.getValue().toString())));

            parsePlayerLootContextPredicate(player.orElse(null));
        }
        if (conditions instanceof PlayerHurtEntityCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<DamagePredicate> damage,
                Optional<LootContextPredicate> entity
        )) {
            textList.add(Text.literal("Hurt an entity"));

            parsePlayerLootContextPredicate(player.orElse(null));
            parseDamagePredicate(damage.orElse(null));
            parseLootContextPredicate(entity.orElse(null), "Entity requirements:");
        }
        if (conditions instanceof PlayerInteractedWithEntityCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<ItemPredicate> item,
                Optional<LootContextPredicate> entity
        )) {
            textList.add(Text.literal("Interact with an entity using a specific item"));

            parsePlayerLootContextPredicate(player.orElse(null));
            parseItemPredicate(item.orElse(null), "Use", "of the following items:");
            parseLootContextPredicate(entity.orElse(null), "Entity requirements:");
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

            parsePlayerLootContextPredicate(player.orElse(null));
        }
        if (conditions instanceof RecipeUnlockedCriterion.Conditions(
                Optional<LootContextPredicate> player,
                RegistryKey<Recipe<?>> recipe
        )) {
            textList.add(Text.literal("Unlock the \"%s\" recipe".formatted(recipe.getValue().toString())));

            parsePlayerLootContextPredicate(player.orElse(null));
        }
        if (conditions instanceof ShotCrossbowCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<ItemPredicate> item
        )) {
            parseItemPredicate(item.orElse(null), "Shoot", "of the following items with a crossbow:");
            parsePlayerLootContextPredicate(player.orElse(null));
        }
        if (conditions instanceof SlideDownBlockCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<RegistryEntry<Block>> block,
                Optional<StatePredicate> state
        )) {
            parseRegistryEntryBlock(block.orElse(null), "Slide down");
            parseStatePredicate(state.orElse(null));

            parsePlayerLootContextPredicate(player.orElse(null));
        }
        if (conditions instanceof StartedRidingCriterion.Conditions(
                Optional<LootContextPredicate> player
        )) {
            textList.add(Text.literal("Start riding an entity"));

            parsePlayerLootContextPredicate(player.orElse(null));
        }
        if (conditions instanceof SummonedEntityCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<LootContextPredicate> entity
        )) {
            textList.add(Text.literal("Summon an entity"));

            parsePlayerLootContextPredicate(player.orElse(null));
            parseLootContextPredicate(entity.orElse(null), "Entity requirements:");
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

            parsePlayerLootContextPredicate(player.orElse(null));
            parseLootContextPredicate(entity.orElse(null), "Entity requirements:");
        }
        if (conditions instanceof TargetHitCriterion.Conditions(
                Optional<LootContextPredicate> player,
                NumberRange.IntRange signalStrength,
                Optional<LootContextPredicate> projectile
        )) {
            textList.add(Text.literal("Hit a Target Block and make it output a redstone signal of %s".formatted(MathUtils.parseNumberRange(signalStrength))));

            parsePlayerLootContextPredicate(player.orElse(null));
            parseLootContextPredicate(projectile.orElse(null), "Projectile requirements:");
        }
        if (conditions instanceof ThrownItemPickedUpByEntityCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<ItemPredicate> item,
                Optional<LootContextPredicate> entity
        )) {
            textList.add(Text.literal("Pick up an item that was thrown by an entity"));

            parsePlayerLootContextPredicate(player.orElse(null));
            parseItemPredicate(item.orElse(null), "Pick up", "of the following items:");
            parseLootContextPredicate(entity.orElse(null), "Entity requirements:");
        }
        if (conditions instanceof TickCriterion.Conditions(
                Optional<LootContextPredicate> player
        )) {
            textList.add(Text.literal("Fulfil the criteria for a certain number of in-game ticks"));

            parsePlayerLootContextPredicate(player.orElse(null));
        }
        if (conditions instanceof TravelCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<LocationPredicate> startPosition,
                Optional<DistancePredicate> distance
        )) {
            textList.add(Text.literal("Travel a certain distance from a certain starting position"));

            parseLocationPredicate(startPosition.orElse(null), "Start position:");
            parseDistancePredicate(distance.orElse(null));
            parsePlayerLootContextPredicate(player.orElse(null));
        }
        if (conditions instanceof UsedEnderEyeCriterion.Conditions(
                Optional<LootContextPredicate> player,
                NumberRange.DoubleRange distance
        )) {
            textList.add(Text.literal("Use an Eye of Ender"));

            textList.add(Text.literal("Distance travelled: %s blocks".formatted(MathUtils.parseNumberRange(distance))));
            parsePlayerLootContextPredicate(player.orElse(null));
        }
        if (conditions instanceof UsedTotemCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<ItemPredicate> item
        )) {
            textList.add(Text.literal("Use a Totem of Undying"));

            parseItemPredicate(item.orElse(null), "Consume", "totems with the following properties:");
            parsePlayerLootContextPredicate(player.orElse(null));
        }
        if (conditions instanceof UsingItemCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<ItemPredicate> item
        )) {
            textList.add(Text.literal("Use an item"));

            parseItemPredicate(item.orElse(null), "Consume", "or more of the following items:");
            parsePlayerLootContextPredicate(player.orElse(null));
        }
        if (conditions instanceof VillagerTradeCriterion.Conditions(
                Optional<LootContextPredicate> player,
                Optional<LootContextPredicate> villager,
                Optional<ItemPredicate> item
        )) {
            textList.add(Text.literal("Receive items from a Villager trade"));

            parseLootContextPredicate(villager.orElse(null), "Villager requirements:");
            parseItemPredicate(item.orElse(null), "Receive", "or more of the following items:");
            parsePlayerLootContextPredicate(player.orElse(null));
        } else {
            textList.add(Text.literal("Unknown criterion type: %s".formatted(conditions.getClass().toString())));
        }

    }
}
