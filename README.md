High level wrapper on top of the Apache HTTP client, including JSON support.

The [Apache HTTP Client](https://hc.apache.org/) Java libraries are great and allow a great flexibility when it comes to deal with HTTP, HTTPS, different methods, header manipulation, etc. However, it is quickly complex to setup and this setup has to be done for each project which needs to connect to remote applications using HTTP.

This library aims to simplify the HTTP(S) connections by offering an easy configuration and a high level view on the HTTP communication.

> This code was initially part of the [Ontrack](https://github.com/nemerosa/ontrack) application and has been extracted from it.

## Getting the library

Using Gradle:

```groovy
dependencies {
   compile 'net.nemerosa:http-client-core:1.4.1'
}
```

or:

```groovy
dependencies {
   compile 'net.nemerosa:http-client-json:1.4.1'
}
```

if you need the JSON support.

> The `http-client-core` depends on JDK8. No support for JDK7 is foreseen.

## Creating a HTTP client

Use the `ClientBuilder` class to configure and create a (HTTP) `Client`:

```java
Client client = ClientBuilder
   .create(url, disableSsl)
   .withLogger(logger)
   .withCredentials(user, password)
   .build()
```

The `url` is the base URL to connect to.

The `disableSsl` flag disables SSL checks (host & certificate validity) should be set to `false` - however, in a testing context, where some servers would not have a valid certificate, it might be useful to set to `true`. If SSL checks are disabled, a warning will be emitted on the logs.

If set, the `logger` must be an implementation of the `ClientLogger` interface. Using the JDK8, it is as simple as doing:

```java
.withLogger(message -> doSomething(message))
```

The default logger writes the message on a SLF4J logger associated with the `Client` class.

The credentials are of course optional and will be used for a [Basic authentication](https://en.wikipedia.org/wiki/Basic_access_authentication).

Finally, the `Client` to use is created using the `build()` method.

> This client can be reused as many times as needed.

## Using the client

To perform a `GET`:

```java
String html = client.get(
   content -> content,
   "relative/path/%s",
   "param1");
```

The argument is a `ResponseParser` interface and is responsible for parsing the HTTP response as text. See below for JSON specific behaviour.

> To perform a delete, just use `delete(...)` instead of `get(...)`.

You can also `POST` or `PUT` any entity:

```java
String html = client.post(
   content -> content,
   new StringEntity(
       "Some text",
       ContentType.create("text/plain", "UTF-8")
   ),
   "relative/path/%s",
   "param1"
);
```

> Use `put(...)` for a `PUT`.

You can also upload a document:

```java
String html = client.upload(
   "parameterName",
   new Document("text/plain", "Some text"),
   "fileName.txt",
   "relative/path/%s",
   "param1"
);
```

... or download one:

```java
Document document = client.download(
   "relative/path/%s",
   "param1"
);
```

## Using the JSON library

The `http-client-json` module leverages the `http-client-core` for dealing with JSON. It relies on the [Jackson JSON library](http://wiki.fasterxml.com/JacksonHome/) (the one used by [Spring](http://spring.io/)).

To create a JSON client, you need a `Client`, configured as shown above:

```java
JsonClient jsonClient = new JsonClientImpl(
   client
);
```

The JSON parsing/serialisation will be done using a default `ObjectMapper`. If you need to provide your own, it's also possible:

```java
JsonClient jsonClient = new JsonClientImpl(
   client,
   objectMapper
);
```

The calls are the same than for a `Client`, but for the fact that you do not need any `ResponseParser` (because JSON is always assumed, for both request and response).

For example:

```java
JsonNode node = jsonClient.get("relative/%s", "path");
```

or:

```java
JsonNode node = jsonClient.post(data, "relative/%s", "path");
```

where `data` is any `Object` that will be serialized as JSON using the `ObjectMapper` associated with the `JsonClient`. This can be a `JsonNode` as well.

## Apache HTTP client version

This library depends on [Apache HTTP Client 4.5](https://hc.apache.org/httpcomponents-client-4.5.x/index.html). This might cause conflicts with other libraries using less recent versions.

## Developing

### Contributing

Contributions are welcome! Fork and create pull requests, or create [issues](https://github.com/nemerosa/http-client/issues).

### Importing in Intellij

* The Lombok plugin must be installed
* Do not forget to enable annotation processing in _Preferences > Compiler > Annotation processors_
