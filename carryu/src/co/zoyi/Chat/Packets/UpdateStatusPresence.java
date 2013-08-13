package co.zoyi.Chat.Packets;

import org.jivesoftware.smack.packet.Presence;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.io.StringWriter;

public class UpdateStatusPresence extends Presence {
    private String defaultOnlineStatusMessage;

    public UpdateStatusPresence(Presence presence, String defaultOnlineStatusMessage) {
        super(presence.getType());

        this.defaultOnlineStatusMessage = defaultOnlineStatusMessage;
        setMode(presence.getMode());
        setStatus(presence.getStatus());
    }

    @Override
    public void setStatus(String s) {
        super.setStatus(s);
        InputSource is = new InputSource(new StringReader(getStatus()));
        Document document = null;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            XPath xpath = XPathFactory.newInstance().newXPath();
            String xPathStr = "//body/statusMsg";
            NodeList nodeList = (NodeList) xpath.evaluate(xPathStr, document, XPathConstants.NODESET);
            Node node = nodeList.item(0);
            if ( node.getTextContent() == null || node.getTextContent().equals("") ) {
                node.setTextContent(defaultOnlineStatusMessage);
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                StreamResult result = new StreamResult(new StringWriter());
                transformer.transform(new DOMSource(document), result);
                setStatus(result.getWriter().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
