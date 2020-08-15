/**
 * IApplicationOps.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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

import org.greenrobot.eventbus.EventBus;

import javafx.stage.Stage;

/**
 * Handles any/all needed operations from individual panel/guis to the
 * {@link Main} class, so that panels don't all have a copy of the whole thing.
 *
 * @author Mark "Grandy" Bishop
 */
public interface IApplicationOps {

	/** @return the primary {@link Stage} of the app. */
	Stage getPrimaryStage();

	/** @return {@link EventBus} the application-wide event bus. */
	EventBus getEventBus();

	/** Open the system default browser with the given url */
	void openBrowser(String url);
}
