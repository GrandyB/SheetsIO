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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.greenrobot.eventbus.Subscribe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import application.data.GoogleSheetsRepository;
import application.events.ApiKeySetEvent;
import application.events.AppInitialisedEvent;
import application.models.ApiKeyStatus;

/**
 * Logic for the apiKey entry gui.
 *
 * @author Mark "Grandy" Bishop
 */
@Component
public class ApiKeyPanel extends BasePanel<ApiKeyPanel.Gui> {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger(ApiKeyPanel.class);

	@Autowired
	private GoogleSheetsRepository googleSheetsRepository;

	public interface Gui extends BasePanel.Gui {
		/** Setup the status circle to reflect the given status. */
		void setCircle(ApiKeyStatus status);

		/** Provide a value to the apiKey box. */
		void setApiKeyField(String value);

		/** Show/hide the help link. */
		void showHelpLink(boolean show);
	}

	@Override
	public void postLayout() {
		updateAfterFileLoad();
	}

	/** Handle the press of the 'set' apiKey button. */
	public void handleSetApiKeyPress(String potentialKey) {
		if (potentialKey == null || potentialKey.trim().isEmpty()) {
			updateUI(ApiKeyStatus.MISSING);
			getExceptionHandler().showErrorDialog("No apiKey given", "Please provide an apiKey");
			return;
		} else {
			getAppProps().setApiKey(potentialKey);

			try {
				googleSheetsRepository.testConnection();
				updateUI(ApiKeyStatus.LOADED);
			} catch (Exception e) {
				updateUI(ApiKeyStatus.ERROR);
				getExceptionHandler().handle(e);
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
		// TODO: will this ever get used now? happens before panels/guis are
		// created
		updateAfterFileLoad();
	}

	private void updateAfterFileLoad() {
		String key = getAppProps().getApiKey();
		getGui().setApiKeyField(key);
		handleSetApiKeyPress(key);
	}
}
