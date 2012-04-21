package de.flower.rmt.ui.page.about;

import de.flower.rmt.ui.page.base.AbstractBaseLayoutPage;
import de.flower.rmt.ui.page.base.AnonymousNavigationPanel;

/**
 * @author flowerrrr
 */
public class AboutPage extends AbstractBaseLayoutPage {

    public AboutPage() {
        setHeading("about.heading", "about.heading.sub");
        add(new AnonymousNavigationPanel());
        addMainPanel(new AboutPanel());
        addSecondaryPanel(new ContactPanel());
    }

    @Override
    protected boolean showAlertMessages() {
        // not nice to see alert messages on this page. confuses the user.
        return false;
    }

}