# Yanki Service

Microservicio encargado del monedero móvil Yanki.

## Funcionalidades

- Registro de usuarios.
- Asociación con tarjeta de débito.
- Envío de dinero.
- Recepción de dinero.
- Integración mediante Kafka.

## Puerto

```
8087
```

## OpenAPI

```
src/main/resources/openapi/yanki-openapi.yml
```

## Docker

```bash
docker build -t yanki-service .
```

## Infraestructura

La infraestructura Docker se encuentra en el repositorio **bank-infra**.