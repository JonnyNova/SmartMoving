package net.smart.utilities;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class SoundUtil {
    public static SoundEvent getSoundEvent(String id) {
        return SoundEvent.REGISTRY.getObject(new ResourceLocation(id));
    }
}
