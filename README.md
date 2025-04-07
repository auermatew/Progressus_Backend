## Telepítés és futtatás ⚙️

### Követelmények
> A projekt [Spring Boot](https://spring.io/projects/spring-boot) keretrendszert használ,<br/> amihez szükséges a [Maven](https://maven.apache.org/download.cgi) telepítése.

> [JDK 17](https://bell-sw.com/pages/downloads/#jdk-17-lts) szükséges a futtatáshoz.

> PostgreSQL adatbázisunkhoz [Docker](https://www.docker.com/products/docker-desktop/) használtunk.<br/> Ennek pontos beállításához alább található [Adatbázis konfiguráció](#adatbázis-konfiguráció).

### Futattás
1. Projekt cloneozása:

`git clone https://github.com/auermatew/Progressus_Backend.git `


2. A már konfigurált Docker container elindítása


3. Backend applikáció elindítása



### Adatbázis konfiguráció
> (A Docker telepítése után) futtatni kell a backend project root mappájában található **docker-compose.yml** fájlt a következő paranccsal:<br/>
`docker-compose up -d`

**(Opcionális)**
**Ha látni szeretné az adatbázist IntelliJ-ben:** </br>
> 1. `View > Tool Windows > Database` 
> 2. `+ ikon > Data Source > PostgreSQL`
> 3. `Host: localhost`
> 4. `Port: 5432`
> 5. `Username: username`
> 6. `Password: password`
> 7. `Apply > OK`


---
## Fejlesztési terv 👽
#### 1. Entities, adatmodell ✅
#### 2. Autentikáció JWT-vel és cookies ✅
#### 3. Autorizáció ✅
#### 4. Tanár features
   - authorization ✅
   - tanórák ✅
   - naptár ✅
#### 5. Diák - tanár interakciók
   - óra jelentkezés ✅
   - óra vétele ✅
   - tanár/óra rating ⏳
#### 6. Egyéb funkciók
   - tantárgyak ✅
   - user interests ⏳
   - képek AWS-sel ⏳
#### 7. Extra funkciók
   - basic admin features ✅
   - featured tanárok ⏳
   - user interests alapján tanár ajánlás ⏳
   - testreszabható tanároldal ⏳
---
## API Dokumentáció | Tesztelés 📊
> ...