package openperipheral.addons.glasses;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagString;
import openmods.utils.StringUtils;

public class TerminalUtils {

	public static Long extractGuid(NBTTagCompound tag) {
		NBTBase guidTag = tag.getTag("guid");
		if (guidTag instanceof NBTTagString) {
			String value = ((NBTTagString)guidTag).data;
			return Long.parseLong(value.toLowerCase(), 36);
		} else if (guidTag instanceof NBTTagLong) return ((NBTTagLong)guidTag).data;

		return null;
	}

	public static String formatTerminalId(long terminalId) {
		return Long.toString(terminalId, 36).toUpperCase();
	}

	static long generateGuid() {
		return Long.parseLong(StringUtils.randomString(8), 36);
	}

	public static final String GLOBAL_MARKER = "GLOBAL";
	public static final String PRIVATE_MARKER = "PRIVATE";

}
