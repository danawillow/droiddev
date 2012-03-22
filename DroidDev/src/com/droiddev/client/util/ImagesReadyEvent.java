package com.droiddev.client.util;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class ImagesReadyEvent extends Event<ImagesReadyEvent.Handler>{
	public interface Handler {
		void onImagesReady(ImagesReadyEvent event);
	}

	private static final Type<ImagesReadyEvent.Handler> TYPE =
			new Type<ImagesReadyEvent.Handler>();

	public static HandlerRegistration register(EventBus eventBus,
			ImagesReadyEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	} 


	public ImagesReadyEvent() {
	}

	@Override
	public Type<ImagesReadyEvent.Handler> getAssociatedType() {
		return TYPE;
	}


	@Override
	protected void dispatch(Handler handler) {
		handler.onImagesReady(this);
	}
}