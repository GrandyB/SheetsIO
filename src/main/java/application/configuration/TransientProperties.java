/**
 * TransientProperties.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2023.
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
package application.configuration;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * Holder for properties that only exist during the lifetime of the application.
 *
 * @author Mark "Grandy" Bishop
 */
@Component
public class TransientProperties {

	@Getter
	@Setter
	private boolean autoUpdate;
}
