/**
 * FileUpdaterTest.java is part of the "SheeTXT" project (c) by Mark "Grandy" Bishop, 2020.
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import application.models.CellData;

public class FileUpdaterTest {
	private static final String FOLDER_NAME = "exampleFolderName";
	private static final String FILE_NAME = "exampleFileName";

	@Mock
	private FileIO io;
	@Mock
	private ConfigHolder configHolder = new ConfigHolder();

	private FileUpdater fileUpdater;

	@Before
	public void setUp() throws IOException {
		MockitoAnnotations.initMocks(this);
		Mockito.when(io.createFolder(Mockito.any())).thenReturn(new File(FOLDER_NAME));
		Mockito.when(configHolder.getCells()).thenReturn(Arrays.asList(new CellData("A3", FILE_NAME)));
		fileUpdater = new FileUpdater(io);
	}

	@After
	public void tearDown() {
		Mockito.verifyNoMoreInteractions(io);
	}

	@Test
	public void test_setup() throws IOException {
		fileUpdater.setup(FOLDER_NAME, configHolder);
		verifySetup(FOLDER_NAME);
	}

	@Test
	public void test_updateFiles() throws IOException {
		// Must first setup
		fileUpdater.setup(FOLDER_NAME, configHolder);
		verifySetup(FOLDER_NAME);

		/*
		 * This case is a bit forced, as these 'updatedCells' should come from the
		 * cache; which should mean they're only for cells that initially came from the
		 * config, not random given ones like we have below.
		 */
		Map<CellData, String> updatedCells = new HashMap<>();
		updatedCells.put(new CellData("E3", FILE_NAME + "1"), "newVal1");
		updatedCells.put(new CellData("B8", FILE_NAME + "2"), "newVal2");

		fileUpdater.updateFiles(updatedCells);
		Mockito.verify(io).writeFile(fileUpdater.createFilePath(FOLDER_NAME, FILE_NAME + "1"), "newVal1");
		Mockito.verify(io).writeFile(fileUpdater.createFilePath(FOLDER_NAME, FILE_NAME + "2"), "newVal2");
	}

	@Test
	public void test_cleanUp_noFolder() throws IOException {
		fileUpdater.cleanUp();
		// Nothing to do
	}

	@Test
	public void test_cleanUp_hasFolder() throws IOException {
		// Must first setup
		fileUpdater.setup(FOLDER_NAME, configHolder);
		verifySetup(FOLDER_NAME);

		fileUpdater.cleanUp();
		Mockito.verify(io).deleteFiles(new File(FOLDER_NAME));
	}

	@Test
	public void test_createFolderPath() {
		String expected = FileUpdater.FOLDER_PREFIX + File.separator + FOLDER_NAME;
		Assert.assertEquals(expected, fileUpdater.createFolderPath(FOLDER_NAME));
	}

	@Test
	public void test_createFilePath() {
		String expected = FileUpdater.FOLDER_PREFIX + File.separator + FOLDER_NAME + File.separator + FILE_NAME;
		Assert.assertEquals(expected, fileUpdater.createFilePath(FOLDER_NAME, FILE_NAME));
	}

	private void verifySetup(String folderName) throws IOException {
		Mockito.verify(io).createFolder(fileUpdater.createFolderPath(folderName));
		Mockito.verify(io, Mockito.times(1)).writeFile(FILE_NAME, "");
	}
}
