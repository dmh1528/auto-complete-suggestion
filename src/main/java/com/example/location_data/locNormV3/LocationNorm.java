package com.example.location_data.locNormV3;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class LocationNorm {
	@JsonIgnore
	private String cityId;
	@JsonIgnore
	private String stateId;
	@JsonIgnore
	private String country;
	@JsonIgnore
	private String taggedFrom;

	public String getCity() {
		return cityId;
	}

	public void setCity(String cityId) {
		this.cityId = cityId;
	}

	public String getState() {
		return stateId;
	}

	public void setState(String state) {
		this.stateId = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getTaggedFrom() {
		return taggedFrom;
	}

	public void setTaggedFrom(String taggedFrom) {
		this.taggedFrom = taggedFrom;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cityId == null) ? 0 : cityId.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((stateId == null) ? 0 : stateId.hashCode());
		result = prime * result + ((taggedFrom == null) ? 0 : taggedFrom.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LocationNorm other = (LocationNorm) obj;
		if (cityId == null) {
			if (other.cityId != null)
				return false;
		} else if (!cityId.equals(other.cityId))
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (stateId == null) {
			if (other.stateId != null)
				return false;
		} else if (!stateId.equals(other.stateId))
			return false;
		if (taggedFrom == null) {
			if (other.taggedFrom != null)
				return false;
		} else if (!taggedFrom.equals(other.taggedFrom))
			return false;
		return true;
	}

}
