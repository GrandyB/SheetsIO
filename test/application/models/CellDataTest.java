/**
 * CellDataTest.java is part of the "SheeTXT" project (c) by Mark "Grandy" Bishop, 2020.
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
package application.models;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import applications.models.CellData;

/**
 * 
 *
 * @author Mark "Grandy" Bishop
 */
class CellDataTest {

	@Test
	void test() {
		CellData data1 = new CellData(1, 3);
		CellData data2 = new CellData("B4", "file");

		Assert.assertTrue(data2.getCol() == 1);
		Assert.assertTrue(data2.getRow() == 3);
		Assert.assertTrue("Expected the two to be equal", data1.equals(data2));
	}

}
