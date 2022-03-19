# The Demo of Distributed Transactions in Spring Kafka Microservices

## Overview

This project demo saga distributed transactions how to work in Microservices with Kafka and Spring Boot

There microservices show as below:

- `Order-Service` - it is gateway service to send new order events to the Kafka topic and process of a distributed transaction
- `Payment-Service` - it performs local transaction on the customer account to perform payment `order` price
- `Inventory-Service` - it performs local transaction on the inventory basing on number of products by `order`

## Description

1. `Order-Service` send a new `Order` -> `OrderStatus.NEW`
2. `Payment-Service` and `Inventory-Service` receive `Order` and handle it by performing a local transaction on the data
3. `Payment-Service` and `Inventory-Service` send a response `Order` -> `OrderStatus.ACCEPT` or `OrderStatus.REJECT`
4. `Order-Service` process incoming stream of orders from `Payment-Service` and `Inventory-Service`, join them by `Order` id and sends Order with a new status -> `OrderStatus.CONFIRMED` or `OrderStatus.ROLLBACK` or `OrderStatus.REJECTED`
5. `Payment-Service` and `Inventory-Service` receive Order with a final status and "commit" or "rollback" a local transaction make before

## Run Applications

1. Startup the Database, Kafka

```shell
docker-compose up -d ./docker/docker-compose.yml
```

2. Startup Microservices
  - `Order-Service`
  - `Payment-Service`
  - `Inventory-Service`

## How to test

- Send one order:

```shell
curl --location --request POST 'http://localhost:18080/orders' --header 'Content-Type: application/json' --data-raw '{
  "customerId": 10,
  "productId": 10,
  "productCount": 5,
  "price": 100,
  "status": "NEW"
}'
```

- Random generate orders:

```shell
curl --location --request POST 'http://localhost:18080/orders/generate'
```

- Search order result by API:

```shell
curl --location --request GET 'http://localhost:18080/orders'
```

- View order result by `kafka-ui`, visit `http://localhost:9021`
