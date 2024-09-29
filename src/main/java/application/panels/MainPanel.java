/**
 * MainPanel.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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

import org.greenrobot.eventbus.Subscribe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import application.IApplicationOps;
import application.events.ApiKeySetEvent;
import application.models.ApiKeyStatus;

/**
 * Panel backing the main ui of the app. No particular logic required to
 * instantiate and show.
 *
 * @author Mark "Grandy" Bishop
 */
@Component
public class MainPanel extends BasePanel<MainPanel.Gui> {

	@Autowired
	private IApplicationOps ops;

	public interface Gui extends BasePanel.Gui {
		void enableMainLayout(boolean enable);
	}

	@Subscribe
	public void handleApiKeySetEvent(ApiKeySetEvent event) {
		switch (event.getStatus()) {
		case LOADED:
			getGui().enableMainLayout(true);
			break;
		case MISSING:
		case INCOMPLETE:
		case ERROR:
			getGui().enableMainLayout(false);
			break;
		default:
			throw new IllegalArgumentException(
					String.format("Unexpected {}: {}", ApiKeyStatus.class.getSimpleName(), event.getStatus()));
		}
	}
}
