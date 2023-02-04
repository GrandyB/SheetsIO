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
import org.greenrobot.eventbus.Subscribe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.google.gson.JsonSyntaxException;

import application.IApplicationOps;
import application.IExceptionHandler;
import application.configuration.ApplicationProperties;
import application.events.ConfigReloadedEvent;
import application.exceptions.GoogleSheetsException;
import application.exceptions.JsonValidationException;
import application.utils.AppUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * BasePanel.
 *
 * @author Mark "Grandy" Bishop
 */
@RequiredArgsConstructor
public abstract class BasePanel<G extends BasePanel.Gui> implements IPanel<G>, IExceptionHandler {
	private static final Logger LOGGER = LogManager.getLogger(BasePanel.class);

	private static final int MAX_EXCEPTION_STACK_LINES = 10;

	public static final String GENERIC_ERROR_END = "\nIf unable to fix locally, please raise an issue with today's log file (in /logs) and any details on how to reproduce at https://github.com/GrandyB/SheetsIO/issues";

	@Getter(AccessLevel.PROTECTED)
	@Setter
	private G gui;

	@Getter
	@Setter
	private IApplicationOps app;

	@Autowired
	@Getter
	private ApplicationProperties appProps;

	@Autowired
	@Getter
	private ApplicationContext applicationContext;

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
		getApp().getEventBus().register(this);
		getGui().init();
	}

	@Override
	public void handleException(Exception e) {
		String headerText = e.getMessage();
		StringBuilder error = new StringBuilder();

		if (e instanceof JsonValidationException) {
			JsonValidationException jsonEx = (JsonValidationException) e;
			error.append(jsonEx.getSummary());
			error.append('\n');

		} else if (e instanceof JsonSyntaxException) {
			error.append("Your json is malformed and needs correcting!\n");
			error.append(e.getMessage());
			error.append(
					"\n\nCheck the line/column numbers in the error above for hints on where your json is failing.\nIf that doesn't help, consider running your config through a validation service such as https://jsonlint.com/\n");

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

		error.append(GENERIC_ERROR_END);

		// Remove all instances of the user's API key
		String sanitisedMessage = AppUtil.sanitiseApiKey(getAppProps().getApiKey(), headerText);
		LOGGER.error(sanitisedMessage);
		String errorMessage = AppUtil.sanitiseApiKey(getAppProps().getApiKey(), error.toString());
		LOGGER.error(errorMessage);
		getGui().showErrorDialog(sanitisedMessage, errorMessage);
	}

	@Subscribe
	public void handleConfigReloadEvent(ConfigReloadedEvent event) {
		// Do nothing
	}

	/** Open a browser window with the given url. */
	public void openBrowser(String url) {
		getApp().openBrowser(url);
	}
}
