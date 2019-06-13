package com.dreamsecurity.ca.business.cert.vo;

public class KeyVo {
	private int id;
	private byte[] publicKeyIdentifier;
	private byte[] publicKey;
	private byte[] privateKey;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public byte[] getPublicKeyIdentifier() {
		return publicKeyIdentifier;
	}
	public void setPublicKeyIdentifier(byte[] publicKeyIdentifier) {
		this.publicKeyIdentifier = publicKeyIdentifier;
	}
	public byte[] getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(byte[] publicKey) {
		this.publicKey = publicKey;
	}
	public byte[] getPrivateKey() {
		return privateKey;
	}
	public void setPrivateKey(byte[] privateKey) {
		this.privateKey = privateKey;
	}
}
