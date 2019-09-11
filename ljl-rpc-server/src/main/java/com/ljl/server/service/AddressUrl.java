package com.ljl.server.service;

/**
 * AddressUrl entity. @author MyEclipse Persistence Tools
 */

public class AddressUrl implements java.io.Serializable {

	// Fields

	private Integer aid;
	private String url;
	private String code;
	private String address;
	private String remark;
	private Integer pid;
	private Integer isleaf;

	// Constructors

	/** default constructor */
	public AddressUrl() {
	}

	/** minimal constructor */
	public AddressUrl(Integer aid) {
		this.aid = aid;
	}

	/** full constructor */
	public AddressUrl(Integer aid, String url, String code, String address,
			String remark) {
		this.aid = aid;
		this.url = url;
		this.code = code;
		this.address = address;
		this.remark = remark;
	}

	// Property accessors

	public Integer getAid() {
		return this.aid;
	}

	public void setAid(Integer aid) {
		this.aid = aid;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getPid() {
		return pid;
	}

	public void setPid(Integer pid) {
		this.pid = pid;
	}

	public Integer getIsleaf() {
		return isleaf;
	}

	public void setIsleaf(Integer isleaf) {
		this.isleaf = isleaf;
	}

}