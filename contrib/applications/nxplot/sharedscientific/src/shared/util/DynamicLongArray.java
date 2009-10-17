/**
 * This file is part of the Shared Scientific Toolbox in Java ("this library"). <br />
 * <br />
 * Copyright (C) 2009 Roy Liu <br />
 * <br />
 * This library is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 2.1 of the License, or (at your option)
 * any later version. <br />
 * <br />
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. <br />
 * <br />
 * You should have received a copy of the GNU Lesser General Public License along with this library. If not, see <a
 * href="http://www.gnu.org/licenses/">http://www.gnu.org/licenses/</a>.
 */

package shared.util;

import java.util.Arrays;

/**
 * An implementation of {@link DynamicArray} for {@code long}s.
 * 
 * @author Roy Liu
 */
public class DynamicLongArray implements DynamicArray<DynamicLongArray, long[], Long> {

    /**
     * The initial capacity.
     */
    final public static int INITIAL_CAPACITY = 127;

    long[] values;

    int size;

    /**
     * Default constructor.
     */
    public DynamicLongArray() {
        this(INITIAL_CAPACITY);
    }

    /**
     * Alternate constructor.
     */
    public DynamicLongArray(int capacity) {

        this.values = new long[capacity];
        this.size = 0;
    }

    /**
     * Alternate constructor.
     */
    protected DynamicLongArray(long[] values, int size) {

        this.values = values;
        this.size = size;
    }

    /**
     * Gets the value at the given index.
     */
    public long get(int index) {

        Control.checkTrue(index >= 0 && index < this.size, //
                "Invalid index");

        return this.values[index];
    }

    /**
     * Sets a value at the given index.
     */
    public DynamicLongArray push(long value, int index) {

        Control.checkTrue(index >= 0 && index < this.size, //
                "Invalid index");

        this.values[index] = value;

        return this;
    }

    /**
     * Pops off the last pushed on value.
     */
    public long pop() {

        Control.checkTrue(this.size > 0, //
                "No more elements");

        if (this.size <= (this.values.length + 1) >>> 2) {
            this.values = Arrays.copyOf(this.values, this.values.length >>> 1);
        }

        return this.values[--this.size];
    }

    /**
     * Pushes on a value.
     */
    public DynamicLongArray push(long value) {

        if (this.size == this.values.length) {
            this.values = Arrays.copyOf(this.values, 2 * this.values.length + 1);
        }

        this.values[this.size++] = value;

        return this;
    }

    public DynamicLongArray clear() {

        this.values = new long[INITIAL_CAPACITY];
        this.size = 0;

        return this;
    }

    public DynamicLongArray ensureCapacity(int capacity) {

        this.values = (this.values.length < capacity) ? Arrays.copyOf(this.values, capacity) : this.values;

        return this;
    }

    public int size() {
        return this.size;
    }

    public int capacity() {
        return this.values.length;
    }

    public long[] values() {
        return Arrays.copyOf(this.values, this.size);
    }

    public Class<Long> getComponentType() {
        return Long.class;
    }

    @Override
    public String toString() {
        return String.format("%s[size = %d, capacity = %d, values = %s]", //
                DynamicLongArray.class.getSimpleName(), //
                size(), capacity(), Arrays.toString(values()));
    }

    @Override
    public DynamicLongArray clone() {
        return new DynamicLongArray(this.values.clone(), this.size);
    }
}