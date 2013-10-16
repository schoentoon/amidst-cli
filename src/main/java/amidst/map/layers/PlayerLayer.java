package amidst.map.layers;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import MoF.SaveLoader;
import MoF.SkinManager;
import amidst.Log;
import amidst.Options;
import amidst.map.Fragment;
import amidst.map.IconLayer;
import amidst.map.Map;
import amidst.map.MapObjectNether;
import amidst.map.MapObjectPlayer;
import amidst.map.MapObjectStronghold;
import amidst.map.MapObjectVillage;

public class PlayerLayer extends IconLayer {
	public SaveLoader saveLoader;
	public static SkinManager skinManager = new SkinManager();
	static {
		skinManager.start();
	}
	public PlayerLayer(SaveLoader saveLoader) {
		super("players");
		setVisibilityPref(Options.instance.showPlayers);
		this.saveLoader = saveLoader;
		
		for (MapObjectPlayer player : saveLoader.getPlayers())
			skinManager.addPlayer(player);
	}
	public void generateMapObjects(Fragment frag) {
		List<MapObjectPlayer> players =  saveLoader.getPlayers();
		for (MapObjectPlayer player : players) {
			if ((player.globalX >= frag.blockX) &&
				(player.globalX < frag.blockX + Fragment.SIZE) &&
				(player.globalY >= frag.blockY) &&
				(player.globalY < frag.blockY + Fragment.SIZE)) {
				player.parentLayer = this;
				player.parentFragment = frag;
				frag.addObject(player);
			}
		}
	}
	
	public void clearMapObjects(Fragment frag) {
		for (int i = 0; i < frag.objectsLength; i++) {
			if (frag.objects[i] instanceof MapObjectPlayer)
				((MapObjectPlayer)frag.objects[i]).parentFragment = null;
			
		}
		super.clearMapObjects(frag);
		
	}
}
