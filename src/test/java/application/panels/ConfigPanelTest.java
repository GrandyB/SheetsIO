/**
 * ConfigPanelTest.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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
package application.panels;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.greenrobot.eventbus.EventBus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import application.AppUtil;
import application.IApplicationOps;
import application.models.ConfigHolder;
import application.models.PropertiesHolder;
import application.services.old.FileIO;
import application.services.old.FileUpdater;
import application.threads.UpdateRunnable;

public class ConfigPanelTest {

	private static final String FILE_NAME = "fileName";
	private static final String FILE_PATH = "C:/file/" + FILE_NAME;
	private static final String PARENT_FILE_NAME = "parentFileName";

	@Mock
	private ConfigPanel.Gui gui;
	@Mock
	private FileIO fileIO;
	@Mock
	private ConfigHolder configHolder;
	@Mock
	private UpdateRunnable updateRunnable;
	@Mock
	private File file;
	@Mock
	private IApplicationOps app;
	@Mock
	private AppUtil appUtil;
	@Mock
	private PropertiesHolder props;
	@Mock
	private EventBus eventBus;

	private File parentFile = new File(PARENT_FILE_NAME);

	private ConfigPanel testee;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		testee = new ConfigPanel(configHolder, fileIO, updateRunnable, appUtil, props);
		testee.setGui(gui);

		testee.setApp(app);
		when(app.getEventBus()).thenReturn(eventBus);
		when(file.getAbsolutePath()).thenReturn(FILE_PATH);
		when(file.getAbsoluteFile()).thenReturn(file);
		when(file.getParentFile()).thenReturn(parentFile);
		when(file.getName()).thenReturn(FILE_NAME);
	}

	@AfterEach
	public void tearDown() {
		Mockito.verifyNoMoreInteractions(gui, fileIO, configHolder, props);
	}

	@Test
	public void test_initialise() throws IOException {
		testee.initialise();
		verify(gui).init();
		verify(fileIO).createFolder(ConfigPanel.LOGS_FOLDER);
		verify(fileIO).createFolder(FileUpdater.FOLDER_PREFIX);
		verify(fileIO).createFolder(ConfigPanel.TEMP_FOLDER);
		verify(props).getProperty(PropertiesHolder.LAST_CONFIG);
	}

	@Test
	public void test_initialise_folderFail() throws IOException {
		when(fileIO.createFolder(ConfigPanel.LOGS_FOLDER)).thenThrow(new IOException("Test"));

		testee.initialise();
		verify(gui).init();
		verify(fileIO).createFolder(ConfigPanel.LOGS_FOLDER);
		verify(gui).showErrorDialog(Mockito.anyString(), Mockito.anyString());
		verify(props).getProperty(PropertiesHolder.LAST_CONFIG);
	}

	@Test
	public void test_handleConfigSelection() throws Exception {
		testee.handleConfigSelection(file);

		verify(configHolder).loadFile(file);
		verify(updateRunnable).updateConfig(true);

		verify(gui).setConfigChooserDirectory(parentFile);
		verify(gui).setConfigLabel(FILE_NAME);
		verify(gui).setReloadConfigLinkVisible(true);
		verify(configHolder).isAutoUpdate();
		verify(gui).setAutoUpdateCheckState(Mockito.anyBoolean());
		verify(props).setProperty(PropertiesHolder.LAST_CONFIG, FILE_PATH);
		verify(props).flush();
	}

	@Test
	public void test_handleConfigSelection_configLoadFileFail() throws Exception {
		Mockito.doThrow(new IOException("Error")).when(configHolder).loadFile(file);

		testee.handleConfigSelection(file);
		verify(configHolder).loadFile(file);
		verify(gui).showErrorDialog(Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void test_handleConfigSelection_runnableConfigUpdate() throws Exception {
		Mockito.doThrow(new IOException("Error")).when(updateRunnable).updateConfig(true);

		testee.handleConfigSelection(file);
		verify(configHolder).loadFile(file);
		verify(updateRunnable).updateConfig(true);
		verify(gui).showErrorDialog(Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void test_handleReloadClickLink() throws Exception {
		when(configHolder.isLoaded()).thenReturn(true);

		testee.handleReloadLinkClick();
		verify(configHolder).isLoaded();
		verify(configHolder).reload();
		verify(updateRunnable).updateConfig(true);
	}

	@Test
	public void test_handleReloadClickLink_configNotLoaded() throws Exception {
		when(configHolder.isLoaded()).thenReturn(false);

		testee.handleReloadLinkClick();
		verify(configHolder).isLoaded();
	}

	@Test
	public void test_handleAutoUpdateCheck_select() throws Exception {
		when(configHolder.isLoaded()).thenReturn(true);
		testee.handleAutoUpdateCheck(true);

		verify(configHolder).setAutoUpdate(true);
		verify(gui).setUpdateNowButtonEnabled(false);
		verify(configHolder).isLoaded();
		verify(updateRunnable).updateConfig(false);
	}

	@Test
	public void test_handleAutoUpdateCheck_configNotLoaded() throws Exception {
		when(configHolder.isLoaded()).thenReturn(false);
		testee.handleAutoUpdateCheck(true);

		verify(configHolder).setAutoUpdate(true);
		verify(gui).setUpdateNowButtonEnabled(false);
		verify(configHolder).isLoaded();
	}

	@Test
	public void test_handleAutoUpdateCheck_updateConfigFail() throws Exception {
		when(configHolder.isLoaded()).thenReturn(true);
		Mockito.doThrow(new IOException("Error")).when(updateRunnable).updateConfig(false);

		testee.handleAutoUpdateCheck(true);

		verify(configHolder).setAutoUpdate(true);
		verify(gui).setUpdateNowButtonEnabled(false);
		verify(configHolder).isLoaded();
		verify(updateRunnable).updateConfig(false);
		verify(gui).showErrorDialog(Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void test_handleAutoUpdateCheck_deselect() throws Exception {
		when(configHolder.isLoaded()).thenReturn(true);

		testee.handleAutoUpdateCheck(false);

		verify(configHolder).setAutoUpdate(false);
		verify(gui).setUpdateNowButtonEnabled(true);
		verify(configHolder).isLoaded();
		verify(updateRunnable).updateConfig(false);
	}

	@Test
	public void test_handleUpdateNowPress() {
		testee.handleUpdateNowPress();

		verify(updateRunnable).runOnce();
	}
}
