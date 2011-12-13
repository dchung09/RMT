package de.flower.rmt.ui.manager.page.player;

import de.flower.rmt.model.User;
import de.flower.rmt.test.AbstractRMTWicketMockitoTests;
import de.flower.rmt.test.TestData;
import org.apache.wicket.model.Model;
import org.testng.annotations.Test;

/**
 * @author flowerrrr
 */

public class PlayerEditPanelTest extends AbstractRMTWicketMockitoTests {

    @Test
    public void testRender() {
        User user = TestData.newUser();
        wicketTester.startComponentInPage(new PlayerEditPanel(Model.of(user)));
        wicketTester.dumpComponentWithPage();
    }

}