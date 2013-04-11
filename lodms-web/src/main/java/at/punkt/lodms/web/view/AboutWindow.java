/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.punkt.lodms.web.view;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author kreisera
 */
@Component
@Scope("session")
public class AboutWindow extends Window {

    public AboutWindow() {
        super("About LOD Management Suite");
        Label aboutText = new Label("<div class=\"lodms_about\"><p>The Linked (Open) Data Manager is developed by the <a href=\"http://www.semantic-web.at\">Semantic Web Company</a> in the course of the <a href=\"http://lod2.eu\">LOD2</a> FP7 project.</p>" +
                                    "<p>It is a Java based Linked (Open) Data Management Suite to schedule and monitor required ETL: Extract - Transform - Load</br>jobs for smooth and efficient Linked (Open) Data Management for web-based Linked Open Data portals (LOD platforms)</br>as well as for sustainable Data Management and Data Integration usage inside of the enterprise / the organisation.</p>" +
                                    "<ul><li>Main Developer: Alexander Kreiser (<a href=\"mailto:akreiser@gmail.com\">akreiser@gmail.com</a>)</li>" +
                                    "<li>Contact: <a href=\"mailto:support@poolparty.biz\">support@poolparty.biz</a></li>" +
                                    "<li>Documentation: <a href=\"https://grips.semantic-web.at/display/LDM\">LOD Management Suite Documentation (LODMS)</a></li>" +
                                    "<li>Google Code: <a href=\"http://code.google.com/p/lod-ms/\">http://code.google.com/p/lod-ms/</a></li></ul>" +
                                    "<p>Release 1.0, <a href=\"http://www.apache.org/licenses/LICENSE-2.0\">Apache Licence 2.0</a></div>",
                                    Label.CONTENT_XHTML);
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.addComponent(aboutText);
        layout.setSizeUndefined();
        setContent(layout);
        center();
    }
}
