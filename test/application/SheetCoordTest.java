/**
 * SheetCoordTest.java is part of the "SheeTXT" project (c) by Mark "Grandy" Bishop, 2020.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
