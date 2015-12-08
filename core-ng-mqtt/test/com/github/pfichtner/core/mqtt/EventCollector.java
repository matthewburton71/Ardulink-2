package com.github.pfichtner.core.mqtt;

import static com.github.pfichtner.ardulink.core.Pin.Type.ANALOG;
import static com.github.pfichtner.ardulink.core.Pin.Type.DIGITAL;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.zu.ardulink.util.ListMultiMap;

import com.github.pfichtner.ardulink.core.Pin.Type;
import com.github.pfichtner.ardulink.core.events.AnalogPinValueChangedEvent;
import com.github.pfichtner.ardulink.core.events.DigitalPinValueChangedEvent;
import com.github.pfichtner.ardulink.core.events.EventListener;
import com.github.pfichtner.ardulink.core.events.PinValueChangedEvent;

public class EventCollector implements EventListener {

	private final ListMultiMap<Type, PinValueChangedEvent> events = new ListMultiMap<Type, PinValueChangedEvent>();

	@Override
	public void stateChanged(AnalogPinValueChangedEvent event) {
		events.put(ANALOG, event);
	}

	@Override
	public void stateChanged(DigitalPinValueChangedEvent event) {
		events.put(DIGITAL, event);
	}

	public List<PinValueChangedEvent> events(Type type) {
		try {
			TimeUnit.MILLISECONDS.sleep(25);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		List<PinValueChangedEvent> list = events.asMap().get(type);
		return list == null ? Collections.<PinValueChangedEvent> emptyList()
				: list;
	}

}