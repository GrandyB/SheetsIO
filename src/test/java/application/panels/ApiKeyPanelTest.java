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

import java.io.IOException;

import org.greenrobot.eventbus.EventBus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import application.AppUtil;
import application.IApplicationOps;
import application.events.ApiKeySetEvent;
import application.events.AppInitialisedEvent;
import application.exceptions.GoogleSheetsException;
import application.models.ApiKeyStatus;
import application.models.PropertiesHolder;

public class ApiKeyPanelTest {
	private static final String SAMPLE_KEY = "123456";
	private static final String SAMPLE_ID = "123";
	private static final String SAMPLE_BOOK = "abc";

	@Mock
	private ApiKeyPanel.Gui gui;
	@Mock
	private PropertiesHolder props;
	@Mock
	private AppUtil util;
	@Mock
	private IApplicationOps ops;
	@Mock
	private EventBus eventBus = new EventBus();

	private ApiKeyPanel testee;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		testee = new ApiKeyPanel(props, util);
		testee.setGui(gui);
		testee.setApp(ops);

		when(ops.getEventBus()).thenReturn(eventBus);
		when(props.getProperty(PropertiesHolder.API_KEY)).thenReturn(SAMPLE_KEY);
		when(props.getProperty(PropertiesHolder.API_KEY_TEST_SPREADSHEET_ID)).thenReturn(SAMPLE_ID);
		when(props.getProperty(PropertiesHolder.API_KEY_TEST_WORKBOOK_ID)).thenReturn(SAMPLE_BOOK);
	}

	@AfterEach
	public void tearDown() {
		Mockito.verifyNoMoreInteractions(gui, props, util);
	}

	@Test
	public void test_handleSetApiKeyPress_success() throws IOException, GoogleSheetsException {
		testee.handleAppInitialised(new AppInitialisedEvent());

		verify(props).getProperty(PropertiesHolder.API_KEY);
		verify(gui).setApiKeyField(SAMPLE_KEY);

		// handleSetApiKeyPress is called - save entered key
		verify(props).setProperty(PropertiesHolder.API_KEY, SAMPLE_KEY);
		verify(props).flush();

		// Try url out
		verify(props).getProperty(PropertiesHolder.API_KEY_TEST_SPREADSHEET_ID);
		verify(props).getProperty(PropertiesHolder.API_KEY_TEST_WORKBOOK_ID);
		verify(util)
				.getGoogleSheetsData(String.format(AppUtil.SPREADSHEET_URL_FORMAT, SAMPLE_ID, SAMPLE_BOOK, SAMPLE_KEY));

		// Success
		verifyUpdateUI(ApiKeyStatus.LOADED, false);
	}

	@Test
	public void test_initialise_exception() throws IOException, GoogleSheetsException {
		Mockito.when(util.getGoogleSheetsData(Mockito.any()))
				.thenThrow(new GoogleSheetsException("url", 1, "message", "status"));
		testee.handleAppInitialised(new AppInitialisedEvent());

		verify(props).getProperty(PropertiesHolder.API_KEY);
		verify(gui).setApiKeyField(SAMPLE_KEY);

		// handleSetApiKeyPress is called - save entered key
		verify(props).setProperty(PropertiesHolder.API_KEY, SAMPLE_KEY);
		verify(props).flush();

		// Try url out
		verify(props).getProperty(PropertiesHolder.API_KEY_TEST_SPREADSHEET_ID);
		verify(props).getProperty(PropertiesHolder.API_KEY_TEST_WORKBOOK_ID);
		verify(util)
				.getGoogleSheetsData(String.format(AppUtil.SPREADSHEET_URL_FORMAT, SAMPLE_ID, SAMPLE_BOOK, SAMPLE_KEY));

		// Fail
		verifyUpdateUI(ApiKeyStatus.ERROR, true);
		verify(gui).showErrorDialog("1 - status", "url\n\nmessage\n" + BasePanel.GENERIC_ERROR_END);

	}

	@Test
	public void test_initialise_emptyKey() throws IOException, GoogleSheetsException {
		when(props.getProperty(PropertiesHolder.API_KEY)).thenReturn("");
		testee.handleAppInitialised(new AppInitialisedEvent());

		verify(props).getProperty(PropertiesHolder.API_KEY);
		verify(gui).setApiKeyField("");

		// handleSetApiKeyPress is called - save entered key
		verify(props).setProperty(PropertiesHolder.API_KEY, "");
		verify(props).flush();

		// Fail
		verifyUpdateUI(ApiKeyStatus.MISSING, true);
		verify(gui).showErrorDialog("No apiKey given", "Please provide an apiKey");

	}

	private void verifyUpdateUI(ApiKeyStatus status, boolean helpLinkEnabled) {
		ArgumentCaptor<ApiKeySetEvent> arg = ArgumentCaptor.forClass(ApiKeySetEvent.class);
		verify(eventBus).post(arg.capture());
		Assertions.assertEquals(status, arg.getValue().getStatus());

		verify(gui).setCircle(status);
		verify(gui).showHelpLink(helpLinkEnabled);
	}
}
