package trust.model.sets;

import java.util.HashMap;
import java.util.Map;

import trust.model.exceptions.MalformedTupleException;
import trust.model.primitives.Context;
import trust.model.primitives.Peer;

import trust.model.math.Stats;

/**
 * Send Recommendation Set implementation
 * 
 * See <b>reputation data structures</b>
 * Catalin Patulea <cat@vv.carleton.ca>
 */
public class SentRecommendationSet extends TrustSet {
	protected Map<String, double[][]> store;
	
	public static final double RC_INIT = 0.1;
	
	/**
	 * Construct an SRS store.
	 * @param stats Statistics helper, holds n
	 */
	public SentRecommendationSet(Stats stats)
	{
		super(stats);
		store = new HashMap<String, double[][]>();
	}
	
	public void store(Context ck, Peer pr, double[][] rc) throws IllegalArgumentException
	{
		if (rc.length != stats.getN()) {
			throw new MalformedTupleException(rc.length, stats);
		}
		
		if (rc[0].length != stats.getN()) {
			throw new MalformedTupleException(rc[0].length, stats);
		}
		
		store.put(keyFrom(ck, pr), rc);
	}
	
	public double[][] retrieve(Context ck, Peer pr)
	{
		double[][] trust = store.get(keyFrom(ck, pr));
		if (trust == null) {
			return misc.makeMatrix(RC_INIT);
		} else {
			return trust;
		}
	}
}
