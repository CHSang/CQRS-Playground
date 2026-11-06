package com.playground.bank.eventstore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.util.UUID;

/**
 * One row per event. The unique (aggregate_id, version) constraint gives
 * optimistic concurrency: two writers can't both append version N.
 */
@Entity
@Table(name = "event_store",
        uniqueConstraints = @UniqueConstraint(columnNames = {"aggregate_id", "version"}))
public class StoredEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sequence;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Column(nullable = false)
    private long version;

    @Column(nullable = false)
    private String type;

    @Lob
    @Column(nullable = false)
    private String payload;

    @Column(nullable = false)
    private Instant occurredAt;

    protected StoredEvent() {
    }

    public StoredEvent(UUID aggregateId, long version, String type, String payload, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.version = version;
        this.type = type;
        this.payload = payload;
        this.occurredAt = occurredAt;
    }

    public Long getSequence() { return sequence; }
    public UUID getAggregateId() { return aggregateId; }
    public long getVersion() { return version; }
    public String getType() { return type; }
    public String getPayload() { return payload; }
    public Instant getOccurredAt() { return occurredAt; }
}
