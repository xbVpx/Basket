import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static boolean loadBasket; //true / false
    public static String loadFromFileName; //any name
    public static String loadFromFileFormat; //json / txt
    public static boolean saveBasket; //true / false
    public static String saveToFileName; //any name
    public static String saveToFileFormat; //json / txt
    public static boolean saveLog; //true / false
    public static String saveLogFileName; //any name (format always .csv)

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        Scanner scanner = new Scanner(System.in);
        String[] products = {"Молоко", "Хлеб", "Гречневая крупа"};
        int[] prices = {60, 40, 80};
        Basket basket = new Basket(products, prices);
        ClientLog clientLog = new ClientLog();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File("shop.xml"));
        Node config = document.getDocumentElement();

        readConfigFromXml(config);

        File fileLoad = new File(loadFromFileName);
        File fileSave = new File(saveToFileName);
        File fileLog = new File(saveLogFileName);

        if (loadBasket) {
            if (fileLoad.exists()) {
                if (loadFromFileFormat.equals("json")) {
                    basket = Basket.loadFromJsonFile(fileLoad);
                } else if (loadFromFileFormat.equals("txt")) {
                    basket = Basket.loadFromTxtFile(fileLoad);
                }
                System.out.println("\nКорзина с покупками восстановлена из файла");
                basket.printCart();
            } else {
                System.out.println("\nКорзина с покупками не восстановлена!!!");
            }
        } else {
            System.out.println("\nКорзина с покупками не восстановлена!!!");
        }

        System.out.println();
        System.out.println("Список возможных товаров для покупки");
        for (int i = 0; i < products.length; i++) {
            System.out.println((i + 1) + " " + products[i] + " - " + prices[i] + " руб. за шт.");
        }

        while (true) {
            System.out.println("Выберите товар и количество или введите 'end'");
            String input = scanner.nextLine();

            if ("end".equals(input)) {
                break;
            }

            String[] parts = input.split(" ");
            int productNum = Integer.parseInt(parts[0]) - 1; // извлекаем № продукта
            int amount = Integer.parseInt(parts[1]); // извлекаем кол-во
            basket.addToCart(productNum, amount);

            if (saveLog) {
                clientLog.log(productNum, amount);
            }
        }

        if (saveBasket) {
            if (saveToFileFormat.equals("json")) {
                Basket.saveJson(fileSave, basket);
            } else if (saveToFileFormat.equals("txt")) {
                basket.saveTxt(fileSave);
            }
        }

        if (saveLog) {
            clientLog.exportAsCSV(fileLog);
        }

        basket.printCart();
    }

    public static void readConfigFromXml(Node config) {
        NodeList nodeList = config.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element element = (Element) node;

                if (element.getParentNode().getNodeName().equals("load")) {
                    if (element.getNodeName().equals("enabled")) {
                        loadBasket = element.getTextContent().equals("true");
                    } else if (element.getNodeName().equals("fileName")) {
                        loadFromFileName = element.getTextContent();
                    } else if (element.getNodeName().equals("format")) {
                        loadFromFileFormat = element.getTextContent();
                    }
                } else if (element.getParentNode().getNodeName().equals("save")) {
                    if (element.getNodeName().equals("enabled")) {
                        saveBasket = element.getTextContent().equals("true");
                    } else if (element.getNodeName().equals("fileName")) {
                        saveToFileName = element.getTextContent();
                    } else if (element.getNodeName().equals("format")) {
                        saveToFileFormat = element.getTextContent();
                    }
                } else if (element.getParentNode().getNodeName().equals("log")) {
                    if (element.getNodeName().equals("enabled")) {
                        saveLog = element.getTextContent().equals("true");
                    } else if (element.getNodeName().equals("fileName")) {
                        saveLogFileName = element.getTextContent();
                    }
                }

                readConfigFromXml(node);
            }
        }
    }
}