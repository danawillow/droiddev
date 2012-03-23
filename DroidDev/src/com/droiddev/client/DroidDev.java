package com.droiddev.client;

import java.util.HashMap;
import java.util.Vector;

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
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.DOMException;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.XMLParser;

public class DroidDev implements EntryPoint {
    private Layout root;
    private AbsolutePanel layoutPanel = new AbsolutePanel();
    private Label text = new Label();
    private HorizontalPanel mainPanel = new HorizontalPanel();
    //private TextArea code = new TextArea();
    private CodeMirrorTextArea code = new CodeMirrorTextArea("code");
    private ImageResources imageResources = ImageResources.instance();
    private DroidDevServiceAsync service = GWT.create(DroidDevService.class);
    
    Vector<String> all_props;
    Vector<String> layout_props;
    
    private HashMap<Element, Layout> elToLayout = new HashMap<Element, Layout>();
    private HashMap<Element, Vector<String>> elToProps = new HashMap<Element, Vector<String>>();
    
    public void onModuleLoad() {
        //RootPanel.get("preview").add(layoutPanel);
    	RootPanel.get().add(mainPanel);
    	
    	VerticalPanel vp = new VerticalPanel();
    	code.setCharacterWidth(50);
        code.setVisibleLines(25);
    	vp.add(code);
    	
    	com.google.gwt.user.client.ui.Button saveButton = new com.google.gwt.user.client.ui.Button("Save", new ClickHandler() {
    		public void onClick(ClickEvent event) {
    			service.saveFile("HelloAndroid/res/layout/main.xml", code.getText(), new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(Void result) {
						final DialogBox dialog = new DialogBox();
						dialog.setHTML("Build results");
						dialog.setWidth("500px");
						dialog.setHeight("500px");
						
						final VerticalPanel dialogPanel = new VerticalPanel();
						final HTML contents = new HTML();
						contents.setHTML("Building...");
						contents.setHeight("470px");
						contents.getElement().getStyle().setProperty("overflow", "auto");
						dialogPanel.add(contents);
						dialog.setWidget(dialogPanel);
						
						dialog.center();
						dialog.show();
						service.build(new AsyncCallback<String>() {
							@Override
							public void onFailure(Throwable caught) {
								GWT.log("failure");
							}

							@Override
							public void onSuccess(String result) {
								contents.setHTML(result);
								com.google.gwt.user.client.ui.Button closeButton = new com.google.gwt.user.client.ui.Button("Close", new ClickHandler() {
									public void onClick(ClickEvent event) {
										dialog.hide();
									}
								});
								dialogPanel.add(closeButton);
								dialog.center();
							}
						});
					}
    			});
    		}
    	});
    	vp.add(saveButton);
    	mainPanel.add(vp);
        
        com.google.gwt.user.client.ui.Button previewButton = new com.google.gwt.user.client.ui.Button("Preview", new ClickHandler() {
        	public void onClick(ClickEvent event) {
        		generatePreview(code.getText());
        	}
        });
        mainPanel.add(previewButton);
    	
        layoutPanel.setSize("320px", "480px");
        layoutPanel.addStyleName("previewPane");
    	mainPanel.add(layoutPanel);
        

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
        
        /* Get XML file */
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, "HelloAndroid/res/layout/main.xml");
        try {
            builder.sendRequest(null, new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    text.setText("Error!");
                }
                
                public void onResponseReceived(Request request, Response response) {
                	code.setText(response.getText());
                    generatePreview(response.getText());
                }
            });
            
        } catch (DOMException e) {
            text.setText("Could not parse XML document.");
        }
        catch (RequestException e) {
            text.setText("Request exception: " + e.getMessage());
        }
    }
    
    public void generatePreview(final String text) {
    	if (imageResources.isReady())
    		doGeneratePreview(text);
    	else {
    		ImagesReadyEvent.register(AndroidEditor.EVENT_BUS, new ImagesReadyEvent.Handler() {
    			public void onImagesReady(ImagesReadyEvent event) {
    				//Window.alert("Images ready, beginning to parse");
    				doGeneratePreview(text);
    			}
    		});
    	}
    }
    
    private void doGeneratePreview(String text) {
    	/* parse the XML document into a DOM */
        Document messageDom = XMLParser.parse(text);
        layoutPanel.clear();
        root = null;
        parseLayoutElement(messageDom.getDocumentElement());
		root.apply();
		root.repositionAllWidgets();
		root.resizeForRendering();
		root.paint();
    }
    
    protected boolean isLayout( String name ) {
        return name.endsWith( "Layout" ) || name.equals( "RadioGroup" ) || name.equals( "Ticker" ) || name.equals( "TableRow" ) || name.equals( "ScrollView" );
    }
            
    public void parseLayoutElement(Element el) {
        String qName = el.getTagName();
        
        //text.setText(qName);
        
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
            //layout_props = l_props;
            
            //((AbstractLayout)l).setHTML(qName + " " + l.getWidth() + " " + l.getHeight());
            layoutPanel.add(l.getCanvas(), l.getX(), l.getY());
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
            	layoutPanel.add(w.getCanvas(), w.getX(), w.getY());
            }
        }
        
        for (int i = 0; i < el.getChildNodes().getLength(); i++) {
            if (el.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
                parseLayoutElement((Element)(el.getChildNodes().item(i)));
            }
        }
    }

    protected void addWidget( Widget w, Element el ) {
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
        /*
        if (layout_props.size() == 0 || layoutStack.size() == 0) {
            return;
        }
        for ( String prop : layout_props.peek() ) {
            if ( getValue(atts,  prop ) != null ) {
                w.setPropertyByAttName( prop, getValue(atts,  prop ) );
            }
        }
        Layout layout = layoutStack.peek();
        */
        
        for (String prop: elToProps.get(el.getParentNode())) {
        	if (el.hasAttribute(prop)) {
        		w.setPropertyByAttName(prop, el.getAttribute(prop));
        	}
        }
        
        //Layout layout = root;
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
        
    }
}