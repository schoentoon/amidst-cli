package amidst.map;

public class IconLayer {
	public String name;
	protected Map map;
	protected final long seed;
	
	public IconLayer(String name, final long seed) {
		this.name = name;
		this.seed = seed;
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

	public void generateMapObjects(Fragment frag) {
	}
	
	public void clearMapObjects(Fragment frag) {
		frag.objectsLength = 0;
	}
}
