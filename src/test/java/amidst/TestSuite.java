package amidst;

import org.apache.commons.cli.ParseException;
import org.junit.Test;

import java.io.IOException;

public class TestSuite {
	@Test
	public void run() throws IOException, ParseException {
		Amidst.main(new String[]{"-j",System.getProperty("minecraftjar", "minecraft.jar"),"-o","output/test.png"});
	}
}
