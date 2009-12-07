package learning;

/**
 * Weka Attribute class
 * 
 * @author Pierre Dinnissen
 */
public class DTAttribute {
	/** Name of the Attribute **/
	public String name;
	/** Type of the Attribute **/
	public String type;
	
	/**
	 * Default Constructor
	 */
	public DTAttribute() {}
	
	/**
	 * Constructor
	 * @param String name of the Attribute
	 * @param String type of the Attribute
	 */
	public DTAttribute(String name, String type)
	{
		this.name = name;
		this.type = type;
	}
	
	/**
	 * XML Digester callback to set the name
	 * @param String rhs for the name
	 */
	public void setName(String rhs){
		name = rhs;
	}
	
	/**
	 * XML Digester callback to set the type
	 * @param String rhs for the type
	 */
	public void setType(String rhs){
		type = rhs;
	}
}
