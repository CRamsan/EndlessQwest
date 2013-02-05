package main;
import java.io.IOException;
import java.util.ArrayList;

public class Enviroment {
	private static Map map;
	private static ArrayList<GameElement> entities;

	public Enviroment(){
		int depth = 2;
		int length = 16;
		Map map = new Map(depth, length, 10);
		try {
			map.createMapFiles();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void createElement(Location location, byte type){
		
	}
	
	public static Map getMap() {
		return map;
	}

	public static ArrayList<GameElement> getEntities() {
		return entities;
	}
}
