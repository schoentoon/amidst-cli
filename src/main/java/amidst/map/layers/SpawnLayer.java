package amidst.map.layers;

import amidst.Log;
import amidst.map.Fragment;
import amidst.map.IconLayer;
import amidst.map.MapObjectSpawn;
import amidst.minecraft.Biome;
import amidst.minecraft.MinecraftUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class SpawnLayer extends IconLayer {
	private MapObjectSpawn spawnObject;
	public static final ArrayList<Biome> validBiomes = new ArrayList<Biome>(Arrays.asList(
			Biome.forest, 
			Biome.plains, 
			Biome.taiga, 
			Biome.taigaHills, 
			Biome.forestHills, 
			Biome.jungle, 
			Biome.jungleHills
		));
	
	public SpawnLayer(final long seed) {
		super("spawnPoint", seed);
		setVisible(true);
		
		Point spawnCenter = getSpawnPosition();
		spawnObject = new MapObjectSpawn(spawnCenter.x, spawnCenter.y);
	}
	
	public void generateMapObjects(Fragment frag) {
		if ((spawnObject.globalX >= frag.blockX) &&
			(spawnObject.globalX < frag.blockX + Fragment.SIZE) &&
			(spawnObject.globalY >= frag.blockY) &&
			(spawnObject.globalY < frag.blockY + Fragment.SIZE)) {
			spawnObject.parentLayer = this;
			frag.addObject(spawnObject);
		}
	}
	
	private Point getSpawnPosition() {
		Random random = new Random(seed);
		Point location = MinecraftUtil.findValidLocation(0, 0, 256, validBiomes, random);
		int x = 0;
		int y = 0;
		if (location != null) {
			x = location.x;
			y = location.y;
		} else {
			Log.debug("Unable to find spawn biome.");
		}

		return new Point(x, y);
	}
	
}
