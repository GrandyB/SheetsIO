/**
 * IExceptionHandler.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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

/**
 * Marker interface for handling exceptions - so that our main GUI area becomes
 * the sole place to handle them, and then display nicely to the user. Threads
 * will make use of this type to send exceptions back to their caller.
 *
 * @author Mark "Grandy" Bishop
 */
public interface IExceptionHandler {
	void handleException(Exception e);
}
