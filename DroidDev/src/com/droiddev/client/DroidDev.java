package com.droiddev.client;

import java.util.HashMap;

import com.droiddev.client.file.File;
import com.droiddev.client.file.Folder;
import com.droiddev.client.file.JavaFile;
import com.droiddev.client.file.OtherFile;
import com.droiddev.client.file.XMLFile;
import com.droiddev.client.util.ImageResources;
import com.droiddev.client.util.ImagesReadyEvent;
import com.droiddev.client.widget.Layout;
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
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

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
				// TODO fix exception if emulator not yet initialized
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

    	splitPanel.addWest(fileTree, 300);
    	splitPanel.addEast(mainPanel, 595);

    	AndroidEditor.instance().layoutPreviewer = new LayoutPreviewer(layoutPanel);

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
                		AndroidEditor.instance().lastXMLFile = (XMLFile)file;
                    	code.setText(response.getText());
                    	AndroidEditor.instance().layoutPreviewer.generatePreview(response.getText());
                	}
                	else if (file.getType() == File.JAVA) {
                		AndroidEditor.instance().lastJavaFile = (JavaFile)file;
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
    	
    	com.google.gwt.user.client.ui.Button previewButton = new com.google.gwt.user.client.ui.Button("Preview Layout", new ClickHandler() {
        	public void onClick(ClickEvent event) {
        		AndroidEditor.instance().layoutPreviewer.generatePreview(code.getText());
        	}
        });
    	
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
    	previewButton.setWidth("100%");
    	previewButton.setStylePrimaryName("previewButton");
    	saveButton.setWidth("100%");
    	saveButton.setStylePrimaryName("buildButton");
    	VerticalPanel buttonPanel = new VerticalPanel();
    	buttonPanel.setWidth("100%");
    	buttonPanel.add(previewButton);
    	buttonPanel.add(saveButton);
    	splitPanel.addSouth(buttonPanel, 80);
    	splitPanel.add(code);
    }
    
    public void addPreviewButtonAndPane() {
    	TabPanel tabs = new TabPanel();
        
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
    		Widget w = LayoutPreviewer.createWidget(s);
    		w.apply();
    		w.paint();
    		widgetPanel.add(w.getCanvasWidget());
    		AndroidEditor.instance().layoutPreviewer.getDragController().makeDraggable(w.getCanvasWidget(), w.getCanvas());
    	}
    }

}