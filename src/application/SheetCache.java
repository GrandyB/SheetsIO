/**
 * SheetCache.java is part of the "SheeTXT" project (c) by Mark "Grandy" Bishop, 2020.
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import application.models.CellData;
import application.models.json.Config;
import application.models.json.GoogleSheetsResponse;

/**
 * Keep track of current values from the google spreadsheet; when an update
 * comes in, track which {@link CellData}s have had their value changed, and
 * only update those text files instead of all files.
 *
 * @author Mark "Grandy" Bishop
 */
public class SheetCache {
	private Map<CellData, String> values = new HashMap<>();

	/**
	 * Prep the cache with the {@link CellData} of the cells we're interested in
	 * from our {@link Config}. This should be the only place we're changing the
	 * cache's size.
	 *
	 * Should be called each time config is updated, to wipe the cache clean and
	 * prep it for the next config.
	 */
	public void setup(List<CellData> cellsOfInterest) {
		values.clear();
		cellsOfInterest.forEach(c -> values.put(c, ""));
	}

	/**
	 * Update the cache, provide a list of changed cell info so we can update files.
	 *
	 * @param fullValueMap
	 *            mutated from {@link GoogleSheetsResponse}, the raw data in full
	 * @return a Map of {@link CellData} to String for the changed cells and their
	 *         new values
	 */
	public Map<CellData, String> update(Map<CellData, String> fullValueMap) {
		Map<CellData, String> changedElements = new HashMap<>();

		// Loop through cache keys
		for (Entry<CellData, String> cacheEntry : this.values.entrySet()) {

			// Look up value in new map, and contrast to stored value
			String newVal = fullValueMap.get(cacheEntry.getKey());

			if (!newVal.equals(cacheEntry.getValue())) {
				changedElements.put(cacheEntry.getKey(), newVal);
				this.values.put(cacheEntry.getKey(), newVal);
			}
		}

		return changedElements;
	}

	public String get(CellData cellData) {
		return values.get(cellData);
	}
}
