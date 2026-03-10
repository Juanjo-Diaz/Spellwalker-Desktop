package com.spellwalker.spellwalker_desktop;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
public class ConexionApi {
  private static final String PROXY_URL = "https://backendspellwalker.onrender.com/api/query";

  public static String postToTurso(String jsonPayload) throws IOException {
    try {
      HttpClient client = HttpClient.newHttpClient();

      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(PROXY_URL))
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
          .build();

      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        return response.body();
      } else {
        System.err.println("Error en el Proxy: " + response.statusCode() + " - " + response.body());
        return response.body() != null ? response.body() : "";
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("Petición interrumpida", e);
    } catch (Exception e) {
      throw new IOException("Error en la comunicación con el proxy", e);
    }
  }

  public static void inicializarBaseDeDatos() {
    try {
      String payload = """
          {
            "requests": [
              {
                "type": "execute",
                "stmt": {
                  "sql": "ALTER TABLE PERSONAJE ADD COLUMN DESCRIPCION TEXT"
                }
              },
              { "type": "close" }
            ]
          }
          """;
      String resp = postToTurso(payload);
      System.out.println("Resultado de inicializar base de datos: " + resp);
    } catch (Exception e) {
      System.out.println("La columna DESCRIPCION probablemente ya existe o hubo un error: " + e.getMessage());
    }
  }

  public static boolean eliminarPersonaje(int idPersonaje) throws IOException {
    String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "DELETE FROM PERSONAJE_SPELLS WHERE ID_PERS = ?",
                "args": [{"type": "integer", "value": "%d"}]
              }
            },
            {
              "type": "execute",
              "stmt": {
                "sql": "DELETE FROM PERSONAJE_ESCUELAS WHERE ID_PERSONAJE = ?",
                "args": [{"type": "integer", "value": "%d"}]
              }
            },
            {
              "type": "execute",
              "stmt": {
                "sql": "DELETE FROM PERSONAJE WHERE PERSONAJE_ID = ?",
                "args": [{"type": "integer", "value": "%d"}]
              }
            },
            { "type": "close" }
          ]
        }
        """.formatted(idPersonaje, idPersonaje, idPersonaje);

    String resp = postToTurso(payload);
    System.out.println("DELETE PERSONAJE RESP: " + resp);
    return !resp.contains("\"type\":\"error\"");
  }

  public static String generarHash(String usuario, String password) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA1");

      StringBuilder sb = new StringBuilder();
      sb.append(usuario);
      sb.append(password);

      md.update(sb.toString().getBytes());
      byte[] pass = md.digest();

      Base64.Encoder encoder = Base64.getEncoder();
      return encoder.encodeToString(pass);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static boolean usuarioExiste(String username) {
    try {
      String payload = """
          {
            "requests": [
              {
                "type": "execute",
                "stmt": {
                  "sql": "SELECT NOMBRE_USUARIO FROM PERFIL WHERE NOMBRE_USUARIO = ?",
                  "args": [{"type": "text", "value": "%s"}]
                }
              },
              { "type": "close" }
            ]
          }
          """.formatted(username);

      String resp = postToTurso(payload);

      return resp.contains("\"rows\":[[");

    } catch (Exception e) {
      System.out.println(e.getMessage());
      return false;
    }
  }

  public static boolean mailExiste(String mail) {
    try {
      String payload = """
          {
            "requests": [
              {
                "type": "execute",
                "stmt": {
                  "sql": "SELECT MAIL FROM PERFIL WHERE MAIL = ?",
                  "args": [{"type": "text", "value": "%s"}]
                }
              },
              { "type": "close" }
            ]
          }
          """.formatted(mail);

      String resp = postToTurso(payload);

      return resp.contains("\"rows\":[[");

    } catch (Exception e) {
      System.out.println(e.getMessage());
      return false;
    }
  }

  public static void registerPerfil(String username, String password, String mail) {
    try {
      if (usuarioExiste(username)) {
        System.out.println("El usuario ya existe");
      }

      if (mailExiste(mail)) {
        System.out.println("El correo ya está registrado");
      }

      String hash = generarHash(username, password);

      String payload = """
          {
            "requests": [
              {
                "type": "execute",
                "stmt": {
                  "sql": "INSERT INTO PERFIL (NOMBRE_USUARIO, CONTRASENYA, MAIL, NOTIFICACIONES) VALUES (?, ?, ?, 1)",
                  "args": [
                    { "type": "text", "value": "%s" },
                    { "type": "text", "value": "%s" },
                    { "type": "text", "value": "%s" }
                  ]
                }
              },
              { "type": "close" }
            ]
          }
          """.formatted(username, hash, mail);

      System.out.println("Payload enviado: " + payload);
      String resp = postToTurso(payload);
      System.out.println("Respuesta del servidor: " + resp);

      if (resp.contains("error")) {
        System.out.println("Error al registrar usuario: " + resp);
      }

      System.out.println("Usuario registrado correctamente");

    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  public static int extraerNumero(String json) {
    try {
      int valueIdx = json.indexOf("\"value\":\"");
      int start;
      if (valueIdx != -1) {
        start = valueIdx + 9;
      } else {
        valueIdx = json.indexOf("\"value\":");
        if (valueIdx == -1)
          return -1;
        start = valueIdx + 8;
      }

      int end = json.indexOf("}", start);
      if (end == -1)
        end = json.indexOf("]", start);
      if (end == -1)
        return -1;

      String valStr = json.substring(start, end).replace("\"", "").trim();
      if (valStr.equals("null") || valStr.isEmpty())
        return -1;
      return Integer.parseInt(valStr);
    } catch (Exception e) {
      return -1;
    }
  }

  public static int obtenersiguienteIdPersonaje() throws IOException {

    String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "SELECT MAX(PERSONAJE_ID) + 1 FROM PERSONAJE"
              }
            },
            { "type": "close" }
          ]
        }
        """;

    String resp = postToTurso(payload);

    return extraerNumero(resp);
  }

  public static int obtenerIdSpellPorNombre(String nombreSpell) throws IOException {

    String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "SELECT ID_SPELL FROM SPELLS WHERE NOMBRE = ?",
                "args": [{"type": "text", "value": "%s"}]
              }
            },
            { "type": "close" }
          ]
        }
        """.formatted(nombreSpell);

    String resp = postToTurso(payload);

    return extraerNumero(resp);
  }

  public static int obtenerIdPersonajePorNombre(String nombrePersonaje) throws IOException {

    String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "SELECT PERSONAJE_ID FROM PERSONAJE WHERE NOMBRE_PERSONAJE = ?",
                "args": [{"type": "text", "value": "%s"}]
              }
            },
            { "type": "close" }
          ]
        }
        """.formatted(nombrePersonaje);

    String resp = postToTurso(payload);

    return extraerNumero(resp);
  }

  public static boolean insertarSpellAPersonaje(String nombrePersonaje, String nombreSpell) throws IOException {

    int idPersonaje = obtenerIdPersonajePorNombre(nombrePersonaje);
    if (idPersonaje == -1) {
      System.out.println("No existe el personaje: " + nombrePersonaje);
      return false;
    }

    int idSpell = obtenerIdSpellPorNombre(nombreSpell);
    if (idSpell == -1) {
      System.out.println("No existe el spell: " + nombreSpell);
      return false;
    }

    String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "INSERT INTO PERSONAJE_SPELLS (ID_SPELL, ID_PERS) VALUES (?, ?)",
                "args": [{"type": "integer", "value": "%d"}, {"type": "integer", "value": "%d"}]
              }
            },
            { "type": "close" }
          ]
        }
        """.formatted(idSpell, idPersonaje);

    String resp = postToTurso(payload);
    System.out.println("INSERT SPELL RESP: " + resp);
    return !resp.contains("\"type\":\"error\"");
  }

  public static int obtenerIdCampanaPorNombre(String nombreCampana) throws IOException {

    String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "SELECT ID_CAMPANA FROM CAMPANA WHERE NOMBRE = ?",
                "args": [{"type": "text", "value": "%s"}]
              }
            },
            { "type": "close" }
          ]
        }
        """.formatted(nombreCampana);

    String resp = postToTurso(payload);

    return extraerNumero(resp);
  }

  public static boolean crearPersonajeConNombreYCampana(String nombrePersonaje, String nombreCampana, String perfil) {

    try {
      int idCampana = obtenerIdCampanaPorNombre(nombreCampana);

      if (idCampana == -1) {
        System.out.println("La campaña '" + nombreCampana + "' no existe. Debe crearse antes.");
        return false;
      }

      int nuevoIdPersonaje = obtenersiguienteIdPersonaje();

      String payload = """
          {
            "requests": [
              {
                "type": "execute",
                "stmt": {
                  "sql": "INSERT INTO PERSONAJE (PERSONAJE_ID, NOMBRE_PERSONAJE, ID_CAMPANA, PERSONAJE_PERFIL) VALUES (?, ?, ?, ?)",
                  "args": [
                    { "type": "integer", "value": "%d" },
                    { "type": "text", "value": "%s" },
                    { "type": "integer", "value": "%d" },
                    { "type": "text", "value": "%s" }
                  ]
                }
              },
              { "type": "close" }
            ]
          }
          """
          .formatted(nuevoIdPersonaje, nombrePersonaje, idCampana, perfil);

      postToTurso(payload);

      System.out.println("Personaje creado correctamente con ID: " + nuevoIdPersonaje);
      return true;

    } catch (Exception e) {
      System.out.println(e.getMessage());
      return false;
    }
  }

  public static boolean login(String username, String password) {
    try {
      String hash = generarHash(username, password);

      String payload = """
          {
            "requests": [
              {
                "type": "execute",
                "stmt": {
                  "sql": "SELECT NOMBRE_USUARIO FROM PERFIL WHERE NOMBRE_USUARIO = ? AND CONTRASENYA = ?",
                  "args": [
                    { "type": "text", "value": "%s" },
                    { "type": "text", "value": "%s" }
                  ]
                }
              },
              { "type": "close" }
            ]
          }
          """.formatted(username, hash);

      String resp = postToTurso(payload);

      return resp.contains("\"rows\":[[");

    } catch (Exception e) {
      System.out.println(e.getMessage());
      return false;
    }
  }

  public static int obtenerIdEscuelaPorNombre(String nombreEscuela) throws IOException {

    String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "SELECT ID_ESCUELAS FROM ESCUELAS WHERE NOMBRE = ?",
                "args": [{"type": "text", "value": "%s"}]
              }
            },
            { "type": "close" }
          ]
        }
        """.formatted(nombreEscuela);

    String resp = postToTurso(payload);

    return extraerNumero(resp);
  }

  public static java.util.List<String> obtenerTodasLasEscuelas() throws IOException {
    String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "SELECT NOMBRE FROM ESCUELAS"
              }
            },
            { "type": "close" }
          ]
        }
        """;

    String resp = postToTurso(payload);
    return extraerNombresDeJson(resp);
  }

  private static List<String> extraerNombresDeJson(String json) {
    List<String> nombres = new ArrayList<>();
    try {
      int rowsIndex = json.indexOf("\"rows\":[");
      if (rowsIndex == -1)
        return nombres;

      String rowsContent = json.substring(rowsIndex + 8);

      String[] parts = rowsContent.split("\\],\\[");
      for (String part : parts) {
        int valueIndex = part.indexOf("\"value\":\"");
        if (valueIndex != -1) {
          int startQuote = valueIndex + 9;
          int nextQuote = part.indexOf("\"", startQuote);
          if (nextQuote != -1) {
            nombres.add(part.substring(startQuote, nextQuote));
          }
        }
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return nombres;
  }

  public static boolean vincularPersonajeAEscuela(int idPersonaje, int idEscuela) throws IOException {
    String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "INSERT INTO PERSONAJE_ESCUELAS (ID_PERSONAJE, ID_ESCUELA) VALUES (?, ?)",
                "args": [{"type": "integer", "value": "%d"}, {"type": "integer", "value": "%d"}]
              }
            },
            { "type": "close" }
          ]
        }
        """.formatted(idPersonaje, idEscuela);

    postToTurso(payload);
    return true;
  }

  public static List<PersonajesId> obtenerPersonajesDeUsuario(String usuario) throws IOException {
    String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "SELECT PERSONAJE_ID, NOMBRE_PERSONAJE, ID_CAMPANA, DESCRIPCION FROM PERSONAJE WHERE PERSONAJE_PERFIL = ?",
                "args": [{"type": "text", "value": "%s"}]
              }
            },
            { "type": "close" }
          ]
        }
        """
        .formatted(usuario);

    String resp = postToTurso(payload);

    List<PersonajesId> lista = new ArrayList<>();
    List<List<String>> rows = extractAllRows(resp);

    for (List<String> row : rows) {
      if (row.size() >= 3) {
        String id = row.get(0);
        String nombre = row.get(1);
        String idCampana = row.get(2);
        String descripcion = (row.size() >= 4 && row.get(3) != null) ? row.get(3) : "";
        lista.add(new PersonajesId(id, nombre, idCampana, descripcion));
      }
    }

    return lista;
  }

  public static String obtenerNombreCampanaPorId(int id) throws IOException {
    String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "SELECT NOMBRE FROM CAMPANA WHERE ID_CAMPANA = ?",
                "args": [{"type": "integer", "value": "%d"}]
              }
            },
            { "type": "close" }
          ]
        }
        """.formatted(id);

    String resp = postToTurso(payload);

    int start = resp.indexOf("\"value\":\"") + 9;
    int end = resp.indexOf("\"", start);

    return resp.substring(start, end);
  }

  public static List<String> obtenerSpellsDePersonaje(int idPersonaje) throws IOException {

    String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "SELECT S.NOMBRE FROM SPELLS S JOIN PERSONAJE_SPELLS PS ON S.ID_SPELL = PS.ID_SPELL WHERE PS.ID_PERS = ?",
                "args": [{"type": "integer", "value": "%d"}]
              }
            },
            { "type": "close" }
          ]
        }
        """
        .formatted(idPersonaje);

    String resp = postToTurso(payload);

    List<String> spells = new ArrayList<>();

    int index = 0;
    while (true) {
      int start = resp.indexOf("\"value\":\"", index);
      if (start == -1)
        break;

      start += 9;
      int end = resp.indexOf("\"", start);
      spells.add(resp.substring(start, end));

      index = end + 1;
    }

    return spells;
  }


  public static List<String> obtenerTodosNombresHechizos() throws IOException {
    String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "SELECT NOMBRE FROM SPELLS"
              }
            },
            { "type": "close" }
          ]
        }
        """;

    String resp = postToTurso(payload);
    return extraerNombresDeJson(resp);
  }

  public static List<String> obtenerEscuelasDePersonaje(int idPersonaje) throws IOException {
    String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "SELECT E.NOMBRE FROM ESCUELAS E JOIN PERSONAJE_ESCUELAS PE ON E.ID_ESCUELAS = PE.ID_ESCUELA WHERE PE.ID_PERSONAJE = ?",
                "args": [{"type": "integer", "value": "%d"}]
              }
            },
            { "type": "close" }
          ]
        }
        """
        .formatted(idPersonaje);

    String resp = postToTurso(payload);
    return extraerNombresDeJson(resp);
  }

  public static String obtenerDescripcionPersonaje(int idPersonaje) throws IOException {
    String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "SELECT DESCRIPCION FROM PERSONAJE WHERE PERSONAJE_ID = ?",
                "args": [{"type": "integer", "value": "%d"}]
              }
            },
            { "type": "close" }
          ]
        }
        """.formatted(idPersonaje);

    String resp = postToTurso(payload);
    try {
      int start = resp.indexOf("\"value\":\"");
      if (start == -1)
        return "";
      start += 9;
      int end = resp.indexOf("\"", start);
      return resp.substring(start, end);
    } catch (Exception e) {
      return "";
    }
  }

  public static void actualizarDescripcionPersonaje(int idPersonaje, String descripcion) throws IOException {
    String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "UPDATE PERSONAJE SET DESCRIPCION = ? WHERE PERSONAJE_ID = ?",
                "args": [
                  {"type": "text", "value": "%s"},
                  {"type": "integer", "value": "%d"}
                ]
              }
            },
            { "type": "close" }
          ]
        }
        """.formatted(descripcion, idPersonaje);

    postToTurso(payload);
  }

  public static void desvincularEscuelaDePersonaje(int idPersonaje, int idEscuela) throws IOException {
    String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "DELETE FROM PERSONAJE_ESCUELAS WHERE ID_PERSONAJE = ? AND ID_ESCUELA = ?",
                "args": [{"type": "integer", "value": "%d"}, {"type": "integer", "value": "%d"}]
              }
            },
            { "type": "close" }
          ]
        }
        """.formatted(idPersonaje, idEscuela);

    postToTurso(payload);
  }

  public static void vincularHechizoAPersonaje(int idPersonaje, int idHechizo) throws IOException {
    String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "INSERT INTO PERSONAJE_SPELLS (ID_PERS, ID_SPELL) VALUES (?, ?)",
                "args": [{"type": "integer", "value": "%d"}, {"type": "integer", "value": "%d"}]
              }
            },
            { "type": "close" }
          ]
        }
        """.formatted(idPersonaje, idHechizo);

    postToTurso(payload);
  }

  public static void desvincularHechizoDePersonaje(int idPersonaje, int idHechizo) throws IOException {
    String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "DELETE FROM PERSONAJE_SPELLS WHERE ID_PERS = ? AND ID_SPELL = ?",
                "args": [{"type": "integer", "value": "%d"}, {"type": "integer", "value": "%d"}]
              }
            },
            { "type": "close" }
          ]
        }
        """.formatted(idPersonaje, idHechizo);

    postToTurso(payload);
  }

  public static boolean crearNuevaCampana(String nombre, String descripcion, String usuarioCreador) throws IOException {
    String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "INSERT INTO CAMPANA (NOMBRE, DESCRIPCION) VALUES (?, ?)",
                "args": [{"type": "text", "value": "%s"}, {"type": "text", "value": "%s"}]
              }
            },
            { "type": "close" }
          ]
        }
        """.formatted(nombre, descripcion);

    String resp = postToTurso(payload);

    if (resp.contains("\"affected_row_count\":1")) {
      int idCampana = obtenerIdCampanaPorNombre(nombre);
      if (idCampana != -1) {
        return vincularPerfilCampana(usuarioCreador, idCampana);
      }
    }
    return false;
  }

  public static boolean vincularPerfilCampana(String usuario, int idCampana) throws IOException {
    String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "INSERT INTO PERFIL_CAMPANA (ID_CAMPANA, NOMBRE_USUARIO) VALUES (?, ?)",
                "args": [{"type": "integer", "value": "%d"}, {"type": "text", "value": "%s"}]
              }
            },
            { "type": "close" }
          ]
        }
        """.formatted(idCampana, usuario);

    String resp = postToTurso(payload);
    return resp.contains("\"affected_row_count\":1");
  }

  public static List<Campana> obtenerCampanasDeUsuarioObjeto(String usuario) throws IOException {
    String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "SELECT C.ID_CAMPANA, C.NOMBRE, C.DESCRIPCION FROM CAMPANA C JOIN PERFIL_CAMPANA PC ON C.ID_CAMPANA = PC.ID_CAMPANA WHERE PC.NOMBRE_USUARIO = ?",
                "args": [{"type": "text", "value": "%s"}]
              }
            },
            { "type": "close" }
          ]
        }
        """
        .formatted(usuario);

    String resp = postToTurso(payload);
    List<Campana> campanas = new ArrayList<>();
    List<List<String>> rows = extractAllRows(resp);
    for (List<String> row : rows) {
      if (row.size() >= 2) {
        int id = parseSafeInt(row.get(0), -1);
        String nombre = row.get(1);
        String descripcion = (row.size() >= 3 && row.get(2) != null) ? row.get(2) : "";
        if (id != -1) {
          campanas.add(new Campana(id, nombre, descripcion));
        }
      }
    }
    return campanas;
  }

  public static List<String> obtenerCampanasDeUsuarioStr(String usuario) throws IOException {
    List<Campana> campanas = obtenerCampanasDeUsuarioObjeto(usuario);
    List<String> nombres = new ArrayList<>();
    for (Campana c : campanas) {
      nombres.add(c.getNombre());
    }
    return nombres;
  }

  public static List<Hechizo> obtenerHechizosDePersonaje(int idPersonaje) throws IOException {
    String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "SELECT S.ID_SPELL, S.NOMBRE, S.COSTE_AP, S.COSTE_MANA, S.TIPO, S.VELOCIDAD, S.LEGENDARIO, S.ESCUELA_ID, S.\\" ES_FUSION\\" FROM SPELLS S JOIN PERSONAJE_SPELLS PS ON S.ID_SPELL = PS.ID_SPELL WHERE PS.ID_PERS = ?",
                "args": [{"type": "integer", "value": "%d"}]
              }
            },
            { "type": "close" }
          ]
        }
        """
        .formatted(idPersonaje);

    String resp = postToTurso(payload);
    return extraerHechizosDeJson(resp);
  }

  public static Hechizo obtenerHechizoPorNombre(String nombre) throws IOException {
    String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "SELECT ID_SPELL, NOMBRE, COSTE_AP, COSTE_MANA, TIPO, VELOCIDAD, LEGENDARIO, ESCUELA_ID, \\" ES_FUSION\\" FROM SPELLS WHERE NOMBRE = ?",
                "args": [{"type": "text", "value": "%s"}]
              }
            },
            { "type": "close" }
          ]
        }
        """
        .formatted(nombre);

    String resp = postToTurso(payload);
    List<Hechizo> lista = extraerHechizosDeJson(resp);
    return lista.isEmpty() ? null : lista.getFirst();
  }

  private static List<Hechizo> extraerHechizosDeJson(String json) {
    List<Hechizo> hechizos = new ArrayList<>();
    List<List<String>> rows = extractAllRows(json);

    for (List<String> values : rows) {
      if (values.size() >= 9) {
        hechizos.add(new Hechizo(
            parseSafeInt(values.get(0), 0),
            values.get(1),
            parseSafeInt(values.get(2), 0),
            parseSafeInt(values.get(3), 0),
            values.get(4),
            values.get(5),
            parseSafeInt(values.get(6), 0),
            parseSafeInt(values.get(7), 0),
            parseSafeInt(values.get(8), 0)));
      }
    }
    return hechizos;
  }

  private static int parseSafeInt(String val, int def) {
    try {
      if (val == null || val.equals("null") || val.isEmpty())
        return def;
      return Integer.parseInt(val);
    } catch (NumberFormatException e) {
      return def;
    }
  }

  public static boolean crearHechizo(String nombre, int costeAp, int costeMana, String tipo, String velocidad,
      int legendario, int escuelaId, int esFusion) throws IOException {
    String payload = String.format(
        """
            {
              "requests": [
                {
                  "type": "execute",
                  "stmt": {
                    "sql": "INSERT INTO SPELLS (NOMBRE, COSTE_AP, COSTE_MANA, TIPO, VELOCIDAD, LEGENDARIO, ESCUELA_ID, \\" ES_FUSION\\") VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    "args": [
                      {"type": "text", "value": "%s"},
                      {"type": "integer", "value": "%d"},
                      {"type": "integer", "value": "%d"},
                      {"type": "text", "value": "%s"},
                      {"type": "text", "value": "%s"},
                      {"type": "integer", "value": "%d"},
                      {"type": "integer", "value": "%d"},
                      {"type": "integer", "value": "%d"}
                    ]
                  }
                },
                { "type": "close" }
              ]
            }
            """,
        nombre, costeAp, costeMana, tipo, velocidad, legendario, escuelaId, esFusion);
    String resp = postToTurso(payload);
    return resp.contains("\"affected_row_count\":1");
  }

  public static void main(String[] args) {
    inicializarBaseDeDatos();
    try {
      debugPersonajes();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  public static void debugPersonajes() throws IOException {
    String payload = """
        {
          "requests": [
            {
              "type": "execute",
              "stmt": {
                "sql": "SELECT * FROM PERSONAJE"
              }
            },
            { "type": "close" }
          ]
        }
        """;
    String resp = postToTurso(payload);
    System.out.println("DEBUG ALL PERSONAJES = " + resp);
  }

  private static List<List<String>> extractAllRows(String json) {
    List<List<String>> result = new ArrayList<>();
    int rowsIdx = json.indexOf("\"rows\":[");
    if (rowsIdx == -1)
      return result;

    String content = json.substring(rowsIdx + 8);
    String[] rows = content.split("\\],\\s*\\[");

    for (String row : rows) {
      if (row.contains("\"rows\":[]"))
        continue;
      result.add(extractRowValues(row));
    }
    return result;
  }

  private static List<String> extractRowValues(String row) {
    List<String> values = new ArrayList<>();
    String cleanRow = row.trim();
    while (cleanRow.startsWith("["))
      cleanRow = cleanRow.substring(1);

    while (cleanRow.endsWith("]") || cleanRow.endsWith("}") || cleanRow.endsWith(",")) {
      if (cleanRow.endsWith("]"))
        cleanRow = cleanRow.substring(0, cleanRow.length() - 1);
      else if (cleanRow.endsWith("}"))
        break;
      else
        cleanRow = cleanRow.substring(0, cleanRow.length() - 1);
    }

    String[] cols = cleanRow.split("},\\s*\\{");
    for (String col : cols) {
      if (col.contains("\"type\":\"null\"")) {
        values.add(null);
      } else {
        int vIdx = col.indexOf("\"value\":");
        if (vIdx != -1) {
          int start = vIdx + 8;
          if (start < col.length() && col.charAt(start) == '\"') {
            start++;
            int end = col.indexOf("\"", start);
            if (end != -1) {
              values.add(col.substring(start, end));
            } else {
              values.add("");
            }
          } else {
            int end = col.indexOf("}", start);
            if (end == -1)
              end = col.length();
            String val = col.substring(start, end).trim();
            if (val.contains(","))
              val = val.substring(0, val.indexOf(","));
            if (val.contains("]"))
              val = val.substring(0, val.indexOf("]"));
            values.add(val.replace("\"", ""));
          }
        } else {
          values.add("");
        }
      }
    }
    return values;
  }
}