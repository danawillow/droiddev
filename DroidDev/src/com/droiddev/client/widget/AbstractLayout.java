package com.droiddev.client.widget;

import java.util.Vector;

public abstract class AbstractLayout extends AbstractWidget implements Layout {
	protected Vector<Widget> widgets;
	
	public AbstractLayout(String tagName) {
		super(tagName);
		this.widgets = new Vector<Widget>();
		apply();
	}
	
	@Override
    public String toString() {
		return getTagName();
	}
	
	public void addWidget(Widget w) {
		assert (w != this);
		widgets.add(w);
		w.setParentLayout(this);
		addEditableProperties(w);
		positionWidget(w);
		this.readWidthHeight();
		if (getParentLayout() != null) {
			getParentLayout().repositionAllWidgets();
		}
	}
	
	public Vector<Widget> getWidgets() {
		return widgets;
	}

	public void removeWidget(Widget w) {
		widgets.remove(w);
		removeEditableProperties(w);
		repositionAllWidgets();
	}
	
	public void removeAllWidgets() {
		for (Widget w : widgets) {
			removeEditableProperties(w);
		}
		widgets.clear();
	}
	
	public void paint() {
	    /*
		Graphics2D g2d = (Graphics2D)g;
	
		drawBackground(g);
		g2d.translate(getX(), getY());
		
		g.setColor(Color.black);
		if (widgets.size() == 0) {
			g.drawString(getTagName(), 2, 15);
		}	
		g.drawRect(0, 0, getWidth(), getHeight());
		for (Widget w : widgets) {
			if (w.isVisible())
				w.paint(g);
		}
		g2d.translate(-getX(),-getY());
		*/
		/*
		Context2d g2d= (Context2d)g;
		
		drawBackground(g);
		g2d.translate(getX(), getY());
		
		g2d.setFillStyle("#000");
		g2d.setStrokeStyle("#000");
		if (widgets.size() == 0) {
			g2d.fillText(getTagName(), 2, 15);
		}
		g2d.strokeRect(0, 0, getWidth(), getHeight());
		for (Widget w : widgets) {
			if (w.isVisible())
				w.paint(g);
		}
		g2d.translate(-getX(),-getY());
		*/
		for (Widget w: widgets) {
			if (w.isVisible())
				w.paint();
		}
	}
	
	
	@Override
    public int getContentWidth() {
		if (widgets.size() > 0) {
			int maxX = 0;
			for (Widget w : widgets) {
				/*LEFT padding already in X value*/ 
				//w.apply();
				int width_w_pad = w.getWidth()+w.getPadding(RIGHT);
				if (w.getX()+width_w_pad > maxX)
					maxX = w.getX()+width_w_pad;
			}
			return maxX;
		}
		else
			return 100;
	}
	
	@Override
  public int getContentHeight() {
		if (widgets.size() > 0) {
			int maxY = 0;
			for (Widget w : widgets) {
				/*TOP padding already in Y value*/ 
				int height_w_pad = w.getHeight()+w.getPadding(BOTTOM);
				if (w.getY()+height_w_pad > maxY)
					maxY = w.getY()+height_w_pad;
			}
			return maxY;
		}
		else
			return 20;
	}
	

	
	public void setPosition(int x, int y) {
		super.setPosition(x, y);
		for (Widget w: widgets) {
			w.setPosition(w.getX(), w.getY());
		}
	}
	
	
	public abstract void positionWidget(Widget w);
	public abstract void repositionAllWidgets();
	
	public void resizeForRendering() {
		for (Widget w : widgets) {
			if (w instanceof Layout) {
				((Layout)w).resizeForRendering();
			}
		}
	}
	
	public void clearRendering() {
		for (Widget w : widgets) {
			w.apply();
			if (w instanceof Layout) {
				((Layout)w).clearRendering();
			}
		}
	}
	
	public boolean containsWidget(Widget w) {
		for (Widget wt : widgets) {
			if (wt.equals(w)) {
				return true;
			}
			else if (wt instanceof Layout) {
				if (((Layout)wt).containsWidget(w)) {
					return true;
				}
			}
		}
		return false;
	}
}