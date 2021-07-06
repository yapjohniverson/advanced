package com.svi.advancedjavatraining.object;

public class PopulationData {
	private String province;
	private String city;
	private float population;

	public PopulationData(String province, String city, float population) {
		super();
		this.province = province;
		this.city = city;
		this.population = population;
	}

	public String getProvince() {
		return province;
	}

	public String getCity() {
		return city;
	}

	public float getPopulation() {
		return population;
	}

}
