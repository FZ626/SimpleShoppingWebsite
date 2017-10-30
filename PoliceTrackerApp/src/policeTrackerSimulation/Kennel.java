package policeTrackerSimulation;

import java.awt.Point;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Kennel {
	private Point location;
	private int capacity;
	ArrayBlockingQueue<Dog> dogs;

	public Kennel(final Point location, final int capacity, final ArrayBlockingQueue<Dog> dogs) {
		this.dogs = dogs;
		this.location = location;
		this.capacity = capacity;
	}

	public int getCapacity() {
		return this.capacity;
	}

	public void setCapacity(final int capacity) {
		this.capacity = capacity;
	}

	public Point getLocation() {
		return this.location;
	}

	public void setLocation(final Point location) {
		this.location = location;
	}

	public synchronized void produce(final Dog dog) {
			try {
				this.dogs.put(dog);
			System.out.println("Police Returns one dog, " + this.dogs.size() + " dogs left");
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}

	public synchronized Dog consume() {
		final Dog dog;
			try {
				dog = this.dogs.poll(50, TimeUnit.MILLISECONDS);
			} catch (final InterruptedException e) {
				e.printStackTrace();
				return null;
			}

			if(dog!=null) {
			System.out.println("Police collects one dog, " + this.dogs.size() + " dogs left");
			} else {
			// No dog to collect;
			}
			return dog;
		}
	}

