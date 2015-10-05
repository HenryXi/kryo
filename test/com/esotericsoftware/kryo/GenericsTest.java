
package com.esotericsoftware.kryo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.io.Serializable;

import org.objenesis.strategy.StdInstantiatorStrategy;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CollectionSerializer.BindCollection;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers.IntArraySerializer;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers.LongArraySerializer;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers.ObjectArraySerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.StringSerializer;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.esotericsoftware.kryo.serializers.FieldSerializer.Bind;
import com.esotericsoftware.kryo.serializers.FieldSerializer.Optional;
import com.esotericsoftware.kryo.serializers.MapSerializer;
import com.esotericsoftware.kryo.serializers.CollectionSerializer;
import com.esotericsoftware.kryo.serializers.MapSerializer.BindMap;

public class GenericsTest extends KryoTestCase {
	{
		supportsCopy = true;
	}

	public void testGenericClassWithGenericFields () throws Exception {
		kryo.setReferences(true);
		kryo.setRegistrationRequired(false);
		kryo.setAsmEnabled(true);
		kryo.register(BaseGeneric.class);

		List list = Arrays.asList(
			new SerializableObjectFoo("one"), 
			new SerializableObjectFoo("two"),
			new SerializableObjectFoo("three"));
		BaseGeneric<SerializableObjectFoo> bg1 = new BaseGeneric<SerializableObjectFoo>(list);

		roundTrip(108, 108, bg1);
	}

	public void testNonGenericClassWithGenericSuperclass () throws Exception {
		kryo.setReferences(true);
		kryo.setRegistrationRequired(false);
		kryo.setAsmEnabled(true);
		kryo.register(BaseGeneric.class);
		kryo.register(ConcreteClass.class);

		List list = Arrays.asList(
			new SerializableObjectFoo("one"), 
			new SerializableObjectFoo("two"),
			new SerializableObjectFoo("three"));
		ConcreteClass cc1 = new ConcreteClass(list);

		roundTrip(108, 108, cc1);
	}

	// A simple serializable class.
	private static class SerializableObjectFoo implements Serializable {
		String name;

		SerializableObjectFoo (String name) {
			this.name = name;
		}

		public SerializableObjectFoo () {
			name = "Default";
		}

		@Override
		public boolean equals (Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			SerializableObjectFoo other = (SerializableObjectFoo)obj;
			if (name == null) {
				if (other.name != null) return false;
			} else if (!name.equals(other.name)) return false;
			return true;
		}
	}

	private static class BaseGeneric<T extends Serializable> {

		// The type of this field cannot be derived from the context.
		// Therefore, Kryo should consider it to be Object.
		private final List<T> listPayload;

		/** Kryo Constructor */
		protected BaseGeneric () {
			super();
			this.listPayload = null;
		}

		protected BaseGeneric (final List<T> listPayload) {
			super();
			// Defensive copy, listPayload is mutable
			this.listPayload = new ArrayList<T>(listPayload);
		}

		public final List<T> getPayload () {
			return this.listPayload;
		}

		@Override
		public boolean equals (Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			BaseGeneric other = (BaseGeneric)obj;
			if (listPayload == null) {
				if (other.listPayload != null) return false;
			} else if (!listPayload.equals(other.listPayload)) return false;
			return true;
		}

	}

	// This is a non-generic class with a generic superclass.
	private static class ConcreteClass2 extends BaseGeneric<SerializableObjectFoo> {
		/** Kryo Constructor */
		ConcreteClass2 () {
			super();
		}

		public ConcreteClass2 (final List listPayload) {
			super(listPayload);
		}
	}

	private static class ConcreteClass1 extends ConcreteClass2 {
		/** Kryo Constructor */
		ConcreteClass1 () {
			super();
		}

		public ConcreteClass1 (final List listPayload) {
			super(listPayload);
		}
	}

	private static class ConcreteClass extends ConcreteClass1 {
		/** Kryo Constructor */
		ConcreteClass () {
			super();
		}

		public ConcreteClass (final List listPayload) {
			super(listPayload);
		}
	}
}
