package com.playground.bank.eventstore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.playground.bank.common.ConcurrencyException;
import com.playground.bank.event.AccountEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Append-only store for account events. After saving, each event is
 * published so read-side projections can update their views.
 */
@Component
public class EventStore {

    private static final Map<String, Class<? extends AccountEvent>> TYPES =
            Arrays.stream(AccountEvent.class.getPermittedSubclasses())
                    .map(c -> c.asSubclass(AccountEvent.class))
                    .collect(Collectors.toMap(Class::getSimpleName, Function.identity()));

    private final StoredEventRepository repository;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher publisher;

    public EventStore(StoredEventRepository repository, ObjectMapper objectMapper,
                      ApplicationEventPublisher publisher) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.publisher = publisher;
    }

    public List<AccountEvent> load(UUID aggregateId) {
        return loadRaw(aggregateId).stream().map(this::deserialize).toList();
    }

    public List<StoredEvent> loadRaw(UUID aggregateId) {
        return repository.findByAggregateIdOrderByVersionAsc(aggregateId);
    }

    /**
     * @param expectedVersion the version the caller's aggregate was loaded at
     *                        (0 for a brand-new aggregate)
     */
    public void append(UUID aggregateId, long expectedVersion, List<AccountEvent> events) {
        long version = expectedVersion;
        try {
            for (AccountEvent event : events) {
                version++;
                repository.save(new StoredEvent(aggregateId, version,
                        event.getClass().getSimpleName(), serialize(event), event.occurredAt()));
            }
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ConcurrencyException(aggregateId, expectedVersion);
        }
        events.forEach(publisher::publishEvent);
    }

    private String serialize(AccountEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot serialize " + event, e);
        }
    }

    private AccountEvent deserialize(StoredEvent stored) {
        Class<? extends AccountEvent> type = TYPES.get(stored.getType());
        if (type == null) {
            throw new IllegalStateException("Unknown event type " + stored.getType());
        }
        try {
            return objectMapper.readValue(stored.getPayload(), type);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot deserialize event " + stored.getSequence(), e);
        }
    }
}
