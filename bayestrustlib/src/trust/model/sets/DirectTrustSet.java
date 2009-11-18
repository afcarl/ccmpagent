package trust.model.sets;

import java.util.*;

import trust.model.exceptions.MalformedTupleException;
import trust.model.primitives.*;

public class DirectTrustSet extends TrustSet {
	/** DTS */
	HashMap<String,double []> set;
	
	/**
	 * Constructor
	 * @param n
	 */
	public DirectTrustSet(int n)
	{
		super(n);
		set = new HashMap<String,double []>();
	}
	
	/**
	 * Set trust n-tuple
	 * @param p Peer
	 * @param c Context
	 * @param d Set of probabilities that p_x has l_j
	 * @return Previous value
	 * @throws MalformedTupleException
	 */
	public double[] store(Context ck, Peer py, double[] d) throws MalformedTupleException
	{
		if (d.length != this.nLevels)
			throw new MalformedTupleException(d.length, this.nLevels);
		
		return set.put(super.keyFrom(ck, py), d);
	}
	
	/**
	 * Get trust n-tuple
	 * @param p Peer
	 * @param c Context
	 * @return
	 */
	public double[] retrieve(Context ck, Peer py)
	{
		return set.get(super.keyFrom(ck, py));
	}
}
