package org.jahdoo.registers;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jahdoo.JahdooMod;
import org.jahdoo.block.crafter.CreatorBlock;
import org.jahdoo.block.infuser.InfuserBlock;
import org.jahdoo.block.light_block.LightBlock;
import org.jahdoo.block.tank.TankBlock;
import org.jahdoo.block.wand.WandBlock;
import org.jahdoo.block.wandBlockManager.WandManagerTableBlock;

import java.util.function.Supplier;

public class BlocksRegister {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(JahdooMod.MOD_ID);

    public static DeferredHolder<Block, Block> WAND_MANAGER_TABLE = registerBlock("infusion_table",
            () -> new WandManagerTableBlock(
                BlockBehaviour
                    .Properties.ofFullCopy(Blocks.DEEPSLATE)
                    .noOcclusion()
            )
    );

    public static DeferredHolder<Block, Block> CREATOR = registerBlock("creator",
        () -> new CreatorBlock(
            BlockBehaviour
                .Properties.ofFullCopy(Blocks.DEEPSLATE)
                .noOcclusion()
        )
    );

    public static DeferredHolder<Block, Block> WAND = BLOCKS.register("wand_mystic",
        () -> new WandBlock(
            BlockBehaviour
                .Properties.ofFullCopy(Blocks.TORCH)
                .noTerrainParticles()
                .instabreak()
                .lightLevel((blockState) -> 12)
        )
    );

    public static DeferredHolder<Block, Block> TANK = registerBlock("tank",
            () -> new TankBlock(
                BlockBehaviour
                    .Properties.ofFullCopy(Blocks.COPPER_BLOCK)
                    .noOcclusion()
            )
    );

    public static DeferredHolder<Block, Block> INFUSER = BLOCKS.register("infuser",
        () -> new InfuserBlock(
            BlockBehaviour
                .Properties.ofFullCopy(Blocks.DEEPSLATE)
                .noOcclusion()
        )
    );

    public static DeferredHolder<Block, Block> CRYSTAL_ORE = registerBlock("crystal_ore",
        () -> new DropExperienceBlock(
            UniformInt.of(3, 6),
            BlockBehaviour
                .Properties.ofFullCopy(Blocks.STONE)
                .strength(2f)
                .lightLevel((blockState) -> 1)
        )
    );

    public static DeferredHolder<Block, Block> LIGHTING = BLOCKS.register("lighting",
        () -> new LightBlock(
            BlockBehaviour
                .Properties.ofFullCopy(Blocks.TORCH)
                .noCollission()
                .instabreak()
                .lightLevel( (blockState) -> 15 )
        )
    );

    public static DeferredHolder<Block, Block> JIDE_POWDER_BLOCk = registerBlock("jide_powder_block",
        () -> new Block(
            BlockBehaviour
                .Properties.ofFullCopy(Blocks.TORCH).noCollission()
        )
    );

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ItemsRegister.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }


    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}
