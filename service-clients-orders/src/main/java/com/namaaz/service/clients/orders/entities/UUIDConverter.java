package com.namaaz.service.clients.orders.entities;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.UUID;

/**
 * Converter pour gérer les UUID avec PostgreSQL
 */
@Converter
public class UUIDConverter implements AttributeConverter<UUID, Object> {

    @Override
    public Object convertToDatabaseColumn(UUID attribute) {
        return attribute;
    }

    @Override
    public UUID convertToEntityAttribute(Object dbData) {
        if (dbData == null) {
            return null;
        }
        if (dbData instanceof UUID) {
            return (UUID) dbData;
        }
        return UUID.fromString(dbData.toString());
    }
}
