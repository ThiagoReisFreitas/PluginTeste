# Como rodar
    
Java 21

Docker Compose com configuração padrão para rodar plugin
```
docker-compose up
```
---
Configurar o config.yml que esta dentro do resources

 Definir o host com o IP do container, se não souber qual é só rodar esse comando
```
docker inspect minecraft_home_db | grep IPAddress
```
O IP costuma ser 172.17.0.2 mas isso pode variar

---

Se não você não tiver o spigot baixado é só baixar no [site oficial](https://getbukkit.org/download/spigot) e escolher a versão igual a do seu jogo

---

Depois de executar o arquivo .jar do spigot é só colocar o arquivo .jar do plugin (que esta em /PluginTeste/build/libs) e copia-lo para a pasta plugins do spigot.

Depois de todos esses passos é só rodar o servidor e se conectar no jogo.


Para informações sobre os comandos ir em /PluginTeste/src/main/resources/plugin.yml

