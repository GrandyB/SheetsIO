/**
 * ExceptionHandlerService.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2023.
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
package application.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.google.gson.JsonSyntaxException;

import application.exceptions.GoogleSheetsException;
import application.exceptions.JsonValidationException;
import application.utils.AppUtil;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Service for explicitly handling exceptions on the app, utilising
 * {@link Platform#runLater} to produce a dialog.
 *
 * @author Mark "Grandy" Bishop
 */
@Service
public class ExceptionHandlerService extends AbstractService {
	private static final Logger LOGGER = LogManager.getLogger(ExceptionHandlerService.class);

	private static final int MAX_EXCEPTION_STACK_LINES = 10;

	public static final String GENERIC_ERROR_END = "\nIf unable to fix locally, please raise an issue with today's log file (in /logs) and any details on how to reproduce at https://github.com/GrandyB/SheetsIO/issues";

	/**
	 * Handle the given exception by throwing an error dialog.
	 * 
	 * @param e
	 *            the {@link Exception} to handle
	 */
	public void handle(Exception e) {
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
			// If stack smaller than preset length, use that; otherwise limit to
			// defined max
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
		showErrorDialog(sanitisedMessage, errorMessage);
	}

	/** Show a dialog. */
	public void showErrorDialog(String header, String message) {
		// Remove all instances of the user's API key
		String apiKey = getAppProps().getApiKey();
		String sanitisedMessage = AppUtil.sanitiseApiKey(apiKey, header);
		String errorMessage = AppUtil.sanitiseApiKey(apiKey, message);

		/*
		 * Exceptions can be thrown within our {@link UpdateRunnable} thread and
		 * beyond, which is separate to the JavaFX application thread;
		 * Platform.runLater allows the code to be ran on the proper thread when
		 * it is able.
		 */
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("SheetsIO error");
			alert.setHeaderText(sanitisedMessage);
			alert.setContentText(errorMessage);
			alert.showAndWait();
		});
	}
}
