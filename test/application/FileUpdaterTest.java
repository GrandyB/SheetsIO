/**
 * FileUpdaterTest.java is part of the "SheeTXT" project (c) by Mark "Grandy" Bishop, 2020.
 */
package application;

import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * TODO: Proper creation/tidyup of files and checks.
 *
 * @author Mark "Grandy" Bishop
 */
class FileUpdaterTest {

	private FileUpdater fileUpdater = new FileUpdater();

	@Test
	void test() throws IOException {
		fileUpdater.setup("testing123");
	}

	@Test
	void test2() throws IOException {
		fileUpdater.setup("fourtyfour");
	}
}
