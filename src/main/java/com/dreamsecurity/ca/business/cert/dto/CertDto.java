package com.dreamsecurity.ca.business.cert.dto;

import java.security.cert.X509Certificate;

import com.dreamsecurity.ca.business.cert.vo.CertVo;

public class CertDto {
	private CertVo eeCertVo;
	private X509Certificate[] certChain;
	
	public CertVo getEeCertVo() {
		return eeCertVo;
	}
	public void setEeCertVo(CertVo eeCertVo) {
		this.eeCertVo = eeCertVo;
	}
	public X509Certificate[] getCertChain() {
		return certChain;
	}
	public void setCertChain(X509Certificate[] certChain) {
		this.certChain = certChain;
	}	
}
