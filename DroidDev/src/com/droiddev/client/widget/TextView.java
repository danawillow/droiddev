package com.droiddev.client.widget;

import java.util.Vector;

import com.droiddev.client.AndroidEditor;
import com.droiddev.client.property.ColorProperty;
import com.droiddev.client.property.Property;
import com.droiddev.client.property.SelectProperty;
import com.droiddev.client.property.StringProperty;
import com.droiddev.client.util.DisplayMetrics;

public class TextView extends AbstractWidget {
	public static final String TAG_NAME = "TextView";
	public static final int START = 0;
	public static final int CENTER = 1;
	public static final int END = 2;

	int fontSize = 14;
	String font = "14px Arial";

	StringProperty text;
	StringProperty hint;
	StringProperty fontSz;
	SelectProperty face;
	SelectProperty style;
	SelectProperty align;
	ColorProperty textColor;

	int pad_x = 6;
	int pad_y = 4;

	public static final String[] propertyNames =
		new String[] {"android:hint", "android:textSize", "android:textStyle", "android:typeface", "android:textColor"};
	

	public static final String[] menuItems = {"void append(String text)", "String getText()", "void setText(String text)"};
	public static final String[] menuFunctions = {"append(String text)", "getText()", "setText(String text)"};
	public static final String[] menuImports = {null, null, null};

	public TextView(String str, String tagName) {
		super(tagName);
		this.setMenuItems(menuItems);
		this.setMenuFunctions(menuFunctions);
		this.setMenuImports(menuImports);

		text = new StringProperty("Text", "android:text", "");
		if (str != null) {
			text.setStringValue(str);
		}
		hint = new StringProperty("Default Text", "android:hint", "");
		fontSz = new StringProperty("Font Size", "android:textSize", fontSize+"sp");
		face = new SelectProperty("Font Face", "android:typeface", new String[] {"normal","sans","serif","monospace"}, 0);
		style = new SelectProperty("Font Style", "android:textStyle", new String[] {"normal", "bold", "italic", "bold|italic"}, 0);
		textColor = new ColorProperty("Text Color", "android:textColor", null);
		align = new SelectProperty("Text Alignment", "android:gravity", new String[] {"left","center","right"}, 2);
		props.add(text);
		props.add(hint);
		props.add(fontSz);
		props.add(face);
		props.add(style);
		props.add(textColor);
		props.add(align);

		apply();
	}
	
	public TextView(String str) {
		this(str, TAG_NAME);
	}

	@Override
    public void apply() {
		super.apply();
		
		if (fontSz.getStringValue() != null && fontSz.getStringValue().length() > 0) {
			fontSize = (DisplayMetrics.readSize(fontSz));
		}

		font = fontSize + "px Arial";
		getCanvas().getContext2d().setFont(font);
		
		this.readWidthHeight();
		this.baseline = fontSize+pad_y/2;
	}

	protected Vector<String> buildLineBreaks(String textVal) {
		Vector<String> res = new Vector<String>();
		if (textVal == null) {
			return res;
		}
		String str = textVal;
		int ix;
		do {
			ix = str.indexOf('\n');
			String txt = str;
			if (ix != -1) {
				txt = str.substring(0, ix);
				str = str.substring(ix+1);
			}
			int width = getWidth();
			if (width < 0) {
				res.add(txt);
				return res;
			}

			int l = stringLength(txt);
			while (l > width) {
				int bk = 1;
				while (stringLength(txt.substring(0,bk)) < width) bk++;
				bk--;
				if (bk == 0) {
					return res;
				}
				String sub = txt.substring(0, bk);
				res.add(sub);
				txt = txt.substring(bk);
				l = stringLength(txt);
			}
			res.add(txt);
		} while (ix != -1);
		return res;
	}

	protected int stringLength(String str) {
		if (str == null)
			return 0;
		return (int)(getCanvas().getContext2d().measureText(str).getWidth());
	}

    public String getText() {
        String txt = text.getStringValue();
        if (txt == null || txt.length() == 0) {
            txt = hint.getStringValue();
        }
        return txt;
    }

	@Override
    protected int getContentWidth() {
		int l = stringLength(getText())+pad_x;
		if (l > AndroidEditor.instance().getScreenX())
			l = AndroidEditor.instance().getScreenX()-getX();
		return l;
	}

	@Override
    protected int getContentHeight() {
		Vector<String> texts = buildLineBreaks(getText());
		if (texts.size() == 0) return fontSize+pad_y;
		int h = texts.size()*(fontSize+1)+pad_y;
		return h;
	}


	protected void drawText(int x, int h) {
		int aln = START;
		if (align.getStringValue().equals("end")) {
			aln = END;
		}
		else if (align.getStringValue().equals("center")) {
			aln = CENTER;
		}
		this.drawText(x, h, aln);
	}

	protected void drawText(int dx, int h, int align) {
		String txt = getText();
		drawText(txt, dx, h, align);
	}

	protected void drawText(String txt, int dx, int h, int align) {
		int tx = 0;
		if (txt == null) {
			return;
		}
		for (String s : buildLineBreaks(txt)) {
			int l = stringLength(s);
			if (align == END) {
				tx = getWidth()-l-pad_x/2+dx;
			}
			else if (align == CENTER) {
				tx = getWidth()/2-l/2+dx;
			}
			else {
				tx = pad_x/2+dx;
			}
			getCanvas().getContext2d().setFont(font);
			getCanvas().getContext2d().fillText(getText(), tx, h);
			h += fontSize+1;
			if (h > getHeight())
				break;
		}
	}
	
	public void paint() {
		getCanvas().setCoordinateSpaceWidth(getWidth());
		getCanvas().setCoordinateSpaceHeight(getHeight());
		getCanvas().getContext2d().setFont(font);
	    drawText(0, fontSize+pad_y/2);
	}

	@Override
    public Vector<Property> getProperties() {
		return props;
	}
}