package openperipheral.addons.glasses;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import openperipheral.addons.BlockOP;
import openperipheral.addons.api.ITerminalItem;

public class BlockGlassesBridge extends BlockOP {

	public BlockGlassesBridge() {
		super(Material.ground);
		setRenderMode(RenderMode.BLOCK_ONLY);
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		blockIcon = register.registerIcon("openperipheraladdons:bridge");
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (player.isSneaking() || world.isRemote) return false;

		TileEntityGlassesBridge te = getTileEntity(world, x, y, z, TileEntityGlassesBridge.class);

		if (te == null) return false;

		ItemStack glassesStack = player.getHeldItem();
		if (glassesStack != null) {
			Item item = glassesStack.getItem();
			if (item instanceof ITerminalItem) {
				((ITerminalItem)item).bindToTerminal(glassesStack, te.getGuid());
				return true;
			}
		}

		return false;
	}
}
