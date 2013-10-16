package amidst.resources;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ResourceLoader {
	private ResourceLoader() {}
	public static BufferedImage getImage(String name) {
		try {
			return ImageIO.read(ResourceLoader.class.getResourceAsStream(name));
		} catch (IOException e) { //Don't forget to run the tests :)
			throw new RuntimeException(e);
		}
	}
}
