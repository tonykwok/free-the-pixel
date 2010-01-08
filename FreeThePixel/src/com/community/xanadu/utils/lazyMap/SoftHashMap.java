/*
 * Copyright (c) 2005-2009 Substance Kirill Grouchnikov. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer. 
 *     
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *     
 *  o Neither the name of Substance Kirill Grouchnikov nor the names of 
 *    its contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */
package com.community.xanadu.utils.lazyMap;

import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * This implementation is taken from <a
 * href="http://www.javaspecialists.co.za/archive/Issue098.html">The Java
 * Specialists' Newsletter [Issue 098]</a> with permission of the original
 * author.
 * 
 * @author Dr. Heinz M. Kabutz
 */
class SoftHashMap<K, V> extends AbstractMap<K, V> implements Serializable {
	private static final long serialVersionUID = 1L;

	/** The internal HashMap that will hold the SoftReference. */
	private final Map<K, SoftReference<V>> hash = new HashMap<K, SoftReference<V>>();

	private final Map<SoftReference<V>, K> reverseLookup = new HashMap<SoftReference<V>, K>();

	/** Reference queue for cleared SoftReference objects. */
	private final ReferenceQueue<V> queue = new ReferenceQueue<V>();

	@Override
	public V get(final Object key) {
		expungeStaleEntries();
		V result = null;
		// We get the SoftReference represented by that key
		SoftReference<V> soft_ref = this.hash.get(key);
		if (soft_ref != null) {
			// From the SoftReference we get the value, which can be
			// null if it has been garbage collected
			result = soft_ref.get();
			if (result == null) {
				// If the value has been garbage collected, remove the
				// entry from the HashMap.
				this.hash.remove(key);
				this.reverseLookup.remove(soft_ref);
			}
		}
		return result;
	}

	private void expungeStaleEntries() {
		Reference<? extends V> sv;
		while ((sv = this.queue.poll()) != null) {
			this.hash.remove(this.reverseLookup.remove(sv.get()));
		}
	}

	@Override
	public V put(final K key, final V value) {
		expungeStaleEntries();
		SoftReference<V> soft_ref = new SoftReference<V>(value, this.queue);
		this.reverseLookup.put(soft_ref, key);
		SoftReference<V> result = this.hash.put(key, soft_ref);
		if (result == null)
			return null;
		return result.get();
	}

	@Override
	public V remove(final Object key) {
		expungeStaleEntries();
		SoftReference<V> result = this.hash.remove(key);
		if (result == null)
			return null;
		return result.get();
	}

	@Override
	public void clear() {
		this.hash.clear();
		this.reverseLookup.clear();
	}

	@Override
	public int size() {
		expungeStaleEntries();
		return this.hash.size();
	}

	/**
	 * Returns a copy of the key/values in the map at the point of calling.
	 * However, setValue still sets the value in the actual SoftHashMap.
	 */
	@Override
	public Set<Entry<K, V>> entrySet() {
		expungeStaleEntries();
		Set<Entry<K, V>> result = new LinkedHashSet<Entry<K, V>>();
		for (final Entry<K, SoftReference<V>> entry : this.hash.entrySet()) {
			final V value = entry.getValue().get();
			if (value != null) {
				result.add(new Entry<K, V>() {
					public K getKey() {
						return entry.getKey();
					}

					public V getValue() {
						return value;
					}

					public V setValue(final V v) {
						entry.setValue(new SoftReference<V>(v, SoftHashMap.this.queue));
						return value;
					}
				});
			}
		}
		return result;
	}
}
