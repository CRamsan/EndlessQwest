package content;

import main.GameElement;
import main.Location;

public class Person extends GameElement{

	public Person(Location location, int id) {
		super(location, id, ElementType.PERSON);
	}

}
