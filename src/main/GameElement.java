package main;

public class GameElement {

	public enum ElementType {
		PERSON, POKEMON, ITEM
	};

	protected Location location;
	protected int id;
	protected ElementType type;

	public GameElement(Location location, int id, ElementType type) {
		this.location = location;
		this.id = id;
		this.type = type;
	}

	public Location getLocation() {
		return location;
	}

	public ElementType getType() {
		return type;	
	}

	public int getId() {
		return id;
	}

}
