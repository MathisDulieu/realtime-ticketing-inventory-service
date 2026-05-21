package com.mathisdulieu.ticketing.inventory.utils;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UuidService {

    public String generateUuid() {
        return UUID.randomUUID().toString();
    }

}
