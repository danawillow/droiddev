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
		{"CharSequence getText()", "int length()", "void setInputType(int type)", "final void setText(CharSequence text)"};
	public static final String[] menuFunctions =
		{"getText()", "length()", "setInputType(int type)", "setText(CharSequence text)"};
	public static final String[] menuImports =
		{null, null, null, null};

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

		/*
		String theme = AndroidEditor.instance().getTheme();
		if (theme == null || theme.equals("default")) {
			fontSz.setStringValue("18sp");
			fontSize = 18;
			img_base = ImageResources.instance().getImage("mdpi/textfield_default.9");
			if (img_base != null) {
				this.img = new NineWayImage(img_base, 11, 6);
			}
			pad_x = 20;
			pad_y = 0;
		}
		else if (theme.equals("light")) {
			img_base = ImageResources.instance().getImage("light/editbox_background_normal.9");
			if (img_base != null) {
				this.img = new NineWayImage(img_base, 10, 10);
			}
			pad_x = 18;
			pad_y = 0;
		}
		 */

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
		//int sup = super.getContentWidth();
		//if (sup > fontSize) {
		//	return sup;
		//}
		//else {
		if (img_base != null) {
			/*
			String theme = AndroidEditor.instance().getTheme();
			if (theme == null || theme.equals("default"))
				return img_base.getHeight(null)-5;
			else if (theme.equals("light"))
				return img_base.getHeight(null)-5;
			 */
			return img_base.getHeight()-5;
		}
		return fontSize;
		//}
	}

	/*
	@Override
	public void paint(Graphics g) {
		if (img == null) {
			g.setColor(Color.white);
			g.fillRoundRect(getX(), getY(), getWidth(), getHeight(), 8, 8);
			g.setColor(Color.darkGray);
			g.drawRoundRect(getX(), getY(), getWidth(), getHeight(), 8, 8);
		}
		else {
			img.paint(g, getX(), getY(),getWidth(), getHeight());
			g.setColor(Color.darkGray);
		}
		g.setFont(f);
		String s;
		if (password.getBooleanValue()) {
			s = "";
			for (int i=0;i<text.getStringValue().length();i++)
				s = s+'\245';
		}
		else {
			s = text.getStringValue();
		}
		g.setColor(textColor.getColorValue());
		//g.drawString(s, getX()+pad_x/2, getY()+fontSize+pad_y/2-1);
		this.drawText(g, 0, (fontSize+getHeight())/2-1);
		g.setColor(Color.black);
		g.fillRect(getX()+pad_x/2-4, getY()+(getHeight()-fontSize)/2-3, 1, fontSize+5);
	}
	 */

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