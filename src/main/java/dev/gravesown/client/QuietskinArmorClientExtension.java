package dev.gravesown.client;

import dev.gravesown.client.model.QuietskinArmorModel;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

/** Client-only bridge from the four Quietskin items to their slot geometry. */
public final class QuietskinArmorClientExtension implements IClientItemExtensions {
    private static final Map<EquipmentSlot, ModelLayerLocation> LAYERS = Map.of(
            EquipmentSlot.HEAD, QuietskinArmorModel.HOOD_LAYER,
            EquipmentSlot.CHEST, QuietskinArmorModel.COAT_LAYER,
            EquipmentSlot.LEGS, QuietskinArmorModel.LEGWRAPS_LAYER,
            EquipmentSlot.FEET, QuietskinArmorModel.BOOTS_LAYER
    );

    private final Map<EquipmentSlot, QuietskinArmorModel> models = new EnumMap<>(EquipmentSlot.class);

    @Override
    public HumanoidModel<?> getHumanoidArmorModel(
            LivingEntity livingEntity,
            ItemStack itemStack,
            EquipmentSlot equipmentSlot,
            HumanoidModel<?> original
    ) {
        ModelLayerLocation layer = LAYERS.get(equipmentSlot);
        if (layer == null) {
            return original;
        }
        return this.models.computeIfAbsent(
                equipmentSlot,
                ignored -> new QuietskinArmorModel(
                        Minecraft.getInstance().getEntityModels().bakeLayer(layer)
                )
        );
    }
}
