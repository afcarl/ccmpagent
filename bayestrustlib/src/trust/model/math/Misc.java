package trust.model.math;

import java.util.Arrays;

import trust.model.exceptions.LevelRangeException;
import trust.model.exceptions.MalformedTupleException;

/**
 * Miscellaneous mathematical helper methods.
 * @author cfournie
 */
public class Misc {
	Stats stats;
	
	public Misc(Stats stats) {
		this.stats = stats;
	}
	
	/**
	 * Maps a continuous level on [0, 1] to a discrete level on [0, N - 1].
	 * @param l continuous level on [0, 1]
	 * @return discrete level in [0, N - 1]
	 */
	public int discretize(double l) {
		checkLevel(l);
		
		// Approximately map [0, 1] to [0, 1).
		double exclusive = l * 0.999;
		
		// Map equally-sized intervals [i/N, (i+1)/N) to i.
		return (int)Math.floor(exclusive * stats.getN());
	}
	
	/**
	 * Creates an empty 2d array of size n*n, initialized to 0.0.
	 * @return empty (zeroed) 2d array
	 */
	public double[][] makeMatrix() {
		return makeMatrix(0.0);
	}
	
	/**
	 * Creates an filled 2d array of size n*n, initialized with value v.
	 * @param v initializing value
	 * @return 2d array initialised to value v
	 */
	public double[][] makeMatrix(double v) {
		double d[][] = new double[this.stats.getN()][this.stats.getN()];
		
		for(double[] a : d)
		{
			Arrays.fill(a, 0, this.stats.getN(), v);
		}
		
		return d;
	}
	
	/**
	 * Creates an array of size n, filled with 0.0.
	 * @return n-tuple initialised to 0.0
	 */
	public double[] makeTuple() {
		return makeTuple(0.0);
	}
	
	/**
	 * Creates a filled array of size n, filled with a specified value.
	 * @param v fill value
	 * @return empty (zeroed) n-tuple
	 */
	public double[] makeTuple(double v) {
		double d[] = new double[this.stats.getN()];
		Arrays.fill(d, 0, this.stats.getN(), v);
		return d;
	}

	public double[] defaultTrustTuple() {
		return makeTuple(1.0 / this.stats.getN());
	}
	
	/**
	 * Checks whether a discrete level is within range
	 * @param l discrete level
	 */
	public void checkLevel(int l) throws LevelRangeException {
		if (l < 0 || l >= this.stats.getN())
			throw new LevelRangeException(l, stats);
	}
	
	/**
	 * Checks whether a cts level is within range
	 * @param l cts level
	 */
	public void checkLevel(double l) throws LevelRangeException {
		if (l < 0 || l > 1.0)
			throw new LevelRangeException(l);
	}
	
	/**
	 * Checks whether a tuple is within range
	 * @param d tuple
	 */
	public void checkTuple(double[] d) throws MalformedTupleException {
		if (d.length != this.stats.getN())
			throw new MalformedTupleException(d.length, this.stats);
	}
	
	/**
	 * Checks whether a matrix size is within range
	 * @param d 2d matrix
	 */
	public void checkMatrix(double[][] d) throws MalformedTupleException {
		if (d.length != this.stats.getN())
			throw new MalformedTupleException(d.length, this.stats);
		
		for(int i = 0; i < d.length; i++)
			if (d[i].length != this.stats.getN())
				throw new MalformedTupleException(d[i].length, this.stats);
	}
	
	/**
	 * Get the expected tuple length
	 * @return length
	 */
	public int getExpectedTupleLength() {
		return this.stats.getN();
	}
	
	/**
	 * Get the maximum discrete level
	 * @return length n-1
	 */
	public int getMaxDiscreteLevel() {
		return this.stats.getN() - 1;
	}
}
