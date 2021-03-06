/**
 * ApiKeyPanel.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.greenrobot.eventbus.Subscribe;

import application.AppUtil;
import application.events.ApiKeySetEvent;
import application.events.AppInitialisedEvent;
import application.exceptions.GoogleSheetsException;
import application.models.ApiKeyStatus;
import application.models.PropertiesHolder;
import lombok.NoArgsConstructor;

/**
 * Logic for the apiKey entry gui.
 *
 * @author Mark "Grandy" Bishop
 */
@NoArgsConstructor
public class ApiKeyPanel extends BasePanel<ApiKeyPanel.Gui> {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger(ApiKeyPanel.class);

	public interface Gui extends BasePanel.Gui {
		/** Setup the status circle to reflect the given status. */
		void setCircle(ApiKeyStatus status);

		/** Provide a value to the apiKey box. */
		void setApiKeyField(String value);

		/** Show/hide the help link. */
		void showHelpLink(boolean show);
	}

	/** Alternative DI constructor, for test use. */
	public ApiKeyPanel(PropertiesHolder props, AppUtil util) {
		super(util, props);
	}

	/** Handle the press of the 'set' apiKey button. */
	public void handleSetApiKeyPress(String potentialKey) {
		getProps().setProperty(PropertiesHolder.API_KEY, potentialKey);
		try {
			getProps().flush();
		} catch (Exception e) {
			updateUI(ApiKeyStatus.ERROR);
			handleException(e);
		}

		if (potentialKey == null || potentialKey.trim().isEmpty()) {
			updateUI(ApiKeyStatus.MISSING);
			getGui().showErrorDialog("No apiKey given", "Please provide an apiKey");
			return;
		} else {
			// Get the "sample"/test URL and try out a connection
			String url = String.format(AppUtil.SPREADSHEET_URL_FORMAT,
					getProps().getProperty(PropertiesHolder.API_KEY_TEST_SPREADSHEET_ID),
					getProps().getProperty(PropertiesHolder.API_KEY_TEST_WORKBOOK_ID), potentialKey);

			try {
				getAppUtil().getGoogleSheetsData(url);
				updateUI(ApiKeyStatus.LOADED);
			} catch (GoogleSheetsException | IOException e) {
				updateUI(ApiKeyStatus.ERROR);
				handleException(e);
			}
		}
	}

	private void updateUI(ApiKeyStatus status) {
		getApp().getEventBus().post(new ApiKeySetEvent(status));
		getGui().setCircle(status);
		getGui().showHelpLink(!ApiKeyStatus.LOADED.equals(status));
	}

	@Subscribe
	public void handleAppInitialised(AppInitialisedEvent e) {
		String key = getProps().getProperty(PropertiesHolder.API_KEY);
		getGui().setApiKeyField(key);
		handleSetApiKeyPress(key);
	}
}
