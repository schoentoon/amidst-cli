package amidst.map;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Layer implements Comparable<Layer> {
	public String name;
	public int size;
	public float depth;
	public float minZoom = 0;
	public float maxZoom = 1024;
	
	public double scale;
	private AffineTransform mat = new AffineTransform();
	
	private int[] defaultData;
	
	public boolean cacheEnabled;
	public CacheManager cacheManager;
	public String cachePath;
	
	protected Map map;
	
	public boolean isTransparent;
	protected final long seed;

	public Layer(String name, CacheManager cacheManager, final long seed) {
		this(name, cacheManager, 1f, seed);
	}
	public Layer(String name, CacheManager cacheManager, float depth, final long seed) {
		this(name, cacheManager, seed, depth, Fragment.SIZE);
	}
	public Layer(String name, CacheManager cacheManager, final long seed, float depth, int size) {
		this.seed = seed;
		this.name = name;
		this.cacheManager = cacheManager;
		this.cacheEnabled = (cacheManager != null);
		this.depth = depth;
		this.size = size;
		defaultData = new int[size*size];
		scale = ((double)Fragment.SIZE)/((double)size);
		for (int i = 0; i < defaultData.length; i++)
			defaultData[i] = 0x00000000;
		isTransparent = true;
	}
	public void setMap(Map map) {
		this.map = map;
	}

	public Map getMap() {
		return map;
	}
	
	public boolean isVisible() {
		return true;
	}
	
	public void unload(Fragment frag) {
		if (cacheEnabled) {
			cacheManager.unload(frag);
		}
	}
	
	
	
	public Layer setMaxZoom(float maxZoom) {
		this.maxZoom = maxZoom;
		return this;
	}
	public Layer setMinZoom(float minZoom) {
		this.minZoom = minZoom;
		return this;
	}
	
	public int compareTo(Layer obj) {
		Layer lObj = (Layer)obj;
		if (depth < lObj.depth) return -1;
		return (depth > lObj.depth)?1:0;
	}
	
	public int[] getDefaultData() {
		return defaultData;
	}
	
	public void load(Fragment frag, int layerID) {
		if (cacheEnabled) {
			cacheManager.load(frag, layerID);
		} else {
			drawToCache(frag, layerID);
			//PluginManager.call(funcDraw, frag, layerID);
		}
	}
	
	public AffineTransform getMatrix(AffineTransform inMat) {
		mat.setTransform(inMat);
		return mat;
	}
	public AffineTransform getScaledMatrix(AffineTransform inMat) {
		mat.setTransform(inMat); mat.scale(scale, scale);
		return mat;
	}
	public AffineTransform getScaledMatrix(AffineTransform inMat, float mipmapScale) {
		mat.setTransform(inMat); mat.scale(scale * mipmapScale, scale * mipmapScale);
		return mat;
	}
	
	public void drawToCache(Fragment fragment, int layerID) {
		
	}
	
	public void drawLive(Fragment fragment, Graphics2D g, AffineTransform mat) {
		
	}
}

