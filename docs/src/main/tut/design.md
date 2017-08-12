---
layout: docs
title:  "Design"
position: 1
---

# Design

The library is designed around 4 [Type Classes](http://danielwestheide.com/blog/2013/02/06/the-neophytes-guide-to-scala-part-12-type-classes.html):
 - Message Decoder (*aka Decoder*)
 - Message Encoder (*aka Encoder*)
 - Field Decoder
 - Field Encoder

`Message` decoders/encoders require to know the `type` and `number` (index) of all fields you want to read/write. Several implementations are available to fit the multiple scenarios:
 - Decode all fields of a protobuf message, or just some of these.
 - Fields are numbered consecutively, or their numbering must be personalized.
 - Transform or validate fields

`Field` decoders/encoders allow to read a specific type at a specific position in a protobuf message. You can use default
instances, or derive new decoders/encoders.

**For performance reasons, protobuf messages are processed in a streaming fashion. Reading the same fields two times, or
reading in reverse order is not allowed (the last constraint will be removed in a near future).**
