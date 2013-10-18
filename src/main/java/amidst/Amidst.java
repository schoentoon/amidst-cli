package amidst;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Amidst {
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
									.withDescription("Output file to write to, %seed% will get replaced with the seed")
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
		opts.addOption(OptionBuilder.withDescription("Mark nether fortresses").create("netherfortress"));
		opts.addOption(OptionBuilder.withDescription("Mark slime chunks").create("slimechunks"));
		opts.addOption(OptionBuilder.withDescription("Don't mark villages").create("novillages"));
		opts.addOption(OptionBuilder.withDescription("Don't mark temples (witch huts are temples too)").create("notemples"));
		opts.addOption(OptionBuilder.withDescription("Don't mark strongholds").create("nostrongholds"));
		opts.addOption(OptionBuilder.withDescription("Don't mark spawn").create("nospawn"));
		opts.addOption(OptionBuilder.withDescription("Draw the seed").create("drawseed"));
		CommandLineParser parser = new PosixParser();
		CommandLine line = parser.parse(opts, args);
		if (line.hasOption('h') || args.length == 0) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("amidst", opts, true);
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
		String seed_string = null;
		GenType genType = GenType.DEFAULT;
		if (line.hasOption('s')) {
			try {
				seed = Long.parseLong(line.getOptionValue('s'));
			} catch (NumberFormatException e) {
				seed_string = line.getOptionValue('s');
				seed = seed_string.hashCode();
				Log.i("Using seed:", seed);
			}
		} else {
			seed = new Random().nextLong();
			Log.i("Using random seed:", seed);
		}
		MinecraftUtil.createBiomeGenerator(seed, genType);
		List<IconLayer> iconLayerList = new ArrayList<IconLayer>(5);
		if (!line.hasOption("novillages"))
			iconLayerList.add(new VillageLayer(seed));
		if (!line.hasOption("nostrongholds"))
			iconLayerList.add(new StrongholdLayer(seed));
		if (!line.hasOption("notemples"))
			iconLayerList.add(new TempleLayer(seed));
		if (!line.hasOption("nospawn"))
			iconLayerList.add(new SpawnLayer(seed));
		if (line.hasOption("netherfortress"))
			iconLayerList.add(new NetherFortressLayer(seed));
		List<Layer> layerList = new ArrayList<Layer>(2);
		layerList.add(new BiomeLayer(seed));
		if (line.hasOption("slimechunks"))
			layerList.add(new SlimeLayer(seed));
		Map map = new Map(layerList.toArray(new Layer[layerList.size()])
						,new Layer[] {new GridLayer(seed)}
						,iconLayerList.toArray(new IconLayer[iconLayerList.size()]));
		map.width = parseInt(line.getOptionValue('W', "1920"), 1920);
		map.height = parseInt(line.getOptionValue('H', "1080"), 1080);
		BufferedImage output = new BufferedImage(map.width, map.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = output.createGraphics();
		int till = (map.width > map.height) ? map.width : map.height;
		till /= 25;
		for (int i = 0; i < till; i++)
			map.draw(g2d);
		if (opts.hasOption("drawseed")) {
			Font textFont = new Font("arial", Font.BOLD, 15);
			Color textColor = new Color(1f, 1f, 1f);
			Color panelColor = new Color(0.2f, 0.2f, 0.2f, 0.7f);
			FontMetrics textMetrics = g2d.getFontMetrics(textFont);
			g2d.setColor(panelColor);
			String raw = Long.toString(seed);
			if (seed_string != null)
				raw += " \"" + seed_string + "\"";
			g2d.fillRect(10, 10, textMetrics.stringWidth(raw) + 20, 30);
			g2d.setColor(textColor);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2d.setFont(textFont);
			g2d.drawString(raw, 20, 30);
		}
		File outputFile = new File(line.getOptionValue('o').replace("%seed%",Long.toString(seed)));
		outputFile.mkdirs();
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
