/**
 * FileUpdaterTest.java is part of the "SheeTXT" project (c) by Mark "Grandy" Bishop, 2020.
 */
package application;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import application.models.CellData;

public class FileUpdaterTest {
	private static final String FILE_NAME = "test123";

	@Mock
	private FileIO io;
	@Mock
	private ConfigHolder configHolder = new ConfigHolder();

	private TestFileUpdater fileUpdater = new TestFileUpdater();

	@Before
	public void setUp() throws IOException {
		MockitoAnnotations.initMocks(this);
		Mockito.when(io.createFolder(Mockito.any())).thenReturn(new File(FILE_NAME));
		Mockito.when(configHolder.getCells()).thenReturn(Arrays.asList(new CellData("A3", FILE_NAME)));
	}

	@After
	public void tearDown() {
		Mockito.verifyNoMoreInteractions(io);
	}

	@Test
	public void test() throws IOException {
		fileUpdater.setup("testing123", configHolder);

		Mockito.verify(io, Mockito.times(1)).writeFile(FILE_NAME, "");
		Mockito.verify(io).createFolder(fileUpdater.createFolderPath("testing123"));
	}

	private class TestFileUpdater extends FileUpdater {
		@Override
		protected FileIO createFileIO() {
			return io;
		}

	}
}
