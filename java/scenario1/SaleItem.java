import java.util.Map;
import java.util.HashMap;

public class SaleItem {
  private Map<Product, Integer> items = new HashMap<>();

  public Map<Product, Integer> getItems() {
    return items;
  }

}
