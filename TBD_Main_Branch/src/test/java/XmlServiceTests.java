import ics372.model.Shipment;
import ics372.services.XmlService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class XmlServiceTests
{
    @Test
    public void processInputFile_Test()
    {
        List<Shipment> shipments;
        XmlService xmlService = new XmlService();
        String content = "<Shipments>\n" +
                            "\t<Warehouse id=\"485\" name=\"Warehouse 120\"> \n" +
                            "\t\t<Shipment type=\"Air\" id=\"15dde\">\n" +
                            "\t\t\t<Weight unit=\"kg\">20.6</Weight>\n" +
                            "\t\t\t<ReceiptDate>1732239329</ReceiptDate>\n" +
                            "\t\t</Shipment>\n" +
                            "\t\t<Shipment type=\"Truck\" id=\"52523\">\n" +
                            "\t\t\t<Weight unit=\"kg\">73</Weight>\n" +
                            "\t\t\t<ReceiptDate>1732239329</ReceiptDate>\n" +
                            "\t\t</Shipment>\n" +
                            "\t</Warehouse>" +
                            "</Shipments>";
        String file = System.getProperty("user.dir") + "test.xml";
        try
        {
            FileUtils.writeStringToFile(new File(file), content, StandardCharsets.UTF_8);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        shipments = (List<Shipment>)xmlService.processInputFile(file);

        assertNotNull(shipments);
        assertEquals(2, shipments.size());
        assertEquals("15dde", shipments.get(0).getShipmentId());
        assertEquals("52523", shipments.get(1).getShipmentId());
    }
}
