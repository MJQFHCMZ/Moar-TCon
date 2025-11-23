package com.existingeevee.moretcon.other;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tconstruct.library.client.CustomFontColor;

/**
 * Custom renderer based on CoFHs CoFHFontRenderer. Uses code from CoFHCore. Modified from Tinker's Construct
 * Credit goes to the devs of those mods.
 * 
 * Additional modifications and additions by ExistingEevee :3
 */
@SideOnly(Side.CLIENT)
public class CustomFontRenderer extends FontRenderer {

	//Essentially everything shoooouuuulllld be the same as the tinkers font renderer, but with a few extra bits and bobs.
	
	//TODO mixin ToolCore ToolPart to use this
	//usecases: unique tooltrait shake
	
	private static CustomFontRenderer instance;

	protected static int marker = ObfuscationReflectionHelper.getPrivateValue(CustomFontColor.class, null, "MARKER");
	
	private boolean dropShadow;
	private int state = 0;
	private int customColorRed;
	private int customColorGreen;
	private int customColorBlue;

	private boolean shakeStyle = false;
	private boolean oscillateStyle = false;

	private CustomFontRenderer(GameSettings gameSettingsIn, ResourceLocation location, TextureManager textureManagerIn) {
		super(gameSettingsIn, location, textureManagerIn, true);
	}

	@Nonnull
	@Override
	public List<String> listFormattedStringToWidth(@Nonnull String str, int wrapWidth) {
		return Arrays.asList(this.wrapFormattedStringToWidth(str, wrapWidth).split("\n"));
	}

	@Override
	public String wrapFormattedStringToWidth(String str, int wrapWidth) {
		int i = this.sizeStringToWidth(str, wrapWidth);

		if (str.length() <= i) {
			return str;
		} else {
			String s = str.substring(0, i);
			char c0 = str.charAt(i);
			boolean flag = c0 == 32 || c0 == 10;
			String s1 = getCustomFormatFromString(s) + str.substring(i + (flag ? 1 : 0));
			return s + "\n" + this.wrapFormattedStringToWidth(s1, wrapWidth);
		}
	}

	public static String getCustomFormatFromString(String text) {		
		String s = "";
		int i = 0;
		int j = text.length();

		while ((i < j - 1)) {
			char c = text.charAt(i);
			// vanilla formatting
			if (c == 167) {

				char c0 = text.charAt(i + 1);

				if (c0 >= 48 && c0 <= 57 || c0 >= 97 && c0 <= 102 || c0 >= 65 && c0 <= 70) {
					s = "\u00a7" + c0;
					i++;
				} else if (c0 >= 107 && c0 <= 111 || c0 >= 75 && c0 <= 79 || c0 == 114 || c0 == 82) {
					s = s + "\u00a7" + c0;
					i++;
				}
			}
			// custom formatting
			else if ((int) c >= marker && (int) c <= marker + 0xFF) {
				s = String.format("%s%s%s", c, text.charAt(i + 1), text.charAt(i + 2));
				i += 2;
			}
			i++;
		}

		return s;
	}

	@Override
	public void resetStyles() {
		this.shakeStyle = false;
		this.oscillateStyle = false;
		super.resetStyles();
	}

	@Override
	public int renderString(@Nonnull String text, float x, float y, int color, boolean dropShadow) {
		this.dropShadow = dropShadow;
		return super.renderString(text, x, y, color, dropShadow);
	}

	@Override
	public float renderUnicodeChar(char letter, boolean italic) {
		// special color settings through char code
		// we use \u2700 to \u27FF, where the lower byte represents the Hue of the color
		if ((int) letter >= marker && (int) letter <= marker + 0xFF) {
			int value = letter & 0xFF;
			switch (state) {
			case 0:
				customColorRed = value;
				break;
			case 1:
				customColorGreen = value;
				break;
			case 2:
				customColorBlue = value;
				break;
			default:
				this.setColor(1f, 1f, 1f, 1f);
				return 0;
			}

			state = ++state % 3;

			int color = (customColorRed << 16) | (customColorGreen << 8) | customColorBlue | (0xff << 24);
			if ((color & -67108864) == 0) {
				color |= -16777216;
			}

			if (dropShadow) {
				color = (color & 16579836) >> 2 | color & -16777216;
			}

			this.setColor(((color >> 16) & 255) / 255f,
					((color >> 8) & 255) / 255f,
					((color >> 0) & 255) / 255f,
					((color >> 24) & 255) / 255f);
			return 0; 
		}

		// invalid sequence encountered
		if (state != 0) {
			state = 0;
			this.setColor(1f, 1f, 1f, 1f);
		}

		return super.renderUnicodeChar(letter, italic);
	}

	@Override // we are completely overwriting this lul
	public void renderStringAtPos(String text, boolean shadow) {
		for (int i = 0; i < text.length(); ++i) {
			char c0 = text.charAt(i);

			/* EV START */
			if (c0 == 167 && i + 2 < text.length() && text.charAt(i + 1) == '^') { // render our custom styles
				char type = text.charAt(i + 2);

				if (type == '0') {
					this.shakeStyle = true;
				}
				if (type == '1') {
					this.oscillateStyle = true;
				}
				i += 2;
				/* EV END */
			} else

			if (c0 == 167 && i + 1 < text.length()) {
				int i1 = "0123456789abcdefklmnor".indexOf(String.valueOf(text.charAt(i + 1)).toLowerCase(Locale.ROOT).charAt(0));

				if (i1 < 16) {
					this.randomStyle = false;
					this.boldStyle = false;
					this.strikethroughStyle = false;
					this.underlineStyle = false;
					this.italicStyle = false;

					/* EV START */
					this.shakeStyle = false;
					this.oscillateStyle = false;
					/* EV END */
					
					if (i1 < 0 || i1 > 15) {
						i1 = 15;
					}

					if (shadow) {
						i1 += 16;
					}

					int j1 = this.colorCode[i1];
					this.textColor = j1;
					setColor((float) (j1 >> 16) / 255.0F, (float) (j1 >> 8 & 255) / 255.0F, (float) (j1 & 255) / 255.0F, this.alpha);
				} else if (i1 == 16) {
					this.randomStyle = true;
				} else if (i1 == 17) {
					this.boldStyle = true;
				} else if (i1 == 18) {
					this.strikethroughStyle = true;
				} else if (i1 == 19) {
					this.underlineStyle = true;
				} else if (i1 == 20) {
					this.italicStyle = true;
				} else if (i1 == 21) {
					this.randomStyle = false;
					this.boldStyle = false;
					this.strikethroughStyle = false;
					this.underlineStyle = false;
					this.italicStyle = false;
					setColor(this.red, this.blue, this.green, this.alpha);

					/* EV START */
					this.shakeStyle = false;
					this.oscillateStyle = false;
					/* EV END */
				}

				++i;
			} else {

				int j = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(c0);

				if (this.randomStyle && j != -1) {
					int k = this.getCharWidth(c0);
					char c1;

					while (true) {
						j = this.fontRandom.nextInt("\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".length());
						c1 = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".charAt(j);

						if (k == this.getCharWidth(c1)) {
							break;
						}
					}

					c0 = c1;
				}

				double posXOff = 0;
				double posYOff = 0;
				
				if (this.shakeStyle) {
					posXOff = Math.random() - 0.5;
					posYOff = Math.random() - 0.5;
				}
				
				if (this.oscillateStyle) {	
					posYOff += Math.cos(Minecraft.getSystemTime() / 1000d * Math.PI);
				}
				
				float f1 = j == -1 || this.unicodeFlag ? 0.5f : 1f;
				boolean flag = (c0 == 0 || j == -1 || this.unicodeFlag) && shadow;

				if (flag) {
					this.posX -= f1;
					this.posY -= f1;
				}

				this.posX += posXOff;
				this.posY += posYOff;
				float f = this.renderChar(c0, this.italicStyle);
				this.posX -= posXOff;
				this.posY -= posYOff;
				
				if (flag) {
					this.posX += f1;
					this.posY += f1;
				}

				if (this.boldStyle) {
					this.posX += f1;

					if (flag) {
						this.posX -= f1;
						this.posY -= f1;
					}
					
					this.posX += posXOff;
					this.posY += posYOff;
					this.renderChar(c0, this.italicStyle);
					this.posX -= posXOff;
					this.posY -= posYOff;
					
					this.posX -= f1;

					if (flag) {
						this.posX += f1;
						this.posY += f1;
					}

					++f;
				}
				this.posX += posXOff;
				this.posY += posYOff;
				doDraw(f);
				this.posX -= posXOff;
				this.posY -= posYOff;
			}
		}
	}
	
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		super.onResourceManagerReload(resourceManager);
		setUnicodeFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLocaleUnicode() || Minecraft.getMinecraft().gameSettings.forceUnicodeFont);
		setBidiFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLanguageBidirectional());
	}

	public static void init(GameSettings gameSettings, ResourceLocation resourceLocation, TextureManager renderEngine) {
		instance = new CustomFontRenderer(gameSettings, resourceLocation, renderEngine);
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(instance);
	}

	public static CustomFontRenderer getInstance() {
		return instance;
	}
}