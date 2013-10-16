package amidst;

import MoF.SaveLoader;
import amidst.json.InstallInformation;
import amidst.json.LauncherProfile;
import amidst.map.IconLayer;
import amidst.map.Layer;
import amidst.map.Map;
import amidst.map.layers.BiomeLayer;
import amidst.map.layers.GridLayer;
import amidst.map.layers.NetherFortressLayer;
import amidst.map.layers.SlimeLayer;
import amidst.map.layers.SpawnLayer;
import amidst.map.layers.StrongholdLayer;
import amidst.map.layers.TempleLayer;
import amidst.map.layers.VillageLayer;
import amidst.minecraft.Minecraft;
import amidst.minecraft.MinecraftUtil;
import com.google.gson.Gson;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Amidst {
	public final static int version_major = 3;
	public final static int version_minor = 4;
	public final static String versionOffset = "";
	public static final Gson gson = new Gson();
	
	public static void main(String args[]) throws ParseException, IOException {
		Options opts = new Options();
		opts.addOption(OptionBuilder.withLongOpt("profile")
									.hasArgs(1)
									.withValueSeparator()
									.withDescription("Use this minecraft profile")
									.create('p'));
		opts.addOption(OptionBuilder.withLongOpt("jar")
									.hasArgs(1)
									.withValueSeparator()
									.withDescription("Use the following minecraft.jar")
									.create('j'));
		opts.addOption(OptionBuilder.withLongOpt("help").create('h'));
		opts.addOption(OptionBuilder.withLongOpt("output")
									.hasArgs(1)
									.withDescription("Output file to write to")
									.create('o'));
		CommandLineParser parser = new PosixParser();
		CommandLine line = parser.parse(opts, args);
		if (line.hasOption('h') || args.length == 0) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("amidst", opts);
			System.exit(0);
		}
		if (line.hasOption('p')) {
			File profileJsonFile = new File(Util.minecraftDirectory + "/launcher_profiles.json");
			LauncherProfile profile = Util.readObject(profileJsonFile, LauncherProfile.class);
			InstallInformation installInformation = profile.profiles.get(line.getOptionValue('p'));
			if (installInformation == null)
				error("No such profile?");
			new Minecraft(installInformation.getJarFile());
		} else if (line.hasOption('j')) {
			new Minecraft(new File(line.getOptionValue('j')));
		} else {
			error("No --profile or --jar specified");
		}
		if (!line.hasOption('o'))
			error("No output file specified");
		Global.instance.seed = 0;
		MinecraftUtil.createBiomeGenerator(Global.instance.seed, SaveLoader.Type.DEFAULT);
		IconLayer[] iconLayers = new IconLayer[]{new VillageLayer()
												,new StrongholdLayer()
												,new TempleLayer()
												,new SpawnLayer()
												,new NetherFortressLayer()};
		Map map = new Map(new Layer[] {new BiomeLayer()
									,new SlimeLayer()}
						,new Layer[] {new GridLayer()}
						,iconLayers);
		map.width = 1920;
		map.height = 1080;
		BufferedImage output = new BufferedImage(map.width, map.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = output.createGraphics();
		int till = (map.width > map.height) ? map.width : map.height;
		till /= 50;
		for (int i = 0; i < till; i++)
			map.draw(g2d);
		File outputFile = new File(line.getOptionValue('o'));
		ImageIO.write(output, "png", outputFile);
		System.err.println("Wrote to " + outputFile.getAbsolutePath());
		System.exit(0);
	}

	public static void error(String error) {
		System.err.println(error);
		System.exit(1);
	}
}
