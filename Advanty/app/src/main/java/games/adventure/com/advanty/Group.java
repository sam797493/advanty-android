
package games.adventure.com.advanty;

import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

public class Group extends Mesh {
	private final Vector<Mesh> mChildren = new Vector<Mesh>();

	@Override
	public void draw(GL10 gl) {
		if (!shouldBeDrawn) return;
		
		int size = mChildren.size();
		gl.glPushMatrix();
		gl.glTranslatef(x, y, z);
		gl.glRotatef(rx, 1, 0, 0);
		gl.glRotatef(ry, 0, 1, 0);
		gl.glRotatef(rz, 0, 0, 1);

		for (int i = 0; i < size; i++) {
			mChildren.get(i).draw(gl);
		}
			
		gl.glPopMatrix();
	}


	public void add(int location, Mesh object) {
		mChildren.add(location, object);
	}


	public boolean add(Mesh object) {
		return mChildren.add(object);
	}


	public void clear() {
		mChildren.clear();
	}


	public Mesh get(int location) {
		return mChildren.get(location);
	}


	public Mesh remove(int location) {
		return mChildren.remove(location);
	}


	public boolean remove(Object object) {
		return mChildren.remove(object);
	}


	public int size() {
		return mChildren.size();
	}

}
