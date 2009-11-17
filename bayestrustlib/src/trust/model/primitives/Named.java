/**
 * 
 */
package trust.model.primitives;

/**
 * Named abstract class.
 * @author cfournie
 */
public abstract class Named {
	protected String name = "";
	
	/**
	 * Constructor
	 * @param name
	 */
	public Named(String name) {
		this.name = name;
	}
	
	/**
	 * Equality operator
	 */
	public boolean equals(Object o) {
		boolean equal = true;
		
		if (o != this) {
			if (o instanceof Named) {
				Named n = (Named)o;
				equal = n.name.equals(this.name);
			} else {
				equal = false;
			}
		}
		
		return equal;
	}
}
