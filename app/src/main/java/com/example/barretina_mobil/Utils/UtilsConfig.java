package com.example.barretina_mobil.Utils;

import android.content.Context;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.File;
import org.w3c.dom.Node;



public class UtilsConfig {

    private static final String MAX_CONFIG_VERSION = "1.0";

    public static void deleteConfig(Context context) {
        File file = new File(context.getFilesDir(), "config.xml");
        if (file.exists()) {
            file.delete();
        }
    }

    public static boolean configExists(Context context) {
        File file = new File(context.getFilesDir(), "config.xml");
        return file.exists();
    }

    public static void saveConfig(Context context,URI serverUrl, String place) {
        Document doc = getDocument();
        doc = construirDocument(doc, serverUrl, place);
        FileOutputStream outputStream = null;
        try {
            outputStream = context.openFileOutput("config.xml", Context.MODE_PRIVATE);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            try {
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(outputStream);
                transformer.transform(source, result);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Error al guardar el document XML", e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al guardar el document XML", e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static Document construirDocument(Document doc, URI serverUrl, String place) {
        Element root = doc.createElement("config");
        root.setAttribute("version", MAX_CONFIG_VERSION);
        Element serverUrlElement = doc.createElement("serverUrl");
        serverUrlElement.appendChild(doc.createTextNode(serverUrl.toString()));
        root.appendChild(serverUrlElement);
        Element placeElement = doc.createElement("place");
        placeElement.appendChild(doc.createTextNode(place));
        root.appendChild(placeElement);
        doc.appendChild(root);
        return doc;
    }

    private static Document getDocument() {
       DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
       try {
           DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
           Document doc = dBuilder.newDocument();
           return doc; 
       } catch (Exception e) {
           throw new RuntimeException("Error al crear el document XML", e);
       }
    }

    public static Config getConfig(Context context) {
        // Read config.xml
        Node rootNode = readConfig(context);
        String version = rootNode.getAttributes().getNamedItem("version").getNodeValue();
        switch (version) {
            case "1.0":
                return getConfig1_0(rootNode);
            default:
                throw new RuntimeException("Version de config no soportada: " + version + " latest version supported: " + MAX_CONFIG_VERSION);
        }
    }

    private static Config getConfig1_0(Node rootNode) {
        Element rootElement = (Element) rootNode;
        Config config = new Config();
        Node serverUrlNode = rootElement.getElementsByTagName("serverUrl").item(0); 
        Node placeNode = rootElement.getElementsByTagName("place").item(0);
        try {
            config.setServerUrl(new URI(serverUrlNode.getTextContent()));
            config.setPlace(placeNode.getTextContent());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error al leer el document XML, serverUrl is not a valid URI, version: 1.0", e);
        }
        return config;
    }

    private static Node readConfig(Context context) {
        File configFile = new File(context.getFilesDir(), "config.xml");
        // Read config.xml
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(configFile);
            doc.getDocumentElement().normalize();
            return doc.getElementsByTagName("config").item(0);
        } catch (Exception e) {
            throw new RuntimeException("Error al leer el document XML", e);
        }
    }
}
