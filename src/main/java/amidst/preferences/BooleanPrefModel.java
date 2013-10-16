package amidst.preferences;

import javax.swing.JToggleButton.ToggleButtonModel;
import java.util.prefs.Preferences;

/** Autosaving ToggleButtonModel
 */
public class BooleanPrefModel extends ToggleButtonModel implements PrefModel<Boolean> {
	private final String key;
	private final Preferences pref;
	
	public BooleanPrefModel(Preferences pref, String key, boolean selected) {
		super();
		this.pref = pref;
		this.key = key;
		//if the preference doesn't exist, set it,
		//else sync the ToggleButtonModel's status
		set(pref.getBoolean(key, selected));
	}
	
	public String getKey() {
		return key;
	}
	
	public Boolean get() {
		assert pref.get(key, null) != null && pref.getBoolean(key, false) == super.isSelected();
		return super.isSelected();
	}
	
	@Override
	public boolean isSelected() {
		return get();
	}
	
	public void set(Boolean value) {
		super.setSelected(value);
		pref.putBoolean(key, value);
	}
	
	@Override
	public void setSelected(boolean value) {
		set(value);
	}
}
