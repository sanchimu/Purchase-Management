package vo;

public class Product {
	String product_id;
	String product_name;
	String category;
	int price;
	String supplier_id;

	public Product() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Product(String product_id, String product_name, String category, int price, String supplier_id) {
		super();
		this.product_id = product_id;
		this.product_name = product_name;
		this.category = category;
		this.price = price;
		this.supplier_id = supplier_id;
	}

	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getSupplier_id() {
		return supplier_id;
	}

	public void setSupplier_id(String supplier_id) {
		this.supplier_id = supplier_id;
	}
}
