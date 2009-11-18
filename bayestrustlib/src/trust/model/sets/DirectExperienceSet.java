package trust.model.sets;

import java.util.HashMap;

import trust.model.exceptions.MalformedTupleException;
import trust.model.math.Misc;
import trust.model.primitives.Context;
import trust.model.primitives.Peer;

public class DirectExperienceSet extends TrustSet {
	/** DES */
	HashMap<String,double [][]> set;
	
	/**
	 * Constructor
	 * @param nLevels
	 */
	public DirectExperienceSet(int nLevels)
	{
		super(nLevels);
	}
	
	/**
	 * Updates data structure to represent an encounter
	 * @param ck Context
	 * @param py Peer
	 * @param lb Trust level determined after encounter
	 * @param la Previous trust level
	 */
	public void storeEncounter(Context ck, Peer py, int lb, int la) {
		Misc.checkLevel(la);
		Misc.checkLevel(lb);
		
		double[][] ec = this.retrieve(ck, py);
		ec[lb][la]++;
		
		this.store(ck, py, ec);
	}
	
	/**
	 * Set trust n-tuple
	 * @param p Peer
	 * @param c Context
	 * @param d Set of probabilities that p_x has l_j
	 * @return Previous value
	 * @throws MalformedTupleException
	 */
	public double[][] store(Context ck, Peer py, double[][] ec)
	{
		Misc.checkMatrix(ec);
		return set.put(super.keyFrom(ck, py), ec);
	}
	
	/**
	 * Get trust n-tuple
	 * @param p Peer
	 * @param c Context
	 * @return
	 */
	public double[][] retrieve(Context ck, Peer py)
	{
		return set.get(super.keyFrom(ck, py));
	}
}
