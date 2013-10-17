package amidst.map.layers;

import amidst.map.Fragment;
import amidst.map.Layer;

import java.util.Random;

public class SlimeLayer extends Layer {
	private static int size = Fragment.SIZE >> 4;
	private Random random = new Random();
	public SlimeLayer(final long seed) {
		super("slime", null, seed, 0.0f, size);
		setVisible(false);
	}
	
	public void drawToCache(Fragment fragment, int layerID) {
		int[] dataCache = Fragment.getIntArray();
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				int xPosition = fragment.getChunkX() + x;
				int yPosition = fragment.getChunkY() + y;
				random.setSeed(seed +
					(long) (xPosition * xPosition * 0x4c1906) + 
                    (long) (xPosition * 0x5ac0db) + 
                    (long) (yPosition * yPosition) * 0x4307a7L + 
                    (long) (yPosition * 0x5f24f) ^ 0x3ad8025f);
				
				dataCache[y * size + x] = (random.nextInt(10) == 0) ? 0xA0FF00FF : 0x00000000;
			}
		}
		
		fragment.setImageData(layerID, dataCache);
	}
	
}
