/**
 * FileUpdaterTest.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import application.exceptions.IllegalFileExtensionException;
import application.models.CellWrapper;
import application.models.json.Cell;

public class FileUpdaterTest {
	private static final String FOLDER_NAME = "exampleFolderName";
	private static final String FILE_NAME = "exampleFileName";
	private static final String TXT_EXTENSION = "txt";

	@Mock
	private FileIO io;
	@Mock
	private ConfigHolder configHolder = new ConfigHolder();

	private FileUpdater fileUpdater;
	private List<CellWrapper> cells = new ArrayList<>();
	private CellWrapper exampleCell;

	@BeforeEach
	public void setUp() throws IOException, IllegalFileExtensionException {
		MockitoAnnotations.initMocks(this);
		Mockito.when(io.createFolder(Mockito.any())).thenReturn(new File(FOLDER_NAME));
		Mockito.when(configHolder.getProjectName()).thenReturn(FOLDER_NAME);
		exampleCell = new CellWrapper(new Cell(FILE_NAME, "A3", TXT_EXTENSION));
		cells.add(exampleCell);
		Mockito.when(configHolder.getCells()).thenReturn(cells);

		fileUpdater = new FileUpdater(io);
	}

	@AfterEach
	public void tearDown() {
		Mockito.verifyNoMoreInteractions(io);
	}

	@Test
	public void test_setup() throws IOException, IllegalFileExtensionException {
		fileUpdater.setup(configHolder);
		verifySetup(FOLDER_NAME);
	}

	@Test
	public void test_updateFiles() throws IOException, IllegalFileExtensionException {
		// Must first setup
		fileUpdater.setup(configHolder);
		verifySetup(FOLDER_NAME);

		/*
		 * This case is a bit forced, as these 'updatedCells' should come from the
		 * cache; which should mean they're only for cells that initially came from the
		 * config, not random given ones like we have below.
		 */
		Map<CellWrapper, String> updatedCells = new HashMap<>();
		CellWrapper a8 = new CellWrapper(new Cell(FILE_NAME + "1", "A8", TXT_EXTENSION));
		updatedCells.put(a8, "newVal1");

		CellWrapper b8 = new CellWrapper(new Cell(FILE_NAME + "2", "B8", TXT_EXTENSION));
		updatedCells.put(b8, "newVal2");

		fileUpdater.updateFiles(updatedCells);
		Mockito.verify(io).writeTextFile(fileUpdater.createFilePath(FOLDER_NAME, a8), "newVal1");
		Mockito.verify(io).writeTextFile(fileUpdater.createFilePath(FOLDER_NAME, b8), "newVal2");
	}

	@Test
	public void test_cleanUp_noFolder() throws IOException {
		fileUpdater.cleanUp();
		// Nothing to do
	}

	@Test
	public void test_cleanUp_hasFolder() throws IOException, IllegalFileExtensionException {
		// Must first setup
		fileUpdater.setup(configHolder);
		verifySetup(FOLDER_NAME);

		fileUpdater.cleanUp();
		Mockito.verify(io).deleteFiles(new File(FOLDER_NAME));
	}

	@Test
	public void test_createFolderPath() {
		String expected = FileUpdater.FOLDER_PREFIX + File.separator + FOLDER_NAME;
		Assertions.assertEquals(expected, fileUpdater.createFolderPath(FOLDER_NAME));
	}

	@Test
	public void test_createFilePath() throws IOException, IllegalFileExtensionException {
		// Must first setup
		fileUpdater.setup(configHolder);
		verifySetup(FOLDER_NAME);

		String expected = FileUpdater.FOLDER_PREFIX + File.separator + FOLDER_NAME + File.separator + FILE_NAME + "."
				+ TXT_EXTENSION;
		Assertions.assertEquals(expected, fileUpdater.createFilePath(FOLDER_NAME, exampleCell));
	}

	private void verifySetup(String folderName) throws IOException {
		Mockito.verify(io).createFolder(fileUpdater.createFolderPath(folderName));
		Mockito.verify(io, Mockito.times(1)).writeTextFile(fileUpdater.createFilePath(folderName, exampleCell), "");
	}
}
