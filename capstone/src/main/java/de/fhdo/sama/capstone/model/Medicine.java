package de.fhdo.sama.capstone.model;

public class Medicine {
	private String id;
	private String name;
	private int quantity;
	private MedicineCategory category;

	public Medicine(String id, String name, int quantity, MedicineCategory category) {
		this.id = id;
		this.name = name;
		this.quantity = quantity;
		this.category = category;
	}

	public static class Builder {
		private String id;
		private String name;
		private int quantity;
		private MedicineCategory category;

		public Builder setId(String id) {
			this.id = id;
			return this;
		}

		public Builder setName(String name) {
			this.name = name;
			return this;
		}

		public Builder setQuantity(int quantity) {
			this.quantity = quantity;
			return this;
		}

		public Builder setCategory(MedicineCategory category) {
			this.category = category;
			return this;
		}

		public Medicine build() {
			return new Medicine(id, name, quantity, category);
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public MedicineCategory getCategory() {
		return category;
	}

	public void setCategory(MedicineCategory category) {
		this.category = category;
	}
}