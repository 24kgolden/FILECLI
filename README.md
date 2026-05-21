<div align="center">

<img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
<img src="https://img.shields.io/badge/Linux-Compatible-FCC624?style=for-the-badge&logo=linux&logoColor=black"/>
<img src="https://img.shields.io/badge/CLI-Tool-00BFFF?style=for-the-badge&logo=gnometerminal&logoColor=white"/>
<img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge"/>

<br/><br/>

```
  ███████╗██╗██╗     ███████╗ ██████╗██╗     ██╗
  ██╔════╝██║██║     ██╔════╝██╔════╝██║     ██║
  █████╗  ██║██║     █████╗  ██║     ██║     ██║
  ██╔══╝  ██║██║     ██╔══╝  ██║     ██║     ██║
  ██║     ██║███████╗███████╗╚██████╗███████╗██║
  ╚═╝     ╚═╝╚══════╝╚══════╝ ╚═════╝╚══════╝╚═╝
```

### 📁 Herramienta CLI de gestión de archivos para Linux — escrita en Java

</div>

---

## 📸 Vista previa

<div align="center">

<img src="./assets/filecli-preview.png" alt="Vista previa de FileCLI" width="900"/>

</div>

```
╔══════════════════════════════════════════╗
║         FileCLI — Herramienta de         ║
║        Gestión de Archivos en Java        ║
╚══════════════════════════════════════════╝

Uso: filecli <comando> [ruta] [opciones]

  ls       <ruta>          Listar archivos
  ls-l     <ruta>          Listar con detalles
  stats    <ruta>          Estadísticas por extensión
  tree     <ruta>          Árbol de directorios
  find     <ruta> <patrón> Buscar archivos
  organize <ruta>          Organizar por categoría
  top      <ruta>          Top 10 archivos más grandes
  help                     Mostrar esta ayuda
```

---

## ✨ Características

| Comando | Descripción |
|--------|-------------|
| `ls` | Lista archivos con iconos y colores según el tipo |
| `ls-l` | Vista detallada: tipo, tamaño, fecha y nombre |
| `stats` | Estadísticas por extensión con barra de progreso visual |
| `tree` | Árbol jerárquico del directorio al estilo Unix |
| `find` | Búsqueda de archivos por nombre o patrón |
| `organize` | Mueve archivos a subcarpetas por categoría automáticamente |
| `top` | Muestra los 10 archivos más pesados del directorio |

---

## 🚀 Instalación

### Opción 1 — Script automático (recomendado)

```bash
git clone https://github.com/tu-usuario/filecli.git
cd filecli
chmod +x install.sh
sudo ./install.sh
```

El script verifica Java, compila el proyecto y registra `filecli` como comando global en `/usr/local/bin`.

### Opción 2 — Manual

```bash
# Compilar
mkdir -p out
javac -d out src/com/filecli/FileCLI.java

# Empaquetar en JAR ejecutable
jar cfe filecli.jar com.filecli.FileCLI -C out .

# Ejecutar directamente
java -jar filecli.jar help
```

### Opción 3 — Alias rápido

```bash
echo 'alias filecli="java -jar /ruta/a/filecli.jar"' >> ~/.bashrc
source ~/.bashrc
```

---

## 💻 Ejemplos de uso

```bash
# Listar archivos con iconos
filecli ls ~/Descargas

# Ver detalles (tamaño + fecha)
filecli ls-l /etc

# Estadísticas de espacio por tipo de archivo
filecli stats ~/Documentos

# Árbol visual del proyecto
filecli tree ~/mi-proyecto

# Buscar todos los PDF en una carpeta
filecli find ~/Descargas .pdf

# Organizar Descargas automáticamente
filecli organize ~/Descargas

# Ver los archivos más pesados del sistema
filecli top ~/
```

---

## 🗂️ Categorías del comando `organize`

Cuando ejecutas `filecli organize <ruta>`, los archivos se mueven automáticamente a las siguientes carpetas:

| Carpeta | Extensiones soportadas |
|---------|------------------------|
| 🖼️ Imágenes | `jpg`, `jpeg`, `png`, `gif`, `bmp`, `svg`, `webp`, `ico`, `tiff` |
| 📄 Documentos | `pdf`, `doc`, `docx`, `xls`, `xlsx`, `ppt`, `pptx`, `txt`, `md`, `csv` |
| 🎬 Videos | `mp4`, `mkv`, `avi`, `mov`, `wmv`, `flv`, `webm` |
| 🎵 Audio | `mp3`, `wav`, `flac`, `aac`, `ogg`, `m4a` |
| 💻 Código | `java`, `py`, `js`, `ts`, `html`, `css`, `c`, `cpp`, `go`, `rs`, `sh`, `json`, `xml`, `yaml` |
| 🗜️ Comprimidos | `zip`, `tar`, `gz`, `bz2`, `7z`, `rar`, `xz` |
| 📎 Otros | todo lo demás |

> ⚠️ El comando `organize` **mueve** los archivos. Asegúrate de tener una copia de seguridad si lo usas en rutas críticas.

---

## 📋 Requisitos

- **Java 11 o superior** (JRE para ejecutar, JDK para compilar)
- Linux, macOS o WSL (Windows Subsystem for Linux)
- Terminal con soporte de colores ANSI (la mayoría las soportan)

```bash
# Verificar tu versión de Java
java -version

# Instalar Java en Ubuntu/Debian si no lo tienes
sudo apt install openjdk-21-jre-headless
```

---

## 🏗️ Estructura del proyecto

```
filecli/
├── src/
│   └── com/
│       └── filecli/
│           └── FileCLI.java     ← Código fuente principal
├── out/                         ← Clases compiladas (generado)
├── filecli.jar                  ← JAR ejecutable (generado)
├── install.sh                   ← Script de instalación
└── README.md
```

---

## 🤝 Contribuciones

¡Las contribuciones son bienvenidas! Si quieres mejorar FileCLI:

1. Haz un **fork** del repositorio
2. Crea una rama: `git checkout -b feature/nueva-funcionalidad`
3. Realiza tus cambios y haz commit: `git commit -m "feat: agrega nueva-funcionalidad"`
4. Sube los cambios: `git push origin feature/nueva-funcionalidad`
5. Abre un **Pull Request**

### Ideas para contribuir

- [ ] Soporte para mover/copiar archivos
- [ ] Modo interactivo con menú navegable
- [ ] Filtros por fecha o tamaño en `ls-l`
- [ ] Exportar estadísticas a CSV o JSON
- [ ] Configuración de categorías personalizadas para `organize`
- [ ] Soporte para colores en Windows (PowerShell)

---

## 📄 Licencia

Este proyecto está bajo la licencia **MIT**. Puedes usarlo, modificarlo y distribuirlo libremente.

---

<div align="center">

Hecho con ☕ y Java · por [24kgolden](https://github.com/24kgolden)

</div>
