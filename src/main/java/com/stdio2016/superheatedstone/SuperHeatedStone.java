package com.stdio2016.superheatedstone;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = SuperHeatedStone.MODID, version = SuperHeatedStone.VERSION)
public class SuperHeatedStone
{
    public static final String MODID = "superheatedstone";
    public static final String VERSION = "1.0";
    public static final Item HOT_STONE, HEATED_STONE, LIQUID_STONE, SUPER_HEATED_STONE;
    public static final Item[] items;
    public static final byte SOCKET_ID = 35;
    private static SimpleNetworkWrapper simpleNetworkWrapper;

    public static class HotItem extends Item {
        public int heat;
        public boolean canEvaporate;
        public HotItem(int temperature, boolean evaporate) {
            heat = temperature;
            canEvaporate = evaporate;
        }
        @Override
        public boolean onLeftClickEntity(ItemStack usedItem, EntityPlayer player, Entity entity) {
            if (usedItem.getItem() == this && entity != null) {
                entity.setFire(heat);
            }
            return super.onLeftClickEntity(usedItem, player, entity);
        }
        @Override
        public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
            if (canEvaporate) {
                Block block = world.getBlockState(pos).getBlock();
                if (!world.isRemote) {
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    player.sendMessage(new TextComponentTranslation("message.the_stone_so_hot_evaporates_block"));
                    world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 1.0f);
                    simpleNetworkWrapper.sendToDimension(new VaporMessage(pos.getX(), pos.getY(), pos.getZ()), player.dimension);
                    return EnumActionResult.SUCCESS;
                }
            }
            return EnumActionResult.PASS;
        }
    }

    // since smelted stones are very hot, all stones in our MOD are actually HotItem and not Block
    private static Item createItem(int heat, String unlocalizedName, boolean canEvaporate) {
        return new HotItem(heat, canEvaporate).setUnlocalizedName(unlocalizedName).setRegistryName(unlocalizedName);
    }

    static {
        HOT_STONE = createItem(0, "hot_stone", false).setCreativeTab(CreativeTabs.MATERIALS);
        HEATED_STONE = createItem(0, "heated_stone", false).setCreativeTab(CreativeTabs.MATERIALS);
        SUPER_HEATED_STONE = createItem(2, "super_heated_stone", false).setCreativeTab(CreativeTabs.MATERIALS);
        LIQUID_STONE = createItem(0, "liquid_stone", true).setCreativeTab(CreativeTabs.MATERIALS);
        items = new Item[] {HOT_STONE, HEATED_STONE, SUPER_HEATED_STONE, LIQUID_STONE};
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        for (Item item : items) {
            GameRegistry.register(item);
        }
        simpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("SuperHeatedChannel");
        simpleNetworkWrapper.registerMessage(VaporHandler.dummy.class, VaporMessage.class,
                SuperHeatedStone.SOCKET_ID, Side.SERVER);
        if (event.getSide() == Side.CLIENT) {
            simpleNetworkWrapper.registerMessage(VaporHandler.class, VaporMessage.class, SuperHeatedStone.SOCKET_ID, Side.CLIENT);
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        GameRegistry.addSmelting(new ItemStack(Blocks.STONE, 1, 0), new ItemStack(HOT_STONE), 0.1f);
        GameRegistry.addSmelting(HOT_STONE, new ItemStack(HEATED_STONE), 0.1f);
        GameRegistry.addSmelting(HEATED_STONE, new ItemStack(SUPER_HEATED_STONE), 0.1f);
        GameRegistry.addSmelting(SUPER_HEATED_STONE, new ItemStack(LIQUID_STONE), 0.1f);
        GameRegistry.addSmelting(LIQUID_STONE, new ItemStack(Blocks.MAGMA), 0.1f);
        GameRegistry.registerFuelHandler(new IFuelHandler() {
            @Override
            public int getBurnTime(ItemStack itemStack) {
                final int ONE_BURN = 200;
                if (itemStack.getItem() == HOT_STONE) return 2 * ONE_BURN;
                if (itemStack.getItem() == HEATED_STONE) return 4 * ONE_BURN;
                if (itemStack.getItem() == SUPER_HEATED_STONE) return 8 * ONE_BURN;
                if (itemStack.getItem() == LIQUID_STONE) return 16 * ONE_BURN;
                return 0;
            }
        });
    }

    private void addImageForItem(Item item) {
        ItemModelMesher modelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        modelMesher.register(item, 0, new ModelResourceLocation(this.MODID+":"+item.getUnlocalizedName().substring(5), "inventory"));
    }

    @EventHandler
    public void load(FMLInitializationEvent event) {
        if (event.getSide() == Side.CLIENT) {
            for (Item item : items) {
                addImageForItem(item);
            }
        }
    }
}
