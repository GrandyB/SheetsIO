/**
 * ApiKeyStatus.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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

import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 
 *
 * @author Mark "Grandy" Bishop
 */
@AllArgsConstructor
public enum ApiKeyStatus {
	LOADED("Loaded successfully", true),
	MISSING("config.properties file is missing", false),
	INCOMPLETE("apiKey entry not found in " + PropertiesHolder.FILE_NAME, false);

	@Getter
	String message;
	@Getter
	boolean loaded;

	/** @return a {@link Circle} that gives indication of success. */
	public static Circle getCircle(ApiKeyStatus status) {
		Circle circle = new Circle(0, 0, 5);
		Tooltip t = new Tooltip(status.getMessage());
		Tooltip.install(circle, t);

		switch (status) {
		case LOADED:
			circle.setFill(Color.GREEN);
			break;
		case INCOMPLETE:
			circle.setFill(Color.ORANGE);
			break;
		case MISSING:
			circle.setFill(Color.RED);
			break;
		default:
			throw new IllegalArgumentException(
					String.format("Unable to handle unknown %s %s", ApiKeyStatus.class.getSimpleName(), status));
		}
		return circle;
	}
}
