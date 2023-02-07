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
package application.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import application.models.CellUpdate;
import application.models.CellWrapper;
import application.models.ConfigurationFile;
import application.models.json.CellBuilder;
import application.models.json.Config;
import application.services.old.FileUpdater;

public class FileUpdaterTest {
	private static final String FOLDER_NAME = "exampleFolderName";
	private static final String FILE_NAME = "exampleFileName";
	private static final String TXT_EXTENSION = "txt";

	@Mock
	private FileIOService io;
	@Mock
	private Config config;

	private FileUpdater fileUpdater;
	private List<CellWrapper> cells = new ArrayList<>();
	private CellWrapper exampleCell;

	@SuppressWarnings("deprecation")
	@BeforeEach
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Mockito.when(io.createFolder(Mockito.any())).thenReturn(new File(FOLDER_NAME));

		exampleCell = new CellWrapper(
				new CellBuilder().withName(FILE_NAME).withCell("A3").withFileExtension(TXT_EXTENSION).build());
		cells.add(exampleCell);

		Mockito.when(config.getProjectName()).thenReturn(FOLDER_NAME);
		ConfigurationFile.get().setupConfigForTest(config, cells);

		fileUpdater = new FileUpdater(io);
	}

	@AfterEach
	public void tearDown() {
		Mockito.verifyNoMoreInteractions(io);
	}

	@Test
	public void test_setup() throws Exception {
		fileUpdater.setup();
		verifySetup(FOLDER_NAME);
	}

	@Test
	public void test_updateFiles() throws Exception {
		// Must first setup
		fileUpdater.setup();
		verifySetup(FOLDER_NAME);

		/*
		 * This case is a bit forced, as these 'updatedCells' should come from the
		 * cache; which should mean they're only for cells that initially came from the
		 * config, not random given ones like we have below.
		 */
		List<CellUpdate> updatedCells = new ArrayList<>();
		CellWrapper a8 = new CellWrapper(
				new CellBuilder().withName(FILE_NAME + "1").withCell("A8").withFileExtension(TXT_EXTENSION).build());
		updatedCells.add(new CellUpdate(a8, "newVal1"));

		CellWrapper b8 = new CellWrapper(
				new CellBuilder().withName(FILE_NAME + "2").withCell("B8").withFileExtension(TXT_EXTENSION).build());
		updatedCells.add(new CellUpdate(b8, "newVal2"));

		ConfigurationFile.get().getCells().add(a8);
		ConfigurationFile.get().getCells().add(b8);

		fileUpdater.updateFiles(updatedCells);
		Mockito.verify(io).writeTextFile(fileUpdater.createFilePath(FOLDER_NAME, a8), "newVal1");
		Mockito.verify(io).writeTextFile(fileUpdater.createFilePath(FOLDER_NAME, b8), "newVal2");
	}

	@Test
	public void test_updateFiles_multipleOutputsConfiguredWithSameCellReference() throws Exception {
		// Must first setup
		fileUpdater.setup();
		verifySetup(FOLDER_NAME);

		List<CellUpdate> updatedCells = new ArrayList<>();
		CellWrapper a8v1 = new CellWrapper(
				new CellBuilder().withName(FILE_NAME + "1").withCell("A8").withFileExtension(TXT_EXTENSION).build());
		CellWrapper a8v2 = new CellWrapper(
				new CellBuilder().withName(FILE_NAME + "2").withCell("A8").withFileExtension(TXT_EXTENSION).build());

		ConfigurationFile.get().getCells().add(a8v1);
		ConfigurationFile.get().getCells().add(a8v2);

		// One update (that would in the app come from the cache/google sheets update
		updatedCells.add(new CellUpdate(a8v1, "newVal"));

		fileUpdater.updateFiles(updatedCells);
		// ...that should update multiple files
		Mockito.verify(io).writeTextFile(fileUpdater.createFilePath(FOLDER_NAME, a8v1), "newVal");
		Mockito.verify(io).writeTextFile(fileUpdater.createFilePath(FOLDER_NAME, a8v2), "newVal");
	}

	@Test
	public void test_cleanUp_noFolder() throws Exception {
		fileUpdater.cleanUp();
		// Nothing to do
	}

	@Test
	public void test_cleanUp_hasFolder() throws Exception {
		// Must first setup
		fileUpdater.setup();
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
	public void test_createFilePath() throws Exception {
		// Must first setup
		fileUpdater.setup();
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
