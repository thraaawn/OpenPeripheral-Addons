package openperipheral.addons.glasses;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.nbt.NBTBase.NBTPrimitive;
import openmods.utils.StringUtils;
import openperipheral.addons.api.ITerminalItem;

public class TerminalUtils {

	public static Long extractGuid(NBTTagCompound tag) {
		NBTBase guidTag = tag.getTag("guid");
		if (guidTag instanceof NBTTagString) {
			String value = ((NBTTagString)guidTag).func_150285_a_();
			return Long.parseLong(value.toLowerCase(), 36);
		} else if (guidTag instanceof NBTTagLong) return ((NBTPrimitive)guidTag).func_150291_c();

		return null;
	}

	public static String formatTerminalId(long terminalId) {
		return Long.toString(terminalId, 36).toUpperCase();
	}

	static long generateGuid() {
		return Long.parseLong(StringUtils.randomString(8), 36);
	}

	public static final UUID GLOBAL_SURFACE_UUID = UUID.fromString("df66eff0-1cae-11e4-8c21-0800200c9a66");
	public static final String GLOBAL_MARKER = "GLOBAL";
	public static final String PRIVATE_MARKER = "PRIVATE";

	public static ItemStack getHeadSlot(EntityPlayer player) {
		return player.inventory.armorItemInSlot(3);
	}

	public static Long tryGetTerminalGuid(ItemStack stack) {
		if (stack != null) {
			Item item = stack.getItem();
			if (item instanceof ITerminalItem) return ((ITerminalItem)item).getTerminalGuid(stack);
		}
		return null;
	}

	public static Long tryGetTerminalGuid(EntityPlayer player) {
		ItemStack stack = getHeadSlot(player);
		return tryGetTerminalGuid(stack);
	}
}
