package dev.gravesown.event;

import dev.gravesown.Gravesown;
import dev.gravesown.config.GravesownConfig;
import dev.gravesown.entity.HollowGrazer;
import dev.gravesown.registry.ModEntities;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;

public final class WorldSpawnEvents {
    @SubscribeEvent
    public void onSpawnPlacementCheck(MobSpawnEvent.SpawnPlacementCheck event) {
        if (!GravesownConfig.REPLACE_NATURAL_VANILLA_SPAWNS.get()
                || !isWorldGenerationSpawn(event.getSpawnType())
                || !isVanilla(event.getEntityType())
                || event.getLevel().getLevel().dimension() != Level.OVERWORLD) {
            return;
        }

        if (event.getEntityType().getCategory() != MobCategory.CREATURE) {
            event.setResult(MobSpawnEvent.SpawnPlacementCheck.Result.FAIL);
        }
    }

    @SubscribeEvent
    public void onFinalizeSpawn(FinalizeSpawnEvent event) {
        Mob original = event.getEntity();
        if (!GravesownConfig.REPLACE_NATURAL_VANILLA_SPAWNS.get()
                || !isWorldGenerationSpawn(event.getSpawnType())
                || !isVanilla(original.getType())
                || event.getLevel().getLevel().dimension() != Level.OVERWORLD
                || shouldPreserve(original)) {
            return;
        }

        event.setSpawnCancelled(true);
        if (original.getType().getCategory() != MobCategory.CREATURE) {
            return;
        }

        HollowGrazer replacement = ModEntities.HOLLOW_GRAZER.get().create(event.getLevel().getLevel());
        if (replacement == null) {
            Gravesown.LOGGER.error("Failed to create Hollow Grazer replacement for {}", original.getType());
            return;
        }

        replacement.moveTo(event.getX(), event.getY(), event.getZ(), original.getYRot(), original.getXRot());
        replacement.finalizeSpawn(
                event.getLevel(),
                event.getDifficulty(),
                MobSpawnType.EVENT,
                null
        );
        event.getLevel().addFreshEntityWithPassengers(replacement);
    }

    @SubscribeEvent
    public void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()
                || event.getLevel().dimension() != Level.OVERWORLD
                || !event.loadedFromDisk()
                || !GravesownConfig.REMOVE_EXISTING_VANILLA_MOBS.get()
                || !(event.getEntity() instanceof Mob mob)
                || !isVanilla(mob.getType())
                || shouldPreserve(mob)) {
            return;
        }

        event.setCanceled(true);
    }

    private static boolean isWorldGenerationSpawn(MobSpawnType spawnType) {
        return spawnType == MobSpawnType.NATURAL || spawnType == MobSpawnType.CHUNK_GENERATION;
    }

    private static boolean isVanilla(EntityType<?> type) {
        ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        return key != null && ResourceLocation.DEFAULT_NAMESPACE.equals(key.getNamespace());
    }

    private static boolean shouldPreserve(Mob mob) {
        return GravesownConfig.PRESERVE_BOSSES.get()
                && (mob instanceof EnderDragon || mob instanceof WitherBoss);
    }
}
