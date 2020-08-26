package ltd.newbee.mall.config.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import javax.validation.Valid;

/**
 * redis 配置
 * @date 2019/2/19
 */
@Slf4j
@Configuration
public class RedisConfig {

    @Value( "${spring.redis.maxRedirects}" )
    private Integer maxRedirects;
    @Value( "${spring.redis.password}" )
    private String password;
    @Value("${spring.redis.host}")
    private String hostName;
    @Value("${spring.redis.port}")
    private Integer port;
    /**
     * 配置 jedis pool
     */
    @Order(0)
    @Bean
    @ConfigurationProperties("spring.redis.jedis.pool")
    public JedisPoolConfig jedisPoolConfig() {
        log.info("初始化JedisPoolConfig>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        return new JedisPoolConfig();
    }

    @Order(0)
    @Bean
    public RedisStandaloneConfiguration redisStandaloneConfiguration() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setDatabase(0);
        redisStandaloneConfiguration.setHostName(hostName);
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
        return redisStandaloneConfiguration;
    }
    /**
     * 配置JedisConnectionFactory
     */
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisClientConfiguration.JedisClientConfigurationBuilder builder = JedisClientConfiguration.builder();
        JedisClientConfiguration.JedisPoolingClientConfigurationBuilder jedisPoolingClientConfigurationBuilder = builder.usePooling();
        jedisPoolingClientConfigurationBuilder.poolConfig(jedisPoolConfig());
        return new JedisConnectionFactory(redisStandaloneConfiguration(), jedisPoolingClientConfigurationBuilder.build());
    }

    /**
     * redisTemplate
     */
    @Bean
    public RedisTemplate redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory( jedisConnectionFactory );

        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility( PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping( ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        // 设置值（value）的序列化采用Jackson2JsonRedisSerializer。
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        // 设置键（key）的序列化采用StringRedisSerializer。
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

}
