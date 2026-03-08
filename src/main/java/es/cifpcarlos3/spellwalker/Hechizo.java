package es.cifpcarlos3.spellwalker;

public class Hechizo {
    private int id;
    private String nombre;
    private int costeAp;
    private int costeMana;
    private String tipo;
    private String velocidad;
    private int legendario;
    private int escuelaId;
    private int esFusion; // En SQLite/LibSQL el boolean se guarda a veces como 0/1

    public Hechizo(int id, String nombre, int costeAp, int costeMana, String tipo, String velocidad, int legendario, int escuelaId, int esFusion) {
        this.id = id;
        this.nombre = nombre;
        this.costeAp = costeAp;
        this.costeMana = costeMana;
        this.tipo = tipo;
        this.velocidad = velocidad;
        this.legendario = legendario;
        this.escuelaId = escuelaId;
        this.esFusion = esFusion;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public int getCosteAp() { return costeAp; }
    public int getCosteMana() { return costeMana; }
    public String getTipo() { return tipo; }
    public String getVelocidad() { return velocidad; }
    public int getLegendario() { return legendario; }
    public int getEscuelaId() { return escuelaId; }
    public int getEsFusion() { return esFusion; }

    public String getLegendarioTexto() { return legendario == 1 ? "Sí" : "No"; }
    public String getEsFusionTexto() { return esFusion == 1 ? "Sí" : "No"; }
}
