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