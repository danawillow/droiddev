package com.droiddev.client;

import java.util.HashMap;
import java.util.Vector;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.droiddev.client.property.StringProperty;
import com.droiddev.client.util.DisplayMetrics;
import com.droiddev.client.util.ImageResources;
import com.droiddev.client.util.ImagesReadyEvent;
import com.droiddev.client.widget.AbsoluteLayout;
import com.droiddev.client.widget.Button;
import com.droiddev.client.widget.CheckBox;
import com.droiddev.client.widget.EditView;
import com.droiddev.client.widget.FrameLayout;
import com.droiddev.client.widget.ImageView;
import com.droiddev.client.widget.Layout;
import com.droiddev.client.widget.LinearLayout;
import com.droiddev.client.widget.ListView;
import com.droiddev.client.widget.RadioButton;
import com.droiddev.client.widget.RadioGroup;
import com.droiddev.client.widget.RelativeLayout;
import com.droiddev.client.widget.TableLayout;
import com.droiddev.client.widget.TableRow;
import com.droiddev.client.widget.TextView;
import com.droiddev.client.widget.Widget;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.xml.client.DOMException;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.XMLParser;

public class LayoutPreviewer {
    Vector<String> all_props;

    private ImageResources imageResources = ImageResources.instance();
    
    private AbsolutePanel layoutPanel;
    private Layout root;
    
    private HashMap<Element, Layout> elToLayout = new HashMap<Element, Layout>();
    private HashMap<Element, Vector<String>> elToProps = new HashMap<Element, Vector<String>>();

    private PickupDragController dragController;
    private PickupDragController previewDragController;
    
    public LayoutPreviewer(AbsolutePanel layoutPanel) {
    	this.layoutPanel = layoutPanel;
    	
        initProps();
        setUpDragAndDrop();
    }
    
    public void setUpDragAndDrop() {
    	/* Set up dragging into preview panel */
    	dragController = new PickupDragController(RootPanel.get(), false) {
    		@Override
    		protected com.google.gwt.user.client.ui.Widget newDragProxy(DragContext context) {
    			Widget w = LayoutPreviewer.createWidget(((CanvasWidget)(context.draggable)).widget.getTagName());
    			w.apply();
    			w.paint();
    			return w.getCanvasWidget();
    		}
    		
    		@Override
    		public void dragStart() {
    			super.dragStart();
    		}
    	};
    	dragController.setBehaviorDragProxy(true);
    	
    	/* Set up dragging within preview panel */
    	previewDragController = new PickupDragController(layoutPanel, false) {
    		@Override
    		public void dragStart() {
    			super.dragStart();
    			for (com.google.gwt.user.client.ui.Widget w: context.selectedWidgets) {
    				w.addStyleName("selectedWidget");
    			}
    		}

    		@Override
    		public void dragMove() {
    			CanvasWidget cw = (CanvasWidget)(context.draggable);
				Widget w = cw.widget;
    			if (cw.mode == CanvasWidget.NORMAL) {
    				super.dragMove();
    				w.setPosition(context.mouseX - layoutPanel.getAbsoluteLeft(), context.mouseY - layoutPanel.getAbsoluteTop());
    			}
    			else {
    				if (cw.mode == CanvasWidget.S || cw.mode == CanvasWidget.SE)
        				w.setHeight(context.mouseY - cw.getAbsoluteTop());
    				if (cw.mode == CanvasWidget.E || cw.mode == CanvasWidget.SE)
        				w.setWidth(context.mouseX - cw.getAbsoluteLeft());
    			}

				root.positionWidget(w);
				root.apply();
    			root.paint();
    		}

    		@Override
    		public void dragEnd() {
    			for (com.google.gwt.user.client.ui.Widget w: context.selectedWidgets) {
    				w.removeStyleName("selectedWidget");
    			}
    			super.dragEnd();
    		}
    	};
    	
    	/* set up dropping from widget picker onto preview pane */
    	SimpleDropController dropController = new SimpleDropController(layoutPanel) {
    		public void onDrop(DragContext context) {
    			Widget w = createWidget(((CanvasWidget)(context.draggable)).widget.getTagName());
    			
    			w.setPosition(context.mouseX - layoutPanel.getAbsoluteLeft(), context.mouseY - layoutPanel.getAbsoluteTop());

				root.addWidget(w);
            	layoutPanel.add(w.getCanvasWidget(), w.getX(), w.getY());
	    		root.paint();
	    		
	    		previewDragController.makeDraggable(w.getCanvasWidget(), w.getCanvas());
	    		w.getCanvasWidget().addClickHandlers();
	    		AndroidEditor.instance().generateXML();
    		}
    	};
    	dragController.registerDropController(dropController);
    	
    	/* set up dropping from preview pane onto preview pane */
    	SimpleDropController previewDropController = new SimpleDropController(layoutPanel) {
    		public void onDrop(DragContext context) {
    			CanvasWidget cw = (CanvasWidget)(context.draggable);
    			Widget w = cw.widget;
    			
    			if (cw.mode == CanvasWidget.NORMAL)
    				w.setPosition(context.mouseX - layoutPanel.getAbsoluteLeft(), context.mouseY - layoutPanel.getAbsoluteTop());
    			root.positionWidget(w);
            	layoutPanel.add(w.getCanvasWidget(), w.getX(), w.getY());
	    		root.paint();
	    		AndroidEditor.instance().generateXML();
    		}
    	};
    	previewDragController.registerDropController(previewDropController);
    	previewDragController.setBehaviorDragStartSensitivity(1);
    }
    
    public PickupDragController getDragController() {
    	return dragController;
    }
    
    public void initProps() {
    	all_props = new Vector<String>();
        all_props.add( "android:layout_width" );
        all_props.add( "android:layout_height" );
        all_props.add( "android:background" );
        all_props.add( "android:padding" );
        all_props.add( "android:id" );
        all_props.add( "android:visibility" );
        all_props.add( "android:layout_marginTop" );
        all_props.add( "android:layout_marginBottom" );
        all_props.add( "android:layout_marginLeft" );
        all_props.add( "android:layout_marginRight" );
    }
    
    public void generatePreview(final String text) {
    	if (imageResources.isReady())
    		doGeneratePreview(text);
    	else {
    		ImagesReadyEvent.register(AndroidEditor.EVENT_BUS, new ImagesReadyEvent.Handler() {
    			public void onImagesReady(ImagesReadyEvent event) {
    				doGeneratePreview(text);
    			}
    		});
    	}
    }
    
    private void doGeneratePreview(String text) {
    	try {
    		/* parse the XML document into a DOM */
    		Document messageDom = XMLParser.parse(text);
    		layoutPanel.clear();
    		root = null;
    		parseLayoutElement(messageDom.getDocumentElement());
    		
    		if (root.getPropertyByAttName("xmlns:android") == null) {
    			root.addProperty(new StringProperty("xmlns", "xmlns:android", "http://schemas.android.com/apk/res/android", false));
    		}
    		root.apply();
    		root.repositionAllWidgets();
    		root.resizeForRendering();
    		root.paint();
    	} catch (DOMException e) {
    		GWT.log("Could not parse XML");
    	}
    	AndroidEditor.instance().layout = root;
    }
    
    protected boolean isLayout( String name ) {
        return name.endsWith( "Layout" ) || name.equals( "RadioGroup" ) || name.equals( "Ticker" ) || name.equals( "TableRow" ) || name.equals( "ScrollView" );
    }
            
    public void parseLayoutElement(Element el) {
        String qName = el.getTagName();
        
        if (isLayout(qName)) {
            Layout l = null;
            Vector<String> l_props = new Vector<String>();
			if ( qName.equals( "AbsoluteLayout" ) )
				l = new AbsoluteLayout();
			else if ( qName.equals( "LinearLayout" ) || ( qName.equals( "RadioGroup" ) ) ) {
				if ( qName.equals( "LinearLayout" ) ) {
					l = new LinearLayout();
				}
				else if ( qName.equals( "RadioGroup" ) ) {
					l = new RadioGroup();
					l.setPropertyByAttName( "android:checkedButton", el.getAttribute("android:checkedButton" ) );
				}
                l.setPropertyByAttName("android:gravity", el.getAttribute("android:gravity"));
                
                if (!el.hasAttribute("android:orientation")) {
                    l.setPropertyByAttName("android:orientation", "horizontal" );
                }
                else {
                    l.setPropertyByAttName("android:orientation", el.getAttribute("android:orientation"));
                }
                
                if (el.hasAttribute("android:weightSum")) {
                    l.setPropertyByAttName("android:weightSum", el.getAttribute("android:weightSum"));
                }
                
                l_props.add( "android:layout_gravity" );
                l_props.add( "android:layout_weight" );
            }
            else if ( qName.equals( "RelativeLayout" ) ) {
				l = new RelativeLayout();
				for ( int i = 0; i < RelativeLayout.propNames.length; i++ ) {
					l_props.add( RelativeLayout.propNames[ i ] );
				}
			}
			else if ( qName.equals( "FrameLayout" ) ) {
				l = new FrameLayout();
			}
			else if ( qName.equals( "TableLayout" ) ) {
				l = new TableLayout();
				l.setPropertyByAttName( "android:stretchColumns", el.getAttribute( "android:stretchColumns" ) );
			}
			else if ( qName.equals( "TableRow" ) ) {
				l = new TableRow();
				l_props.add( "android:layout_column" );
				l_props.add( "android:layout_span" );
			}
            
            if (root == null) {
                l.setPosition( AndroidEditor.OFFSET_X, AndroidEditor.OFFSET_Y );
                for ( String prop : all_props ) {
                    if ( el.hasAttribute(prop)) {
                        l.setPropertyByAttName( prop, el.getAttribute(prop ) );
                    }
                }
                l.apply();
                root = l;
            }
            else {
            	addWidget(l, el);
            }
            elToLayout.put(el, l);
            elToProps.put(el, l_props);
            
            layoutPanel.add(l.getCanvasWidget(), l.getX(), l.getY());
        }
        else {
            Widget w = null;
            if ( qName.equals( "Button" ) ) {
				String txt = el.getAttribute("android:text" );
				Button b = new Button( txt );
				w = b;
			}
            else if (qName.equals("TextView")) {
                String txt = el.getAttribute("android:text");
                w = new TextView( txt );
                if (el.hasAttribute("android:textAlign")) {
                    w.setPropertyByAttName( "android:textAlign", el.getAttribute("android:textAlign"));
                }
            }
            else if ( qName.equals( "EditText" ) ) {
				String txt = el.getAttribute("android:text" );
				EditView et = new EditView( txt );
				String hint = el.getAttribute("android:hint");
				if (hint != null) {
					et.setPropertyByAttName("android:hint", hint);
				}
				for ( int i = 0; i < EditView.propertyNames.length; i++ ) {
					et.setPropertyByAttName( EditView.propertyNames[ i ], el.getAttribute(EditView.propertyNames[ i ] ) );
				}
				w = et;
			}
            else if (qName.equals( "CheckBox" ) || qName.equals( "RadioButton" ) ) {
				String txt = el.getAttribute("android:text" );
				if ( qName.equals( "CheckBox" ) )
					w = new CheckBox( txt );
				else if ( qName.equals( "RadioButton" ) )
					w = new RadioButton( txt );
				w.setPropertyByAttName( "android:checked", el.getAttribute("android:checked" ) );
			}
			else if ( qName.equals( "ListView" ) ) {
				w = new ListView();
			}
			else if ( qName.equals( "ImageView" ) ) {
				w = new ImageView();
				w.setPropertyByAttName( "android:src", el.getAttribute( "android:src" ) );
			}
            if (w != null) {
            	addWidget(w, el);
            	layoutPanel.add(w.getCanvasWidget(), w.getX(), w.getY());
            }
        }
        
        for (int i = 0; i < el.getChildNodes().getLength(); i++) {
            if (el.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
                parseLayoutElement((Element)(el.getChildNodes().item(i)));
            }
        }
    }

    protected void addWidget( final Widget w, Element el ) {
        if ( w instanceof TextView ) {
            for ( int i = 0; i < TextView.propertyNames.length; i++ ) {
                w.setPropertyByAttName( TextView.propertyNames[ i ], el.getAttribute(TextView.propertyNames[ i ] ) );
            }
        }

        for ( String prop : all_props ) {
            if (el.hasAttribute(prop)) {
                w.setPropertyByAttName( prop, el.getAttribute(prop));
            }
        }
        
        // TODO come back here in the droiddraw code and take a look at layout_props
        
        for (String prop: elToProps.get(el.getParentNode())) {
        	if (el.hasAttribute(prop)) {
        		w.setPropertyByAttName(prop, el.getAttribute(prop));
        	}
        }
        
        Layout layout = elToLayout.get(el.getParentNode());
        w.apply();
        if ( layout instanceof LinearLayout ) {
            w.setPosition( layout.getWidth(), layout.getHeight() );
        }
        layout.addWidget( w );
        
        if ( layout instanceof AbsoluteLayout ) {
            int x = DisplayMetrics.readSize( el.getAttribute("android:layout_x" ) );
            int y = DisplayMetrics.readSize( el.getAttribute("android:layout_y" ) );
            w.setPropertyByAttName( "android:layout_x", el.getAttribute( "android:layout_x" ) );
            w.setPropertyByAttName( "android:layout_y", el.getAttribute( "android:layout_y" ) );
            w.setPosition( x, y );
        }
        
        previewDragController.makeDraggable(w.getCanvasWidget(), w.getCanvas());
        w.getCanvasWidget().addClickHandlers();
    }
    
    public static Widget createWidget(String str) {
		/*if (str.equals(ToggleButton.TAG_NAME))
			return new ToggleButton("Toggle On", "Toggle Off");
		else */if (str.equals(Button.TAG_NAME))
			return new Button(Button.TAG_NAME);
		else if (str.equals(CheckBox.TAG_NAME))
			return new CheckBox(CheckBox.TAG_NAME);
		else if (str.equals(EditView.TAG_NAME))
			return new EditView(EditView.TAG_NAME);
		else if (str.equals(TextView.TAG_NAME))
			return new TextView(TextView.TAG_NAME);
		/*else if (str.equals(AnalogClock.TAG_NAME))
			return new AnalogClock();
		else if (str.equals(DigitalClock.TAG_NAME))
			return new DigitalClock();
		else if (str.equals(ProgressBar.TAG_NAME))
			return new ProgressBar();*/
		else if (str.equals(LinearLayout.TAG_NAME))
			return new LinearLayout();
		else if (str.equals(AbsoluteLayout.TAG_NAME))
			return new AbsoluteLayout();
		else if (str.equals(RelativeLayout.TAG_NAME))
			return new RelativeLayout();
		else if (str.equals(RadioButton.TAG_NAME))
			return new RadioButton(RadioButton.TAG_NAME);
		else if (str.equals(RadioGroup.TAG_NAME))
			return new RadioGroup();
		/*else if (str.equals(TimePicker.TAG_NAME))
			return new TimePicker();*/
		else if (str.equals(ListView.TAG_NAME))
			return new ListView();
		/*else if (str.equals(Ticker.TAG_NAME))
			return new Ticker();
		else if (str.equals(Spinner.TAG_NAME))
			return new Spinner();*/
		else if (str.equals(ImageView.TAG_NAME))
			return new ImageView();
		/*else if (str.equals(ImageButton.TAG_NAME))
			return new ImageButton();
		else if (str.equals(AutoCompleteTextView.TAG_NAME))
			return new AutoCompleteTextView("AutoComplete");*/
		else if (str.equals(TableRow.TAG_NAME))
			return new TableRow();
		else if (str.equals(TableLayout.TAG_NAME))
			return new TableLayout();
		else if (str.equals(FrameLayout.TAG_NAME))
			return new FrameLayout();
		/*else if (str.equals(ScrollView.TAG_NAME))
			return new ScrollView();
		else if (str.equals(GridView.TAG_NAME))
			return new GridView();
		else if (str.equals(Gallery.TAG_NAME))
			return new Gallery();
		else if (str.equals(DatePicker.TAG_NAME))
			return new DatePicker();
		else if (str.equals(ImageSwitcher.TAG_NAME))
			return new ImageSwitcher();
		else if (str.equals(TabHost.TAG_NAME))
			return new TabHost();
		else if (str.equals(TabWidget.TAG_NAME))
			return new TabWidget();
		else if (str.equals(MapView.TAG_NAME))
			return new MapView();
		else if (str.equals(RatingBar.TAG_NAME)) {
			return new RatingBar();
		}*/
		else
			return null;
	}
}
