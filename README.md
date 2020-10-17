# Encryption framework

⚠️ **under construction** ⚠️

## Getting started with Spring Data JPA
  
To see an example go to [test dummy project](test/dummy)
  
- install project
- add the starter to your spring-boot project (with spring-data repository):
```
<dependency>
    <groupId>com.maciek.wojtaczka</groupId>
    <artifactId>encryption-framework-spring-starter</artifactId>
</dependency>
```
- mark String sensitive fields in your entity with `@Encrypt` annotation
The annotation supports few features like: lazy decrypting, setting encryption algorithm, making field searchable.

### Proxies (aspects)
By default, proxies for encryption and hashing are enabled, and allow to automatic encrypt/decrypt entities.
Encryption is being done by `EncryptionJpaAspect` that intercepts every call to `save*(*)` and `saveAll(*)` methods from 
`JpaRepository` and encrypt the input.
Decryption is being done by the same aspect that intercept every call to `findBy*(*)` and decrypt the output.
For searchable fields `BlindIdSearchAspect` is enabled, it intercepts call to `find*(*, ..)` methods and has the input 
however it is in experimental stage and requires some convention (more info inside the class). Moreover, it doesn't 
support all the cases.

##Getting started without Spring Data JPA (or without proxies) - EntityEncryptor api
In case of no Spring Data proxies won't be enabled. Proxies can be turned off manually as well by the properties:
`encryption.framework.spring-data.proxy.encrypt.enabled=false`
`encryption.framework.spring-data.proxy.hash.enabled=false`

One can manually call EntityEncryptor api for each encrypt/decrypt operation. In case of using the starter one can 
inject `EntityEncryptor` bean and simply use its api (encrrypt/decrypt methods) before inserting entities into a 
storage and after retrieving them. However, some features require other class usage.

### Lazy decryption
TBD

### Setting encryption algorithms
TBD

### Searchable fields (blind id)
TBD

### Auto re-encryption
TBD

