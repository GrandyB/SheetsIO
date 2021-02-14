/**
 * VersionedString.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2021.
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

import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 
 *
 * @author Mark "Grandy" Bishop
 */
@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VersionedString {
	@Getter
	private String value;
	@Getter
	private Date lastModified;
	@Getter
	private int id;

	public static VersionedString of(String value) {
		return new VersionedString(value, new Date(System.currentTimeMillis()), 0);
	}

	public static VersionedString update(VersionedString old, String newValue) {
		return new VersionedString(newValue, new Date(System.currentTimeMillis()), old.getId() + 1);
	}

	public boolean hasSameValue(VersionedString other) {
		return other != null && value.equals(other.getValue());
	}
}
