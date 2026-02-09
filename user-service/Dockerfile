# Temel imaj olarak OpenJDK 17 kullanıyoruz (Projenizin Java sürümüne göre 21 vs değiştirebilirsiniz)
FROM eclipse-temurin:17-jdk-alpine

# Uygulamanın çalışacağı dizin
WORKDIR /app

# Jar dosyasını kopyala (Maven build sonrası oluşan jar)
# Not: target/*.jar ifadesi bazen birden fazla dosya varsa hata verebilir, net isim vermek daha garantidir.
COPY target/*.jar app.jar

# Uygulamayı başlat
ENTRYPOINT ["java", "-jar", "app.jar"]