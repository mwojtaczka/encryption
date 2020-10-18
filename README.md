# Encryption framework

⚠️ **under construction** ⚠️

Before start install project to your local maven repository:
`mvn install`

## Getting started with Spring Data JPA
  
To see an example go to [test dummy project](test/dummy)

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

## Getting started without Spring Data JPA or without proxies or without Spring at all
In case of no Spring Data proxies won't be enabled. Proxies can be also turned off manually by the properties:
`encryption.framework.spring-data.proxy.encrypt.enabled=false`
`encryption.framework.spring-data.proxy.hash.enabled=false`

Steps are similar to above, but one has to manually inject preconfigured beans and simply use its api 
(like encrypt/decrypt methods) before inserting entities into a storage and after retrieving them.  
The api described below.

## Getting started without Spring
The base project is framework agnostic and doesn't require Spring. In that case just add base encryption framework 
module:
```
<dependency>
    <groupId>com.maciek.wojtaczka</groupId>
    <artifactId>encryption-framework-base</artifactId>
</dependency>
```
Then all the beans has to be configured manually. To see how one can look up configuration in spring-autoconfigure 
module. While the instances are configured one can mark fields with `@Encrypt` annotation and the api.  
The api described below.

## Encryption framework api - 'encryption-framework-base'

### Marking fields to be encrypted
Use `@Encrypt` annotation to mark each field to be encrypted. Currently, only `String` related fields are supported.
That means one can apply the annotation to such a fields:
```
@Encrypt
String sensitive;
@Encrypt
List<String> listOfSensitives;
@Encrypt
Set<String> setOfSensitives;
@Encrypt
Entity embeddedEntityWithSensitives;
@Encrypt
List<Entity> listOfEmbeddedEntities;
@Encrypt
Set<Entity> setOfEmbeddedEntities;
```

### Encryption and decryption of entity
Use `EntityEncryptor` instance either to encrypt or decrypt entity. U can specify the key name that will be used to 
perform the operation.

#### Few words about encryption keys - and implementations that need to be overriden by the user 
- key names and `KeyNameResolver`
 Each entity can be encrypted with different key. One can pass the key name along with the entity:
 `EntityEncryptor.encryptObject(Object object, String keyName)`
 otherwise, the key name will be provided by the instance of `KeyNameResolver`. The only implementation of that 
 interface provided by the framework (`StaticKeyNameResolver`), returns static names: `encryption-key` for encryption 
 process and `blind-id-key` for blind id hashing. However, it is recommended to override the implementation, 
 for instance to resolve the name base on the entity or security context.
- encryption keys and `EncryptionKeyProvider`
 Each key name used to encrypt entity needs to conform to `EncryptionKey` provided by `EncryptionKeyProvider`. The only 
 implementation of the provider interface that framework currently provides is `InMemoryStaticKeyProvider` that generates 
 one key during class init and returns it regardless the key name or version. It is highly recommended implementing own 
 key provider base on used key storage.

### Lazy decryption
As decryption process is considered as costly, one can postpone it to the moment when the field is needed.  
`@Encrypt(lazy=true)` needs to be setup to enable lazy decryption. Moreover, one has to specify the moment when 
decryption will happen (typically the field's getter):
```
@Encrypt(lazy = true)
String lazySensitive;
...
String getLazySensitive() {
    StaticDecryptor.decryptField(this, "lazySensitive", "test_key");
    return lazySensitive;
}
```
Until the getter is called, the field remains encrypted.

### Setting encryption algorithms
For each sensitive field, different encryption mechanism can be specified:
`@Encrypt(lazy = 'algorithmName')`
The algorithm name has to conform to one of the defined `CipherMechanism` implementations. The framework provides 
with one implementation (`AesGcmNoPaddingMechanism`). User can define more mechanism by implementing the interface and 
setting them up in the configuration.  
>Spring users that leverage on auto-configuration may simply register new beans of the `CipherMechanism` interface that 
>will be injected into the framework. 

### Searchable fields (blind id)
As the sensitive fields should be encrypted with non-deterministic mechanism it makes it not possible to query entities
by that field. For that purpose additional field - so called blind id is required.  
Basically blind id is a hash of the field, so any time one wants to query by that field one has to query by its 
blind id.  
To enable blind id, setup the annotation:
```
@Encrypt(searchable = true)
String searchable;
```
Then create corresponding blind id field (the naming convention is crucial):
`String searchableBlindId;`
The blind id field will be populated with hash of the annotated field value. Framework provides with one hashing 
algorithm: `HmacSHA256`. In case one would like to provide custom hashing mechanism, the way is similar as with 
encryption mechanisms. Implement `CipherMechanism` interface (without decrypt method) and set it up while configuring 
EntityEncryptor instance.
>Spring users that leverage on auto-configuration can just register another bean implementing `CipherMechanism`.
>and set it up by property:  
>`encryption.framework.blindId.algorithm=hashingMechanismName`
In order to query entities by the field, one has to hash the value before passing it to the querying method, like:
```
BlindIdConverter blindIdConverter;

List<Entity> findBySearchable(String searchableValue){
  searchableHashed = blindIdConverter.hash(searchableValue);
  Entity encryptedEntity findBySearchableBlindId(searchableHashed);

  retun ...
}
```

### Auto re-encryption
The framework supports versioned keys, it means that encryption always is conducted with the latest key, and decryption 
with the key of version from metadata. The framework can be configured to detect during decryption process if the key 
the entity has been encrypted with is stale. If so, an asynchronous update is being performed. 
To make it happen one has to provide implementation of `EntityUpdater` interface and decorate `EntityEncryptor` with 
`AsyncReencryptDecorator`.
>Spring users that leverage on auto-configuration have to only register bean that implements `EntityUpdater`.
>Moreover, Spring module of this framework provides with implementation for JPA based projects (`JpaEntityUpdater`). 

