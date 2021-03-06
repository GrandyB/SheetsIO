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
package application.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import application.models.json.Cell;
import application.models.json.CellBuilder;

public class CellWrapperTest {

	@Test
	void test_B3() throws Exception {
		CellWrapper coord = new CellWrapper(createCell("B3"));
		Assertions.assertEquals(1, coord.getCol());
		Assertions.assertEquals(2, coord.getRow());
		Assertions.assertEquals("B3", coord.getCoordString());
		// Test defaults when not supplied
		Assertions.assertEquals(FileExtension.fromRaw("txt"), coord.getFileExtension());
	}

	@Test
	void test_A1() throws Exception {
		CellWrapper coord = new CellWrapper(createCell("A1"));
		Assertions.assertEquals(0, coord.getCol());
		Assertions.assertEquals(0, coord.getRow());
		Assertions.assertEquals("A1", coord.getCoordString());
	}

	@Test
	void test_AA45() throws Exception {
		CellWrapper coord = new CellWrapper(createCell("AA45"));
		Assertions.assertEquals(26, coord.getCol());
		Assertions.assertEquals(44, coord.getRow());
		Assertions.assertEquals("AA45", coord.getCoordString());
	}

	@Test
	void test_RG1552() throws Exception {
		CellWrapper coord = new CellWrapper(createCell("RG1552"));
		Assertions.assertEquals(474, coord.getCol());
		Assertions.assertEquals(1551, coord.getRow());
		Assertions.assertEquals("RG1552", coord.getCoordString());
	}

	private Cell createCell(String ref) {
		return new CellBuilder().withName(ref).withCell(ref).build();
	}
}
