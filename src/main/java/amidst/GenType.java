package amidst;

import amidst.minecraft.Minecraft;
import amidst.minecraft.MinecraftObject;

public enum GenType {
	DEFAULT("default"), FLAT("flat"), LARGE_BIOMES("largeBiomes"), AMPLIFIED("amplified");
	private final String s;

	GenType(String s) {
		this.s = s;
	}

	@Override
	public String toString() {
		return s;
	}
	public MinecraftObject get() {
		return (MinecraftObject) Minecraft.getActiveMinecraft().getClassByName("WorldType").getValue(s);
	}

	public static GenType fromMixedCase(String name) {
		for (GenType t : values())
			if (t.s.equals(name))
				return t;
		throw new IllegalArgumentException("Value " + name + " not implemented");
	}

}
