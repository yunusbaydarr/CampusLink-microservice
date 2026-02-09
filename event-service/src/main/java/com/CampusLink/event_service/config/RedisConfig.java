package com.CampusLink.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {


        @SuppressWarnings("deprecation")
        @Bean
        public RedisCacheConfiguration cacheConfiguration() {
            ObjectMapper objectMapper = new ObjectMapper();

            // 1. ÖNEMLİ: Java 8 tarih desteği (Event servisindeki LocalDateTime için)
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            // 2. KRİTİK: List<Long> ve DTO dönüşümleri için tip bilgisini ekle
            // Bu ayar olmazsa her reload'da "Integer cannot be cast to Long" hatası alırsın
            PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                    .allowIfBaseType(Object.class)
                    .build();
            objectMapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

            // 3. Bilinmeyen alanlar gelirse (DTO değişirse) patlamasın
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

            return RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofHours(1)) // 1 saat cache
                    .disableCachingNullValues() // Null değerleri cache'leme (Hata durumunda null cache'lenirse hep null döner)
                    .serializeValuesWith(
                            RedisSerializationContext.SerializationPair.fromSerializer(serializer)
                    );
        }
    }

