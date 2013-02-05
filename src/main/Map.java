package main;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class Map {

	public enum QUARTER {
		NW, NE, SW, SE
	};

	private static final String mapRoot = "Map";

	private Region map;
	private int depth;
	private int areaLenght;
	private int numberOfAreas;
	private int bufferSize;
	private HashMap<Integer, Area> table;
	private ArrayList<Area> queue;

	public Map(int depth, int areaLenght, int bufferSize) {
		this.numberOfAreas = 0;
		this.areaLenght = areaLenght;
		this.depth = depth;
		this.bufferSize = bufferSize;
		this.table = new HashMap<Integer, Area>(bufferSize);
		this.queue = new ArrayList<Map.Area>(bufferSize);
		this.map = new Region(depth, areaLenght);
	}

	public void createMapFiles() throws IOException {
		this.numberOfAreas = 0;
		File root = new File(mapRoot);
		if (!root.exists()) {
			root.mkdir();
		}
		this.map.createMapFile(depth, areaLenght);
	}

	public byte getTerrainAt(Location location) {
		return this.map.getTerrainAt(location);
	}

	public GameElement getElementAt(Location location) {
		return this.map.getElementAt(location);
	}

	private boolean isAllocated(Area a) {
		return table.containsKey(a.getId());
	}

	private boolean isAllocable() {
		return (queue.size() < bufferSize);
	}

	private void allocateArea(Area a) {
		if (isAllocated(a)) {
			return;
		} else {
			if (isAllocable()) {
				a.loadMapFile();
				queue.add(a);
				table.put(a.getId(), a);
			} else {
				do {
					deallocateArea();
				} while (!isAllocable());
				allocateArea(a);
			}
		}
	}

	private boolean deallocateArea() {
		Area area;
		for (int i = 0; i < queue.size(); i++) {
			area = queue.get(i);
			if (!area.isActive()) {
				table.remove(queue.remove(i).getId());
				area.unLoadMapFile();
				return true;
			}
		}
		return false;
	}

	public class Region extends Geoarea {
		private Geoarea nw;
		private Geoarea ne;
		private Geoarea sw;
		private Geoarea se;

		private int depth;

		public Region(int maxDepth, int areaLength) {
			this.depth = 0;
			this.nw = new Region(this.depth + 1, maxDepth, areaLength);
			this.ne = new Region(this.depth + 1, maxDepth, areaLength);
			this.sw = new Region(this.depth + 1, maxDepth, areaLength);
			this.se = new Region(this.depth + 1, maxDepth, areaLength);
		}

		public Region(int currentDepth, int maxDepth, int areaLength) {
			this.depth = currentDepth;
			if (this.depth < maxDepth) {
				this.nw = new Region(this.depth + 1, maxDepth, areaLength);
				this.ne = new Region(this.depth + 1, maxDepth, areaLength);
				this.sw = new Region(this.depth + 1, maxDepth, areaLength);
				this.se = new Region(this.depth + 1, maxDepth, areaLength);
			} else {
				this.nw = new Area(areaLength);
				this.ne = new Area(areaLength);
				this.sw = new Area(areaLength);
				this.se = new Area(areaLength);
			}
		}

		@Override
		public byte getTerrainAt(Location location) {
			switch (QUARTER.values()[location.getArea()[depth]]) {
			case NW:
				return this.nw.getTerrainAt(location);
			case NE:
				return this.ne.getTerrainAt(location);
			case SW:
				return this.sw.getTerrainAt(location);
			case SE:
				return this.se.getTerrainAt(location);
			default:
				break;
			}
			return -1;
		}

		@Override
		public void createMapFile(int maxDepth, int areaLength)
				throws IOException {
			this.nw.createMapFile(maxDepth, areaLength);
			this.ne.createMapFile(maxDepth, areaLength);
			this.sw.createMapFile(maxDepth, areaLength);
			this.se.createMapFile(maxDepth, areaLength);
		}

		@Override
		public GameElement getElementAt(Location location) {
			switch (QUARTER.values()[location.getArea()[depth]]) {
			case NW:
				return this.nw.getElementAt(location);
			case NE:
				return this.ne.getElementAt(location);
			case SW:
				return this.sw.getElementAt(location);
			case SE:
				return this.se.getElementAt(location);
			default:
				break;
			}
			return null;
		}

	}

	public class Area extends Geoarea {
		private byte[][] map;
		private GameElement[][] elementsMatrix;
		private HashMap<Integer, GameElement> elementsTable;
		private int id;
		private boolean active;

		public Area(int length) {
			this.id = numberOfAreas++;
		}

		public int getId() {
			return id;
		}

		public boolean isActive() {
			return active;
		}

		public void unLoadMapFile() {
			this.map = null;
			this.elementsMatrix = null;
			this.elementsTable = null;
		}

		public void loadMapFile() {

		}

		@Override
		public void createMapFile(int maxDepth, int areaLength)
				throws IOException {
			byte[] tmp = new byte[areaLength * areaLength];
			for (int i = 0; i < areaLength * areaLength; i++) {
				tmp[i] = 'f';
			}
			Path file = FileSystems.getDefault().getPath(
					mapRoot + File.separator + id + ".amf");
			byte[] buf = tmp;
			Files.write(file, buf);
		}

		@Override
		public byte getTerrainAt(Location location) {
			if (isAllocated(this)) {
				allocateArea(this);
			}
			return this.map[location.getX()][location.getY()];
		}

		@Override
		public GameElement getElementAt(Location location) {
			if (!isAllocated(this)) {
				allocateArea(this);
			}
			return this.elementsMatrix[location.getX()][location.getY()];
		}

		public GameElement getElementById(int id) {
			if (!isAllocated(this)) {
				allocateArea(this);
			}
			return this.elementsTable.get(id);
		}
	}

	public abstract class Geoarea {

		public abstract void createMapFile(int maxDepth, int areaLength)
				throws IOException;

		public abstract byte getTerrainAt(Location location);

		public abstract GameElement getElementAt(Location location);

	}

}
