package vo;

public class SupplierInfo {
	String supplier_id;
	String supplier_name;
	String contact_number;
	String address;

	public SupplierInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SupplierInfo(String supplier_id, String supplier_name, String contact_number, String address) {
		super();
		this.supplier_id = supplier_id;
		this.supplier_name = supplier_name;
		this.contact_number = contact_number;
		this.address = address;
	}

	public String getSupplier_id() {
		return supplier_id;
	}

	public void setSupplier_id(String supplier_id) {
		this.supplier_id = supplier_id;
	}

	public String getSupplier_name() {
		return supplier_name;
	}

	public void setSupplier_name(String supplier_name) {
		this.supplier_name = supplier_name;
	}

	public String getContact_number() {
		return contact_number;
	}

	public void setContact_number(String contact_number) {
		this.contact_number = contact_number;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
