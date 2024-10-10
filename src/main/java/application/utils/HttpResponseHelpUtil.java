package application.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpResponseHelpUtil {
	private static final String GENERAL = "\n\nIf the issue persists, check the log file for the full stack trace and open an issue at "
			+ "https://github.com/GrandyB/SheetsIO/issues";
	private static final String CODE_500 = "\nResponses with 5XX codes are 'ignored' and data acquisition is retried in the next update loop, "
			+ "so these should only be a problem if they repeat/aren't a one-off hiccup.";

	public static String getHelperTextFor(int code) {
		String helpText = "No specific advice for this.";

		if (code == 200) {
			helpText = "Code '" + code
					+ "' is a 'successful response', suggesting the request was made successfully and some kind of response was given in an expected way.\n";
		} else if (code >= 201 && code < 300) {
			helpText = "Code '" + code
					+ "' is a 'successful response' (by HTTP standards), however it is not the 200 OK response expected by our application.\n";
		} else if (code >= 300 && code < 400) {
			helpText = "Code '" + code
					+ "' is a 'redirect message', which usually suggests that the requested location results in a redirect to another location.\n"
					+ "In some cases, this is due to websites masking direct links (and so you should find another website to use).\n"
					+ "Alternatively, it might just be user error in what link was copied/used - ensure that it is a direct link to the resource you want to use.";
		} else if (code >= 400 && code < 500) {
			helpText = "Code '" + code
					+ "' is a 'client error response', which suggests an issue with the data update request.\n";
		} else if (code >= 500) {
			helpText = "Code '" + code
					+ "' is a 'server error response', which would suggest it's probably an issue outside of SheetsIO itself.\n";
		}

		switch (code) {
		case 400:
			helpText += "The server cannot or will not process the request due to something that is perceived to be a client error.\n"
					+ "Ensure that things like your 'tab name' doesn't contain any characters which a URL wouldn't like (e.g. a forward slash).\n"
					+ "Ensure you have set the spreadsheet ID correctly.";
		case 401:
			helpText += "This means SheetsIO is 'unauthenticated' and requires authentication.\n"
					+ "This can happen if your API key has not been properly associated with the Google Sheets API, or has expired - "
					+ "please re-review the setup instructions on the repo for creating an API key.";
		case 403:
			helpText += "This means that the server recognises our API key as valid, however...\n"
					+ "Ensure that your Google Sheet's sharing options is set to 'anyone with the link can view'.\n"
					+ "Ensure that you have not exceeded the quota available to your API key.\n";
		case 404:
			helpText += "The server cannot find the requested resource - which can happen if the spreadsheet or tab doesn't exist.";
		case 429:
			helpText += "This is returned if we do too many requests in a short period of time - stop spamming!\n"
					+ "Ensure your update interval in the properties file is not too small, consider changing it back to the default of 2000.\n"
					+ "Ensure your API key is only being used by you and that you don't have any other processes using it.";
		}
		if (code >= 500) {
			helpText += CODE_500;
		}

		return helpText + GENERAL;
	}

}