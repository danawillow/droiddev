package com.droiddev.client.widget;

import java.util.Vector;

import com.droiddev.client.property.BooleanProperty;
import com.droiddev.client.property.Property;
import com.droiddev.client.property.SelectProperty;
import com.droiddev.client.property.StringProperty;
import com.droiddev.client.util.ImageResources;
import com.droiddev.client.util.NineWayImage;
import com.google.gwt.user.client.ui.Image;

public class EditView extends TextView {

	public static final String TAG_NAME = "EditText";
	public static final String[] menuItems =
		{"Editable getText()", "int length()", "void setInputType(int type)", "void setText(String text)"};
	public static final String[] menuFunctions =
		{"getText()", "length()", "setInputType(int type)", "setText(String text)"};
	public static final String[] menuImports =
		{null, null, "android.text.InputType", null};

	BooleanProperty password;
	SelectProperty numeric;
	BooleanProperty phone;
	BooleanProperty autoText;
	SelectProperty capitalize;
	StringProperty digits;

	NineWayImage img;
	Image img_base;

	public static final String[] propertyNames =
			new String[] {"android:password", "android:capitalize", "android:numeric", "android:phoneNumber","android:autoText","android:digits"};

	public EditView(String txt) {
		super(txt, TAG_NAME);
		this.setTagName(TAG_NAME);
		this.setMenuItems(menuItems);
		this.setMenuFunctions(menuFunctions);
		this.setMenuImports(menuImports);

		password = new BooleanProperty("Password", "android:password", false);
		capitalize = new SelectProperty("Capitalize", "android:capitalize", new String[] {"sentences", "words"}, 0);
		numeric = new SelectProperty("Number Format", "android:numeric", new String[] {"integer", "signed", "decimal"}, 0);
		phone = new BooleanProperty("Phone Number", "android:phoneNumber", false);
		autoText = new BooleanProperty("Correct Spelling", "android:autoText", false);
		digits = new StringProperty("Valid Characters", "android:digits", "");

		props.add(password);
		props.add(numeric);
		props.add(phone);
		props.add(autoText);
		props.add(capitalize);
		props.add(digits);

		fontSz.setStringValue("18sp");
		fontSize = 18;
		getCanvas().getContext2d().setFont(font);
		img_base = ImageResources.instance().get("textfield_default.9.png");
		img = new NineWayImage(img_base, 11, 6);
		pad_x = 20;
		pad_y = 0;

		apply();
	}

	@Override
	public Vector<Property> getProperties() {
		Vector<Property> ret = super.getProperties();
		if (digits.getStringValue() == null || digits.getStringValue().length() < 1)
			ret.remove(digits);
		return ret;
	}

	@Override
	public int getContentWidth() {
		if (password != null && password.getBooleanValue()) {
			String s = "";
			for (int i=0;i<text.getStringValue().length();i++)
				s = s+'\245';
			return stringLength(s)+pad_x;
		}
		else {
			return super.getContentWidth();
		}
	}

	@Override
	public int getContentHeight() {
		if (img_base != null) {
			return img_base.getHeight()-5;
		}
		return fontSize;
	}

	@Override
	public void paint() {
		getCanvas().setCoordinateSpaceWidth(getWidth());
		getCanvas().setCoordinateSpaceHeight(getHeight());
		img.paint(getCanvas().getContext2d(), 0, 0, getWidth(), getHeight());
		font = fontSize + "px Arial";
		getCanvas().getContext2d().setFont(font);
		String s;
		if (password.getBooleanValue()) {
			s = "";
			for (int i=0;i<text.getStringValue().length();i++)
				s = s+'\245';
		}
		else {
			s = text.getStringValue();
		}
		drawText(0, (getHeight()+fontSize)/2-1, CENTER);
		getCanvas().getContext2d().fillRect(pad_x/2-4, (getHeight()-fontSize)/2-3, 1, fontSize+5);
	}
}