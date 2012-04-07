package com.droiddev.client.widget;

import com.droiddev.client.property.StringProperty;

public class RadioGroup extends LinearLayout {
	public static final String TAG_NAME = "RadioGroup";
	public static final String[] menuItems =
		{"void check(int id)", "void clearCheck()", "int getCheckedRadioButtonId()",
		"setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener listener"};
	public static final String[] menuFunctions =
		{"check(int id)", "clearCheck()", "getCheckedRadioButtonId()",
		"setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener listener"};
	StringProperty checkedItem;
	public RadioGroup() {
		super();
		checkedItem = new StringProperty("Default Button", "android:checkedButton", "");
		// Defaults are different in RadioGroup *sigh*
		orientation.setDefaultIndex(1);
		orientation.setSelectedIndex(1);
		addProperty(checkedItem);
		this.setTagName(TAG_NAME);
		this.setMenuItems(menuItems);
		this.setMenuFunctions(menuFunctions);
	}
}