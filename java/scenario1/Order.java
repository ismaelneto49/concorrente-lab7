import java.util.Random;

public class Order {
    private String id; 
    private OrderStatus status;
    private SaleItem saleItem;

    public Order(String id) {
        this.id = id;
    }

    public boolean hasFinished() {
        return status.equals(OrderStatus.FINISHED);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SaleItem getSaleItem() {
        return saleItem;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
