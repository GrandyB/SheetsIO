/**
 * ConfigGuiTest.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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
package application.guis;

import org.greenrobot.eventbus.EventBus;

//import static org.hamcrest.CoreMatchers.equalTo;
//import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;

import application.IApplicationOps;
import javafx.stage.Stage;

public class ConfigGuiTest extends ApplicationTest {

	@Mock
	private IApplicationOps app;
	@Mock
	private EventBus eventBus;
	private ConfigGui testee;

	@Override
	public void start(Stage stage) throws Exception {
		MockitoAnnotations.initMocks(this);
		Mockito.when(app.getEventBus()).thenReturn(eventBus);
		testee = new ConfigGui(app);

		stage.setScene(testee.getScene());
		stage.show();
		stage.toFront();
	}

	@Test
	public void test_one() {
		// assertThat(lookup(".config-name-label").query().isVisible(), equalTo(true));
	}
}
