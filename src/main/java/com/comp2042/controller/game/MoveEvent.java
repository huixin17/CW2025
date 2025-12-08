package com.comp2042.controller.game;

/**
 * Represents a single input action made during gameplay.
 * Stores what type of movement was requested and where the
 * action originated from (player or automated system).
 */
public final class MoveEvent {
    private final EventType eventType;
    private final EventSource eventSource;

    /**
     * Creates a new MoveEvent describing a specific input.
     *
     * @param eventType  the kind of movement (left, right, rotate, etc.)
     * @param eventSource indicates whether the move came from the user or system
     */
    public MoveEvent(EventType eventType, EventSource eventSource) {
        this.eventType = eventType;
        this.eventSource = eventSource;
    }

    /** @return the type of movement requested */
    public EventType getEventType() {
        return eventType;
    }

    /** @return where the event originated from */
    public EventSource getEventSource() {
        return eventSource;
    }
}