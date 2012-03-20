package com.droiddev.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.Window;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.DOMException;
import com.google.gwt.xml.client.XMLParser;


import com.droiddev.client.widget.Layout;

public class DroidDev implements EntryPoint {
    private Layout root;
    private VerticalPanel mainPanel = new VerticalPanel();
    private Label text = new Label();
    
    public void onModuleLoad() {
        
        RootPanel.get("preview").add(mainPanel);
        mainPanel.add(text);
        
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, "main.xml");
        try {
            Request response = builder.sendRequest(null, new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    text.setText("Error!");
                }
                
                public void onResponseReceived(Request request, Response response) {
                    //Window.alert("Response received");
                    //Window.alert("Status: " + response.getStatusCode() + "\n" + response.getText());

                    // parse the XML document into a DOM
                    Document messageDom = XMLParser.parse(response.getText());
                    text.setText(messageDom.getDocumentElement().getTagName());
                }
            });
            
        } catch (DOMException e) {
            text.setText("Could not parse XML document.");
        }
        catch (RequestException e) {
            text.setText("Request exception: " + e.getMessage());
        }
    }
}