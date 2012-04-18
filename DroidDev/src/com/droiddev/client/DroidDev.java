package com.droiddev.client;

import java.util.HashMap;
import java.util.Vector;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.droiddev.client.file.File;
import com.droiddev.client.file.Folder;
import com.droiddev.client.file.JavaFile;
import com.droiddev.client.file.OtherFile;
import com.droiddev.client.file.XMLFile;
import com.droiddev.client.property.Property;
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
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.DOMException;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.Text;
import com.google.gwt.xml.client.XMLParser;

public class DroidDev implements EntryPoint {
    private SplitLayoutPanel splitPanel = new SplitLayoutPanel();
    private HorizontalPanel mainPanel = new HorizontalPanel();
    private FlowPanel widgetPanel = new FlowPanel();
    private AbsolutePanel layoutPanel = new AbsolutePanel();
    
    private CodeMirrorTextArea code = new CodeMirrorTextArea("code");
	private Tree fileTree = new Tree();
    
    private ImageResources imageResources = ImageResources.instance();
    private DroidDevServiceAsync service = GWT.create(DroidDevService.class);
    private Layout root;
    
    private PickupDragController dragController;
    private PickupDragController previewDragController;
    
    Vector<String> all_props;
    Vector<String> layout_props;
    
    private HashMap<Element, Layout> elToLayout = new HashMap<Element, Layout>();
    private HashMap<Element, Vector<String>> elToProps = new HashMap<Element, Vector<String>>();
    
    Frame emulator;
   
    
    /**
     * Initialize everything
     */
    public void onModuleLoad() {
    	service.startVMAndADB(new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("failure!");
			}

			@Override
			public void onSuccess(Void result) {
				emulator.setUrl("http://localhost:6080/vnc_auto.html?host=localhost&port=6080");
			}
    	});
    	
    	Window.addWindowClosingHandler(new Window.ClosingHandler() {
			@Override
			public void onWindowClosing(ClosingEvent event) {
				service.closeVMAndADB(new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) { }

					@Override
					public void onSuccess(Void result) { }
				});
			}
		});
    	
    	RootLayoutPanel.get().add(splitPanel);
    	dragController = new PickupDragController(RootPanel.get(), false) {
    		@Override
    		protected com.google.gwt.user.client.ui.Widget newDragProxy(DragContext context) {
    			Widget w = createWidget(((CanvasWidget)(context.draggable)).widget.getTagName());
    			w.apply();
    			w.paint();
    			return w.getCanvasWidget();
    		}
    	};
    	dragController.setBehaviorDragProxy(true);

    	splitPanel.addWest(fileTree, 300);
    	splitPanel.addEast(mainPanel, 650);

        initProps();

    	addCodePanelAndBuildButton();
        
        addPreviewButtonAndPane();

    	listFiles(true);
    }
    
    public void listFiles(final boolean andImport) {
    	RequestBuilder listFilesRB = new RequestBuilder(RequestBuilder.GET, URL.encode(GWT.getModuleBaseURL() + "listFiles"));
    	try {
    		listFilesRB.sendRequest(null, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request,
						Response response) {
					if (response.getStatusCode() == 200) {
						JsArray<DirInfo> dirInfo = asArrayOfDirInfo(response.getText());
						for (int i = 0; i < dirInfo.length(); i++) {
							TreeItem item = dirInfoToTreeItem(dirInfo.get(i), null);
							fileTree.addItem(item);
							item.setState(true);
						}
						
						fileTree.addSelectionHandler(new SelectionHandler<TreeItem>() {
							@Override
							public void onSelection(
									SelectionEvent<TreeItem> event) {
								
							}
						});
						
						if (andImport) {
							for (File file: AndroidEditor.instance().files) {
								if (file.getType() == File.JAVA || file.getType() == File.XML)
									importFile(file);
							}
						}
					}
					else {
						GWT.log("Couldn't retrieve JSON: " + response.getStatusText());
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					GWT.log("Couldn't retrieve JSON");
				}
    		});
    	} catch (RequestException e) {
    		GWT.log("Couldn't retrieve JSON");
    	}
    }
    
    /**
     * Convert the string of JSON into JavaScript object.
     */
    private final native JsArray<DirInfo> asArrayOfDirInfo(String json) /*-{
      return eval(json);
    }-*/;
    
    public TreeItem dirInfoToTreeItem(DirInfo d, DirInfo parent) {
		File file;
		String name = d.getName();
		String path = parent.getName() + "/" + d.getName();
		
		/* Create appropriate file type */
    	if (d.getName().contains("/"))
    		file = new Folder(name, path);
    	else if (d.getName().endsWith("xml"))
    		file = new XMLFile(name, path);
    	else if (d.getName().endsWith("java"))
    		file = new JavaFile(name, path);
    	else
    		file = new OtherFile(name, path);
    	
    	AndroidEditor.instance().files.add(file);

    	/* Create tree item based on file */
    	FileChoice c = new FileChoice(file);
    	TreeItem item = new TreeItem(c);
		item.setState(true);
    	
    	JsArray<DirInfo> children = d.getChildren();
    	
    	for (int i = 0; i < children.length(); i++) {
    		TreeItem child = dirInfoToTreeItem(children.get(i), d);
    		item.addItem(child);
    		child.setState(true);
    	}
    	
    	return item;
    }
    
    private class FileChoice extends Composite {
    	private HorizontalPanel panel = new HorizontalPanel();
    	private Label fileName;
    	private Anchor delete = new Anchor("[X]");
    	private File file;
    	
    	public FileChoice(File file) {
    		this.file = file;
    		fileName = new Label(file.getFileName());
    		
    		panel.add(fileName);
    		panel.add(delete);
    		
    		delete.addClickHandler(new ClickHandler() {
        		@Override
        		public void onClick(ClickEvent event) {
        			final DialogBox dialog = new DialogBox();
        			dialog.setText("Are you sure you wish to delete this file?");
        			HorizontalPanel buttons = new HorizontalPanel();
        			com.google.gwt.user.client.ui.Button closeButton = new com.google.gwt.user.client.ui.Button("Cancel", new ClickHandler() {
						public void onClick(ClickEvent event) {
							dialog.hide();
						}
					});
        			com.google.gwt.user.client.ui.Button okButton = new com.google.gwt.user.client.ui.Button("OK", new ClickHandler() {
						public void onClick(ClickEvent event) {
		        			String toDelete = FileChoice.this.file.getPath();
		        			if (toDelete == null) toDelete = fileName.getText();
		        			if (toDelete == null) {
		        				GWT.log("No entry for " + fileName.getText());
		        				return;
		        			}
		        			service.deleteFile(toDelete, new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									// TODO Auto-generated method stub
								}

								@Override
								public void onSuccess(Void result) {
									fileTree.clear();
									listFiles(false);
								}
							});
							dialog.hide();
						}
					});
					buttons.add(closeButton);
					buttons.add(okButton);
					dialog.setWidget(buttons);
					dialog.center();
        		}
        	});
    		
    		fileName.addClickHandler(new ClickHandler() {
    			@Override
    			public void onClick(ClickEvent event) {
    				AndroidEditor.instance().switchToFile(FileChoice.this.file);
    			}
    		});
    		
    		initWidget(panel);
    	}
    }
    
    public void importFile(final File file) {
    	RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, file.getPath());
        try {
            builder.sendRequest(null, new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    System.out.println(exception.getMessage());
                }
                
                public void onResponseReceived(Request request, Response response) {
                	/* If a code file, put its content in the file object */
                	if (file.getType() == File.JAVA)
                		((JavaFile)file).setContent(response.getText());
                	else if (file.getType() == File.XML)
                		((XMLFile)file).setContent(response.getText());
                	
                	/* main.xml should be the first thing seen */
                	if (file.getFileName().equals("main.xml")) {
                		AndroidEditor.instance().currFile = file;
                		AndroidEditor.instance().lastXMLFile = file.getPath();
                    	code.setText(response.getText());
                        generatePreview(response.getText());
                	}
                	else if (file.getType() == File.JAVA) {
                		AndroidEditor.instance().lastJavaFile = file.getPath();
                		/*
                		service.getJavaFields(response.getText(), new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) { }

							@Override
							public void onSuccess(Void result) { }
                		});
                		*/
                	}
                }
            });
            
        }
        catch (RequestException e) {
            System.out.println("Request exception: " + e.getMessage());
        }
    }
    
    public void addCodePanelAndBuildButton() {
    	code.setCharacterWidth(50);
        code.setVisibleLines(25);
    	
    	AndroidEditor.instance().code = code;
    	
    	com.google.gwt.user.client.ui.Button saveButton = new com.google.gwt.user.client.ui.Button("Save and Build", new ClickHandler() {
    		public void onClick(ClickEvent event) {
    			//generateXML();
    			// TODO figure out generating XML on save without overwriting user changes
    			
    			/* save the state of the current editor's contents */
    			File currFile = AndroidEditor.instance().currFile;
    			if (currFile.getType() == File.JAVA)
    				((JavaFile)currFile).setContent(code.getText());
    			else if (currFile.getType() == File.XML)
    				((XMLFile)currFile).setContent(code.getText());
    			
    			/* create map of names to content */
    			HashMap<String, String> fileContents = new HashMap<String, String>();
    			for (File f: AndroidEditor.instance().files) {
        			if (f.getType() == File.JAVA)
        				fileContents.put(f.getPath(), ((JavaFile)f).getContent());
        			else if (f.getType() == File.XML)
        				fileContents.put(f.getPath(), ((XMLFile)f).getContent());
    			}
    			
    			service.saveFile(fileContents, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(Void result) {
						final DialogBox dialog = new DialogBox(true);
						dialog.setHTML("Build results");
						
						final VerticalPanel dialogPanel = new VerticalPanel();
						final HTML contents = new HTML();
						contents.setHTML("Building...");
						dialogPanel.add(contents);
						ScrollPanel sp = new ScrollPanel(dialogPanel);
						sp.setSize("500px", "500px");
						dialog.setWidget(sp);
						
						dialog.center();
						dialog.show();
						service.build(new AsyncCallback<String>() {
							@Override
							public void onFailure(Throwable caught) {
								GWT.log("failure");
								dialog.hide();
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
    	saveButton.setWidth("100%");
    	saveButton.setStylePrimaryName("buildButton");
    	splitPanel.addSouth(saveButton, 40);
    	splitPanel.add(code);
    }
    
    public void addPreviewButtonAndPane() {
    	TabPanel tabs = new TabPanel();
    	
    	VerticalPanel buttonPanel = new VerticalPanel();
    	
    	com.google.gwt.user.client.ui.Button previewButton = new com.google.gwt.user.client.ui.Button("Preview Layout -->", new ClickHandler() {
        	public void onClick(ClickEvent event) {
        		generatePreview(code.getText());
        	}
        });
    	com.google.gwt.user.client.ui.Button xmlButton = new com.google.gwt.user.client.ui.Button("<-- Generate XML", new ClickHandler() {
        	public void onClick(ClickEvent event) {
        		generateXML();
        	}
        });
        
    	buttonPanel.add(previewButton);
    	buttonPanel.add(xmlButton);
    	mainPanel.add(buttonPanel);
        
        widgetPanel.setSize("200px", "480px");
        if (imageResources.isReady())
            addWidgetsToPanel();
    	else {
    		ImagesReadyEvent.register(AndroidEditor.EVENT_BUS, new ImagesReadyEvent.Handler() {
    			public void onImagesReady(ImagesReadyEvent event) {
    		        addWidgetsToPanel();
    			}
    		});
    	}
        widgetPanel.addStyleName("widgetPanel");
        HorizontalPanel widgetsAndPreview = new HorizontalPanel();
        widgetsAndPreview.add(widgetPanel);
    	
        layoutPanel.setSize("320px", "480px");
        layoutPanel.addStyleName("previewPane");
        widgetsAndPreview.add(layoutPanel);
        tabs.add(widgetsAndPreview, "Layout Preview");
        tabs.selectTab(0);
        mainPanel.add(tabs);
    	AndroidEditor.instance().layoutPanel = layoutPanel;
    	
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
    		}
    	};
    	previewDragController.registerDropController(previewDropController);
    	previewDragController.setBehaviorDragStartSensitivity(1);
    	
    	/* Disable regular right click on layout panel */
    	layoutPanel.addDomHandler(new ContextMenuHandler() {
    		@Override
    		public void onContextMenu(ContextMenuEvent event) {
    			event.preventDefault();
    			event.stopPropagation();
    		}
    	}, ContextMenuEvent.getType());
    	
    	/* set up emulator panel */
    	HorizontalPanel emulatorPanel = new HorizontalPanel();
    	VerticalPanel emulatorButtons = new VerticalPanel();
    	com.google.gwt.user.client.ui.Button homeButton = new com.google.gwt.user.client.ui.Button("Home", new ClickHandler() {
        	public void onClick(ClickEvent event) {
        		service.pressKey(DroidDevService.HOME_CODE, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(Void v) {
					}
				});
			}
        });
    	com.google.gwt.user.client.ui.Button menuButton = new com.google.gwt.user.client.ui.Button("Menu", new ClickHandler() {
        	public void onClick(ClickEvent event) {
        		service.pressKey(DroidDevService.MENU_CODE, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(Void v) {
					}
				});
			}
        });
    	com.google.gwt.user.client.ui.Button backButton = new com.google.gwt.user.client.ui.Button("Back", new ClickHandler() {
        	public void onClick(ClickEvent event) {
        		service.pressKey(DroidDevService.BACK_CODE, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(Void v) {
					}
				});
			}
        });
    	emulatorButtons.add(homeButton);
    	emulatorButtons.add(menuButton);
    	emulatorButtons.add(backButton);
    	emulatorPanel.add(emulatorButtons);
    	
    	//Frame emulator = new Frame("http://localhost:6080/vnc_auto.html?host=localhost&port=6080");
    	emulator = new Frame("about:blank");
    	emulator.setWidth(widgetsAndPreview.getOffsetWidth() + "px");
    	emulator.setHeight("720px");
    	emulatorPanel.add(emulator);
    	tabs.add(emulatorPanel, "Emulator");
    }

    public void addWidgetsToPanel() {
    	String[] widgets = {"Button", "TextView", "EditText", "RadioButton", "RadioGroup", "CheckBox", "ImageView"};

    	for (String s: widgets) {
    		Widget w = createWidget(s);
    		w.apply();
    		w.paint();
    		widgetPanel.add(w.getCanvasWidget());
    		dragController.makeDraggable(w.getCanvasWidget(), w.getCanvas());
    	}
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

    public Widget createWidget(String str) {
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

    public void generateXML() {
    	String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
    	xml += generateWidget(root);
    	
    	((XMLFile)AndroidEditor.instance().getFileByName("main.xml")).setContent(xml);
    }
    
    @SuppressWarnings("unchecked")
	public String generateWidget(Widget w) {
    	String xml = "";
		xml += "<"+w.getTagName();
		Vector<Property> props = (Vector<Property>)w.getProperties().clone();
		if (w != root)
			w.getParentLayout().addOutputProperties(w, props);
		for (Property prop : props) {
			if (prop.getValue() != null && prop.getValue().toString().length() > 0 && !prop.isDefault()) {
				// Work around an android bug... *sigh*
				if (w instanceof CheckBox && prop.getAttributeName().equals("android:padding"))
					continue;
				String value;
				if (prop instanceof StringProperty) {
					Document d = XMLParser.createDocument();
					Text textNode= d.createTextNode(((StringProperty)prop).getRawStringValue());
					value = textNode.toString();
				} else {
					value = prop.getValue().toString();
				}
				xml += "\n";
				xml += "\t" + prop.getAttributeName()+"=\""+ value +"\"";
			}
		}
		if (w instanceof Layout) {
			xml += ">\n";
			for (Widget wt : ((Layout)w).getWidgets()) {
				xml += generateWidget(wt);
			}
			xml += "</"+w.getTagName()+">\n";
		} else {
			xml += " />\n";
		}
		return xml;
	}

}