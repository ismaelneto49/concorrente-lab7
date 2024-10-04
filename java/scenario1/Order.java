public class Order {
    private String id; 
    private OrderStatus status;
    private SaleItem saleItem;
    private String clientId;

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

    public void setSaleItem(SaleItem saleItem) {
        this.saleItem = saleItem;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    public String getClientId() {
        return clientId;
    }
}
