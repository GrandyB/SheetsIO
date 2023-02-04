/**
 * AbstractRepository.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2023.
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
package application.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

import application.configuration.ApplicationProperties;
import lombok.Getter;

/**
 * Parent class for repositories. Has basic fields for access to context and
 * application properties.
 *
 * @author Mark "Grandy" Bishop
 */
@Repository
public abstract class AbstractRepository {

	@Autowired
	@Getter
	private ApplicationContext context;

	@Autowired
	@Getter
	private ApplicationProperties appProps;
}
