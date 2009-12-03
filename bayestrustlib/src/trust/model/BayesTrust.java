package trust.model;

import java.util.LinkedList;
import java.util.List;

import trust.model.sets.*;
import trust.model.exceptions.DuplicatePeerException;
import trust.model.exceptions.LevelRangeException;
import trust.model.math.Misc;
import trust.model.math.Stats;
import trust.model.primitives.*;

/**
 * Implementation of the paper "B-trust: Bayesian Trust Framework for Pervasive
 * Computing" by Daniele Quercia, Stephen Hailes, Licia Capra (2006).
 * 
 * Catalin Patulea, Chris Fournier
 */
public class BayesTrust {
	/** List of contexts */
	protected List<Context> c = new LinkedList<Context>();
	/** List of peers */
	protected List<Peer> p = new LinkedList<Peer>();
	/** Direct Trust Set */
	protected DirectTrustSet dts;
	/** Direct Experience Set */
	protected DirectExperienceSet des;
	/** Recommended Trust Set */
	protected RecommendedTrustSet rts;
	/** Sent Recommendation Set */
	protected SentRecommendationSet srs;
	/** Statistics helper */
	protected Stats stats;
	/** Miscellaneous methods */
	protected Misc misc;
	
	/** Weighting factor sigma, defines how heavily direct trust is considered
	 *  versus recommended trust */
	private static final double SIGMA = 0.5;
	
	/** Initial recommended trust evolution rate. Range:
	 * 0.25 - all recommenders are untrustworthy
	 * 1.0  - all recommenders are completely trustworthy
	 * (initial value of EC matrix diagonal) 
	 */
	private static final double ETA = 0.4;
	
	/**
	 * Constructor
	 * @param n Number of levels
	 * @param contexts All possible contexts
	 */
	public BayesTrust(int n, List<Context> contexts) {
		this.stats = new Stats(n);
		this.misc = new Misc(stats);
		
		this.dts = new DirectTrustSet(stats);
		this.des = new DirectExperienceSet(stats);
		this.rts = new RecommendedTrustSet(stats);
		this.srs = new SentRecommendationSet(stats);
		
		this.c = new LinkedList<Context>(contexts);
	}
	
	/**
	 * Adds and initializes the system for a new peer
	 * @throws DuplicatePeerException 
	 * @return true upon success, else failure
	 */
	public boolean addPeer(Peer py) throws DuplicatePeerException {
		boolean success = false;
		
		if (p.contains(py))
			throw new DuplicatePeerException(py);
		
		for (Context ck : c)
		{
			// Init DTS with trust values resulting in no confidence (variance = 0), or 1/n
			dts.store(ck, py, this.misc.defaultTrustTuple());
			
			// Init DES with blank experience
			des.store(ck, py, this.misc.makeMatrix());
			
			// Init RTS
			rts.store(ck, py, this.misc.defaultTrustTuple());
			
			// Init SRS
			double[][] m = this.misc.makeMatrix(1.0 - 3 * ETA);
			for (int i = 0; i < m.length; i++) {
				m[i][i] = ETA;
			}
			srs.store(ck, py, m);
		}
		
		success = this.p.add(py);
		
		return success;
	}
	
	/**
	 * Record an encounter
	 * 
	 * See <b>Trust evolution through direct experience evaluation</b>
	 * @param ck Context encountered during
	 * @param py Peer encountered
	 * @param ctsBeta Satisfaction level of encounter (cts level [0, 1])
	 */
	public void storeEncounter(Context ck, Peer py, double ctsBeta) throws LevelRangeException {
		int beta = misc.discretize(ctsBeta);
		
		// Prior belief of direct trust in py.
		double[] d = this.dts.retrieve(ck, py);
		
		// Prior belief of what px has experienced with py.
		double [][] ec = srs.retrieve(ck, py);
		
		// Calculate d^t_a (line 2, fig 1 of paper)
		double [] newD = misc.makeTuple();
		for (int alpha = 0; alpha < stats.getN(); alpha++) {
			double denom = 0.0;
			for (int gamma = 0; gamma < stats.getN(); gamma++) {
				denom += d[gamma] * pDEgivenDT(ec, beta, gamma);
			}
			newD[alpha] = d[alpha] * pDEgivenDT(ec, beta, alpha) / denom;
		}
		
		// Update direct trust
		dts.store(ck, py, newD);
		
		// Calculate new EC_{b}
		for (int alpha = 0; alpha < stats.getN(); alpha++) {
			ec[alpha][beta] = ec[alpha][beta] + newD[alpha];
		}

		// Update direct experience
		des.store(ck, py, ec);
	}
	
	/**
	 * Record a recommendation
	 * 
	 * See <b>Trust evolution through direct recommendation evaluation</b>
	 * @param ck context
	 * @param pr recommender
	 * @param py subject of recommendation
	 * @param ctsBeta continuous recommendation level on [0, 1.0]
	 */
	public void storeRecommendation(Context ck, Peer pr, Peer py, double ctsBeta) throws LevelRangeException {
		int beta = misc.discretize(ctsBeta);
		
		// Prior belief of recommended trust in py.
		double [] r = rts.retrieve(ck, py);
		
		// Prior belief of what pr will send about py.
		double [][] rc = srs.retrieve(ck, pr);
		
		double [] newR = misc.makeTuple();
		for (int alpha = 0; alpha < stats.getN(); alpha++) {
			System.out.println("Alpha = " + alpha + ":");
			
			double pSRandRT = r[alpha] * pSRgivenRT(rc, beta, alpha);
			System.out.println(
				"  p(Sent " + beta + " | Rcmd " + alpha + ") = " + pSRgivenRT(rc, beta, alpha) + " * " +
				"p(Rcmd " + alpha + ") = " + r[alpha]);
			
			double pSR = 0.0;
			for (int gamma = 0; gamma < stats.getN(); gamma++) {
				System.out.println(
					"    p(Sent " + beta + " | Rcmd " + gamma + ") = " + pSRgivenRT(rc, beta, gamma) + " * " +
					"p(Rcmd " + gamma + ") = " + r[gamma]);
				pSR += r[gamma] * pSRgivenRT(rc, beta, gamma);
			}
			
			System.out.println(
				"  p(Sent " + beta + "; Rcmd " + alpha + ") = " + pSRandRT + " / " +
				"p(Sent " + beta + ") = " + pSR);
			newR[alpha] = pSRandRT / pSR;
			System.out.println("  p(new Rcmd " + alpha + ") = " + newR[alpha]);
		}
		
		rts.store(ck, py, newR);
		
		for (int alpha = 0; alpha < stats.getN(); alpha++) {
			rc[alpha][beta] = rc[alpha][beta] + newR[alpha];
		}
		
		srs.store(ck, pr, rc);
	}
	
	/**
	 * Probability of DE_{x,y} = beta,given DT=_{x,y} = alpha
	 * See Fig 1: trust evolution formulae, line 1
	 * @param ec
	 * @param beta
	 * @param alpha
	 * @return Probability of DE given DT
	 */
	private double pDEgivenDT(double [][] ec, int beta, int alpha) {
		double denom = 0.0;
		for (int gamma = 0; gamma < stats.getN(); gamma++) {
			denom += ec[alpha][gamma]; 
		}
		return ec[alpha][beta] / denom;
	}
	
	/**
	 * Probability of SR_{r,x} = beta,given RT=_{x,y} = alpha
	 * See Fig 1: trust evolution formulae, line 3
	 * @param rc
	 * @param beta
	 * @param alpha
	 * @return Probability of SR given RT
	 */
	public double pSRgivenRT(double [][] rc, int beta, int alpha) {
		double denom = 0.0;
		for (int gamma = 0; gamma < stats.getN(); gamma++) {
			denom += rc[alpha][gamma]; 
		}
		return rc[alpha][beta] / denom;
	}
	
	/**
	 * Retrieves the recommended trust in peer py, as computed from
	 * recommendations received from other peers. For unit testing only.
	 * 
	 * @param ck context
	 * @param py subject of recommendation
	 * @return pmf of recommended trust
	 */
	public double[] getRecommendedTrust(Context ck, Peer py) {
		return rts.retrieve(ck, py);
	}
	
	
	/**
	 * Returns overall trust in a peer for a context
	 * @param ck Context
	 * @param py Peer
	 * @return level from 0 to n
	 */
	public double[] getOverallTrust(Context ck, Peer py) {
		double[] t = misc.makeTuple();
		double[] d = dts.retrieve(ck, py);
		double[] r = rts.retrieve(ck, py);
		
		for (int j = 0; j < stats.getN(); j++)
		{

			double dy = (j+1) * d[j];
			double ry = (j+1) * r[j];
			
			t[j] = (SIGMA * dy) + ((1-SIGMA) * ry);
		}
		
		return t;
	}

	/**
	 * Mean, or condensed, overall trust value
	 * @param ck
	 * @param py
	 * @return Cts trust value [0,1)
	 */
	public double getCondensedOverallTrust(Context ck, Peer py) {
		double [] pTxy = getOverallTrust(ck, py);
		double cTxy = stats.mean(pTxy);
		
		return misc.continuate(cTxy);
	}
	
	
	/**
	 * Returns the confidence in the overall trust as a probability
	 * @param ck Context
	 * @param py Peer
	 * @return cts value from [0,1]
	 */
	public double getOverallTrustConfidence(Context ck, Peer py) {
		double[] d = dts.retrieve(ck, py);
		double[] r = rts.retrieve(ck, py);
		double[] t = this.misc.makeTuple();
		
		for (int j = 0; j < stats.getN(); j++)
		{
			t[j] = (SIGMA * d[j]) + ((1-SIGMA) * r[j]);
		}
		
		return stats.confidencePmfMax() / stats.variancePmf(t);
	}
	
	/**
	 * Gets the number of levels
	 */
	public int getN() {
		return stats.getN();
	}
}
