package dev.gravesown.client.renderer;

import com.mojang.datafixers.util.Pair;
import dev.gravesown.Gravesown;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;

/** Uses the stable boat animation rig with an original Gravesown hull texture. */
public final class GloamSkiffRenderer extends BoatRenderer {
    private static final ResourceLocation TEXTURE = Gravesown.id("textures/entity/gloam_skiff.png");

    public GloamSkiffRenderer(EntityRendererProvider.Context context) {
        super(context, false);
    }

    @Override
    public ResourceLocation getTextureLocation(Boat boat) {
        return TEXTURE;
    }

    @Override
    public Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(Boat boat) {
        return Pair.of(TEXTURE, super.getModelWithLocation(boat).getSecond());
    }
}
