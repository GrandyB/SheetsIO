/**
 * SheetCache.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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
package application.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.stereotype.Component;

import application.models.json.Config;
import application.models.json.GoogleSheetsResponse;

/**
 * Keep track of current values from the google spreadsheet; when an update
 * comes in, track which {@link CellWrapper}s have had their value changed, and
 * only update those text files instead of all files.
 *
 * @author Mark "Grandy" Bishop
 */
@Component
public class SheetCache {
	private Map<CellWrapper, String> values = new HashMap<>();

	/**
	 * Prep the cache with the {@link CellWrapper} of the cells we're interested in
	 * from our {@link Config}. This should be the only place we're changing the
	 * cache's size.
	 *
	 * Should be called each time config is updated, to wipe the cache clean and
	 * prep it for the next config.
	 */
	public void setup(List<CellWrapper> cellsOfInterest) {
		values.clear();
		cellsOfInterest.forEach(c -> values.put(c, ""));
	}

	/**
	 * Update the cache, provide a list of changed cell info (from the data source)
	 * so we can update files.
	 *
	 * @param updatedValueMap
	 *            mutated from {@link GoogleSheetsResponse}, the raw data in full
	 * @return a Map of {@link CellWrapper} to String for the changed cells and
	 *         their new values
	 */
	public List<CellUpdate> update(Map<CellWrapper, String> updatedValueMap) {
		List<CellUpdate> changedElements = new ArrayList<>();

		// Loop through cache keys
		for (Entry<CellWrapper, String> cacheEntry : this.values.entrySet()) {

			// Look up value in new map, and contrast to stored value
			String newVal = updatedValueMap.get(cacheEntry.getKey());
			String cacheValue = cacheEntry.getValue();

			if (newVal == null) {
				// We didn't find the cell (from config) in the update (from sheet)
				// TODO Might want to request a range (e.g. A1:B3) in the API request itself
				newVal = "";
			}

			if (!newVal.equals(cacheValue) || newVal.isEmpty()) {
				// Collect a list of the new values
				changedElements.add(new CellUpdate(cacheEntry.getKey(), newVal));
				// ...and update the cache
				this.values.put(cacheEntry.getKey(), newVal);
			}
		}

		return changedElements;
	}

	/** @return String the data from the cell, from the cache. */
	public String get(CellWrapper cellData) {
		return values.get(cellData);
	}

	/**
	 * @return {@link CellWrapper} based on the name of the output (e.g.
	 *         caster1Name).
	 */
	public Optional<CellWrapper> findByName(String name) {
		return values.keySet().stream().filter(cw -> name.equals(cw.getName())).findFirst();
	}
}
