package co.com.reportstools.models;

public class Audit {

	private String UNI_ID;
	private String herramienta;
	private String campo;
	private String valorAnterior;
	private String valorActual;
	private String usuario;

	public String getUNI_ID() {
		return UNI_ID;
	}

	public void setUNI_ID(String uNI_ID) {
		UNI_ID = uNI_ID;
	}

	public String getHerramienta() {
		return herramienta;
	}

	public void setHerramienta(String herramienta) {
		this.herramienta = herramienta;
	}

	public String getValorAnterior() {
		return valorAnterior;
	}

	public void setValorAnterior(String valorAnterior) {
		this.valorAnterior = valorAnterior;
	}

	public String getValorActual() {
		return valorActual;
	}

	public void setValorActual(String valorActual) {
		this.valorActual = valorActual;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getCampo() {
		return campo;
	}

	public void setCampo(String campo) {
		this.campo = campo;
	}
	
	

}
