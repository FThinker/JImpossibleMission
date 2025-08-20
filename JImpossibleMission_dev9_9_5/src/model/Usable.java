package model;

/**
 * Defines a contract for objects that can be "used" by an entity.
 * This interface can be implemented by interactable objects in the game world.
 */
public interface Usable {
	
	/**
	 * Executes the "use" action of the object.
	 */
	public void use();
}