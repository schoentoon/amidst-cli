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
import java.util.Random;

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
		opts.addOption(OptionBuilder.withLongOpt("seed")
									.hasArgs(1)
									.withDescription("The seed to use for the output")
									.create('s'));
		opts.addOption(OptionBuilder.withLongOpt("width")
									.hasArgs(1)
									.withDescription("The width of the output image")
									.create('W'));
		opts.addOption(OptionBuilder.withLongOpt("height")
									.hasArgs(1)
									.withDescription("The width of the output image")
									.create('H'));
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
		long seed;
		if (line.hasOption('s')) {
			try {
				seed = Long.parseLong(line.getOptionValue('s'));
			} catch (NumberFormatException e) {
				seed = line.getOptionValue('s').hashCode();
				Log.i("Using seed:", seed);
			}
		} else {
			seed = new Random().nextLong();
			Log.i("Using random seed:", seed);
		}
		MinecraftUtil.createBiomeGenerator(seed, SaveLoader.Type.DEFAULT);
		IconLayer[] iconLayers = new IconLayer[]{new VillageLayer(seed)
												,new StrongholdLayer(seed)
												,new TempleLayer(seed)
												,new SpawnLayer(seed)
												,new NetherFortressLayer(seed)};
		Map map = new Map(new Layer[] {new BiomeLayer(seed)
									,new SlimeLayer(seed)}
						,new Layer[] {new GridLayer(seed)}
						,iconLayers);
		map.width = parseInt(line.getOptionValue('W', "1920"), 1920);
		map.height = parseInt(line.getOptionValue('H', "1080"), 1080);
		BufferedImage output = new BufferedImage(map.width, map.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = output.createGraphics();
		int till = (map.width > map.height) ? map.width : map.height;
		till /= 25;
		for (int i = 0; i < till; i++)
			map.draw(g2d);
		File outputFile = new File(line.getOptionValue('o'));
		ImageIO.write(output, "png", outputFile);
		Log.i("Wrote to", outputFile.getAbsolutePath());
		System.exit(0);
	}

	private static int parseInt(String str, int def) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return def;
		}
	}

	public static void error(String error) {
		System.err.println(error);
		System.exit(1);
	}
}
