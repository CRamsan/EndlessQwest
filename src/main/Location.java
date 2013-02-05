package main;
public class Location {

	private byte[] area;
	private int x;
	private int y;

	public Location(byte[] area, int x, int y) {
		this.area = area;
		this.x = x;
		this.y = y;
	}

	public int getDepth() {
		return this.area.length;
	}

	public byte[] getArea() {
		return area;
	}

	public void setArea(byte[] area) {
		this.area = area;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

}
