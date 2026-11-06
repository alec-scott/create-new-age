package org.antarcticgardens.cna;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

/**
 * PORT-NOTE(26.1): was built on Create's SoundEntryBuilder (and a datagen sounds.json provider). Create Fly has no
 * public builder, so the sound event is registered directly; the committed generated sounds.json is used.
 */
public class CNASounds {
    public static final Sound GEIGER_COUNTER = register("geiger_counter", SoundSource.PLAYERS);

    private static Sound register(String name, SoundSource category) {
        Identifier id = CreateNewAge.asResource(name);
        SoundEvent event = Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
        return new Sound(event, category);
    }

    public record Sound(SoundEvent event, SoundSource category) {
        public void playOnServer(Level level, BlockPos pos, float volume, float pitch) {
            level.playSound(null, pos, event, category, volume, pitch);
        }
    }

    public static void load() {
    }
}
