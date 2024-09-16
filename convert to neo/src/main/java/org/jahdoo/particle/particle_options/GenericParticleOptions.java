package org.jahdoo.particle.particle_options;


import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.stream.IntStream;

import static org.jahdoo.particle.ParticleStore.getColouredParticle;

public record GenericParticleOptions(
    int type,
    int colour,
    int fade,
    int lifetime,
    float size,
    boolean setStaticSize,
    double speed
) implements ParticleOptions {

    @Override
    public @NotNull ParticleType<?> getType() {
        return getColouredParticle.get(type);
    }


    public void writeToNetwork(FriendlyByteBuf pBuffer) {
        pBuffer.writeInt(type);
        pBuffer.writeInt(colour);
        pBuffer.writeInt(fade);
        pBuffer.writeInt(lifetime);
        pBuffer.writeFloat(size);
        pBuffer.writeBoolean(setStaticSize);
        pBuffer.writeDouble(speed);
    }

    public @NotNull String writeToString() {
        return type + " " + colour + " " + fade + " " + lifetime + " " + size + " " + setStaticSize + " " + speed;
    }

//    public static final Codec<GenericParticleOptions> CODEC = RecordCodecBuilder.create(
//        (instance) -> instance.group(
//            Codec.INT.fieldOf("type").forGetter(GenericParticleOptions::type),
//            Codec.INT.fieldOf("colour").forGetter(GenericParticleOptions::colour),
//            Codec.INT.fieldOf("fade").forGetter(GenericParticleOptions::fade),
//            Codec.INT.fieldOf("lifetime").forGetter(GenericParticleOptions::lifetime),
//            Codec.FLOAT.fieldOf("size").forGetter(GenericParticleOptions::size),
//            Codec.BOOL.fieldOf("setStaticSize").forGetter(GenericParticleOptions::setStaticSize),
//            Codec.DOUBLE.fieldOf("speed").forGetter(GenericParticleOptions::speed)
//        ).apply(instance, GenericParticleOptions::new)
//    );

    public static StreamCodec<? super ByteBuf, GenericParticleOptions> STREAM_CODEC = StreamCodec.of(
        (buf, option) -> {
            buf.writeInt(option.type);
            buf.writeInt(option.colour);
            buf.writeInt(option.fade);
            buf.writeInt(option.lifetime);
            buf.writeFloat(option.size);
            buf.writeBoolean(option.setStaticSize);
            buf.writeDouble(option.speed);
        },
        (buf) -> {
            return new GenericParticleOptions(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readFloat(), buf.readBoolean(), buf.readDouble());
        }
    );

    public static MapCodec<GenericParticleOptions> MAP_CODEC = RecordCodecBuilder.mapCodec(object ->
        object.group(
            Codec.INT.fieldOf("type").forGetter(GenericParticleOptions::type),
            Codec.INT.fieldOf("colour").forGetter(GenericParticleOptions::colour),
            Codec.INT.fieldOf("fade").forGetter(GenericParticleOptions::fade),
            Codec.INT.fieldOf("lifetime").forGetter(GenericParticleOptions::lifetime),
            Codec.FLOAT.fieldOf("size").forGetter(GenericParticleOptions::size),
            Codec.BOOL.fieldOf("setStaticSize").forGetter(GenericParticleOptions::setStaticSize),
            Codec.DOUBLE.fieldOf("speed").forGetter(GenericParticleOptions::speed)
        ).apply(object, GenericParticleOptions::new)
    );

//    public static final ParticleOptions.Deserializer<GenericParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
//        public @NotNull GenericParticleOptions fromCommand(@NotNull ParticleType<GenericParticleOptions> type, StringReader reader) throws CommandSyntaxException {
//            var types = reader.readInt();
//            reader.expect(' ');
//            var colour = reader.readInt();
//            reader.expect(' ');
//            var fade = reader.readInt();
//            reader.expect(' ');
//            var lifetime = reader.readInt();
//            reader.expect(' ');
//            var size = reader.readFloat();
//            reader.expect(' ');
//            var setStaticSize = reader.readBoolean();
//            reader.expect(' ');
//            var speed = reader.readDouble();
//            return new GenericParticleOptions(types, colour, fade, lifetime, size, setStaticSize, speed);
//        }
//
//        public @NotNull GenericParticleOptions fromNetwork(@NotNull ParticleType<GenericParticleOptions> type, FriendlyByteBuf buffer) {
//            return new GenericParticleOptions(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readFloat(), buffer.readBoolean(), buffer.readDouble());
//        }
//    };


}
