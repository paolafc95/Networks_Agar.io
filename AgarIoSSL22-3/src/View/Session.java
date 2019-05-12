package View;

import java.awt.Color;
import java.io.Serializable;


public class Session implements Serializable {
	
	public final static int DIAMETRO_INICIAL = 40; 
	public final static int PUNTAJE_INICIAL = 0;
	public final static String ESTADO_ACTIVO = "activo";
	public final static String ESTADO_INACTIVO = "inactivo";
	public final static String PETICION_PERSONAJE = "peticion";
	public final static String ASIGNADO_PERSONAJE = "asignado";
	public final static String ACCEPT="accept";
	public final static String ERROR_PASSWORD="Error password";
	public final static String ERROR_NICK="error nick";
	public final static String VERIFICAR_LOGIN = "verificar_login";
	public final static String VERIFICAR_REGISTRO = "verificar_registro";
	public final static String ESPECT="Espect";
	
	private String id;
	private int idArreglo;
	private int diametro;//El diametro aumentara 3 puntos cada que come comida
	private int posX;
	private int posY;
	private Color color;
	private int puntaje;//Aumentará un punto cada que come comida
	private String estado;
	private int value;
	private String email;
	private String pass;
	
	public int getIdArreglo() {
		return idArreglo;
	}

	public void setIdArreglo(int idArreglo) {
		this.idArreglo = idArreglo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}
	//para login
	public Session() {
		this.estado = VERIFICAR_LOGIN;
	}
	//para crear cuenta
	public Session(String id, int posX, int posY) {
		this.id = id;
		this.posX = posX;
		this.posY = posY;
		this.puntaje = PUNTAJE_INICIAL;
		this.color = new Color(randColor(),randColor(),randColor());
		this.diametro = DIAMETRO_INICIAL;
		this.estado = ESTADO_ACTIVO;
	}
	
	public int randColor(){
		return (int)(Math.random()*256);
	}
	
	public int randPosicionX() {
		return (int)(Math.random()*(/*InterfazPrincipal.ANCHO*/ - 20));
	}
	
	public int randPosicionY() {
		return (int)(Math.random()*(/*InterfazPrincipal.LARGO*/ - 20));
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getDiametro() {
		return diametro;
	}

	public void setDiametro(int diametro) {
		this.diametro = diametro;
	}

	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getPuntaje() {
		return puntaje;
	}

	public void setPuntaje(int puntaje) {
		this.puntaje = puntaje;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	

}
