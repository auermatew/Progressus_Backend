## Telepítés és futtatás ⚙️

### Követelmények
> A projekt [Spring Boot](https://spring.io/projects/spring-boot) keretrendszert használ,<br/> amihez szükséges a [Maven](https://maven.apache.org/download.cgi) telepítése.

> [JDK 17](https://bell-sw.com/pages/downloads/#jdk-17-lts) szükséges a futtatáshoz.

> PostgreSQL adatbázisunkhoz [Docker](https://www.docker.com/products/docker-desktop/) használtunk.<br/> Ennek pontos beállításához alább található [Docker konfiguráció](#docker-konfiguráció).

### Futattás
1. Projekt cloneozása:

`git clone https://github.com/auermatew/Progressus_Backend.git `


2. A már konfigurált Docker container elindítása


3. Backend applikáció elindítása



### Docker konfiguráció
> A Docker telepítése után a következő parancsokkal tudod elindítani a PostgreSQL adatbázist:

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