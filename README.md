# Ejercicio Tecnico

Aplicacion Springboot para creacion, authenticacion y obtencion de informacion de usuario mediante mail.

## Tabla de contenidoa

- [Iniciando el Proyecto](#iniciando-el-proyecto)
- [Project Overview](#project-overview)
- [Prerequisites](#prerequisitos)

# Iniciando el proyecto


## Clonar Repositorio

Debe iniciar una terminal en un equipo que cuente con git y clonar el repositorio

```
git clone https://github.com/CamiloPoblet/ejercicioTecnico_2.git
```
## Iniciar Proyecto
Dentro del proyecto encontrara un archivo .bat llamado *"run.bat"* haga doble click sobre el arhivo .bat y la aplicacion se ejecutara automaticamente.
El archivo run.bat hara un build del proyecto con gradle y ejecutara el jar resultante para verificar que la aplicacion se genero y ejecuto con exito puede ingresar a la ruta *"http://localhost:8080/swagger-ui/index.html"* donde tendra la documentacion generada a traves de Swagger con los distintos endpoints de la applicacion, en esta seccion tambien puede visualizar los distintos schemas utilizados por la api y probar los distintos endpoints con sus request de ejemplo

# Project Overview

### Endpoints
La API Cuenta con 3 endpoints fundamentales

#### /api/users/registry 
> Para la creacion de usuariios nuevos y la devolucion de un token para el usuario creado, el token generado tiene una validez de 24hrs

#### /api/users/auth
> Para la autenticacion de usuariios ya existentes y la devolucion de un token para el usuario autentica, el token generado tiene una validez de 24hrs

### /api/users/search
> Para la busqueda de los datos de un usuario ya existente a traves del email registrado, este endpoint esta segurizado por lo cual sera necesario que haga uso de un token generado por cualquier de los endpoints anteriormente nombrados.

# Prerequisitos
Java 17+
