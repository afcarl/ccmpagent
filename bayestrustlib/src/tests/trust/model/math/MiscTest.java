package tests.trust.model.math;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import trust.model.math.*;
import trust.model.exceptions.*;

public class MiscTest {
	public static final int TRUST_LEVELS = 4;

	private Misc misc;
	
	@Before
	public void setUp() throws Exception {
		misc = new Misc(new Stats(4));
	}

	@Test
	public void testDiscretize() {
		assertEquals(0, misc.discretize(0.0));
		assertEquals(1, misc.discretize(0.3));
		assertEquals(3, misc.discretize(0.9));
		assertEquals(3, misc.discretize(0.999));
		assertEquals(3, misc.discretize(1.0));
	}
	
	@Test(expected=LevelRangeException.class)
	public void testDiscretizeNegative() {
		misc.discretize(-0.3);
	}

	@Test(expected=LevelRangeException.class)
	public void testDiscretizeGreaterThanOne() {
		misc.discretize(1.2);
	}
	
	@Test
	public void testMakeTupleDoesntCrash() {
		misc.makeTuple();
	}
	
	@Test
	public void testMakeTupleLDoesntCrash() {
		misc.makeTuple(3.14);
	}
	
	@Test
	public void testExpectedTupleLength() {
		assertEquals(TRUST_LEVELS, this.misc.getExpectedTupleLength());
	}
	
	@Test
	public void testMaxDiscreteLevel() {
		assertEquals(TRUST_LEVELS - 1, this.misc.getMaxDiscreteLevel());
	}
}
