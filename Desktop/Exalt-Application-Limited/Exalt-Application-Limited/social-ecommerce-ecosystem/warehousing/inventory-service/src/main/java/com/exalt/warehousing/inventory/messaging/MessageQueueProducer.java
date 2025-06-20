package com.exalt.warehousing.inventory.messaging;

/**
 * Interface for sending messages to the message queue.
 * This is a simplified abstraction that will be implemented to use
 * the actual message queue system (Kafka, RabbitMQ, etc.).
 */
public interface MessageQueueProducer {

    /**
     * Send a message to a topic.
     *
     * @param topic   The topic to send the message to
     * @param message The message to send
     * @param <T>     The type of the message
     */
    <T> void send(String topic, T message);
}
