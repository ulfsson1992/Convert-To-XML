import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * Created by Hugo on 2017-05-18.
 *
 */

public class ConvertToXML {

    DocumentBuilderFactory dbFactory;
    DocumentBuilder dBuilder;
    Document doc;

    public void convertFileToXML(File file, String xmlName) {

        try {
            // Load a buffered reader with data from the commandline
            BufferedReader buf = new BufferedReader(new FileReader(file));

            // Initialize xml file
            dbFactory = DocumentBuilderFactory.newInstance();
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.newDocument();

            // Create root element
            Element root = doc.createElement("people");
            doc.appendChild(root);

            // Parse the document
            String line;
            Element currentPerson = null;
            while((line = buf.readLine()) != null) {
                String[] split = line.split("[|]");

                switch (split[0].toCharArray()[0]) {
                    case 'P':   // New person
                        Element person = createNode("person", new String[]{"firstname", "lastname"}, split);
                        root.appendChild(person);

                        currentPerson = person;
                        break;
                    case 'F':   // Family
                        // If the parent node of the current person is not the root node it means that we are in another
                        // family node and must reset to the correct parent node
                        if(currentPerson.getParentNode() != root)
                            currentPerson = (Element) currentPerson.getParentNode();

                        // For every attribute, make sure there exists a parent node
                        if (currentPerson != null) {
                            Element family = createNode("family", new String[]{"name", "born"}, split);
                            currentPerson.appendChild(family);

                            currentPerson = family;
                        }
                        break;
                    case 'T':   // Telephone
                        if (currentPerson != null) {
                            Element phone = createNode("phone", new String[]{"mobile", "home"}, split);
                            currentPerson.appendChild(phone);
                        }
                        break;
                    case 'A':   // Address
                        if (currentPerson != null) {
                            Element adress = createNode("adress", new String[]{"street", "city", "zipcode"}, split);
                            currentPerson.appendChild(adress);
                        }
                        break;
                    default:

                }
            }

            // Save the document object model to file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(xmlName + ".xml"));
            transformer.transform(source, result);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private Element createNode(String name, String[] labels, String[] values) {
        Element node = null;
        if(labels.length == values.length-1) {
            node = doc.createElement(name);

            for(int i = 1; i < values.length; i++) {
                Element e = doc.createElement(labels[i-1]);
                e.appendChild(doc.createTextNode(values[i]));
                node.appendChild(e);
            }
        } else {
            throw new ArrayIndexOutOfBoundsException("Incorrect amount of parameters following " + values[0]);
        }
        return node;
    }
}
