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

import java.io.IOException;

import org.greenrobot.eventbus.EventBus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import application.events.ApiKeySetEvent;
import application.models.ApiKeyStatus;

public class MainPanelTest {

	@Mock
	private MainPanel.Gui gui;

	private MainPanel testee;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		testee = new MainPanel();
		testee.setGui(gui);

	}

	@AfterEach
	public void tearDown() {
		Mockito.verifyNoMoreInteractions(gui);
	}

	@Test
	public void test_initialise() throws IOException {
		initiialiseAndVerify();
	}

	@Test
	public void test_handleApiKeySet_error() {
		testEvent(ApiKeyStatus.ERROR, false);
	}

	@Test
	public void test_handleApiKeySet_incomplete() {
		testEvent(ApiKeyStatus.INCOMPLETE, false);
	}

	@Test
	public void test_handleApiKeySet_missing() {
		testEvent(ApiKeyStatus.MISSING, false);
	}

	@Test
	public void test_handleApiKeySet_loaded() {
		testEvent(ApiKeyStatus.LOADED, true);
	}

	private void testEvent(ApiKeyStatus status, boolean expectedEnable) {
		initiialiseAndVerify();
		EventBus.getDefault().post(new ApiKeySetEvent(status));

		verify(gui).enableMainLayout(expectedEnable);
	}

	private void initiialiseAndVerify() {
		testee.initialise();
		verify(gui).init();
	}
}
