package trust.model.sets;

import java.util.Arrays;

import trust.model.math.Misc;
import trust.model.math.Stats;
import trust.model.primitives.Context;
import trust.model.primitives.Peer;

/**
 * Generic methods for trust sets.
 * @author cfournie
 */
public abstract class TrustSet {
	/** Statistics instance, containing n */
	protected Stats stats;
	/** Misc methods instance */
	protected Misc misc;
	
	/**
	 * Constructor
	 * @param stats Statistics helper, holds n
	 */
	protected TrustSet(Stats stats) {
		this.stats = stats;
		this.misc = new Misc(stats);
	}
	
	/**
	 * Returns a suitable key for usage in hashmaps from a Context and a Peer
	 * @param c Context
	 * @param p Peer
	 * @return unique key
	 */
	protected String keyFrom(Context c, Peer p) {
		return c.getName() + "." + p.getName();
	}

	/**
	 * Get default trust value for bootstrapping
	 * @return default trust value
	 */
	protected double defaultTrust() {
		return 1.0 / this.stats.getN();
	}
	
	/**
	 * Get default trust value array for bootstrapping
	 * @return default trust value array
	 */
	protected double[] defaultTrustArray() {
		double[] trust = new double[this.stats.getN()];
		double defaultVal = this.defaultTrust();
		Arrays.fill(trust, defaultVal);
		return trust;
	}
}
