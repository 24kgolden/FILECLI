# FileCLI 
### Herramienta de gestión de archivos en Java para Linux

---

##  Características

| Comando      | Descripción                                      |
|-------------|--------------------------------------------------|
| `ls`        | Lista archivos con iconos y colores              |
| `ls-l`      | Lista detallada con tamaño y fecha               |
| `stats`     | Estadísticas por extensión con barra de progreso |
| `tree`      | Árbol visual del directorio                      |
| `find`      | Busca archivos por nombre/patrón                 |
| `organize`  | Organiza archivos en carpetas por categoría      |
| `top`       | Top 10 archivos más pesados                      |

---

##  Instalación rápida

```bash
chmod +x install.sh
sudo ./install.sh
```

---

##  Uso

```bash
filecli help
filecli ls ~/Descargas
filecli ls-l /etc
filecli stats ~/Documentos
filecli tree ~/proyecto
filecli find ~/Descargas .pdf
filecli organize ~/Descargas
filecli top ~/
```

---

##  Compilar manualmente

```bash
mkdir -p out
javac -d out src/com/filecli/FileCLI.java
jar cfe filecli.jar com.filecli.FileCLI -C out .
java -jar filecli.jar help
```

---

##  Requisitos

- Java 11+ (JRE para ejecutar, JDK para compilar)
- Linux / macOS / WSL

---

##  Categorías del comando `organize`

| Carpeta       | Extensiones                              |
|--------------|------------------------------------------|
| Imágenes     | jpg, png, gif, svg, webp…               |
| Documentos   | pdf, doc, docx, xls, txt, md, csv…      |
| Videos       | mp4, mkv, avi, mov…                     |
| Audio        | mp3, wav, flac, ogg…                    |
| Código       | java, py, js, ts, html, sh, json…       |
| Comprimidos  | zip, tar, gz, 7z, rar…                  |
| Otros        | todo lo demás                            |
