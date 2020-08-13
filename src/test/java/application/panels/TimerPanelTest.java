/**
 * TimerPanelTest.java is part of the "SheetsIO" project (c) by Mark "Grandy" Bishop, 2020.
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
package application.panels;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import application.models.TimerDuration;
import application.threads.TimerRunnable;

public class TimerPanelTest {
	private static final String TIME_DISPLAY = "01:23";

	@Mock
	private TimerPanel.Gui gui;
	@Mock
	private TimerDuration time;
	@Mock
	private TimerRunnable timerRunnable;

	private TimerPanel testee;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		testee = new TimerPanel(time, timerRunnable);
		testee.setGui(gui);

		when(time.getDisplay()).thenReturn(TIME_DISPLAY);
	}

	@AfterEach
	public void tearDown() {
		Mockito.verifyNoMoreInteractions(gui, time, timerRunnable);
	}

	@Test
	public void test_initialise() {
		testee.initialise();

		verify(gui).init();
		verify(gui).updatePreview(TIME_DISPLAY);
		verify(time).getDisplay();
		verify(timerRunnable).setTimer(Mockito.any());
		verify(timerRunnable).run();
	}

	@Test
	public void test_decreaseTick_didntDecrease() {
		boolean didDecrease = testee.decreaseTick();

		verify(time).decrease();
		verify(time).getDisplay();
		verify(gui).updatePreview(Mockito.anyString());
		Assertions.assertEquals(false, didDecrease);
	}

	@Test
	public void test_decreaseTick_didDecrease() {
		when(time.decrease()).thenReturn(true);
		boolean didDecrease = testee.decreaseTick();

		verify(time).decrease();
		verify(time).getDisplay();
		verify(gui).updatePreview(TIME_DISPLAY);
		Assertions.assertEquals(true, didDecrease);
	}

	@Test
	public void test_getDisplay() {
		when(time.getDisplay()).thenReturn(TIME_DISPLAY);
		Assertions.assertEquals(TIME_DISPLAY, testee.getDisplay());
		verify(time).getDisplay();
	}

	@Test
	public void test_reset() throws Exception {
		testee.reset();

		verify(time).getTotalSeconds();
		verify(time).update(0);
		verify(timerRunnable).updateFile(true);
		verify(gui).setPlayPauseButtonText("Start");
		verify(time).getDisplay();
		verify(gui).updatePreview(TIME_DISPLAY);
	}

	@Test
	public void test_reset_updateFileFail() throws Exception {
		Mockito.doThrow(new IOException("Error")).when(timerRunnable).updateFile(true);
		when(time.getTotalSeconds()).thenReturn(5);
		testee.reset();

		verify(time).getTotalSeconds();
		verify(time).update(0);
		verify(timerRunnable).updateFile(true);

		verify(gui).showErrorDialog(Mockito.anyString(), Mockito.anyString());
		verify(time).update(5);
		verify(time).getDisplay();
		verify(gui).updatePreview(TIME_DISPLAY);
	}

	@Test
	public void test_handleUpdateButtonClick() throws Exception {
		testee.handleUpdateButtonClick(1, 2, 3);

		verify(time).setTimeAndFormat(1, 2, 3);
		verify(timerRunnable).updateFile(false);
		verify(time).getDisplay();

		verify(time).getDisplay();
		verify(gui).updatePreview(TIME_DISPLAY);
	}

	@Test
	public void test_handleUpdateButtonClick_fileUpdateFail() throws Exception {
		Mockito.doThrow(new IOException("Error")).when(timerRunnable).updateFile(false);
		testee.handleUpdateButtonClick(1, 2, 3);

		verify(time).setTimeAndFormat(1, 2, 3);
		verify(timerRunnable).updateFile(false);
		verify(time).getDisplay();

		verify(gui).showErrorDialog(Mockito.anyString(), Mockito.anyString());
		verify(time).getDisplay();
		verify(gui).updatePreview(TIME_DISPLAY);
	}

	@Test
	public void test_handlePlayPuaseButtonPress() {
		testee.handlePlayPauseButtonPress(1, 2, 3);

		// Initial state = true
		verify(time).setTimeAndFormat(1, 2, 3);

		// isCurrentlyRunning = false
		verify(timerRunnable).unpause();
		verify(gui).setPlayPauseButtonText("Pause");

		testee.handlePlayPauseButtonPress(4, 5, 6);
		// Initial state = false
		// isCurrentlyRunning = true
		verify(timerRunnable).pause();
		verify(gui).setPlayPauseButtonText("Resume");
	}
}
