/**
 * SheetCoordTest.java is part of the "SheeTXT" project (c) by Mark "Grandy"
 * Bishop, 2020.
 */
package application;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import application.models.CellData;

public class SheetCoordTest {
	private static final String FILE = "file";

	@Test
	void test_B3() {
		CellData coord = new CellData("B3", FILE);
		Assert.assertEquals(1, coord.getCol());
		Assert.assertEquals(2, coord.getRow());
		Assert.assertEquals("B3", coord.getCoordString());
	}

	@Test
	void test_A1() {
		CellData coord = new CellData("A1", FILE);
		Assert.assertEquals(0, coord.getCol());
		Assert.assertEquals(0, coord.getRow());
		Assert.assertEquals("A1", coord.getCoordString());
	}

	@Test
	void test_AA45() {
		CellData coord = new CellData("AA45", FILE);
		Assert.assertEquals(26, coord.getCol());
		Assert.assertEquals(44, coord.getRow());
		Assert.assertEquals("AA45", coord.getCoordString());
	}

	@Test
	void test_RG1552() {
		CellData coord = new CellData("RG1552", FILE);
		Assert.assertEquals(474, coord.getCol());
		Assert.assertEquals(1551, coord.getRow());
		Assert.assertEquals("RG1552", coord.getCoordString());
	}
}
