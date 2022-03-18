# The demo of spring kafka microservices project

## Overview

This project demo distributed transactions in Microservices with Kafka Streams and Spring Boot

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
