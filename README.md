# Documentación

**Instalación de la aplicación en GNU/Linux**

Requerimientos:

JAVA 8

Tomcat 8.5

Servidor de base de datos MariaDB =< 10.3  ó  MySQL =< 5.6

**Instalación de paquetes y dependencias**

Ubuntu 16.04

    $ sudo apt-get install -y curl wget git nginx-full build-essential
    $ sudo apt-get -y openjdk-8-jdk

**Instalación del motor de la base de datos**

    $ sudo apt-get install software-properties-common
    $ sudo apt-key adv --recv-keys --keyserver hkp://keyserver.ubuntu.com:80 0xF1656F24C74CD1D8
    $ sudo add-apt-repository 'deb [arch=amd64,arm64,i386,ppc64el] http://mirror.lstn.net/mariadb/repo/10.3/ubuntu xenial main'
    $ sudo apt-get update
    $ sudo apt-get install mariadb-server

RHEL 7 / CentOS 7

    $ sudo yum install -y wget curl git epel-release
    $ sudo yum install -y nginx java-1.8.0-openjdk

**Creamos el archivo del repositorio de MariaDB para la instalación del motor de base de datos.**

    $ sudo vim /etc/yum/repos.d/MariaDB.repo

Y el contenido debe ser este:
```
# MariaDB 10.3 CentOS repository list - created 2019-04-22 00:04 UTC
# http://downloads.mariadb.org/mariadb/repositories/

[mariadb]
name = MariaDB
baseurl = http://yum.mariadb.org/10.3/centos7-amd64
gpgkey=https://yum.mariadb.org/RPM-GPG-KEY-MariaDB
gpgcheck=1
```
    $ sudo yum install MariaDB-server MariaDB-client

**Instalación del Tomcat**

La versión del tomcat que usaremos es la 8.5 , para la instalación del proyecto se entrega un archivo ZIP con los archivos de configuración ya creados y con el archivo WAR de la aplicación, solo hay que modificar y agregar algunas rutas y la base de datos.

1.- Descomprimimos el archivo ZIP en la locación que gusten, por ejemplo en /opt

     $ unzip tomcat.zip -d /opt/
     
2.- Creamos un usuario sin permisos de administración y se lo asignamos al Tomcat.
    
     $ sudo useradd example -Umd /home/example
     $ sudo chown example:example -R /opt/tomcat

3.- Hay que modificar las siguientes variables del archivo /opt/tomcat/bin/setenv.sh

```
JAVA_HOME : Para CentOS usamos /usr/lib/jvm/jre y para ubuntu /usr/lib/jvm/open-jdk
CATALINA_PID=/opt/tomcat/temp/tomcat.pid
CATALINA_HOME=/opt/tomcat
CATALINE_BASE=/opt/tomcat
```
[database](https://photos.app.goo.gl/YfnnTfBmN5DKxUYGA)

4.- Creamos la base de datos y el usuario para la misma.

    $ mysql -u root -p

    mysql > create database resuelve;
    mysql > grant all privileges on resuelve.* to ‘resuelve’@’localhost’ identified by ‘lacontraseña’;
    mysql > flush privileges;
    mysql > exit;

5.- Configuramos la base en el archivo /opt/tomcat/conf/server.xml en la sección que se llama database connection, por los valores que le dimos a nuestra base.

[server.xml](https://photos.app.goo.gl/Banot64wBtvqqQ1J7)

6.- Para probar el servicio, ejecutamos la siguiente orden y veremos algo parecido a esto.

    $ su -l example -c '/opt/tomcat/bin/startup.sh && tail -f -n 50 /opt/tomcat/logs/catalina.out'
    
[server log](https://photos.app.goo.gl/vY6UTaMzk5Gqdicj9)

7.- Probamos que la aplicación está arriba desde el navegador usando , la IP del servidor por el puerto 8080 en este caso es http://localhost:8080 

[run app](https://photos.app.goo.gl/AKy4CKkHM7qtCgwk6)

8.- Aplicación como servicio del sistema.

Utilizaremos systemd para esta funcionalidad, esto sirve tanto para CentOS o Ubuntu.

Creamos el archivo /etc/systemd/system/tomcat.service con el siguiente contenido:

```
# Systemd unit file for tomcat

[Unit]
Description=Apache Tomcat Web Application Container
After=syslog.target network.target

[Service]
Type=forking

EnvironmentFile=-/opt/tomcat/bin/setenv.sh

ExecStart=/opt/tomcat/bin/startup.sh
ExecStop=/opt/tomcat/bin/shutdown.sh

User=example
Group=example

[Install]
WantedBy=multi-user.target
```

9.- Hay que dar de alta el nuevo servicio e iniciarlo

    $ sudo systemctl daemon-reload
    $ sudo systemctl enable tomcat
    $ sudo systemctl start tomcat

10.- Para el uso de la aplicación:

Se hará un envío de dato tipo rest (usar un servicio como postman, por ejemplo) con la siguiente estructura:

```JSON
url: http://${domain}/home
type: POST
dataType: JSON

data: 	[{  
      "nombre":"Juan Perez",
      "nivel":"F",
      "goles":10,
      "sueldo":50000,
      "bono":25000,
      "sueldo_completo":null,
      "equipo":"rojo"
   },
   {  
      "nombre":"EL Cuauh",
      "nivel":"Cuauh",
      "goles":30,
      "sueldo":100000,
      "bono":30000,
      "sueldo_completo":null,
      "equipo":"azul"
   },
   {  
      "nombre":"Cosme Fulanito",
      "nivel":"A",
      "goles":7,
      "sueldo":20000,
      "bono":10000,
      "sueldo_completo":null,
      "equipo":"azul"

   },
   {  
      "nombre":"El Rulo",
      "nivel":"B",
      "goles":9,
      "sueldo":30000,
      "bono":15000,
      "sueldo_completo":null,
      "equipo":"rojo"

   },
   {  
      "nombre":"Fer",
      "goles_minimos":10,
      "goles":9,
      "sueldo":30000,
      "bono":15000,
      "sueldo_completo": 14250,
      "equipo":"rojo"
   }]
   ```
