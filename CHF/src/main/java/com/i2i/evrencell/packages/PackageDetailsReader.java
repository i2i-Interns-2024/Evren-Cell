package com.i2i.evrencell.packages;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class PackageDetailsReader {
    private static final Logger logger = Logger.getLogger(PackageDetailsReader.class.getName());
    private static final String XML_FILE_PATH = "CHF/src/main/resources/package_details.xml";
    private final Map<Integer, PackageDetails> packageDetailsByIdMap = new HashMap<>();

    public PackageDetailsReader() {
        loadPackageDetails();
    }

    private void loadPackageDetails() {
        try {
            File xmlFile = new File(XML_FILE_PATH);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(xmlFile);
            document.getDocumentElement().normalize();

            NodeList nodeList = document.getElementsByTagName("package");

            for (int temp = 0; temp < nodeList.getLength(); temp++) {
                Node node = nodeList.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    int packageId = Integer.parseInt(element.getElementsByTagName("packageId").item(0).getTextContent());
                    String packageName = element.getElementsByTagName("packageName").item(0).getTextContent();
                    double price = Double.parseDouble(element.getElementsByTagName("price").item(0).getTextContent());
                    int amountMinutes = Integer.parseInt(element.getElementsByTagName("amountMinutes").item(0).getTextContent());
                    int amountData = Integer.parseInt(element.getElementsByTagName("amountData").item(0).getTextContent());
                    int amountSms = Integer.parseInt(element.getElementsByTagName("amountSms").item(0).getTextContent());

                    PackageDetails packageDetails = new PackageDetails(packageId, packageName, price, amountMinutes, amountData, amountSms);
                    packageDetailsByIdMap.put(packageId, packageDetails);
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while reading package details: " + e.getMessage());
        }
    }

    public PackageDetails getPackageDetailsById(int packageId) {
        return packageDetailsByIdMap.get(packageId);
    }

}
