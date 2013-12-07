package openperipheral.addons.glasses;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import openperipheral.util.Property;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class Drawable {

	private enum Type {
		GRADIENT {
			@Override
			public Drawable create() {
				return new GradientBox();
			}
		},
		BOX {
			@Override
			public Drawable create() {
				return new SolidBox();
			}
		},
		TEXT {
			@Override
			public Drawable create() {
				return new Text();
			}
		},
		LIQUID {
			@Override
			public Drawable create() {
				return new LiquidIcon();
			}
		},
		ITEM {
			@Override
			public Drawable create() {
				return new ItemIcon();
			}
		};

		public abstract Drawable create();

		public static final Type[] TYPES = values();
	}

	@Property
	public short x;

	@Property
	public short y;

	@Property
	public short z;

	protected Drawable() {}

	protected Drawable(short x, short y) {
		this.x = x;
		this.y = y;
	}

	@SideOnly(Side.CLIENT)
	public void draw(float partialTicks) {
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		drawContents(partialTicks);
		GL11.glPopMatrix();
	}

	@SideOnly(Side.CLIENT)
	protected abstract void drawContents(float partialTicks);

	protected abstract Type getType();

	public static class SolidBox extends Drawable {
		@Property
		public short width;

		@Property
		public short height;

		@Property
		public int color;

		@Property
		public float opacity;

		private SolidBox() {}

		public SolidBox(short x, short y, short width, short height, int color, float opacity) {
			super(x, y);
			this.width = width;
			this.height = height;
			this.color = color;
			this.opacity = opacity;
		}

		@Override
		@SideOnly(Side.CLIENT)
		protected void drawContents(float partialTicks) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			tessellator.setColorRGBA_I(color, (int)(opacity * 255));

			tessellator.addVertex(0, 0, 0);
			tessellator.addVertex(0, height, 0);

			tessellator.addVertex(width, height, 0);
			tessellator.addVertex(width, 0, 0);

			tessellator.draw();
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
		}

		@Override
		public Type getType() {
			return Type.BOX;
		}

	}

	public static class GradientBox extends Drawable {
		@Property
		public short width;

		@Property
		public short height;

		@Property
		public int color1;

		@Property
		public float opacity1;

		@Property
		public int color2;

		@Property
		public float opacity2;

		@Property
		public int gradient;

		private GradientBox() {}

		public GradientBox(short x, short y, short width, short height, int color1, float opacity1, int color2, float opacity2, int gradient) {
			super(x, y);
			this.width = width;
			this.height = height;
			this.color1 = color1;
			this.opacity1 = opacity1;
			// compat hack
			if (gradient == 0) {
				this.color2 = color1;
				this.opacity2 = opacity1;
			} else {
				this.color2 = color2;
				this.opacity2 = opacity2;
			}
			this.gradient = gradient;
		}

		@Override
		@SideOnly(Side.CLIENT)
		protected void drawContents(float partialTicks) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glShadeModel(GL11.GL_SMOOTH);

			Tessellator tessellator = Tessellator.instance;

			tessellator.startDrawingQuads();
			tessellator.setColorRGBA_I(color1, (int)(opacity1 * 255));

			if (gradient == 1) {
				tessellator.addVertex(0, height, 0);
				tessellator.addVertex(width, height, 0);
			} else {
				tessellator.addVertex(width, height, 0);
				tessellator.addVertex(width, 0, 0);

			}

			tessellator.setColorRGBA_I(color2, (int)(opacity2 * 255));

			if (gradient == 1) {
				tessellator.addVertex(width, 0, 0);
				tessellator.addVertex(0, 0, 0);
			} else {
				tessellator.addVertex(0, 0, 0);
				tessellator.addVertex(0, height, 0);
			}

			tessellator.draw();
			GL11.glShadeModel(GL11.GL_FLAT);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_ALPHA_TEST);

		}

		@Override
		public Type getType() {
			return Type.GRADIENT;
		}

	}

	public static class ItemIcon extends Drawable {
		@Property
		public float scale = 1;

		@Property
		public float angle = 30;

		@Property
		public int id;

		@Property
		public int meta;

		private static ItemStack drawStack = new ItemStack(0, 1, 0);

		private ItemIcon() {}

		public ItemIcon(short x, short y, int id, int meta) {
			super(x, y);
			this.id = id;
			this.meta = meta;
		}

		@Override
		@SideOnly(Side.CLIENT)
		protected void drawContents(float partialTicks) {
			drawStack.itemID = id;

			Item item;
			try {
				item = drawStack.getItem();
			} catch (ArrayIndexOutOfBoundsException e) {
				return;
			}

			if (item == null) return;
			drawStack.setItemDamage(meta);

			FontRenderer renderer = FMLClientHandler.instance().getClient().fontRenderer;

			if (item instanceof ItemBlock) GlassesRenderingUtils.renderRotatingBlockIntoGUI(renderer, drawStack, 1, 1, scale, this.angle);
			else GlassesRenderingUtils.renderItemIntoGUI(renderer, drawStack, 0, 0, this.scale);

		}

		@Override
		public Type getType() {
			return Type.ITEM;
		}

	}

	public static class LiquidIcon extends Drawable {
		@Property
		public short width;

		@Property
		public short height;

		@Property
		public String fluid;

		@Property
		public float alpha = 1;

		private LiquidIcon() {}

		public LiquidIcon(short x, short y, short width, short height, String fluid) {
			super(x, y);
			this.width = width;
			this.height = height;
			this.fluid = fluid;
		}

		@Override
		@SideOnly(Side.CLIENT)
		protected void drawContents(float partialTicks) {
			Fluid drawLiquid = FluidRegistry.getFluid(fluid);

			if (drawLiquid == null) return;

			Icon fluidIcon = drawLiquid.getFlowingIcon();
			if (fluidIcon == null) return;

			final int iconWidth = fluidIcon.getIconWidth();
			final int iconHeight = fluidIcon.getIconHeight();

			if (iconWidth <= 0 || iconHeight <= 0) return;

			TextureManager render = FMLClientHandler.instance().getClient().renderEngine;
			render.bindTexture(TextureMap.locationBlocksTexture);
			float xIterations = (float)width / iconWidth;
			float yIterations = (float)height / iconHeight;

			for (float xIteration = 0; xIteration < xIterations; xIteration += 1) {
				for (float yIteration = 0; yIteration < yIterations; yIteration += 1) {
					// Draw whole or partial
					final float xDrawSize = Math.min(xIterations - xIteration, 1);
					final float yDrawSize = Math.min(yIterations - yIteration, 1);

					GlassesRenderingUtils.drawTexturedQuadAdvanced(
							xIteration * iconWidth,
							yIteration * iconHeight,
							fluidIcon,
							xDrawSize * iconWidth,
							yDrawSize * iconHeight,
							xDrawSize,
							yDrawSize,
							alpha);
				}
			}

		}

		@Override
		public Type getType() {
			return Type.LIQUID;
		}

	}

	public static class Text extends Drawable {
		@Property
		public String text;

		@Property
		public int color;

		@Property
		public double alpha = 1;

		@Property
		public float scale = 1;

		private Text() {}

		public Text(short x, short y, String text, int color) {
			super(x, y);
			this.text = text;
			this.color = color;
		}

		@Override
		@SideOnly(Side.CLIENT)
		protected void drawContents(float partialTicks) {
			FontRenderer fontRenderer = FMLClientHandler.instance().getClient().fontRenderer;
			GL11.glScalef(scale, scale, scale);
			fontRenderer.drawString(text, 0, 0, ((int)(alpha * 255) << 24 | color));
		}

		@Override
		public Type getType() {
			return Type.TEXT;
		}
	}

	public int getTypeId() {
		return getType().ordinal();
	}

	public static Drawable createFromTypeId(int id) {
		Type type = Type.TYPES[id];
		return type.create();
	}
}
