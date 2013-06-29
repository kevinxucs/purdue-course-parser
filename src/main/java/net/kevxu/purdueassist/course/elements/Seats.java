/*
 * Seats.java
 *
 * The MIT License
 *
 * Copyright (c) 2013 Kaiwen Xu and Rendong Chen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished
 * to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.kevxu.purdueassist.course.elements;

/**
 * Class for seat information.
 *
 * @author Kaiwen Xu (kevin)
 */
public class Seats {
	private int capacity;
	private int actual;
	private int remaining;

	/**
	 * Constructor.
	 *
	 * @param capacity
	 *            number of total seats.
	 * @param actual
	 *            number of already registered seats.
	 * @param remaining
	 *            number of available seats.
	 */
	public Seats(int capacity, int actual, int remaining) {
		this.capacity = capacity;
		this.actual = actual;
		this.remaining = remaining;
	}

	/**
	 * Constructor for copying object.
	 *
	 * @param seats
	 *            Seat object to be copied.
	 */
	public Seats(Seats seats) {
		this.capacity = seats.getCapacity();
		this.actual = seats.getActual();
		this.remaining = seats.getRemaining();
	}

	/**
	 * @return number of available seats.
	 */
	public int getRemaining() {
		return remaining;
	}

	/**
	 * @return number of total seats.
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * @return number of already registered seats.
	 */
	public int getActual() {
		return actual;
	}

	@Override
	public String toString() {
		return "Capacity: " + capacity + "; " + "Actual: " + actual + "; "
				+ "Remaining: " + remaining + ";";
	}
}