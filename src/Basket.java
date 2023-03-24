import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class Basket implements Serializable {
    protected String[] products;
    protected int[] prices;
    protected int[] productsBuy;

    public Basket(String[] products, int[] prices) {
        this.products = products;
        this.prices = prices;
        productsBuy = new int[products.length]; //кол-во купленного
    }

    public void addToCart(int productNum, int amount) {
        productsBuy[productNum] += amount;
    }

    public void printCart() {
        System.out.println();
        System.out.println("Ваша корзина:");
        int sumProducts = 0;
        for (int i = 0; i < productsBuy.length; i++) {
            sumProducts += productsBuy[i] * prices[i];
            if (productsBuy[i] > 0) {
                System.out.println(products[i] + " " + productsBuy[i] +
                        " шт. по " + prices[i] + " руб. - всего " +
                        (productsBuy[i] * prices[i]) + " руб.");
            }
        }
        System.out.println("   ---");
        System.out.println("Итого " + sumProducts + " руб.");
    }

    public void saveTxt(File file) {
        try (FileWriter fr = new FileWriter(file, false)) {
            for (String product : products) {
                fr.write(product + "@");
            }
            fr.write("\n");
            for (int price : prices) {
                fr.write(price + "@");
            }
            fr.write("\n");
            for (int productBuy : productsBuy) {
                fr.write(productBuy + "@");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Basket loadFromTxtFile(File textFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(textFile))) {
            String[] products = br.readLine().split("@");

            String[] pricesStr = br.readLine().split("@");
            int[] prices = new int[pricesStr.length];
            for (int i = 0; i < pricesStr.length; i++) {
                prices[i] = Integer.parseInt(pricesStr[i]);
            }

            Basket basket = new Basket(products, prices);
            String[] productsBuyStr = br.readLine().split("@");
            for (int i = 0; i < productsBuyStr.length; i++) {
                basket.productsBuy[i] = Integer.parseInt(productsBuyStr[i]);
            }

            return basket;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static void saveJson(File fileJson, Basket basket) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try (FileWriter writer = new FileWriter(fileJson)) {
            writer.write(gson.toJson(basket));
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Basket loadFromJsonFile(File fileJson) {
        Basket basket;
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try (FileReader reader = new FileReader(fileJson)) {
            basket = gson.fromJson(reader, Basket.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return basket;
    }
}