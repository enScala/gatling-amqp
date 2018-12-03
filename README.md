[![Build Status](https://travis-ci.org/dieselr/gatling-amqp.svg?branch=master)](https://travis-ci.org/dieselr/gatling-amqp)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/sc.ala/gatling-amqp_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/sc.ala/gatling-amqp_2.12)

Introduction
============

Gatling AMQP support

- CAUTION: This is not official library!
    - but using 'io.gatling.amqp' for FQCN to deal with 'private[gatling]', sorry.
- inspired by https://github.com/fhalim/gatling-rabbitmq forked from https://github.com/maiha/gatling-amqp (thanks!)


Library
=======

Current libraries version
- scala 2.12.6 (no support for scala 2.11.x)
- amqp-client-4.9.0
- gatling-2.3.1
- gatling-sbt-2.2.2 (whit dependency to `SBT` [1.2.3))

Usage
=====

## handy cli (use AmqpProtocol as console utility) [feature from 0.6]


```scala
scala> import io.gatling.amqp.Predef._
scala> amqp.declare(queue("q3", durable = true)).run
```


## publish (normal)


```scala
  implicit val amqpProtocol: AmqpProtocol = amqp
    .host("localhost")
    .port(5672)
    .auth("guest", "guest")
    .poolSize(10)

  val req = PublishRequest("q1", "{foo:1}")

  val scn = scenario("AMQP Publish").repeat(1000) {
    exec(amqp("Publish").publish(req))
  }

  setUp(scn.inject(rampUsers(10) over (1 seconds)))
    .protocols(amqpProtocol)
```

## publish (with persistent)

- PublishRequest.persistent make request DeliveryMode(2)
- known issue: persistent reset request's properties

```scala
  val req = PublishRequest("q1", "{foo:1}").persistent
```

## publish (with confirmation)

- set "confirmMode()" in protocol that invokes "channel.confirmSelect()"


```scala
  implicit val amqpProtocol: AmqpProtocol = amqp
    .host("localhost")
    .port(5672)
    .auth("guest", "guest")
    .poolSize(10)
    .confirmMode()

  val req = PublishRequest("q1", "{foo:1}")

  val scn = scenario("AMQP Publish(ack)").repeat(1000) {
    exec(amqp("Publish").publish(req))
  }

  setUp(scn.inject(rampUsers(10) over (1 seconds)))
    .protocols(amqpProtocol)
```

## declare queues

```scala
  implicit val amqpProtocol: AmqpProtocol = amqp
    .host("localhost")
    .port(5672)
    .auth("guest", "guest")
    .declare(queue("q1", durable = true, autoDelete = false))
```

## declare exchange and binding


```scala
  val x = exchange("color", "direct", autoDelete = false)
  val q = queue("orange")
  implicit val amqpProtocol: AmqpProtocol = amqp
    .host("localhost")
    .port(5672)
    .auth("guest", "guest")
    .declare(x)
    .declare(q)
    .bind(x, q, routingKey = "orange")
```

- full code: src/test/scala/io/gatling/amqp/PublishingSimulation.scala

## consume (auto acked)

**!!!It is not Supported after migration to newest libraries**
```scala
  implicit val amqpProtocol: AmqpProtocol = amqp
    .host("amqp")
    .port(5672)
    .auth("guest", "guest")

  val scn = scenario("AMQP Publish(ack)").exec {
    amqp("Consume").consume("q1", autoAck = true)
  }

  setUp(scn.inject(atOnceUsers(1)))
    .protocols(amqpProtocol)
```

- full code: src/test/scala/io/gatling/amqp/ConsumingSimulation.scala

## consume (manual acked)

- not implemented yet

## working with session context

```scala
  implicit val amqpProtocol: AmqpProtocol = amqp
    .host("localhost")
    .port(5672)
    .auth("guest", "guest")
    .poolSize(10)
    .confirmMode()

  val value = 1

  val scn = scenario("AMQP Publish(ack)").repeat(1000) {
    exec(amqp("Publish").publish(
      session => 
        PublishRequest("q1", s"{foo:${value}")
      )
    )
  }

  setUp(scn.inject(rampUsers(10) over (1 seconds)))
    .protocols(amqpProtocol)
```

**Note:** All `publish` anc `consume` methods has support for usage of session and alias replacing from session attribute.

Run
===
## sbt directly

```bash
% sbt
> gatling:test

% sbt
> gatling:testOnly io.gatling.amqp.PublishingSimulation

% sbt
> gatling:testOnly io.gatling.amqp.ConsumingSimulation
```

**Note:** try `sbt -J-Xmx8192m -J-XX:MaxPermSize=256m` for publishing massive messages

## shell script to store gatling stdout logs and simulation sources
**!!! Need to be checked if it is working**
```bash
% ./run p foo
```

- stored in "stats/p/foo"


Benchmark 
=========

After migration to newest libraries version no benchmark was run. [Reference](https://github.com/maiha/gatling-amqp#benchmark) to old Benchmark.


TODO
====
- declare exchanges, queues and bindings in action builder context (to test declaration costs)
- make AmqpProtocol immutable
- make Builder mutable
- mandatory
- consume action (manual ack)
- publish library to public repository
- enrich support for different signature of methods: `publish` and `consume`
- migrate to new gatling-3.x version
- migrate to new amqp-client-5.x version
- remove usage of library `pl.project13.scala:rainbow`

License
=======

released under the [MIT License](http://www.opensource.org/licenses/MIT).
