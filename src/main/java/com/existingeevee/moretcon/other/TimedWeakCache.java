package com.existingeevee.moretcon.other;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

public class TimedWeakCache<K, V> {

	private static class Entry<V> {
		final V value;
		final long time;

		Entry(V value) {
			this.value = value;
			this.time = System.currentTimeMillis();
		}
	}

	private final WeakHashMap<K, Entry<V>> cache = new WeakHashMap<K, Entry<V>>();

	private final int maxEntries;
	private final long maxAgeMs;

	public TimedWeakCache(int maxEntries, long maxAgeMs) {
		this.maxEntries = maxEntries;
		this.maxAgeMs = maxAgeMs;
	}

	public V get(K key) {
		trim();

		Entry<V> entry = cache.get(key);
		if (entry == null) {
			return null;
		}

		if (System.currentTimeMillis() - entry.time > maxAgeMs) {
			cache.remove(key);
			return null;
		}

		return entry.value;
	}

	public void put(K key, V value) {
		cache.put(key, new Entry<V>(value));
		trim();
	}

	public boolean contains(K key) {
		return get(key) != null;
	}

	public void clear() {
		cache.clear();
	}

	private void trim() {
		long now = System.currentTimeMillis();

		Iterator<Map.Entry<K, Entry<V>>> it = cache.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<K, Entry<V>> e = it.next();
			if (now - e.getValue().time > maxAgeMs) {
				it.remove();
			}
		}

		while (cache.size() > maxEntries) {
			K oldestKey = null;
			long oldestTime = Long.MAX_VALUE;

			for (Map.Entry<K, Entry<V>> e : cache.entrySet()) {
				if (e.getValue().time < oldestTime) {
					oldestTime = e.getValue().time;
					oldestKey = e.getKey();
				}
			}

			if (oldestKey == null) {
				return;
			}

			cache.remove(oldestKey);
		}
	}
}