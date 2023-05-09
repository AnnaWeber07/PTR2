# PTR2
Message Broker System

I designed an actor-based message broker using Akka in Scala. This is a good approach as it provides a highly scalable and fault-tolerant system. 

The system can be broken down into the following components:

- Publisher: Sends messages to the message broker
- Subscribers: Receives messages from the message broker
- Connection Server Manager: Manages the connection of producers and consumers to the message broker
- Router: Routes messages from the publisher to the correct subscribers based on the topic
- Topic Actors: Manages messages for a particular topic
- Dead Letter Channel: Handles messages that cannot be delivered to a subscriber
- Batcher: Batches and sends messages to a database for storage
