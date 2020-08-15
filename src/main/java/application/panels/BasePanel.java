/**
 * BasePanel.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.google.gson.JsonSyntaxException;

import applicaiton.events.ConfigReloadedEvent;
import application.AppUtil;
import application.IExceptionHandler;
import application.exceptions.GoogleSheetsException;
import application.exceptions.JsonValidationException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * BasePanel
 *
 * @author Mark "Grandy" Bishop
 */
public abstract class BasePanel<G extends BasePanel.Gui> implements IPanel<G>, IExceptionHandler {
	private static final Logger LOGGER = LogManager.getLogger(BasePanel.class);

	private static final int MAX_EXCEPTION_STACK_LINES = 10;

	@Getter(AccessLevel.PROTECTED)
	@Setter
	private G gui;

	public interface Gui {
		/** Perform initialisation of the Gui. */
		void init();

		/**
		 * Show an error dialog on screen, with the given header/message, but sanitised.
		 */
		void showErrorDialog(String header, String message);
	}

	/**
	 * Perform any Gui-related initialisation. Use {@link #preInitialise()} for any
	 * non-Gui related initialisation.
	 */
	public void initialise() {
		EventBus.getDefault().register(this);
		getGui().init();
	}

	@Override
	public void handleException(Exception e) {
		String headerText = e.getMessage();
		StringBuilder error = new StringBuilder();

		if (e instanceof JsonValidationException) {
			error.append("Error while attempting to load config values into the application.\n");
			JsonValidationException jsonEx = (JsonValidationException) e;
			jsonEx.getViolations().forEach(v -> {
				error.append(v.getMessage());
				error.append('\n');
			});

		} else if (e instanceof JsonSyntaxException) {
			error.append("Your json is malformed and needs correcting!\n");
			error.append(e.getMessage());
			error.append(
					"\n\nCheck the line/column numbers in the error above for hints on where your json is failing.\nIf that doesn't help, consider running your config through a validation service such as https://jsonlint.com/ - removing your apiKey first of course!\n");

		} else if (e instanceof GoogleSheetsException) {
			GoogleSheetsException gsEx = (GoogleSheetsException) e;
			headerText = gsEx.getHeader();
			error.append(gsEx.getMessage());
			error.append("\n");
		} else {
			StackTraceElement[] stack = e.getStackTrace();
			// If stack smaller than preset length, use that; otherwise limit to defined max
			for (int i = 0; i < (stack.length > MAX_EXCEPTION_STACK_LINES ? MAX_EXCEPTION_STACK_LINES
					: stack.length); i++) {
				error.append(stack[i].toString());
				error.append('\n');
			}
			error.append("...\n");

		}

		error.append(
				"\nIf unable to fix locally, please raise an issue with today's log file (in /logs) and any details on how to reproduce at https://github.com/GrandyB/SheetsIO/issues");

		// Remove all instances of the user's API key
		String sanitisedMessage = AppUtil.sanitiseApiKey(headerText);
		LOGGER.error(sanitisedMessage);
		String errorMessage = AppUtil.sanitiseApiKey(error.toString());
		LOGGER.error(errorMessage);
		getGui().showErrorDialog(sanitisedMessage, errorMessage);
	}

	@Subscribe
	public void handleConfigReloadEvent(ConfigReloadedEvent event) {
		// Do nothing
	}
}
