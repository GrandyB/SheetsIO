package application.models.json;

import java.util.HashMap;
import java.util.Map;

import application.models.CellWrapper;

/** A non-response that skips the update loop. */
public class SkipGoogleSheetsResponse extends GoogleSheetsResponse {
	@Override
	public Map<CellWrapper, String> getMutatedRowColumnData() {
		return new HashMap<>();
	}
}