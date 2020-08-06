/**
 * FileUpdaterTest.java is part of the "SheeTXT" project (c) by Mark "Grandy" Bishop, 2020.
 */
package application;

import java.io.IOException;

import org.junit.jupiter.api.Test;

class FileUpdaterTest {

	private FileUpdater fileUpdater = new FileUpdater();
	private ConfigHolder configHolder = new ConfigHolder();

	@Test
	void test() throws IOException {
		fileUpdater.setup("testing123", configHolder);
	}

	@Test
	void test2() throws IOException {
		fileUpdater.setup("fourtyfour", configHolder);
	}
}
