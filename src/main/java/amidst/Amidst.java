package amidst;

import amidst.json.InstallInformation;
import amidst.json.LauncherProfile;
import amidst.minecraft.Minecraft;
import com.google.gson.Gson;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;

public class Amidst {
	public final static int version_major = 3;
	public final static int version_minor = 4;
	public final static String versionOffset = "";
	public static final Gson gson = new Gson();
	
	public static void main(String args[]) throws ParseException, FileNotFoundException, MalformedURLException {
		Options opts = new Options();
		opts.addOption(OptionBuilder.withArgName("profile=default")
				                    .hasArgs(1)
				                    .withValueSeparator()
				                    .withDescription("Use this minecraft profile")
				                    .create('p'));
		opts.addOption(OptionBuilder.withArgName("jarfile=path")
				                    .hasArgs(1)
		                            .withValueSeparator()
		                            .withDescription("Use the following minecraft.jar")
		                            .create('j'));
		CommandLineParser parser = new PosixParser();
		CommandLine line = parser.parse(opts, args);
		Minecraft minecraft = null;
		if (line.hasOption('p')) {
			File profileJsonFile = new File(Util.minecraftDirectory + "/launcher_profiles.json");
			LauncherProfile profile = Util.readObject(profileJsonFile, LauncherProfile.class);
			InstallInformation installInformation = profile.profiles.get(line.getOptionValue('p'));
			if (installInformation == null)
				error("No such profile?");
			minecraft = new Minecraft(installInformation.getJarFile());
		} else if (line.hasOption('j')) {
			minecraft = new Minecraft(new File(line.getOptionValue('j')));
		}
	}

	public static void error(String error) {
		System.err.println(error);
		System.exit(1);
	}
}
